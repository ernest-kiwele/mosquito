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

package com.eussence.mosquito.http.api;

import com.eussence.mosquito.api.http.RequestMapper;
import com.eussence.mosquito.api.http.ResponseMapper;
import com.eussence.mosquito.http.api.mapping.DefaultRequestMapper;
import com.eussence.mosquito.http.api.mapping.DefaultResponseMapper;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;

/**
 * A builder for {@link DefaultClient client} instances
 * 
 * @author Ernest Kiwele
 */
public class DefaultClientBuilder {

	private HttpClient client;
	private RequestMapper<HttpClientRequest> requestMapper;
	private ResponseMapper<HttpClientResponse> responseMapper;

	public static DefaultClientBuilder instance() {
		return new DefaultClientBuilder();
	}

	public DefaultClient build() {

		// Ensure that defaults are used where
		// values were not supplied.
		this.fillDefaults();

		return new DefaultClient(requestMapper, responseMapper, this.client);
	}

	private void fillDefaults() {
		if (null == this.requestMapper) {
			this.requestMapper = DefaultRequestMapper.getInstance();
		}

		if (null == this.responseMapper) {
			this.responseMapper = DefaultResponseMapper.getInstance();
		}

		if (null == this.client) {
			HttpClientOptions o = new HttpClientOptions().setTryUseCompression(true);
			this.client = Vertx.vertx().createHttpClient(o);
		}
	}

	public DefaultClientBuilder requestMapper(RequestMapper<HttpClientRequest> requestMapper) {
		this.requestMapper = requestMapper;

		return this;
	}

	public DefaultClientBuilder responseMapper(ResponseMapper<HttpClientResponse> responseMapper) {
		this.responseMapper = responseMapper;

		return this;
	}

}
