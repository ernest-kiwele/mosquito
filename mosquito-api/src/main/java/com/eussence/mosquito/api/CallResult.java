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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.api.qa.Assertion;
import com.eussence.mosquito.api.qa.AssertionResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result object for a Call/Request.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallResult implements Result {

	@Builder.Default
	private MapObject _meta = new MapObject();

	private String key;
	private boolean executed;

	private Request request;
	private Response response;

	private boolean assertionsExecuted;
	private boolean scriptMode;
	private String script;

	private List<Assertion> assertions;
	private List<AssertionResult> assertionResults;

	private Instant startDate;
	private Instant endDate;

	@Override
	public boolean isSuccessful() {
		return this.assertionResults == null || this.assertionResults.stream()
				.allMatch(AssertionResult::isSucceeded);
	}

	@Override
	public Map<String, Result> getChildResults() {
		return new HashMap<>();
	}
}
