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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
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

/**
 * An object holding results of an execution.
 * 
 * @author Ernest Kiwele
 */
public class ExecutionResult implements Result {

	private String id;
	private boolean assertionsRun;
	private boolean metricsCollected;

	private Environment environment;
	private Map<String, Dataset> datasets;
	private Map<String, Vars> vars;

	private Date startDate;
	private Date endDate;

	private MapObject details;

	private Map<String, CallChainResult> callChainResults;
	private List<AssertionResult> assertionResults = new ArrayList<>();

	public ExecutionResult() {
	}

	public ExecutionResult(String id, boolean assertionsRun, boolean metricsCollected, Environment environment,
			Map<String, Dataset> datasets, Map<String, Vars> vars, Map<String, CallChainResult> callChainResults) {
		this.id = id;
		this.assertionsRun = assertionsRun;
		this.metricsCollected = metricsCollected;
		this.environment = environment;
		this.datasets = datasets;
		this.vars = vars;
		this.callChainResults = callChainResults;
	}

	@Override
	public boolean isSuccessful() {
		return (assertionResults == null || assertionResults.isEmpty()
				|| assertionResults.stream().allMatch(AssertionResult::isSucceeded))
				&& (this.callChainResults == null || this.callChainResults.isEmpty()
						|| this.callChainResults.values().stream().allMatch(Result::isSuccessful));
	}

	@Override
	public Map<String, Result> getChildResults() {
		return this.callChainResults == null ? new HashMap<>()
				: this.callChainResults.entrySet().stream()
						.map(entry -> new AbstractMap.SimpleEntry<String, Result>(entry.getKey(), entry.getValue()))
						.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
	}

	/**
	 * Get the value of id
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the value of id
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the value of assertionsRun
	 * 
	 * @return the assertionsRun
	 */
	public boolean isAssertionsRun() {
		return assertionsRun;
	}

	/**
	 * Set the value of assertionsRun
	 * 
	 * @param assertionsRun
	 *            the assertionsRun to set
	 */
	public void setAssertionsRun(boolean assertionsRun) {
		this.assertionsRun = assertionsRun;
	}

	/**
	 * Get the value of metricsCollected
	 * 
	 * @return the metricsCollected
	 */
	public boolean isMetricsCollected() {
		return metricsCollected;
	}

	/**
	 * Set the value of metricsCollected
	 * 
	 * @param metricsCollected
	 *            the metricsCollected to set
	 */
	public void setMetricsCollected(boolean metricsCollected) {
		this.metricsCollected = metricsCollected;
	}

	/**
	 * Get the value of environment
	 * 
	 * @return the environment
	 */
	public Environment getEnvironment() {
		return environment;
	}

	/**
	 * Set the value of environment
	 * 
	 * @param environment
	 *            the environment to set
	 */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * Get the value of datasets
	 * 
	 * @return the datasets
	 */
	public Map<String, Dataset> getDatasets() {
		return datasets;
	}

	/**
	 * Set the value of datasets
	 * 
	 * @param datasets
	 *            the datasets to set
	 */
	public void setDatasets(Map<String, Dataset> datasets) {
		this.datasets = datasets;
	}

	/**
	 * Get the value of vars
	 * 
	 * @return the vars
	 */
	public Map<String, Vars> getVars() {
		return vars;
	}

	/**
	 * Set the value of vars
	 * 
	 * @param vars
	 *            the vars to set
	 */
	public void setVars(Map<String, Vars> vars) {
		this.vars = vars;
	}

	/**
	 * Get the value of callChainResults
	 * 
	 * @return the callChainResults
	 */
	public Map<String, CallChainResult> getCallChainResults() {
		return callChainResults;
	}

	/**
	 * Set the value of callChainResults
	 * 
	 * @param callChainResults
	 *            the callChainResults to set
	 */
	public void setCallChainResults(Map<String, CallChainResult> callChainResults) {
		this.callChainResults = callChainResults;
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
	 * Get the value of details
	 * 
	 * @return the details
	 */
	public MapObject getDetails() {
		return details;
	}

	/**
	 * Set the value of details
	 * 
	 * @param details
	 *            the details to set
	 */
	public void setDetails(MapObject details) {
		this.details = details;
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

}
