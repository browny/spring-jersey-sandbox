package com.sandbox.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;

public class AppContextIntializer implements
	ApplicationContextInitializer<ConfigurableApplicationContext>
{
	private static final Logger logger = LoggerFactory.getLogger(AppContextIntializer.class);

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext)
	{
		MutablePropertySources propertySources = applicationContext.getEnvironment()
			.getPropertySources();

		ResourcePropertySource defaultPropertySource;
		try {
			defaultPropertySource = new ResourcePropertySource(SpringAppConfig.DEFAULT_PROP_FILE);
		} catch (IOException e) {
			logger.error("Can't load resource[{}]: {}", SpringAppConfig.DEFAULT_PROP_FILE,
				e.getMessage());
			throw new RuntimeException(e);
		}

		propertySources.addLast(defaultPropertySource);

		logger.info("Add property file [{}]", SpringAppConfig.DEFAULT_PROP_FILE);
	}
}
