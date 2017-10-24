package br.com.sysk.berkeleyQueue.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.sysk.berkeleyQueue.exception.JsonException;

public class JsonUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
	
	private static ObjectMapper mapper;
	
	public static <T> T fromJson(String json, Class<T> clazz) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=fromJson status=init");
		}
		T obj = null;
		try {
			obj = getMapper().readValue(json, clazz);
		} catch (IOException e) {
			LOGGER.error("function=fromJson msg=[Erro ao Deserializar a partir do json '{}']", json, e);
			throw new JsonException("Erro ao Deserializar a String Json", e);
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=fromJson status=done");
		}
		return obj;
	}

	public static String toJson(Object obj) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=toJson status=init");
		}
		String json = null;
		try {
			json = getMapper().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			LOGGER.error("function=toJson msg=[Erro ao Serializar o objeto '{}']", obj.getClass().getName(), e);
			throw new JsonException("Erro ao Serializar o objeto", e);
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("function=toJson status=done");
		}
		return json;
	}
	
	private static ObjectMapper getMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper();
		}
		return mapper;
	}
}
