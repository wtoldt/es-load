package com.wtoldt.es.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:com/wtoldt/es/config/application.properties")
public class PropertyConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		final PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		placeholderConfigurer.setIgnoreUnresolvablePlaceholders(Boolean.TRUE);
		placeholderConfigurer.setOrder(0);

		return placeholderConfigurer;
	}
}
