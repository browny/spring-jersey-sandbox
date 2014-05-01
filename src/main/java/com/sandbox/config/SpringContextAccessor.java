package com.sandbox.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;

/**
 * Provides a singleton, static way to access the context of Spring.<p>
 */
public class SpringContextAccessor
{
	private static ApplicationContext applicationContext;

	/**
	 * Gets the context of Spring.
	 *
	 * @return The feasible context of Spring
	 *
	 * @throws ApplicationContextException
	 *             while the context of Spring is not ready
	 */
	public static ApplicationContext getApplicationContext()
	{
		if (applicationContext == null) {
			throw new ApplicationContextException("Can't get application context of Spring");
		}

		return applicationContext;
	}

	/**
	 * Sets the reference of context of Spring.
	 *
	 * @param newContext
	 *            The context of Spring or null to clear the reference
	 */
	static void setApplicationContext(ApplicationContext newContext)
	{
		applicationContext = newContext;
	}
}
