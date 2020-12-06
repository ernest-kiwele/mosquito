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

import java.time.Instant;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.http.okhttp.factory.ResponseFactory;

import okhttp3.Protocol;

/**
 * 
 * @author Ernest Kiwele
 */
public class OkHttpDriverTest {

	@Mock
	private ClientManager clientManager;
	@Mock
	private ResponseFactory responseFactory;

	private okhttp3.Response response = new okhttp3.Response.Builder().code(200)
			.request(new okhttp3.Request.Builder().url("http://localhost/")
					.build())
			.protocol(Protocol.HTTP_1_1)
			.message("OK")
			.build();

	@InjectMocks
	private OkHttpDriver driver = OkHttpDriver.getInstance();

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		Mockito.when(this.clientManager.http(Mockito.isA(Request.class), Mockito.isA(Function.class)))
				.thenReturn(ResponseHolder.builder()
						.response(response)
						.build());

//		Mockito.when(this.responseFactory.fromHttpResponse(Mockito.isA(ResponseHolder.class), Mockito.isA(Instant.class))).thenAnswer((context) -> ((ResponseHolder)context.getArgument(0)).getre )
	}

	@Test
	void testHttpCall() {
		var mrq = Request.builder()
				.build();
		var mrs = Response.builder()
				.build();

		this.driver.http(mrq);

		Mockito.verify(this.clientManager)
				.http(Mockito.eq(mrq), Mockito.isA(Function.class));
		Mockito.verify(this.responseFactory)
				.fromHttpResponse(Mockito.isA(ResponseHolder.class), Mockito.isA(Instant.class));
	}

	@Test
	void testHttpCallException() {
		Mockito.when(
				this.responseFactory.fromHttpResponse(Mockito.isA(ResponseHolder.class), Mockito.isA(Instant.class)))
				.thenThrow(MosquitoException.class);
		var mrq = Request.builder()
				.build();
		var mrs = Response.builder()
				.build();

		this.driver.http(mrq);

		Mockito.verify(this.clientManager)
				.http(Mockito.eq(mrq), Mockito.isA(Function.class));
		Mockito.verify(this.responseFactory)
				.fromHttpResponse(Mockito.isA(ResponseHolder.class), Mockito.isA(Instant.class));
		Mockito.verify(this.responseFactory)
				.fromException(Mockito.isA(MosquitoException.class));
	}

	@Test
	void testAsyncHttp() {
		Mockito.when(
				this.responseFactory.fromHttpResponse(Mockito.isA(ResponseHolder.class), Mockito.isA(Instant.class)))
				.thenThrow(MosquitoException.class);
		var mrq = Request.builder()
				.build();
		var mrs = Response.builder()
				.build();

		this.driver.asyncHttp(mrq)
				.join();

		Mockito.verify(this.clientManager)
				.http(Mockito.eq(mrq), Mockito.isA(Function.class));
		Mockito.verify(this.responseFactory)
				.fromHttpResponse(Mockito.isA(ResponseHolder.class), Mockito.isA(Instant.class));
		Mockito.verify(this.responseFactory)
				.fromException(Mockito.isA(MosquitoException.class));
	}
}
