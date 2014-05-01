package com.sandbox.test;

import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.sandbox.config.ServiceConfig;
import static com.sandbox.config.SpringAppConfig.BEAN_NAME_PROP_PLACEHOLDER_CONF;

@Configuration
@Import({ServiceConfig.class})
public class ServiceTestConfig
{
	@Bean(name=BEAN_NAME_PROP_PLACEHOLDER_CONF)
	public static PropertySourcesPlaceholderConfigurer buildPlaceholderConfigurer()
	{
		return new PropertySourcesPlaceholderConfigurer();
	}
}
