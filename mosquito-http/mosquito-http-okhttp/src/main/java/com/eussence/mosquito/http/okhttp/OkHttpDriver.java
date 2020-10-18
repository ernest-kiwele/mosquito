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
import java.util.concurrent.CompletableFuture;

import com.eussence.mosquito.api.exception.CheckedFunction;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.http.api.HttpDriver;
import com.eussence.mosquito.http.okhttp.factory.ResponseFactory;

import okhttp3.ResponseBody;

/**
 * Main driver class for the OkHttp module.
 * 
 * @author Ernest Kiwele
 */
public class OkHttpDriver implements HttpDriver {

	private static final OkHttpDriver instance = new OkHttpDriver();

	private OkHttpDriver() {
	}

	public static OkHttpDriver getInstance() {
		return instance;
	}

	public Response http(Request request) {
		try {
			Instant startTime = Instant.now();
			ResponseHolder<String> response = ClientManager.instance()
					.http(request, CheckedFunction.wrap(ResponseBody::string));
			return ResponseFactory.instance()
					.fromHttpResponse(response, startTime);
		} catch (Exception ex) {
			return ResponseFactory.instance()
					.fromException(ex);
		}
	}

	public CompletableFuture<Response> asyncHttp(Request request) {
		return CompletableFuture.supplyAsync(() -> this.http(request));
	}
}
