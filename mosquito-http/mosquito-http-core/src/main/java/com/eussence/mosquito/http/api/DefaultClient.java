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

import java.util.concurrent.CompletableFuture;

import com.eussence.mosquito.api.http.ClientBinding;
import com.eussence.mosquito.api.http.ConnectionConfig;
import com.eussence.mosquito.api.http.HttpMethod;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.RequestMapper;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.api.http.ResponseMapper;
import com.eussence.mosquito.http.driver.vertx.VertxHttpBridge;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;

/**
 * An immutable object responsible for making HTTP service calls.
 * 
 * @author Ernest Kiwele
 */
public class DefaultClient implements ClientBinding<HttpClient, HttpClientRequest, HttpClientResponse> {

	private final RequestMapper<HttpClientRequest> requestMapper;
	private final ResponseMapper<HttpClientResponse> responseMapper;
	private final HttpClient httpClient;

	public DefaultClient(RequestMapper<HttpClientRequest> requestMapper,
			ResponseMapper<HttpClientResponse> responseMapper, HttpClient client) {
		this.requestMapper = requestMapper;
		this.responseMapper = responseMapper;
		this.httpClient = client;
	}

	public static DefaultClientBuilder builder() {
		return new DefaultClientBuilder();
	}

	/**
	 * Get the value of requestMapper
	 * 
	 * @return the requestMapper
	 */
	public RequestMapper<HttpClientRequest> getRequestMapper() {
		return requestMapper;
	}

	/**
	 * Get the value of responseMapper
	 * 
	 * @return the responseMapper
	 */
	public ResponseMapper<HttpClientResponse> getResponseMapper() {
		return responseMapper;
	}

	@Override
	public Response send(Request request) {
		return VertxHttpBridge.getInstance().sendNow(this.httpClient, request);
	}

	@Override
	public CompletableFuture<Response> sendAsync(Request request) {
		return VertxHttpBridge.getInstance().send(this.httpClient, request);
	}

	public static void main(String[] args) throws Exception {

		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

		System.out.println(mapper.writeValueAsString(DefaultClient.builder().build()
				.send(Request.builder().connectionConfig(new ConnectionConfig(false, 5000, 5000, false))
						.method(HttpMethod.GET).uri("http://localhost:8010/jenkins/").build())));
		// https://api.stackexchange.com/2.2/questions?order=desc&sort=hot&site=stackoverflow
		System.out.println("Done......");
	}
}
