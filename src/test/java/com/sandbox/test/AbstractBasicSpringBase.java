package com.sandbox.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

/**
 * The base environement for test on Spring.<p>
 */
@ContextConfiguration(
	classes=BasicSpringTestConfig.class,
	initializers=com.sandbox.config.AppContextIntializer.class
)
public class AbstractBasicSpringBase extends AbstractTestNGSpringContextTests
{
	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected AbstractBasicSpringBase() {
		super();
	}

	/**
	 * Utility method for getting logging object
	 */
	final protected Logger getLogger()
	{
		return logger;
	}
}
