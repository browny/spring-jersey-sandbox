package com.sandbox.config;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Configuration
@ComponentScan(basePackages = { "com.fanzytv.resources" })
public class JaxRsConfig
{
	private Logger logger = LoggerFactory.getLogger(JaxRsConfig.class);

	@Inject
	private Environment environment;

	@Bean @Scope(SCOPE_SINGLETON)
	public ObjectMapper buildObjectMapper()
	{
		Boolean prettyJson = environment.getProperty("json.output.pretty", Boolean.class);

		ObjectMapper objectMapper = new ObjectMapper();
		if (prettyJson) {
			logger.info("Jackson with pretty JSON");
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		}

		objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig()
			.getDefaultVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.NONE)
			.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
			.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
			.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

		return objectMapper;
	}

	@Bean @Scope(SCOPE_SINGLETON)
	public JacksonJsonProvider buildJsonProviderForJersey(ObjectMapper objectMapper)
	{
		return new JacksonJsonProvider(objectMapper);
	}

}
