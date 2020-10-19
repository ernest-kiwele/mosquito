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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.eussence.mosquito.api.AuthType;
import com.eussence.mosquito.api.exception.CheckedExecutable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An object representing the HTTP call to be made.
 * 
 * @author Ernest Kiwele
 */
@Getter
@Setter
@Builder

@NoArgsConstructor
@AllArgsConstructor
public final class Request {

	private String uri;

	@Builder.Default
	private HttpMethod method = HttpMethod.GET;

	@Builder.Default
	private Map<String, String> headers = new HashMap<>();

	@Builder.Default
	private Map<String, String> parameters = new HashMap<>();

	@Builder.Default
	private Body body = Body.builder()
			.build();

	private AuthType authType;

	private AuthData authData;

	@Builder.Default
	private ConnectionConfig connectionConfig = new ConnectionConfig(true, 60000L, 180000L, false);

	// readers

	public URI uri() {
		if (StringUtils.isBlank(this.uri)) {
			return CheckedExecutable.wrap(() -> new URI("http://localhost:80/?" + this.queryString()));
		}

		return CheckedExecutable.wrap(() -> new URI(this.uri + "?" + this.queryString()));
	}

	public String queryString() {
		if (null != this.parameters && !this.parameters.isEmpty()) {
			return this.parameters.entrySet()
					.stream()
					.map(e -> e.getKey() + "=" + e.getValue())
					.collect(Collectors.joining("&"));
		}

		return "";
	}

	public void applyParameters(BiConsumer<String, String> paramTaker) {
		if (null == this.parameters || this.parameters.isEmpty())
			return;

		this.parameters.forEach(paramTaker);
	}

	public void applyHeaders(BiConsumer<String, String> headerTaker) {
		if (null == this.headers || this.headers.isEmpty())
			return;

		this.headers.forEach(headerTaker);
	}
}
