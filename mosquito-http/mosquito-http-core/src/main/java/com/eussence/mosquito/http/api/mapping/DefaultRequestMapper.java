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

package com.eussence.mosquito.http.api.mapping;

import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.RequestMapper;

import io.vertx.core.http.HttpClientRequest;

/**
 * A singleton implementation of {@link RequestMapper request mapper}.
 * 
 * @author Ernest Kiwele
 */
public class DefaultRequestMapper implements RequestMapper<HttpClientRequest> {

	private static final DefaultRequestMapper instance = new DefaultRequestMapper();

	private DefaultRequestMapper() {
		if (null != instance) {
			throw new IllegalStateException("DefaultRequestMapper cannot be instantiated multiple times");
		}
	}

	public static DefaultRequestMapper getInstance() {
		return instance;
	}

	@Override
	public HttpClientRequest map(Request request) {
		return null;
	}
}
