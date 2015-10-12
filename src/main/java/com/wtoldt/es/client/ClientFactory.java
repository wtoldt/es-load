package com.wtoldt.es.client;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.wtoldt.es.domain.ErrorReportingBulkProcessorListener;

@Component
public class ClientFactory {
	private static final Log LOG = LogFactory.getLog(ClientFactory.class);
	private Client client;
	private BulkProcessor bulkProcessor;

	@Value("${es.load.client.clustername}")
	private String clusterName;

	@Value("${es.load.client.hostname}")
	private String hostName;

	@Value("${es.load.client.port}")
	private String clientPort;

	@Value("${es.load.bulkprocessor.actions}")
	private String bulkActions;

	@Value("${es.load.bulkprocessor.flushInterval}")
	private String flushInterval;

	@Value("${es.load.bulkprocessor.concurrentrequests}")
	private String concurrentRequests;

	@PostConstruct
	public void init() {
		LOG.info("client init");

		final Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
		final TransportClient transportClient = new TransportClient(settings);
		transportClient.addTransportAddress(new InetSocketTransportAddress("localhost", Integer.valueOf(clientPort)));
		this.client = transportClient;

		// Create BulkProcessor
		final ErrorReportingBulkProcessorListener bulkProcessorListener = new ErrorReportingBulkProcessorListener();
		final BulkProcessor bulkProcessor = BulkProcessor.builder(client, bulkProcessorListener)
				.setBulkActions(Integer.valueOf(bulkActions))
				.setFlushInterval(TimeValue.timeValueSeconds(Integer.valueOf(flushInterval)))
				.setConcurrentRequests(Integer.valueOf(concurrentRequests)).build();
		this.bulkProcessor = bulkProcessor;
	}

	public Client getClient() {
		return this.client;
	}

	public BulkProcessor getBulkProcessor() {
		return this.bulkProcessor;
	}

	@PreDestroy
	public void destroy() throws Exception {
		this.client.close();
		this.bulkProcessor.close();
	}

}
