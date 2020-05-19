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

import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.eussence.mosquito.api.qa.AssertionResult;

/**
 * Result of an execution of a call chain.
 * 
 * @author Ernest Kiwele
 */
public class CallChainResult implements Result {

	private String uniqueId;

	private MapObject _meta;

	private String key;
	private Map<String, CallResult> callResults = new HashMap<>();

	private boolean succeeded;
	private List<AssertionResult> assertionResults;

	private Date startDate;
	private Date endDate;

	public boolean isSucceeded() {
		return this.succeeded && (this.assertionResults == null
				|| this.assertionResults.stream().allMatch(AssertionResult::isSucceeded));
	}

	@Override
	public Map<String, Result> getChildResults() {
		return this.callResults == null ? new HashMap<>()
				: this.callResults.entrySet().stream().map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()))
						.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
	}

	@Override
	public boolean isSuccessful() {
		return this.succeeded && (this.callResults == null || this.callResults.isEmpty()
				|| this.callResults.values().stream().allMatch(Result::isSuccessful));
	}

	/**
	 * Get the value of _meta
	 * 
	 * @return the _meta
	 */
	public MapObject get_meta() {
		return _meta;
	}

	/**
	 * Set the value of _meta
	 * 
	 * @param _meta
	 *            the _meta to set
	 */
	public void set_meta(MapObject _meta) {
		this._meta = _meta;
	}

	/**
	 * Get the value of key
	 * 
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Set the value of key
	 * 
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Get the value of callResults
	 * 
	 * @return the callResults
	 */
	public Map<String, CallResult> getCallResults() {
		return callResults;
	}

	/**
	 * Set the value of callResults
	 * 
	 * @param callResults
	 *            the callResults to set
	 */
	public void setCallResults(Map<String, CallResult> callResults) {
		this.callResults = callResults;
	}

	/**
	 * Get the value of startDate
	 * 
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Set the value of startDate
	 * 
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Get the value of endDate
	 * 
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Set the value of endDate
	 * 
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Get the value of assertionResults
	 * 
	 * @return the assertionResults
	 */
	public List<AssertionResult> getAssertionResults() {
		return assertionResults;
	}

	/**
	 * Set the value of assertionResults
	 * 
	 * @param assertionResults
	 *            the assertionResults to set
	 */
	public void setAssertionResults(List<AssertionResult> assertionResults) {
		this.assertionResults = assertionResults;
	}

	/**
	 * Set the value of succeeded
	 * 
	 * @param succeeded
	 *            the succeeded to set
	 */
	public void setSucceeded(boolean succeeded) {
		this.succeeded = succeeded;
	}

	public String getUniqueId() {
		if (null == this.uniqueId) {
			this.uniqueId = UUID.randomUUID().toString();
		}
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
}
