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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.eussence.mosquito.api.AuthType;
import com.eussence.mosquito.api.exception.CheckedExecutable;
import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.api.http.AuthData;
import com.eussence.mosquito.api.http.BodyPart;
import com.eussence.mosquito.api.http.HttpMethod;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.utils.JsonMapper;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.ResponseBody;

/**
 * 
 * @author Ernest Kiwele
 *
 */
public class ClientManagerTest {

	@Mock
	private OkHttpClient defaultClient;

	@InjectMocks
	private ClientManager clientManager = ClientManager.instance();

	private final Map<String, String> responseBody = Map.of("a", "B");

	private okhttp3.Request okhttpRequest = new okhttp3.Request.Builder().url("http://localhost/")
			.build();

	private okhttp3.Response okhttpResponse = new okhttp3.Response.Builder().code(200)
			.message("OK")
			.request(okhttpRequest)
			.protocol(Protocol.HTTP_1_1)
			.body(ResponseBody.create(okhttp3.MediaType.get("application/json"), JsonMapper.json(responseBody)))
			.build();
	@Mock
	private okhttp3.Call mockCall;

	private Request get = Request.builder()
			.get()
			.uri("http://localhost/")
			.build();

	private Request post = Request.builder()
			.post()
			.entity(responseBody)
			.mediaType(MediaType.APPLICATION_JSON)
			.uri("http://localhost/1/")
			.build();

	@BeforeEach
	public void before() throws Exception {
		MockitoAnnotations.openMocks(this);

		Mockito.when(this.defaultClient.newCall(Mockito.isA(okhttp3.Request.class)))
				.thenReturn(this.mockCall);
		Mockito.when(this.mockCall.execute())
				.thenReturn(okhttpResponse);
	}

	@Test
	void testGet() {

		Mockito.when(this.defaultClient.newCall(Mockito.isA(okhttp3.Request.class)))
				.thenAnswer(context -> {
					okhttp3.Request request = (okhttp3.Request) context.getArgument(0);

					// Verify request content:
					Assertions.assertNull(request.body());
					Assertions.assertEquals("Bearer password", request.header("Authorization"));

					return this.mockCall;
				});

		this.get.setAuthType(AuthType.BEARER_TOKEN);
		this.get.setAuthData(AuthData.builder()
				.credentials("password".toCharArray())
				.build());

		ResponseHolder<Map<String, String>> response = this.clientManager.http(this.get,
				b -> JsonMapper.fromJson(CheckedExecutable.wrap(b::string), Map.class));

		Assertions.assertEquals(this.responseBody, response.getPayload());
	}

	@Test
	void testFailedGet() {
		Assertions.assertThrows(MosquitoException.class,
				() -> this.clientManager.http(this.get, b -> JsonMapper.fromJson(CheckedExecutable.wrap(() -> {
					int a = 2 / 0;
					return b.string();
				}), Map.class)));
	}

	@Test
	void testPost() {

		Mockito.when(this.defaultClient.newCall(Mockito.isA(okhttp3.Request.class)))
				.thenAnswer(context -> {
					okhttp3.Request request = (okhttp3.Request) context.getArgument(0);

					// Verify request content:
					Assertions.assertEquals("application/json", request.body()
							.contentType()
							.toString());
					Assertions.assertEquals(9, request.body()
							.contentLength());

					Assertions.assertTrue(request.header("Authorization")
							.startsWith("Basic "));

					return this.mockCall;
				});

		this.post.setAuthType(AuthType.BASIC_AUTH);
		this.post.setAuthData(AuthData.builder()
				.credentials("password".toCharArray())
				.username("user")
				.build());
		ResponseHolder<Map<String, String>> response = this.clientManager.http(this.post,
				b -> JsonMapper.fromJson(CheckedExecutable.wrap(b::string), Map.class));

		Mockito.verify(this.defaultClient)
				.newCall(Mockito.isA(okhttp3.Request.class));// this must run for mocked assertions to be confirmed
		Assertions.assertEquals(this.responseBody, response.getPayload());
	}

	@Test
	void testFailedPost() {

		Assertions.assertThrows(MosquitoException.class,
				() -> this.clientManager.http(this.post, b -> JsonMapper.fromJson(CheckedExecutable.wrap(() -> {
					int a = 2 / 0;
					return b.string();
				}), Map.class)));
	}

	@Test
	void testMultipartPutStringPayload() throws IOException {

		Mockito.when(this.defaultClient.newCall(Mockito.isA(okhttp3.Request.class)))
				.thenAnswer(context -> {
					okhttp3.Request request = (okhttp3.Request) context.getArgument(0);

					// Verify request content:
					Assertions.assertTrue(request.body()
							.contentType()
							.toString()
							.startsWith("multipart/form-data"));
					Assertions.assertTrue(request.body()
							.contentLength() > 10);
					Assertions.assertEquals("def", request.header("abc"));

					Assertions.assertEquals("password", request.header("x-token"));

					return this.mockCall;
				});

		this.post.setAuthType(AuthType.BEARER_TOKEN);
		this.post.setAuthData(AuthData.builder()
				.headerName("x-token")
				.credentials("password".toCharArray())
				.build());
		this.post.setHeaders(Map.of("abc", "def"));
		this.post.setMethod(HttpMethod.PUT);
		this.post.getBody()
				.setMultipart(true);
		this.post.getBody()
				.part(BodyPart.builder()
						.entity("abcdef")
						.mediaType(MediaType.APPLICATION_JSON)
						.build());
		this.post.getBody()
				.part(BodyPart.builder()
						.entity("{}".getBytes())
						.mediaType(MediaType.APPLICATION_OCTET_STREAM)
						.build());
		this.post.getBody()
				.part(BodyPart.builder()
						.entity(new ByteArrayInputStream("{}".getBytes()))
						.mediaType(MediaType.APPLICATION_OCTET_STREAM)
						.build());
		String uid = UUID.randomUUID()
				.toString();
		this.post.getBody()
				.part(BodyPart.fromFile(Files.createTempFile(uid, ".json")
						.toFile()
						.getAbsolutePath()));
		ResponseHolder<Map<String, String>> response = this.clientManager.http(this.post,
				b -> JsonMapper.fromJson(CheckedExecutable.wrap(b::string), Map.class));

		Mockito.verify(this.defaultClient)
				.newCall(Mockito.isA(okhttp3.Request.class));
		Assertions.assertEquals(this.responseBody, response.getPayload());
	}

	@Test
	void testBadPart() throws IOException {
		Mockito.when(this.defaultClient.newCall(Mockito.isA(okhttp3.Request.class)))
				.thenAnswer(context -> this.mockCall);

		this.post.setHeaders(Map.of("abc", "def"));
		this.post.setMethod(HttpMethod.PATCH);
		this.post.getBody()
				.setMultipart(true);
		this.post.getBody()
				.part(BodyPart.builder()
						.entity(new Object())
						.build()
						.header("a", "B"));
		Assertions.assertThrows(MosquitoException.class, () -> this.clientManager.http(this.post,
				b -> JsonMapper.fromJson(CheckedExecutable.wrap(b::string), Map.class)));
	}

	@Test
	void testBadMethod() throws IOException {
		Assertions.assertThrows(NullPointerException.class, () -> this.clientManager.http(Request.builder()
				.method((HttpMethod) null)
				.build(), b -> JsonMapper.fromJson(CheckedExecutable.wrap(b::string), Map.class)));
	}
}
