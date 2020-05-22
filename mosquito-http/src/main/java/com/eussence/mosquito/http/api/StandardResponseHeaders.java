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

package com.eussence.mosquito.http.api;

/**
 * A collection of the most commonly used HTTP headers.
 * 
 * @author Ernest Kiwele
 *
 */
public enum StandardResponseHeaders {

	CONTENT_TYPE("Content-Type"),

	CONTENT_LENGTH("Content-Length"),

	DATE("Date"),

	SERVER("Server"),

	COOKIE("Set-Cookie");

	private final String headerName;

	StandardResponseHeaders(String n) {
		this.headerName = n;
	}

	public String getHeaderName() {
		return headerName;
	}
}
