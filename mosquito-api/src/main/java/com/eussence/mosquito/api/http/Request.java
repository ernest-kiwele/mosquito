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

import java.util.Map;

import com.eussence.mosquito.api.AuthType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * An object representing the HTTP call to be made.
 * 
 * @author Ernest Kiwele
 */
@Getter
@Builder

@NoArgsConstructor
@AllArgsConstructor
public final class Request {

	private String uri;

	@Builder.Default
	private HttpMethod method = HttpMethod.GET;

	@Builder.Default
	private Map<String, String> headers = Map.of();

	@Builder.Default
	private Map<String, String> parameters = Map.of();

	private Body body;

	private AuthType authType;

	private AuthData authData;

	@Builder.Default
	private ConnectionConfig connectionConfig = new ConnectionConfig(true, 60000L, 180000L, false);
}
