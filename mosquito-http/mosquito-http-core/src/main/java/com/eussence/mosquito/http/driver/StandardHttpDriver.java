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

import java.util.concurrent.CompletableFuture;

import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.http.api.HttpDriver;

/**
 * Driver for the java.net.http-based implementation of the HTTP client
 * machinery.
 * 
 * @author Ernest Kiwele
 */
public class StandardHttpDriver implements HttpDriver {

	private static final StandardHttpDriver instance = new StandardHttpDriver();

	private StandardHttpDriver() {
	}

	public static StandardHttpDriver getInstance() {
		return instance;
	}

	@Override
	public Response http(Request request) {
		// TODO implement call
		return null;
	}

	@Override
	public CompletableFuture<Response> asyncHttp(Request request) {
		// TODO implement call
		return null;
	}
}
