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

package com.eussence.mosquito.http.driver;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.eussence.mosquito.api.http.Body;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.http.api.StandardResponseHeaders;

/**
 * Converts java.net.http response objects into Mosquito's response data.
 * 
 * @author Ernest Kiwele
 */
public class StandardResponseFactory {

	public Response create(HttpResponse<?> response) {
		
		return Response.builder().status(response.statusCode())
				.headers(response.headers().map())
				.length(Integer.parseInt(response.headers().firstValue(StandardResponseHeaders.CONTENT_LENGTH.getHeaderName()).orElse("-1")))
				.cookies(this.readCookies(response))
				.failed(false)
				.uri(response.uri().toASCIIString())
				.body(body)
		
//		this.status = status;
//		this.statusReason = statusReason;
//		this.length = contentLength;
//		this.body = rawPayload;
//		this.uri = uri;
//		this.headers = Collections.unmodifiableMap(headers);
//		this.cookies = Collections.unmodifiableMap(cookies);
//
//		this.failed = false;
//		this.exception = null;
//		this.duration = duration;
	}

	private Body readBody(HttpResponse<?> resp) {
		Object entity = resp.body();

		String contentType = resp.headers()
				.firstValue(StandardResponseHeaders.CONTENT_TYPE.getHeaderName())
				.orElse(null);
		if (StringUtils.isBlank(contentType)) {
			return Body.builder()
					.build();
		}
	}

	private Map<String, String> readCookies(HttpResponse<?> resp) {
		return resp.headers()
				.allValues(StandardResponseHeaders.COOKIE.getHeaderName())
				.stream()
				.map(s -> s.split("="))
				.collect(Collectors.toMap(arr -> arr[0], arr -> arr[1], (c1, c2) -> c1));
	}

	public Response create(Throwable exception) {
		return new Response(exception);
	}
}
