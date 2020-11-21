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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.ws.rs.core.MediaType;

import com.eussence.mosquito.api.AuthType;
import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.command.CommandLanguage;
import com.eussence.mosquito.api.command.Resolver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A request template defines dynamically evaluated expressions for request
 * parameters and data.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestTemplate {

	private String key;

	@Builder.Default
	private CommandLanguage lang = CommandLanguage.GROOVY;

	private String uriTemplate;

	private Map<String, String> headerTemplates;

	private Map<String, String> parameterTemplates;

	private HttpMethod method;
	private String entityTemplate;
	private String mediaType;

	private AuthType authType;
	private String authCredentialsTemplate;
	private String authHeaderName;

	private String dataSet;

	@Builder.Default
	private Map<String, String> postResponseVariables = new HashMap<>();

	private boolean multipart;
	private List<String> partFiles = new ArrayList<>();

	public RequestTemplate headerTemplate(String k, String v) {
		this.headerTemplates.put(k, v);
		return this;
	}

	public RequestTemplate parameterTemplate(String k, String v) {
		this.parameterTemplates.put(k, v);
		return this;
	}

	public Request toRequest(Function<CommandLanguage, Resolver> resolverFactory, Supplier<MapObject> contextSupplier) {
		return RequestTemplateMapper.instance()
				.toRequest(this, resolverFactory, contextSupplier);
	}

	public Request toRequest() {
		return RequestTemplateMapper.instance()
				.toRequest(this);
	}

	public Response call() {
		return this.toRequest()
				.call();
	}

	public static class RequestTemplateBuilder {
		private HttpMethod method = HttpMethod.GET;
		private Map<String, String> headerTemplates = new HashMap<>();
		private Map<String, String> parameterTemplates = new HashMap<>();
		private Body body = Body.builder()
				.build();

		public RequestTemplateBuilder uri(String uri) {
			return this.uriTemplate(uri);
		}

		public RequestTemplateBuilder to(String uri) {
			return this.uriTemplate(uri);
		}

		public RequestTemplateBuilder from(String uri) {
			return this.uriTemplate(uri);
		}

		public RequestTemplateBuilder header(String name, String value) {
			this.headerTemplates.put(name, value);
			return this;
		}

		public RequestTemplateBuilder header(Map<String, String> headers) {
			if (null == headers) {
				return this;
			}

			this.headerTemplates.putAll(headers);
			return this;
		}

		public RequestTemplateBuilder param(String name, String value) {
			this.parameterTemplates.put(name, value);
			return this;
		}

		public RequestTemplateBuilder parameter(String name, String value) {
			return this.param(name, value);
		}

		public RequestTemplateBuilder param(Map<String, String> params) {
			if (null != params) {
				this.parameterTemplates.putAll(params);
			}

			return this;
		}

		public RequestTemplateBuilder params(Map<String, String> params) {
			return this.param(params);
		}

		public RequestTemplateBuilder parameters(Map<String, String> params) {
			return this.param(params);
		}

		public RequestTemplateBuilder parameter(Map<String, String> params) {
			return this.param(params);
		}

		public RequestTemplateBuilder entity(Object o) {
			if (this.body != null) {
				this.body.setEntity(o);
			} else {
				this.body = Body.builder()
						.entity(o)
						.build();
			}
			return this;
		}

		public RequestTemplateBuilder mediaType(String mt) {
			if (this.body != null) {
				this.body.setMediaType(mt);
			} else {
				this.body = Body.builder()
						.mediaType(mt)
						.build();
			}
			return this;
		}

		public RequestTemplateBuilder json(Object o) {
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

		public RequestTemplateBuilder json(Map<String, Object> o) {
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

		public RequestTemplateBuilder get() {
			return this.method(HttpMethod.GET);
		}

		public RequestTemplateBuilder post() {
			return this.method(HttpMethod.POST);
		}

		public RequestTemplateBuilder put() {
			return this.method(HttpMethod.PUT);
		}

		public RequestTemplateBuilder delete() {
			return this.method(HttpMethod.DELETE);
		}

		public RequestTemplateBuilder patch() {
			return this.method(HttpMethod.PATCH);
		}

		public RequestTemplateBuilder head() {
			return this.method(HttpMethod.HEAD);
		}

		public RequestTemplateBuilder options() {
			return this.method(HttpMethod.OPTIONS);
		}

		public RequestTemplateBuilder trace() {
			return this.method(HttpMethod.TRACE);
		}

		public RequestTemplateBuilder connect() {
			return this.method(HttpMethod.CONNECT);
		}

		public RequestTemplateBuilder method(String m) {
			return this.method(HttpMethod.of(m)
					.orElse(HttpMethod.GET));
		}

		public RequestTemplateBuilder method(HttpMethod m) {
			this.method = m;
			return this;
		}
	}
}
