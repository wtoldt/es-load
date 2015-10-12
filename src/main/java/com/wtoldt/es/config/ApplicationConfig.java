package com.wtoldt.es.config;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.wtoldt.es.client.ClientFactory;



@ComponentScan("com.wtoldt.es")
@Configuration
public class ApplicationConfig {

	@Autowired
	private ClientFactory clientFactory;

	@Bean
	public Client transportClientConfigurer() {
		return clientFactory.getClient();
	}

	@Bean
	public BulkProcessor bulkProcessorConfigurer() {
		return clientFactory.getBulkProcessor();
	}

}
