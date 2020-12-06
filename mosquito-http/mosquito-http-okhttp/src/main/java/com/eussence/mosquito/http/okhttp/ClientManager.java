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

package com.eussence.mosquito.http.okhttp;

import java.io.File;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.eussence.mosquito.api.AuthType;
import com.eussence.mosquito.api.exception.CheckedRunnable;
import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.api.http.AuthData;
import com.eussence.mosquito.api.http.Body;
import com.eussence.mosquito.api.http.BodyPart;
import com.eussence.mosquito.api.http.HttpMethod;
import com.eussence.mosquito.api.http.Request;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * The main client orchestrator.
 * 
 * @author Ernest Kiwele
 */
public class ClientManager {

	private static final ClientManager instance = new ClientManager();
	private OkHttpClient defaultClient = new OkHttpClient();

	public static ClientManager instance() {
		return instance;
	}

	public <T> ResponseHolder<T> http(Request request, Function<ResponseBody, T> bodyExtractor) {
		switch (Objects.requireNonNull(request.getMethod(), "Request method may not be null")) {
			case GET:
			case CONNECT:
			case DELETE:
			case OPTIONS:
			case TRACE:
			case HEAD:
				return this.bodyLessRequest(this.getClient(request), request.getUri(), request.getHeaders(),
						request.getParameters(), request.getAuthType(), request.getAuthData(), request.getMethod(),
						bodyExtractor);
			case POST:
			case PUT:
			case PATCH:
				return this.bodiedRequest(this.getClient(request), request, bodyExtractor);
			default:
				throw new MosquitoException("Unsupported request method: " + request.getMethod());

		}
	}

	protected OkHttpClient getClient(Request request) {
		// TODO: Implement client selection based on request connection settings
		return this.defaultClient;
	}

	protected <T> ResponseHolder<T> bodyLessRequest(OkHttpClient client, String uri, Map<String, String> headers,
			Map<String, String> query, AuthType authType, AuthData authData, HttpMethod method,
			Function<ResponseBody, T> bodyExtractor) {
		var request = new okhttp3.Request.Builder().url(this.appendQuery(uri, query));
		request = this.setBodyLessMethod(method, request);
		request = this.setAuthData(request, authType, authData);

		this.applyHeaders(headers, request);

		try (Response response = client.newCall(request.build())
				.execute()) {
			return ResponseHolder.<T>builder()
					.response(response)
					.payload(bodyExtractor.apply(response.body()))
					.build();
		} catch (Exception e) {
			throw new MosquitoException(e);
		}
	}

	protected <T> ResponseHolder<T> bodiedRequest(OkHttpClient client, Request req,
			Function<ResponseBody, T> bodyExtractor) {

		var request = new okhttp3.Request.Builder().url(this.appendQuery(req.getUri(), req.getParameters()));
		request = this.setBodiedMethod(req.getBody(), req.getMethod(), request);
		request = this.setAuthData(request, req.getAuthType(), req.getAuthData());

		req.applyParameters(request::header);
		req.applyHeaders(request::header);

		try (Response response = client.newCall(request.build())
				.execute()) {
			return ResponseHolder.<T>builder()
					.response(response)
					.payload(bodyExtractor.apply(response.body()))
					.build();
		} catch (Exception e) {
			throw new MosquitoException(e);
		}
	}

	private okhttp3.Request.Builder setAuthData(okhttp3.Request.Builder request, AuthType authType, AuthData authData) {
		if (null != authType) {
			switch (authType) {
				case BASIC_AUTH:
					return request.header("Authorization", "Basic " + Base64.getEncoder()
							.encodeToString(
									(authData.getUsername() + ":" + new String(authData.getCredentials())).getBytes()));
				case BEARER_TOKEN:
					if (StringUtils.isNotBlank(authData.getHeaderName())) {
						return request.header(authData.getHeaderName(), new String(authData.getCredentials()));
					} else {
						return request.header("Authorization", "Bearer " + new String(authData.getCredentials()));
					}
				default:
					throw new MosquitoException("Unsupported auth type: " + authType);
			}
		}

		return request;
	}

	private okhttp3.Request.Builder setBodyLessMethod(HttpMethod method, okhttp3.Request.Builder request) {
		switch (method) {
			case GET:
				return request.get();
			case DELETE:
				return request.delete();
			case HEAD:
				return request.head();
			case OPTIONS:
				return request.method("OPTIONS", null);
			case TRACE:
				return request.method("TRACE", null);
			case CONNECT:
				return request.method("CONNECT", null);
			default:
				throw new MosquitoException("Unsupported method for empty-bodied request: " + method);
		}
	}

	private okhttp3.Request.Builder setBodiedMethod(Body inBody, HttpMethod method, okhttp3.Request.Builder request) {
		switch (method) {
			case POST:
				return request.post(this.createRequestBody(inBody));
			case PUT:
				return request.put(this.createRequestBody(inBody));
			case PATCH:
				return request.patch(this.createRequestBody(inBody));
			default:
				throw new MosquitoException("Unsupported method for bodied request: " + method);
		}
	}

	private RequestBody createRequestBody(Body body) {
		if (body.isMultipart()) {
			MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
			for (BodyPart part : body.getParts()) {
				if (null != part.getEntity()) {
					if (part.getEntity() instanceof InputStream) {
						byte[] content = new byte[(int) part.getSize()];
						CheckedRunnable.wrap(() -> IOUtils.readFully((InputStream) part.getEntity(), content));
						requestBody.addPart(Headers.of(part.getHeaders()), RequestBody.create(content));
					} else if (part.getEntity() instanceof byte[]) {
						requestBody.addPart(Headers.of(part.getHeaders()),
								RequestBody.create((byte[]) part.getEntity()));
					} else if (part.isFromFile() && StringUtils.isNotBlank(part.getLoadedFromFile())) {
						requestBody.addPart(Headers.of(part.getHeaders()), RequestBody
								.create(new File(part.getLoadedFromFile()), MediaType.get(part.getMediaType())));
					} else if (part.getEntity() instanceof String) {
						requestBody.addPart(Headers.of(part.getHeaders()),
								RequestBody.create((String) part.getEntity(), MediaType.get(part.getMediaType())));
					} else {
						throw new MosquitoException(
								"Unable to resolve the entity from part's data: " + part.getEntity());
					}
				}
			}
			return requestBody.build();
		} else if (body.isString()) {
			return RequestBody.create(body.string(), okhttp3.MediaType.parse(body.getMediaType()));
		} else {
			return RequestBody.create(body.bytes(), okhttp3.MediaType.parse(body.getMediaType()));
		}
	}

	private void applyHeaders(Map<String, String> headers, okhttp3.Request.Builder request) {
		Optional.ofNullable(headers)
				.ifPresent(m -> m.forEach(request::header));
	}

	private String appendQuery(String uri, Map<String, String> query) {
		return uri + "?" + Optional.ofNullable(query)
				.stream()
				.flatMap(m -> m.entrySet()
						.stream())
				.map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining("&"));
	}
}
