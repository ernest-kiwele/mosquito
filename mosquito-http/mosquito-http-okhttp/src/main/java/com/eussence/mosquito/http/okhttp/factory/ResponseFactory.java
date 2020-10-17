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

package com.eussence.mosquito.http.okhttp.factory;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import com.eussence.mosquito.api.exception.CheckedExecutable;
import com.eussence.mosquito.api.http.HttpCookie;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.http.api.ContentTypeHandler;
import com.eussence.mosquito.http.api.StandardResponseHeaders;
import com.eussence.mosquito.http.api.common.ReponseContentHandler;
import com.eussence.mosquito.http.okhttp.ResponseHolder;

/**
 * A factory for converting OkHttp responses into API responses.
 * 
 * @author Ernest Kiwele
 */
public class ResponseFactory {
	private static final ResponseFactory instance = new ResponseFactory();

	private ResponseFactory() {
		if (null != instance) {
			throw new IllegalStateException("Cannot create multiple instances of ResponseFactory");
		}
	}

	public static ResponseFactory instance() {
		return instance;
	}

	public Response fromHttpResponse(ResponseHolder<String> httpResponseHolder, Instant durationStartDate) {
		var httpResponse = httpResponseHolder.getResponse();

		Map<String, List<String>> headers = httpResponse.headers()
				.toMultimap()
				.entrySet()
				.stream()
				.map(m -> Map.entry(m.getKey()
						.toLowerCase(), m.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		String contentType = ContentTypeHandler
				.extractMediaType(httpResponse.header(StandardResponseHeaders.CONTENT_TYPE.getHeaderName()));

		return Response.builder()
				.body(ReponseContentHandler.standardHandler(contentType)
						.process(CheckedExecutable.wrap(httpResponseHolder::getPayload), headers))
				.status(httpResponse.code())
				.statusReason(Status.fromStatusCode(httpResponse.code())
						.getReasonPhrase())
				.uri(httpResponse.request()
						.url()
						.toString())
				.headers(headers)
				.cookies(this.readCookies(headers))
				.failed(false)
				.duration(httpResponse.receivedResponseAtMillis())
				.length(httpResponse.body()
						.contentLength() < 0
								? (httpResponseHolder.getPayload() == null ? "" : httpResponseHolder.getPayload())
										.length()
								: httpResponse.body()
										.contentLength())
				.build();
	}

	public Response fromException(Throwable exception) {
		return new Response(exception);
	}

	protected Map<String, HttpCookie> readCookies(Map<String, List<String>> headers) {
		return Optional.ofNullable(headers.get(StandardResponseHeaders.COOKIE.getHeaderName()))
				.stream()
				.flatMap(List::stream)
				.map(HttpCookie::forHeader)
				.collect(Collectors.toMap(HttpCookie::getName, Function.identity()));
	}

}
