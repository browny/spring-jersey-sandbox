package com.sandbox.resources;

import static com.sandbox.config.SpringContextAccessor.getApplicationContext;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class ApiApplication extends ResourceConfig
{
	public ApiApplication() {
		packages("com.sandbox.resources");
		register(MultiPartFeature.class);
		register(getApplicationContext().getBean(JacksonJsonProvider.class));
	}
}
