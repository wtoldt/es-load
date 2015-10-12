package com.wtoldt.es.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;


public class ErrorReportingBulkProcessorListener implements BulkProcessor.Listener {
	private static Log LOG = LogFactory.getLog(ErrorReportingBulkProcessorListener.class);

	private boolean bulkError = false;

	public boolean hasBulkError() {
		return this.bulkError;
	}

	@Override
	public void beforeBulk(final long executionId, final BulkRequest request) {
		LOG.debug("Begin bulk insert: " + executionId);
	}

	@Override
	public void afterBulk(final long executionId, final BulkRequest request, final Throwable e) {
		bulkError = true;

		LOG.error("Error during bulk insert: " + executionId, e);
	}

	@Override
	public void afterBulk(final long executionId, final BulkRequest request, final BulkResponse response) {
		LOG.debug("End bulk insert: " + executionId);
	}
}
