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

package com.eussence.mosquito.api.execution;

import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.eussence.mosquito.api.CallChainResult;
import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.Result;
import com.eussence.mosquito.api.data.Dataset;
import com.eussence.mosquito.api.data.Environment;
import com.eussence.mosquito.api.data.Vars;
import com.eussence.mosquito.api.qa.AssertionResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An object holding results of an execution.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionResult implements Result {

	private String id;
	private boolean assertionsRun;
	private boolean metricsCollected;

	private Environment environment;
	private Map<String, Dataset> datasets;
	private Map<String, Vars> vars;

	private Instant startDate;
	private Instant endDate;

	private MapObject details;

	private Map<String, CallChainResult> callChainResults;
	@Builder.Default
	private List<AssertionResult> assertionResults = new ArrayList<>();

	@Override
	public boolean isSuccessful() {
		return (assertionResults == null || assertionResults.isEmpty() || assertionResults.stream()
				.allMatch(AssertionResult::isSucceeded))
				&& (this.callChainResults == null || this.callChainResults.isEmpty() || this.callChainResults.values()
						.stream()
						.allMatch(Result::isSuccessful));
	}

	@Override
	public Map<String, Result> getChildResults() {
		return this.callChainResults == null ? new HashMap<>()
				: this.callChainResults.entrySet()
						.stream()
						.map(entry -> new AbstractMap.SimpleEntry<String, Result>(entry.getKey(), entry.getValue()))
						.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
	}
}
