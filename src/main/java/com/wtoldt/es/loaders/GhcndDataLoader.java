package com.wtoldt.es.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wtoldt.es.domain.GhcndDataDay;
import com.wtoldt.es.domain.GhcndStation;
import com.wtoldt.es.utils.GhcndDataUtils;

@Component
public class GhcndDataLoader extends AbstractLoader {
	private static final Log LOG = LogFactory.getLog(GhcndDataLoader.class);

	private static final long STATION_PAGE_SIZE = 5000;
	private int totalIndexes = 0;

	@Override
	public void load() throws IOException {
		LOG.info("Starting GHCND data load...");
		final long startTimeMillis = System.currentTimeMillis();

		// Prepare Index
		ClusterStateResponse stateResponse = client.admin().cluster()
				.prepareState().execute().actionGet();
		if (!stateResponse.getState().metaData().hasIndex("weatherdata")) {
			LOG.info("Creating weatherdata index...");
			// create index
			client.admin().indices().prepareCreate("weatherdata").execute()
					.actionGet();
			// refresh state response
			stateResponse = client.admin().cluster().prepareState().execute()
					.actionGet();
		}

		if (!stateResponse.getState().metaData().index("weatherdata")
				.mappings().containsKey("data")) {
			LOG.info("Creating data mappings...");
			// add mapping to index
			final XContentBuilder mappingBuilder = XContentFactory
					.jsonBuilder().startObject().startObject("data")
					.startObject("properties").startObject("date")
					.field("type", "date").field("format", "date").endObject()
					.startObject("station").startObject("properties")
					.startObject("location").field("type", "geo_point")
					.endObject().endObject().endObject().endObject()
					.endObject().endObject();

			client.admin().indices().preparePutMapping("weatherdata")
					.setType("data").setSource(mappingBuilder).execute()
					.actionGet();
		}

		// Fetch total stations
		final long totalStations = client.prepareSearch("weatherdata")
				.setQuery(QueryBuilders.matchAllQuery()).execute().actionGet()
				.getHits().getTotalHits();
		LOG.info("found " + totalStations + " stations...");

		// Loop through results and list ids
		long currentFrom = 0;
		long currentSize = Math.min(STATION_PAGE_SIZE, totalStations);
		final ObjectMapper mapper = new ObjectMapper();

		while (currentSize <= totalStations) {
			final float percentComplete = currentSize / totalStations;
			LOG.info("loop from " + currentFrom + " to " + currentSize
					+ " out of " + totalStations + " (" + percentComplete
					+ "% complete, " + totalIndexes + " total indexes, "
					+ ((System.currentTimeMillis() - startTimeMillis) / 1000)
					+ " seconds)");
			final SearchResponse searchResponse = client
					.prepareSearch("weatherdata").setTypes("stations")
					.setFrom((int) currentFrom).setSize((int) currentSize)
					.setQuery(QueryBuilders.matchAllQuery()).execute()
					.actionGet();

			for (final SearchHit searchHit : searchResponse.getHits()) {
				final GhcndStation ghcndStation = mapper.readValue(
						searchHit.getSourceAsString(), GhcndStation.class);
				indexFile((String) searchHit.getSource().get("id"),
						ghcndStation);
			}

			currentFrom = currentSize;
			currentSize = currentSize + STATION_PAGE_SIZE;
		}
		bulkProcessor.flush();
		final long finishTime = (System.currentTimeMillis() - startTimeMillis) / 1000;
		LOG.info("finished loading in " + finishTime + " seconds.");

		LOG.info("waiting 5 seconds for bulk to finish...");
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (final InterruptedException e) {
			LOG.error("someone interrupted my sleep. " + e.toString());
		}

	}

	private void indexFile(final String stationId, GhcndStation ghcndStation) {
		LOG.debug("opening " + stationId + ".dly...");
		// final File file = new File("test-data/" + stationId + ".dly");
		final File file = new File(datadir + "/" + stationId + ".dly");
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;

			while ((line = reader.readLine()) != null) {
				parseDataLine(line, ghcndStation);
			}

		} catch (final IOException e) {
			LOG.error("GHCND data load io error");
			e.printStackTrace();
		}
	}

	private void parseDataLine(String line, GhcndStation ghcndStation)
			throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final int blockSize = 8; // VALUE+flags
		int begin = 21; // VALUE1 starts at 21
		int end = begin + blockSize;
		int i = 1; // Index of the loop, also the day.

		while (end <= line.length()) {

			final GhcndDataDay dataDay = new GhcndDataDay();
			dataDay.setStationId(line.substring(0, 11).trim());
			dataDay.setStation(ghcndStation);
			dataDay.setYear(line.substring(11, 15).trim());
			dataDay.setMonth(line.substring(15, 17).trim());
			dataDay.setDay(i < 10 ? "0".concat(String.valueOf(i)) : String
					.valueOf(i));
			dataDay.setDate(dataDay.getYear() + "-" + dataDay.getMonth() + "-"
					+ dataDay.getDay());
			dataDay.setElement(line.substring(17, 21).trim());
			dataDay.setValue(line.substring(begin, end - 3).trim());
			dataDay.setmFlag(line.substring(end - 3, end - 2).trim());
			dataDay.setqFlag(line.substring(end - 2, end - 1).trim());
			dataDay.setsFlag(line.substring(end - 1, end).trim());

			// TODO: set descriptions
			dataDay.setElementDesc(GhcndDataUtils.getElementDescription(dataDay
					.getElement()));

			// LOG.info(dataDay);

			// only load if date is valid
			if (isDateValid(dataDay.getDate())) {
				final String key = ghcndStation.getId() + "_"
						+ dataDay.getElement() + "_" + dataDay.getDate();
				bulkProcessor
						.add(client.prepareIndex("weatherdata", "data", key)
								.setSource(mapper.writeValueAsBytes(dataDay))
								.request());
				totalIndexes++;
			}
			begin = end;
			end = end + blockSize;
			i++;
		}

	}

	private boolean isDateValid(String date) {
		try {
			final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			df.setLenient(false);
			df.parse(date);
			return true;
		} catch (final ParseException e) {
			return false;
		}
	}
}
