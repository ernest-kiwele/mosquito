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

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

/**
 * A simple list of supported HTTP methods.
 * 
 * @author Ernest Kiwele
 */
public enum HttpMethod {

	GET(false),

	POST(true),

	PUT(true),

	DELETE(false),

	OPTIONS(false),

	HEAD(false),

	PATCH(true),

	TRACE(false),

	CONNECT(false);

	private final boolean bodied;

	HttpMethod(boolean bodied) {
		this.bodied = bodied;
	}

	public boolean isBodied() {
		return bodied;
	}

	public static Optional<HttpMethod> of(String s) {
		return Arrays.stream(values())
				.filter(v -> StringUtils.equalsAnyIgnoreCase(v.name(), StringUtils.trim(s)))
				.findAny();
	}
}
