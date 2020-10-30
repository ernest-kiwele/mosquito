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
import java.util.Map;

import com.eussence.mosquito.api.command.CommandLanguage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A request template defines dynamically evaluated expressions for request
 * parameters and data.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestTemplate {

	private String key;

	@Builder.Default
	private CommandLanguage lang = CommandLanguage.GROOVY;

	private String uriTemplate;

	@Builder.Default
	private Map<String, String> headerTemplates = new HashMap<>();

	@Builder.Default
	private Map<String, String> parameterTemplates = new HashMap<>();

	private String entityTemplate;

	private String authTypeTemplate;
	private String authCredentialsTemplate;
	private String authHeaderName;

	private String dataSet;

	@Builder.Default
	private Map<String, String> postResponseVariables = new HashMap<>();

	public RequestTemplate headerTemplate(String k, String v) {
		this.headerTemplates.put(k, v);
		return this;
	}

	public RequestTemplate parameterTemplate(String k, String v) {
		this.parameterTemplates.put(k, v);
		return this;
	}
}
