/**
 * Copyright 2018 eussence.com and contributors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eussence.mosquito.api.utils;

import java.text.SimpleDateFormat;

import com.eussence.mosquito.api.exception.CheckedExecutable;
import com.eussence.mosquito.api.exception.MosquitoException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * A JSON mapper that executes JSON serialization and deserialization.
 * 
 * @author Ernest Kiwele
 */
public class JsonMapper {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final ObjectMapper prettyObjectMapper = new ObjectMapper();

	static {
		SimpleModule module = new SimpleModule();
		module.addSerializer(new GroovyStringSerializer());

		objectMapper.registerModule(module)
				.registerModule(new JavaTimeModule())
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		prettyObjectMapper.registerModule(module)
				.registerModule(new JavaTimeModule())
				.enable(SerializationFeature.INDENT_OUTPUT)
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.setDefaultPropertyInclusion(JsonInclude.Value.construct(Include.ALWAYS, Include.NON_NULL))
				.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SXXX"));
	}

	public static String json(Object o) throws MosquitoException {
		return CheckedExecutable.wrap(() -> objectMapper.writeValueAsString(o));
	}

	public static String jsonPretty(Object o) throws MosquitoException {
		return CheckedExecutable.wrap(() -> prettyObjectMapper.writeValueAsString(o));
	}

	public static <T> T fromJson(String s, Class<T> target) throws MosquitoException {
		return CheckedExecutable.wrap(() -> objectMapper.readValue(s, target));
	}

	public static <T> T fromJson(String s, TypeReference<T> target) throws MosquitoException {
		return CheckedExecutable.wrap(() -> objectMapper.readValue(s, target));
	}

	public static ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public static ObjectMapper getPrettyobjectmapper() {
		return prettyObjectMapper;
	}
}
