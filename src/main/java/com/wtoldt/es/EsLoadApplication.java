package com.wtoldt.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.wtoldt.es.config.ApplicationConfig;

@SpringBootApplication
public class EsLoadApplication {

    public static void main(String[] args) {
    	final SpringApplication app = new SpringApplication(ApplicationConfig.class);
    	app.setShowBanner(false);
    	app.run(args);
    }
}
