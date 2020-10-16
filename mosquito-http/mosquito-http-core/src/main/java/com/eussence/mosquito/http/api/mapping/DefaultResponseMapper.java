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

import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.api.http.ResponseMapper;

import io.vertx.core.http.HttpClientResponse;

/**
 * Default implementation for {@link ResponseMapper response mapper}.
 * 
 * @author Ernest Kiwele
 */
public class DefaultResponseMapper implements ResponseMapper<HttpClientResponse> {
	private static final DefaultResponseMapper instance = new DefaultResponseMapper();

	private DefaultResponseMapper() {
		if (null != instance) {
			throw new IllegalStateException("DefaultResponseMapper cannot be instantiated multiple times");
		}
	}

	public static DefaultResponseMapper getInstance() {
		return instance;
	}

	@Override
	public Response map(HttpClientResponse response) {
		return null;
	}
}
