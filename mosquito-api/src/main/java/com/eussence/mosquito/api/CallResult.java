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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.api.qa.Assertion;
import com.eussence.mosquito.api.qa.AssertionResult;

/**
 * Result object for a Call/Request.
 * 
 * @author Ernest Kiwele
 */
public class CallResult implements Result {

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

	private Date startDate;
	private Date endDate;

	@Override
	public boolean isSuccessful() {
		return this.assertionResults == null || this.assertionResults.stream().allMatch(AssertionResult::isSucceeded);
	}

	@Override
	public Map<String, Result> getChildResults() {
		return new HashMap<>();
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
	 * Get the value of executed
	 * 
	 * @return the executed
	 */
	public boolean isExecuted() {
		return executed;
	}

	/**
	 * Set the value of executed
	 * 
	 * @param executed
	 *            the executed to set
	 */
	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	/**
	 * Get the value of request
	 * 
	 * @return the request
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * Set the value of request
	 * 
	 * @param request
	 *            the request to set
	 */
	public void setRequest(Request request) {
		this.request = request;
	}

	/**
	 * Get the value of response
	 * 
	 * @return the response
	 */
	public Response getResponse() {
		return response;
	}

	/**
	 * Set the value of response
	 * 
	 * @param response
	 *            the response to set
	 */
	public void setResponse(Response response) {
		this.response = response;
	}

	/**
	 * Get the value of assertionsExecuted
	 * 
	 * @return the assertionsExecuted
	 */
	public boolean isAssertionsExecuted() {
		return assertionsExecuted;
	}

	/**
	 * Set the value of assertionsExecuted
	 * 
	 * @param assertionsExecuted
	 *            the assertionsExecuted to set
	 */
	public void setAssertionsExecuted(boolean assertionsExecuted) {
		this.assertionsExecuted = assertionsExecuted;
	}

	/**
	 * Get the value of scriptMode
	 * 
	 * @return the scriptMode
	 */
	public boolean isScriptMode() {
		return scriptMode;
	}

	/**
	 * Set the value of scriptMode
	 * 
	 * @param scriptMode
	 *            the scriptMode to set
	 */
	public void setScriptMode(boolean scriptMode) {
		this.scriptMode = scriptMode;
	}

	/**
	 * Get the value of script
	 * 
	 * @return the script
	 */
	public String getScript() {
		return script;
	}

	/**
	 * Set the value of script
	 * 
	 * @param script
	 *            the script to set
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * Get the value of assertions
	 * 
	 * @return the assertions
	 */
	public List<Assertion> getAssertions() {
		return assertions;
	}

	/**
	 * Set the value of assertions
	 * 
	 * @param assertions
	 *            the assertions to set
	 */
	public void setAssertions(List<Assertion> assertions) {
		this.assertions = assertions;
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
}
