package com.wtoldt.es;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.wtoldt.es.loaders.AbstractLoader;

@Component
public class LoadManager {
	private static final Log LOG = LogFactory.getLog(LoadManager.class);

	@Value("#{'${es.load.loaders}'.split(',')}")
	private List<String> loadersToRun;

	@Autowired
	private List<AbstractLoader> loaders;

	@Value("${es.load.loadwait}")
	private String loadwait;



	@PostConstruct
	public void init() throws IOException {
		LOG.info("initializing load manager...");

		// Loop through list of loaders
		for (final String loaderName : loadersToRun) {
			for (final AbstractLoader loader : loaders) {
				if (loader.getClass().getSimpleName().equalsIgnoreCase(loaderName)) {
					loader.load();

					// Give elasticsearch time to index
					LOG.info("waiting " + loadwait + " seconds...");
					try {
						TimeUnit.SECONDS.sleep(Integer.valueOf(loadwait));
					} catch (final InterruptedException e) {
						LOG.error("someone interrupted my sleep. " + e.toString());
					}
				}
			}
		}
	}

}
