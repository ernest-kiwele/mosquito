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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.eussence.mosquito.api.exception.CheckedExecutable;
import com.eussence.mosquito.api.http.Response;
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
				.toMultimap();
		String contentType = httpResponse.header(StandardResponseHeaders.CONTENT_TYPE.getHeaderName());

		return Response.builder()
				.body(ReponseContentHandler.standardHandler(contentType)
						.process(CheckedExecutable.wrap(() -> httpResponseHolder.getPayload()), headers))
				.status(httpResponse.code())
				.statusReason(Status.fromStatusCode(httpResponse.code())
						.getReasonPhrase())
				.uri(httpResponse.request()
						.url()
						.toString())
				.headers(headers)
				.cookies(this.readCookies(httpResponse))
				.failed(false)
				.duration(durationStartDate == null ? 0 : ChronoUnit.MILLIS.between(durationStartDate, Instant.now()))
				.length(Integer.parseInt(StringUtils.firstNonBlank(
						httpResponse.header(StandardResponseHeaders.CONTENT_LENGTH.getHeaderName()), "0")))
				.build();
	}

	public Response fromException(Throwable exception) {
		return new Response(exception);
	}

	protected Map<String, String> readCookies(okhttp3.Response resp) {
		return Optional.ofNullable(resp.headers()
				.toMultimap()
				.get(StandardResponseHeaders.COOKIE.getHeaderName()))
				.stream()
				.flatMap(List::stream)
				.map(s -> s.split("="))
				.collect(Collectors.toMap(arr -> arr[0], arr -> arr[1], (c1, c2) -> c1));
	}
}
