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

package com.eussence.mosquito.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.eussence.mosquito.api.http.RequestTemplate;
import com.eussence.mosquito.api.qa.Assertion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A call defines an HTTP request/response instance with related configuration,
 * response handling, validation, and metrics/data capturing.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Call {

	private String key;
	private String description;
	private String comments;
	private List<String> dependsOn;

	@Builder.Default
	private Instant dateCreated = Instant.now();

	@Builder.Default
	private List<Assertion> assertions = new ArrayList<>();

	private boolean scriptMode;
	private String script;

	private String responseScript;

	@Builder.Default
	private RequestTemplate requestTemplate = RequestTemplate.builder()
			.build();

}
