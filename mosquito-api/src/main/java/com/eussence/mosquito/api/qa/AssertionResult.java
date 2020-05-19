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

package com.eussence.mosquito.api.qa;

import java.util.HashMap;
import java.util.Map;

/**
 * A recorded result of an assertion.
 * 
 * @author Ernest Kiwele
 */
public class AssertionResult {

	private Map<String, Object> _md = new HashMap<>();

	private String id;

	private boolean error;
	private boolean succeeded;

	private Assertion assertion;

	private String expectationMessage;
	private String errorMessage;
	private String stackTrace;

	/**
	 * Get the value of _md
	 * 
	 * @return the _md
	 */
	public Map<String, Object> get_md() {
		return _md;
	}

	/**
	 * Set the value of _md
	 * 
	 * @param _md
	 *            the _md to set
	 */
	public void set_md(Map<String, Object> _md) {
		this._md = _md;
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
	 * Get the value of error
	 * 
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * Set the value of error
	 * 
	 * @param error
	 *            the error to set
	 */
	public void setError(boolean error) {
		this.error = error;
	}

	/**
	 * Get the value of succeeded
	 * 
	 * @return the succeeded
	 */
	public boolean isSucceeded() {
		return succeeded;
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

	/**
	 * Get the value of assertion
	 * 
	 * @return the assertion
	 */
	public Assertion getAssertion() {
		return assertion;
	}

	/**
	 * Set the value of assertion
	 * 
	 * @param assertion
	 *            the assertion to set
	 */
	public void setAssertion(Assertion assertion) {
		this.assertion = assertion;
	}

	/**
	 * Get the value of expectationMessage
	 * 
	 * @return the expectationMessage
	 */
	public String getExpectationMessage() {
		return expectationMessage;
	}

	/**
	 * Set the value of expectationMessage
	 * 
	 * @param expectationMessage
	 *            the expectationMessage to set
	 */
	public void setExpectationMessage(String expectationMessage) {
		this.expectationMessage = expectationMessage;
	}

	/**
	 * Get the value of errorMessage
	 * 
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Set the value of errorMessage
	 * 
	 * @param errorMessage
	 *            the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Get the value of stackTrace
	 * 
	 * @return the stackTrace
	 */
	public String getStackTrace() {
		return stackTrace;
	}

	/**
	 * Set the value of stackTrace
	 * 
	 * @param stackTrace
	 *            the stackTrace to set
	 */
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
}
