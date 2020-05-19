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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.eussence.mosquito.api.data.TemplatedObject;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.qa.Assertion;

/**
 * A call defines an HTTP request/response instance with related configuration,
 * response handling, validation, and metrics/data capturing.
 * 
 * @author Ernest Kiwele
 */
public class Call {

	private String key;
	private String description;
	private String comments;
	private String dependsOn;
	private Date dateCreated = new Date();

	private List<Assertion> assertions = new ArrayList<>();
	private boolean scriptMode = false;
	private String script;

	private String responseScript;
	private TemplatedObject<Request> requestTemplate = TemplatedObject.instance(Request.class);

	public Call scriptMode(boolean m) {
		this.scriptMode = m;
		return this;
	}

	public Call script(String s) {
		this.script = s;
		return this;
	}

	public Call key(String k) {
		this.key = k;

		return this;
	}

	public Call description(String d) {
		this.description = d;

		return this;
	}

	public Call comments(String c) {
		this.comments = c;

		return this;
	}

	public Call dependsOn(String d) {
		this.dependsOn = d;
		return this;
	}

	public Call assertions(List<Assertion> ass) {
		this.assertions = null == ass ? new ArrayList<>() : ass;
		return this;
	}

	private List<Assertion> assertions() {
		if (null == this.assertions) {
			this.assertions = new ArrayList<>();
		}
		return this.assertions;
	}

	public Call assertion(Assertion ass) {
		this.assertions().add(ass);
		return this;
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
	 * Get the value of dependsOn
	 * 
	 * @return the dependsOn
	 */
	public String getDependsOn() {
		return dependsOn;
	}

	/**
	 * Set the value of dependsOn
	 * 
	 * @param dependsOn
	 *            the dependsOn to set
	 */
	public void setDependsOn(String dependsOn) {
		this.dependsOn = dependsOn;
	}

	/**
	 * Get the value of dateCreated
	 * 
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * Set the value of dateCreated
	 * 
	 * @param dateCreated
	 *            the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
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
	 * Get the value of responseScript
	 * 
	 * @return the responseScript
	 */
	public String getResponseScript() {
		return responseScript;
	}

	/**
	 * Set the value of responseScript
	 * 
	 * @param responseScript
	 *            the responseScript to set
	 */
	public void setResponseScript(String responseScript) {
		this.responseScript = responseScript;
	}

	/**
	 * Get the value of requestTemplate
	 * 
	 * @return the requestTemplate
	 */
	public TemplatedObject<Request> getRequestTemplate() {
		return requestTemplate;
	}

	/**
	 * Set the value of requestTemplate
	 * 
	 * @param requestTemplate
	 *            the requestTemplate to set
	 */
	public void setRequestTemplate(TemplatedObject<Request> requestTemplate) {
		this.requestTemplate = requestTemplate;
	}
}
