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

package com.eussence.mosquito.core.api.data;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An concrete cache proxy provides access to data meant to be cached and used
 * during execution of call chains.
 * 
 * @author Ernest Kiwele
 */
public interface CacheProxy {

	public static final String SHARED_DATA_MAP = "com.eussence.mosquito.map.shared";

	ObjectMapper objectMapper = new ObjectMapper();

	default <T> String json(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to write JSON: " + e.getMessage(), e);
		}
	}

	default <T> T fromJson(String json, Class<T> type) {
		try {
			return objectMapper.readValue(json, type);
		} catch (IOException ioe) {
			throw new RuntimeException("Failed to read JSON: " + ioe.getMessage(), ioe);
		}
	}

	void put(String key, Object val);

	<T> T get(String key, Class<T> valueClass);

	CompletableFuture<Void> putAsync(String key, Object val);
}
