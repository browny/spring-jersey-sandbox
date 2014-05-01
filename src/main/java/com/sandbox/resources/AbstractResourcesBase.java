package com.sandbox.resources;

import javax.ws.rs.Produces;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides base enviornemnt for development of JAX-RS web service.<p.
 */
@Produces(APPLICATION_JSON)
public abstract class AbstractResourcesBase
{
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Gets the logger with name of implementing sub-class.
	 * <p>
	 *
	 * @return The logger with name of implementing sub-class
	 */
	protected Logger getLogger()
	{
		return logger;
	}
}
