package com.sandbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackages = { "com.sandbox.config" })
@EnableScheduling
public class SpringAppConfig
{
	public final static String DEFAULT_PROP_FILE = "classpath:application.properties";

	// Defines the name of bean for "propertyPlaceholder".
	public final static String BEAN_NAME_PROP_PLACEHOLDER_CONF = "propertyPlaceholder";


	@Bean(name = BEAN_NAME_PROP_PLACEHOLDER_CONF)
	public static PropertySourcesPlaceholderConfigurer buildPlaceholderConfigurer()
	{
		return new PropertySourcesPlaceholderConfigurer();
	}

}
