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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An object holding the result of an HTTP call.
 * 
 * @author Ernest Kiwele
 */
@Getter
@Setter

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {

	private int status;

	private String statusReason;

	private long length;

	private Body body;

	private String uri;

	@Builder.Default
	private Map<String, List<String>> headers = new HashMap<>();

	@Builder.Default
	private Map<String, HttpCookie> cookies = new HashMap<>();

	private boolean failed;

	private long duration;

	private String errorMessage;

	@JsonIgnore
	private Throwable exception;

	@JsonIgnore
	private Request request;

	public Response(Throwable exception) {
		this.status = 0;
		this.statusReason = null;
		this.length = -1;
		this.body = null;
		this.uri = null;
		this.headers = null;
		this.cookies = null;

		this.failed = true;
		this.exception = exception;
		this.errorMessage = exception.getMessage();
	}
}
