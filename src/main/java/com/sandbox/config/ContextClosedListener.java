package com.sandbox.config;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

@Named
public class ContextClosedListener implements ApplicationListener<ContextClosedEvent>
{
	private final Logger logger = LoggerFactory.getLogger(ContextClosedListener.class);

	/**
	 * Clean the context of spring for outside.
	 */
	@Override
	public void onApplicationEvent(ContextClosedEvent e)
	{
		logger.info("Clean the context of Spring for outside");

		SpringContextAccessor.setApplicationContext(null);

        try {
            AbandonedConnectionCleanupThread.shutdown();
        } catch (InterruptedException exception) {
            logger.warn("Can't shutdown AbandonedConnectionCleanupThread of MySQL", exception.getMessage());
        }
	}

}
