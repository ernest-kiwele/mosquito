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

/**
 * An assertion is a check that verifies conditions on completion.
 * 
 * @author Ernest Kiwele
 */
public class Assertion {

	private String id;
	private String description;
	private String booleanExpression;
	private String messageTemplate;

	public Assertion() {
	}

	public Assertion(String id, String description, String booleanExpression, String messageTemplate) {
		this.id = id;
		this.description = description;
		this.booleanExpression = booleanExpression;
		this.messageTemplate = messageTemplate;
	}

	public static Assertion instance(String id, String description, String booleanExpression, String messageTemplate) {
		return new Assertion(id, description, booleanExpression, messageTemplate);
	}

	public Assertion id(String id) {
		this.id = id;
		return this;
	}

	public Assertion description(String d) {
		this.description = d;
		return this;
	}

	public Assertion expr(String e) {
		this.booleanExpression = e;
		return this;
	}

	public Assertion booleanExpression(String e) {
		this.booleanExpression = e;
		return this;
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
	 * Get the value of booleanExpression
	 * 
	 * @return the booleanExpression
	 */
	public String getBooleanExpression() {
		return booleanExpression;
	}

	/**
	 * Set the value of booleanExpression
	 * 
	 * @param booleanExpression
	 *            the booleanExpression to set
	 */
	public void setBooleanExpression(String booleanExpression) {
		this.booleanExpression = booleanExpression;
	}

	/**
	 * Get the value of messageTemplate
	 * 
	 * @return the messageTemplate
	 */
	public String getMessageTemplate() {
		return messageTemplate;
	}

	/**
	 * Set the value of messageTemplate
	 * 
	 * @param messageTemplate
	 *            the messageTemplate to set
	 */
	public void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}
}
