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

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.Charset;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import com.eussence.mosquito.api.http.Body;
import com.eussence.mosquito.api.http.HttpMethod;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.utils.JsonMapper;

/**
 * A factory for Standard HTTP client's http requests.
 * 
 * @author Ernest Kiwele
 */
public class StandardRequestFactory {

	private static final StandardRequestFactory instance = new StandardRequestFactory();

	private StandardRequestFactory() {
	}

	public static StandardRequestFactory instance() {
		return instance;
	}

	public HttpRequest createRequest(Request request) {
		HttpMethod method = request.getMethod() == null ? HttpMethod.GET : request.getMethod();

		switch (method) {
			case GET:
			case DELETE:
			case OPTIONS:
			case HEAD:
			case TRACE:
			case CONNECT:
			default:
				return this.emptyRequest(request, method);
			case POST:
			case PUT:
			case PATCH:
				return this.bodiedRequest(request, method);
		}
	}

	private HttpRequest emptyRequest(Request request, HttpMethod method) {

		HttpRequest.Builder builder = HttpRequest.newBuilder(request.uri());

		request.applyHeaders(builder::header);
		builder.method(request.getMethod()
				.name(), BodyPublishers.noBody());

		return builder.build();
	}

	private BodyPublisher bodyPublisherFor(Body body) {
		switch (body.getMediaType()) {
			case MediaType.APPLICATION_JSON: {
				if (body.isString()) {
					return BodyPublishers.ofString(body.textEntity());
				} else {
					return BodyPublishers.ofString(JsonMapper.json(body.getEntity()));
				}
			}
			case MediaType.APPLICATION_OCTET_STREAM: {
				return BodyPublishers.ofByteArray(body.bytes());
			}
			default: {// MediaType.TEXT_PLAIN: {
				if (StringUtils.isBlank(body.getCharSet())) {
					return BodyPublishers.ofString(body.textEntity());
				} else {
					return BodyPublishers.ofString(body.textEntity(), Charset.forName(body.getCharSet()));
				}
			}
		}
	}

	private HttpRequest bodiedRequest(Request request, HttpMethod method) {

		HttpRequest.Builder builder = HttpRequest.newBuilder(request.uri());

		request.applyHeaders(builder::header);
		builder.method(request.getMethod()
				.name(), this.bodyPublisherFor(request.getBody()));

		return builder.build();
	}
}
