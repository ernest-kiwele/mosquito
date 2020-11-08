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

package com.eussence.mosquito.http.driver.vertx;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import com.eussence.mosquito.api.http.Body;
import com.eussence.mosquito.api.http.HttpCookie;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.Response;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.RequestOptions;

/**
 * Internal invocation bridge to Vert.x HTTP client.
 * 
 * @author Ernest Kiwele
 */
public class VertxHttpBridge {

	private static final VertxHttpBridge instance = new VertxHttpBridge();

	private VertxHttpBridge() {
		if (null != instance) {
			throw new IllegalStateException("Cannot create multiple VertxHttpBridge instances");
		}
	}

	public static VertxHttpBridge getInstance() {
		return instance;
	}

	private RequestOptions uriRequestOptions(String uri) {
		URI u;
		try {
			u = new URI(uri);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Could not parse URI: " + e.getMessage(), e);
		}

		RequestOptions options = new RequestOptions().setHost(u.getHost())
				.setSsl("https".equalsIgnoreCase(u.getScheme()))
				.setURI(u.getPath() + "/?" + u.getQuery());

		if (0 > u.getPort()) {
			if ("http".equalsIgnoreCase(u.getScheme())) {
				options.setPort(80);
			} else {
				options.setPort(443);
			}
		} else {
			options.setPort(u.getPort());
		}

		return options;
	}

	public HttpClientRequest mapRequest(Handler<HttpClientResponse> responsehandler,
			Handler<Throwable> exceptionHandler, HttpClient client, Request request) {

		HttpClientRequest req;

		switch (request.getMethod()) {
			case GET: {
				req = client.get(this.uriRequestOptions(request.getUri()))
						.handler(responsehandler)
						.exceptionHandler(exceptionHandler);
				break;
			}
			case POST: {
				req = client.post(this.uriRequestOptions(request.getUri()))
						.setChunked(true)
						.handler(responsehandler)
						.putHeader("Content-Type", request.getBody()
								.getMediaType())
						.exceptionHandler(exceptionHandler)
						.write(Buffer.buffer(request.getBody()
								.bytes()));
				break;
			}
			case PUT: {
				req = client.put(this.uriRequestOptions(request.getUri()))
						.setChunked(true)
						.handler(responsehandler)
						.putHeader("Content-Type", request.getBody()
								.getMediaType())
						.exceptionHandler(exceptionHandler)
						.write(Buffer.buffer(request.getBody()
								.bytes()));
				break;
			}
			case DELETE: {
				req = client.delete(this.uriRequestOptions(request.getUri()))
						.handler(responsehandler)
						.exceptionHandler(exceptionHandler);
				break;
			}
			case HEAD: {
				req = client.head(this.uriRequestOptions(request.getUri()))
						.handler(responsehandler)
						.exceptionHandler(exceptionHandler);
				break;
			}
			case OPTIONS: {
				req = client.options(this.uriRequestOptions(request.getUri()))
						.handler(responsehandler)
						.exceptionHandler(exceptionHandler);
				break;
			}
			case PATCH: {
				req = client.post(this.uriRequestOptions(request.getUri()))
						.setRawMethod("PATCH")
						.handler(responsehandler)
						.exceptionHandler(exceptionHandler);
				break;
			}
			default: {
				throw new IllegalArgumentException("Method not supported: " + request.getMethod());
			}
		}

		request.getHeaders()
				.forEach(req::putHeader);

		req.putHeader("User-Agent", "ernest.kiwele");
		return req.setTimeout(request.getConnectionConfig()
				.getConnectionTimeout())
				.setFollowRedirects(request.getConnectionConfig()
						.isFollowRedirects());
	}

	public void fillResponseFuture(HttpClientResponse response, CompletableFuture<Response> future) {

		Map<String, List<String>> headers = response.headers()
				.entries()
				.stream()
				.collect(Collectors.groupingBy(Map.Entry::getKey,
						Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

		Map<String, HttpCookie> cookies = response.cookies()
				.stream()
				.map(HttpCookie::forHeader)
				.collect(Collectors.toMap(HttpCookie::getName, Function.identity()));

		Response.ResponseBuilder builder = Response.builder()
				.status(response.statusCode())
				.headers(headers)
				.statusReason(response.statusMessage())
				.cookies(cookies);

		response.bodyHandler(body -> {

			// System.out.println("Body handler invoked...: " + new
			// String(body.getBytes()));

			builder.length(body.length());
			String contentTypeHeader = response.getHeader("Content-Type");
			String mediaType = extractMediaType(contentTypeHeader);
			String charSet = extractCharSet(contentTypeHeader);

			if (isTextContentType(response.getHeader("Content-Type"))) {
				builder.body(Body.builder()
						.entity(body.getString(0, body.length()))
						.mediaType(mediaType)
						.charSet(charSet)
						.build());
			} else {
				builder.body(Body.builder()
						.entity(body.getBytes())
						.mediaType(mediaType)
						.charSet(charSet)
						.build());
			}

			future.complete(builder.build());
		});
	}

	private String extractCharSet(String contentTypeHeader) {
		if (StringUtils.isBlank(contentTypeHeader)) {
			return null;
		}

		String[] parts = contentTypeHeader.split(";");
		for (String part : parts) {
			if (part.trim()
					.startsWith("charset=")) {
				return part.split("=")[1];
			}
		}

		return null;
	}

	private String extractMediaType(String contentTypeHeader) {
		if (StringUtils.isBlank(contentTypeHeader)) {
			return null;
		}

		return contentTypeHeader.split(";")[0];
	}

	private boolean isTextContentType(String contentType) {
		return StringUtils.containsAny(contentType, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
				MediaType.TEXT_HTML, MediaType.TEXT_PLAIN);
	}

	public Response sendNow(HttpClient client, Request request) {

		return send(client, request).exceptionally(exception -> Response.builder()
				.exception(exception)
				.build())
				.join();
	}

	public CompletableFuture<Response> send(HttpClient client, Request request) {

		CompletableFuture<Response> responseFuture = new CompletableFuture<>();

		HttpClientRequest req = this.mapRequest(resp -> fillResponseFuture(resp, responseFuture),
				th -> responseFuture.completeExceptionally(th), client, request);

		Date start = new Date();
		req.end();

		return responseFuture.thenApply(resp -> {
			resp.setDuration(new Date().getTime() - start.getTime());
			return resp;
		});
	}
}
