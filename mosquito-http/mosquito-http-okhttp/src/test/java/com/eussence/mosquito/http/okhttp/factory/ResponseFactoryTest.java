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

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.api.utils.JsonMapper;
import com.eussence.mosquito.http.okhttp.ResponseHolder;

import okhttp3.Protocol;
import okhttp3.ResponseBody;

/**
 * 
 * @author Ernest Kiwele
 */
public class ResponseFactoryTest {

	private final Map<String, String> responseBody = Map.of("a", "B");

	private okhttp3.Request okhttpRequest = new okhttp3.Request.Builder().url("http://localhost/")
			.build();

	private okhttp3.Response okhttpResponse = new okhttp3.Response.Builder().code(201)
			.message("OK")
			.request(okhttpRequest)
			.header("content-type", "application/json")
			.header("something", "someValue")
			.header("set-cookie",
					"prov=17963160-40be-432a-b3f2-eecd7ebdd4f3; expires=Fri, 01 Jan 2055 00:00:00 GMT; domain=company.com; path=/; secure; samesite=none; httponly")
			.protocol(Protocol.HTTP_1_1)
			.receivedResponseAtMillis(System.currentTimeMillis())
			.sentRequestAtMillis(System.currentTimeMillis() - 1000)
			.body(ResponseBody.create(okhttp3.MediaType.get("application/json"), JsonMapper.json(responseBody)))
			.build();

	@Test
	void testResponse() {
		ResponseHolder<String> httpResponseHolder = ResponseHolder.<String>builder()
				.response(okhttpResponse)
				.payload(JsonMapper.json(Map.of("a", "B")))
				.build();

		Response rsp = ResponseFactory.instance()
				.fromHttpResponse(httpResponseHolder, Instant.now()
						.plusMillis(-1000));

		Assertions.assertFalse(rsp.isFailed());
		Assertions.assertEquals(201, rsp.getStatus());
		Assertions.assertEquals(List.of("someValue"), rsp.getHeaders()
				.get("something"));
		Assertions.assertEquals(MediaType.APPLICATION_JSON, rsp.getBody()
				.getMediaType());
		Assertions.assertEquals("http://localhost/", rsp.getUri());
		Assertions.assertTrue(rsp.getDuration() >= 1000);
		Assertions.assertEquals(1, rsp.getCookies()
				.size());
	}

	@Test
	void testResponseFromError() {
		Response rsp = ResponseFactory.instance()
				.fromException(new MosquitoException("Something failed"));

		Assertions.assertTrue(rsp.isFailed());
		Assertions.assertEquals(0, rsp.getStatus());
		Assertions.assertEquals("Something failed", rsp.getErrorMessage());
	}
}
