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

package com.eussence.mosquito.core.api.execution;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.data.Dataset;
import com.eussence.mosquito.api.data.Environment;
import com.eussence.mosquito.api.data.Vars;

/**
 * A once-off object used to send information to local or remote objects
 * expected to run call chains.
 * 
 * @author Ernest Kiwele
 */
public class ExecutionSchedule {

	private String id = UUID.randomUUID().toString();

	private String description;
	private String comments;

	private MapObject details = MapObject.instance();

	private Collection<CallChain> callChains;
	private Environment environment;
	private Map<String, Dataset> datasets;
	private Map<String, Vars> vars;

	{
		this.id = UUID.randomUUID().toString();
	}

	public ExecutionSchedule() {
	}

	public ExecutionSchedule(String id, Collection<CallChain> callChains, Environment environment,
			Map<String, Dataset> datasets, Map<String, Vars> vars) {
		this.callChains = callChains;
		this.environment = environment;
		this.datasets = datasets;
		this.vars = vars;
		this.id = id;
	}

	public MapObject getContext() {
		MapObject mo = MapObject.instance();

		mo.putAll(this.vars);
		mo.putAll(this.datasets);
		mo.putAll(this.environment.getVars());

		mo = mo.add("env", this.environment.getVars()).add("data", this.datasets).add("vars", this.vars);

		return mo;
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
	 * Get the value of callChains
	 * 
	 * @return the callChains
	 */
	public Collection<CallChain> getCallChains() {
		return callChains;
	}

	/**
	 * Set the value of callChains
	 * 
	 * @param callChains
	 *            the callChains to set
	 */
	public void setCallChains(Collection<CallChain> callChains) {
		this.callChains = callChains;
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
	 * Get the value of description
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the value of description
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the value of comments
	 * 
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * Set the value of comments
	 * 
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
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
}
