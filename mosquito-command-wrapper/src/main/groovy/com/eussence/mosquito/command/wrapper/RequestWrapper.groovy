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

package com.eussence.mosquito.command.wrapper

import com.eussence.mosquito.api.http.HttpMethod
import com.eussence.mosquito.api.http.Request

/**
 * A request factory is a client-focused version of a request builder, 
 * which exposes easy to use methods for constructing requests through user 
 * interaction.
 * 
 * @author Ernest Kiwele
 */
class RequestWrapper {

	@Delegate
	Request request

	String name = "_default"

	RequestWrapper header(String name, String value) {
		request.headers[name] = value
		this
	}

	RequestWrapper headers(Map<String, String> values) {
		request.headers = request.headers + values ?: [:]
		this
	}

	RequestWrapper get() {
		request.method = HttpMethod.GET
		this
	}

	RequestWrapper post() {
		request.method = HttpMethod.POST
		this
	}

	RequestWrapper put() {
		request.method = HttpMethod.PUT
		this
	}

	RequestWrapper head() {
		request.method = HttpMethod.HEAD
		this
	}

	RequestWrapper options() {
		request.method = HttpMethod.OPTIONS
		this
	}

	RequestWrapper patch() {
		request.method = HttpMethod.PATCH
		this
	}

	RequestWrapper delete() {
		request.method = HttpMethod.DELETE
		this
	}
}
