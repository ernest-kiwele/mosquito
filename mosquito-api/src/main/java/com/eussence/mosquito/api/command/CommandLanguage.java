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

package com.eussence.mosquito.api.command;

/**
 * A list of languages used for interpreted commands.
 * 
 * @author Ernest Kiwele
 */
public enum CommandLanguage {

	GROOVY("Groovy", "grv"),

	JAVASCRIPT("JavaScript", "js"),

	PYTHON("Python", "py");

	private final String key;
	private final String abbreviation;

	CommandLanguage(String key, String abbreviation) {
		this.key = key;
		this.abbreviation = abbreviation;
	}

	public String getKey() {
		return key;
	}

	public String getAbbreviation() {
		return abbreviation;
	}
}
