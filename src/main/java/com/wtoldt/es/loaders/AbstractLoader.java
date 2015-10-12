package com.wtoldt.es.loaders;

import java.io.IOException;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractLoader {

	@Autowired
	protected Client client;

	@Autowired
	protected BulkProcessor bulkProcessor;

	@Value("${es.load.datadir}")
	protected String datadir;

	public abstract void load() throws IOException;

}
