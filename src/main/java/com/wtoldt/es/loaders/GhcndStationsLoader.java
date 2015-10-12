package com.wtoldt.es.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wtoldt.es.domain.GhcndStation;

@Component
public class GhcndStationsLoader extends AbstractLoader {
	private static final Log LOG = LogFactory.getLog(GhcndStationsLoader.class);

	@Override
	public void load() throws IOException {
		LOG.info("Starting GHCND station load...");
		final long startTimeMillis = System.currentTimeMillis();

		// Preapre Index
		ClusterStateResponse stateResponse = client.admin().cluster().prepareState().execute().actionGet();
		if (!stateResponse.getState().metaData().hasIndex("weatherdata")) {
			// create index
			client.admin().indices().prepareCreate("weatherdata").execute().actionGet();
			// refresh state response
			stateResponse = client.admin().cluster().prepareState().execute().actionGet();
		}

		if (!stateResponse.getState().metaData().index("weatherdata").mappings().containsKey("stations")) {
			// add mapping to index
			final XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
					.startObject()
						.startObject("stations")
							.startObject("properties")
								.startObject("location")
									.field("type", "geo_point")
								.endObject()
							.endObject()
						.endObject()
					.endObject();

			client.admin().indices()
					.preparePutMapping("weatherdata")
					.setType("stations")
					.setSource(mappingBuilder)
					.execute().actionGet();
		}

		final File file = new File(datadir + "/stations.txt");
		BufferedReader reader = null;
		final ObjectMapper mapper = new ObjectMapper();
		final StringBuilder failedLines = new StringBuilder();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			int lineNumber = 1;
			int successes = 0;
			int failures = 0;

			//TODO: use bulk processor instead
			while ((line = reader.readLine()) != null) {
				if (isLineValid(line)){
					final GhcndStation ghcndStation = parseStationLine(line);
					final byte[] jsonBytes = mapper.writeValueAsBytes(ghcndStation);
					final IndexResponse response = client.prepareIndex("weatherdata", "stations", ghcndStation.getId())
							.setSource(jsonBytes)
							.execute()
							.actionGet();
					successes++;
				} else {
					failures++;
					failedLines.append(lineNumber + ", ");
				}
				lineNumber++;
			}

			final long finishTime = (System.currentTimeMillis() - startTimeMillis) / 1000;
			LOG.info("...finished GHCND station load.");
			LOG.info("completed in " + finishTime + " seconds");
			LOG.info(successes + " successful indexes (inserts)");
			LOG.info(failures + " failed indexes on lines");
			LOG.info(failedLines.toString());


		} catch (final IOException e) {
			LOG.error("GHCND station load io error");
			e.printStackTrace();
		}
	}

	private GhcndStation parseStationLine(final String line) {
		final GhcndStation station = new GhcndStation();
		station.setId(line.substring(0, 11).trim());
		station.setLat(line.substring(12, 20).trim());
		station.setLon(line.substring(21, 30).trim());
		station.setLocation(station.getLat() + ", " + station.getLon());
		station.setElevation(line.substring(31, 37).trim());
		station.setState(line.substring(38, 40).trim());
		station.setName(line.substring(41, 71).trim());
		station.setGsnFlag(line.substring(72, 75).trim());
		station.setHcnCrnFlag(line.substring(76, 79).trim());
		station.setWmoId(line.substring(80, 85).trim());

		return station;
	}

	private boolean isLineValid(final String line) {
		return line.length() >= 85;
	}

}
