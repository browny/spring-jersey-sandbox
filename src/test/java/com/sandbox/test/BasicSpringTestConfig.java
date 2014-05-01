package com.sandbox.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sandbox.config.ContextClosedListener;
import com.sandbox.config.ContextRefreshedListener;

@Configuration
public class BasicSpringTestConfig
{
	@Bean
	public ContextClosedListener buildContextClosedListener()
	{
		return new ContextClosedListener();
	}

	@Bean
	public ContextRefreshedListener buildContextRefreshedListener()
	{
		return new ContextRefreshedListener();
	}

}
