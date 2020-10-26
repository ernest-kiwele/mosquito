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
package com.eussence.mosquito.command.wrapper

import com.eussence.mosquito.api.CallChain
import com.eussence.mosquito.api.MapObject
import com.eussence.mosquito.api.data.Dataset
import com.eussence.mosquito.api.data.Environment
import com.eussence.mosquito.api.data.Vars
import com.eussence.mosquito.api.http.Request
import com.eussence.mosquito.api.http.Response
import com.eussence.mosquito.http.api.DefaultClient
import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * The ether is the execution context from which all commands are evaluated. It
 * holds contextual and environment data.
 * 
 * @author Ernest Kiwele
 */
public class Ether {

	private static final long serialVersionUID = -7825203213429363914L

	private Map<String, CallChain> callChains = [:]
	private Map<String, Environment> environments = ["_dev": Environment.builder()
		.key("dev")
		.name("Development")
		.vars(MapObject.instance()
		.add("hello", "World"))
		.build()]

	private Map<String, Dataset> dataSets = [:]
	private Map<String, Object> vars = [:]
	private String currentChain = "default"
	@JsonIgnore
	private DefaultClient client = DefaultClient.builder().build()
	private String _env = "_dev"

	private Map<String, RequestWrapper> requests = CappedHashMap.<RequestWrapper>instance(5)
	private Map<String, Response> responses = CappedHashMap.<Response>instance(5)

	private RequestWrapper req = new RequestWrapper()
	private Request lastRequest = null
	private ResponseWrapper response = null

	private String contextType = 'request'

	private boolean allMapped = false

	public Ether() {
	}

	public enum EtherKey {
		LIVE_CHAIN("live.chain")

		private final String key

		private EtherKey(String key) {
			this.key = key
		}

		public String getKey() {
			return key
		}
	}

	public Ether putAllFields() {
		if(allMapped) {
			return this
		}

		return MapObject.instance().add("callChains", callChains)
				.add("environments", environments)
				.add("dataSets", dataSets)
				.add("vars", vars)
				.add("currentChain", currentChain)
				.add("client", client)
				.add("_env", _env)
				.add("req", req)
				.add("lastRequest", lastRequest)
				.add("response", response)
				.add("contextType", contextType)
	}

	public void newRequest(String name) {
		requests[name] = new RequestWrapper()
	}

	public void selectRequest(String name) {
		req = requests[name]
	}

	//Getters/setters

	/**
	 * Get the value of callChains
	 * @return the callChains
	 */
	public Map<String, CallChain> getCallChains() {
		return callChains
	}

	/**
	 * Set the value of callChains
	 * @param callChains the callChains to set
	 */
	public void setCallChains(Map<String, CallChain> callChains) {
		this.callChains = callChains
	}

	/**
	 * Get the value of environments
	 * @return the environments
	 */
	public Map<String, Environment> getEnvironments() {
		return environments
	}

	/**
	 * Set the value of environments
	 * @param environments the environments to set
	 */
	public void setEnvironments(Map<String, Environment> environments) {
		this.environments = environments
	}

	/**
	 * Get the value of dataSets
	 * @return the dataSets
	 */
	public Map<String, Dataset> getDataSets() {
		return dataSets
	}

	/**
	 * Set the value of dataSets
	 * @param dataSets the dataSets to set
	 */
	public void setDataSets(Map<String, Object> dataSets) {
		this.dataSets = dataSets
	}

	/**
	 * Get the value of vars
	 * @return the vars
	 */
	public Map<String, Vars> getVars() {
		return vars
	}

	/**
	 * Set the value of vars
	 * @param vars the vars to set
	 */
	public void setVars(Map<String, Object> vars) {
		this.vars = vars
	}

	/**
	 * Get the value of currentChain
	 * @return the currentChain
	 */
	public String getCurrentChain() {
		return currentChain
	}

	/**
	 * Set the value of currentChain
	 * @param currentChain the currentChain to set
	 */
	public void setCurrentChain(String currentChain) {
		this.currentChain = currentChain
	}

	/**
	 * Get the value of client
	 * @return the client
	 */
	public DefaultClient getClient() {
		return client
	}

	/**
	 * Set the value of client
	 * @param client the client to set
	 */
	public void setClient(DefaultClient client) {
		this.client = client
	}

	/**
	 * Get the value of _env
	 * @return the _env
	 */
	public String get_env() {
		return _env
	}

	/**
	 * Set the value of _env
	 * @param _env the _env to set
	 */
	public void set_env(String _env) {
		this._env = _env
	}

	/**
	 * Get the value of requests
	 * @return the requests
	 */
	public Map<String, RequestWrapper> getRequests() {
		return requests
	}

	/**
	 * Set the value of requests
	 * @param requests the requests to set
	 */
	public void setRequests(Map<String, RequestWrapper> requests) {
		this.requests = requests
	}

	/**
	 * Get the value of responses
	 * @return the responses
	 */
	public Map<String, Response> getResponses() {
		return responses
	}

	/**
	 * Set the value of responses
	 * @param responses the responses to set
	 */
	public void setResponses(Map<String, Response> responses) {
		this.responses = responses
	}

	/**
	 * Get the value of request
	 * @return the request
	 */
	public RequestWrapper getRequest() {
		return req
	}

	/**
	 * Set the value of request
	 * @param request the request to set
	 */
	public void setRequest(RequestWrapper request) {
		this.req = request
	}

	/**
	 * Get the value of response
	 * @return the response
	 */
	public ResponseWrapper getResponse() {
		return response
	}

	/**
	 * Set the value of response
	 * @param response the response to set
	 */
	public void setResponse(ResponseWrapper response) {
		this.response = response
	}

	public Request getLastRequest() {
		return lastRequest
	}

	public void setLastRequest(Request lastRequest) {
		this.lastRequest = lastRequest
	}

	public String getContextType() {
		return contextType
	}
	public void setContextType(String contextType) {
		this.contextType = contextType
	}
}
