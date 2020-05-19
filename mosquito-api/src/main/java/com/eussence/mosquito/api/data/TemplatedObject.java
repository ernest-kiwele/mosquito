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

package com.eussence.mosquito.api.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A holder of a "mock" version of an object, which is to be converted into a
 * "real" version after parsing templates.
 * 
 * @author Ernest Kiwele
 */
public class TemplatedObject<T extends Object> {

	private Map<String, String> templates = new HashMap<>();

	@JsonIgnore
	private Class<T> realType;

	/**
	 * For use in reflective calls.
	 */
	public TemplatedObject() {

	}

	public TemplatedObject(Class<T> type) {

		Objects.requireNonNull(type, "Type class is required");

		this.realType = type;

		this.init();
	}

	public void init() {
		Arrays.stream(this.realType.getDeclaredFields()).forEach(field -> templates.put(field.getName(), "null"));
	}

	public static <T extends Object> TemplatedObject<T> instance(Class<T> objectType) {
		return new TemplatedObject<T>(objectType);
	}

	public Class<T> getRealType() {
		return realType;
	}

	public Map<String, String> getTemplates() {
		return templates;
	}

	public TemplatedObject<T> set(String key, String template) {

		if (!this.templates.containsKey(key)) {
			System.out.println("[WARNING] - Potentially incorrect key added: '" + key + "'. Known keys: "
					+ this.templates.keySet());
		}

		this.templates.put(key, template);

		return this;
	}

	/**
	 * Set the value of templates
	 * 
	 * @param templates
	 *            the templates to set
	 */
	public void setTemplates(Map<String, String> templates) {
		this.templates = templates;
	}

	/**
	 * Set the value of realType
	 * 
	 * @param realType
	 *            the realType to set
	 */
	public void setRealType(Class<T> realType) {
		this.realType = realType;
	}
}
