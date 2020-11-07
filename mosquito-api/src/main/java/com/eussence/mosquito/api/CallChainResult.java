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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eussence.mosquito.api.qa.AssertionResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of an execution of a call chain.
 * 
 * @author Ernest Kiwele
 */
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallChainResult implements Result {

	private String uniqueId;

	private MapObject _meta;

	private String key;

	@Builder.Default
	private Map<String, CallResult> callResults = new HashMap<>();

	private boolean succeeded;
	private List<AssertionResult> assertionResults;

	private Instant startDate;
	private Instant endDate;

	public boolean isSucceeded() {
		return this.succeeded && (this.assertionResults == null || this.assertionResults.stream()
				.allMatch(AssertionResult::isSucceeded));
	}

	@Override
	public Map<String, Result> getChildResults() {
		return this.callResults == null ? Map.of() : Collections.unmodifiableMap(this.callResults);
	}

	@Override
	public boolean isSuccessful() {
		return this.succeeded && (this.callResults == null || this.callResults.isEmpty() || this.callResults.values()
				.stream()
				.allMatch(Result::isSuccessful));
	}
}
