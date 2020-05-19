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

import java.util.Date;

/**
 * 
 * @author Ernest Kiwele
 */
public class ExecutionEvent {

	private String type;
	private Date eventDate = new Date();

	public static ExecutionEvent newInstance(String type) {
		ExecutionEvent e = new ExecutionEvent();

		e.type = type;
		e.eventDate = new Date();

		return e;
	}

	/**
	 * Get the value of type
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the value of type
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the value of eventDate
	 * 
	 * @return the eventDate
	 */
	public Date getEventDate() {
		return eventDate;
	}

	/**
	 * Set the value of eventDate
	 * 
	 * @param eventDate
	 *            the eventDate to set
	 */
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
}
