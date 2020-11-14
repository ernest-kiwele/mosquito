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

package com.eussence.mosquito.api.http;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import com.eussence.mosquito.api.AuthType;
import com.eussence.mosquito.api.exception.CheckedExecutable;

import groovy.lang.Closure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An object representing the HTTP call to be made.
 * 
 * @author Ernest Kiwele
 */
@Getter
@Setter
@Builder(toBuilder = true)

@NoArgsConstructor
@AllArgsConstructor
public final class Request {

	public static Function<Request, Response> requestHandler;

	private String uri;

	@Builder.Default
	private HttpMethod method = HttpMethod.GET;

	private Map<String, String> headers;

	@Builder.Default
	private Map<String, String> parameters = new HashMap<>();

	private Body body;

	private AuthType authType;

	private AuthData authData;

	private String dataSet;

	private String dataSetRecordId;
	private Map<String, Object> dataSetRecord;

	@Builder.Default
	private ConnectionConfig connectionConfig = new ConnectionConfig(true, 60000L, 180000L, false);

	// readers

	public URI uri() {
		if (StringUtils.isBlank(this.uri)) {
			return CheckedExecutable.wrap(() -> new URI("http://localhost:80/?" + this.queryString()));
		}

		return CheckedExecutable.wrap(() -> new URI(this.uri + "?" + this.queryString()));
	}

	public String queryString() {
		if (null != this.parameters && !this.parameters.isEmpty()) {
			return this.parameters.entrySet()
					.stream()
					.map(e -> e.getKey() + "=" + e.getValue())
					.collect(Collectors.joining("&"));
		}

		return "";
	}

	public void applyParameters(BiConsumer<String, String> paramTaker) {
		if (null == this.parameters || this.parameters.isEmpty())
			return;

		this.parameters.forEach(paramTaker);
	}

	public void applyHeaders(BiConsumer<String, String> headerTaker) {
		if (null == this.headers || this.headers.isEmpty())
			return;

		this.headers.forEach(headerTaker);
	}

	public Response call() {
		return requestHandler.apply(this);
	}

	public Response call(Closure<Object> modifier) {
		var builder = this.toBuilder();
		modifier.setDelegate(builder);
		modifier.setResolveStrategy(Closure.DELEGATE_FIRST);
		modifier.call();

		return requestHandler.apply(builder.build());
	}

	public static class RequestBuilder {
		private Map<String, String> headers = new HashMap<>();
		private Body body = Body.builder()
				.build();

		public RequestBuilder to(String uri) {
			return this.uri(uri);
		}

		public RequestBuilder from(String uri) {
			return this.uri(uri);
		}

		public RequestBuilder header(String name, String value) {
			this.headers.put(name, value);
			return this;
		}

		public RequestBuilder header(Map<String, String> headers) {
			if (null == headers) {
				return this;
			}

			this.headers.putAll(headers);
			return this;
		}

		public RequestBuilder entity(Object o) {
			if (this.body != null) {
				this.body.setEntity(o);
			} else {
				this.body = Body.builder()
						.entity(o)
						.build();
			}
			return this;
		}

		public RequestBuilder mediaType(String mt) {
			if (this.body != null) {
				this.body.setMediaType(mt);
			} else {
				this.body = Body.builder()
						.mediaType(mt)
						.build();
			}
			return this;
		}

		public RequestBuilder json(Object o) {
			if (this.body != null) {
				this.body.setMediaType(MediaType.APPLICATION_JSON);
				this.body.setEntity(o);
			} else {
				this.body = Body.builder()
						.mediaType(MediaType.APPLICATION_JSON)
						.entity(o)
						.build();
			}
			return this;
		}

		public RequestBuilder json(Map<String, Object> o) {
			if (this.body != null) {
				this.body.setMediaType(MediaType.APPLICATION_JSON);
				this.body.setEntity(o);
			} else {
				this.body = Body.builder()
						.mediaType(MediaType.APPLICATION_JSON)
						.entity(o)
						.build();
			}
			return this;
		}
	}
}
