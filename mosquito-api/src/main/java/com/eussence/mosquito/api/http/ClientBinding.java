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

import java.util.concurrent.CompletableFuture;

/**
 * An objecting binding Mosquito to specific HTTP client implementations. This
 * is a typical <code>Client<code> that makes an entry point into the HTTP
 * client call chain.
 * 
 * @author Ernest Kiwele
 */
public interface ClientBinding<CL, RQ, RS> {

	/**
	 * Return the request mapper used by this client binding.
	 */
	RequestMapper<RQ> getRequestMapper();

	/**
	 * Return the response mapper used by this client binding.
	 */
	ResponseMapper<RS> getResponseMapper();

	/**
	 * Submit a request to be executed synchronously.
	 * 
	 * @param request
	 *            The request to be submitted.
	 * @return The response representing the outcome of the call.
	 */
	Response send(Request request);

	/**
	 * Make an asynchronous execution of the request.
	 * 
	 * @param request
	 *            The request to be submitted.
	 * @return A future of the result.
	 */
	CompletableFuture<Response> sendAsync(Request request);
}
