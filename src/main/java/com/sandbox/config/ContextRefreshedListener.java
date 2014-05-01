package com.sandbox.config;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@Named
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent>
{
	private Logger logger = LoggerFactory.getLogger(ContextRefreshedListener.class);

	/**
	 * Sets the context of spring for outside.
	 * <p>
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent e)
	{
		logger.info("Set the context of Spring for outside");

		SpringContextAccessor.setApplicationContext(e.getApplicationContext());
	}
}
