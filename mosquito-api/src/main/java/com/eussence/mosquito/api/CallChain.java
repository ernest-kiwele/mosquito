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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eussence.mosquito.api.qa.Assertion;

/**
 * A chain of call objects that may or may not have interdependencies.
 * 
 * @author Ernest Kiwele
 */
public class CallChain {

	private String key;
	private String name;
	private String description;
	private String comments;

	private Map<String, Call> calls = new HashMap<>();
	private List<Assertion> assertions = new ArrayList<>();

	private ExpressionLanguage expressionLanguage = ExpressionLanguage.GROOVY;

	private boolean scriptMode = false;
	private String script;

	private String createdBy;
	private Date dateCreated = new Date();

	public CallChain() {
	}

	public CallChain(String key, String name, String description, String comments, Map<String, Call> calls,
			List<Assertion> assertions, ExpressionLanguage expressionLanguage, boolean scriptMode, String script,
			String createdBy, Date dateCreated) {
		super();
		this.key = key;
		this.name = name;
		this.description = description;
		this.comments = comments;
		this.calls = calls;
		this.assertions = assertions;
		this.expressionLanguage = expressionLanguage;
		this.scriptMode = scriptMode;
		this.script = script;
		this.createdBy = createdBy;
		this.dateCreated = dateCreated;
	}

	public CallChain script(String s) {
		this.script = s;
		return this;
	}

	public CallChain scriptMode(boolean s) {
		this.scriptMode = s;
		return this;
	}

	public CallChain key(String k) {
		this.key = k;
		return this;
	}

	public CallChain name(String n) {
		this.name = n;
		return this;
	}

	public CallChain description(String d) {
		this.description = d;
		return this;
	}

	public CallChain comments(String c) {
		this.comments = c;
		return this;
	}

	public CallChain calls(Map<String, Call> calls) {
		if (null == calls) {
			this.calls = new HashMap<>();
		} else {
			this.calls = calls;
		}

		return this;
	}

	public CallChain call(String key, Call call) {
		this.calls.put(key, call);

		return this;
	}

	public CallChain assertions(List<Assertion> ass) {
		if (null == ass) {
			this.assertions = new ArrayList<>();
		} else {
			this.assertions = ass;
		}

		return this;
	}

	private List<Assertion> assertions() {
		if (null == this.assertions) {
			this.assertions = new ArrayList<Assertion>();
		}

		return this.assertions;
	}

	public CallChain assertion(Assertion ass) {
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
	 * Get the value of name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the value of name
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Get the value of calls
	 * 
	 * @return the calls
	 */
	public Map<String, Call> getCalls() {
		return calls;
	}

	/**
	 * Set the value of calls
	 * 
	 * @param calls
	 *            the calls to set
	 */
	public void setCalls(Map<String, Call> calls) {
		this.calls = calls;
	}

	/**
	 * Get the value of createdBy
	 * 
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Set the value of createdBy
	 * 
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
	 * Get the value of expressionLanguage
	 * 
	 * @return the expressionLanguage
	 */
	public ExpressionLanguage getExpressionLanguage() {
		return expressionLanguage;
	}

	/**
	 * Set the value of expressionLanguage
	 * 
	 * @param expressionLanguage
	 *            the expressionLanguage to set
	 */
	public void setExpressionLanguage(ExpressionLanguage expressionLanguage) {
		this.expressionLanguage = expressionLanguage;
	}
}
