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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A map object that supports dynamically resolved values using templates of a
 * given language.
 * 
 * @author Ernest Kiwele
 */
public class TemplatedMapObject extends MapObject {

	private ExpressionLanguage _lang = ExpressionLanguage.GROOVY;
	private Map<String, String> _meta = new HashMap<>();

	public Map<String, String> __meta() {
		if (null == this._meta) {
			this._meta = new HashMap<>();
		}

		return this._meta;
	}

	public ExpressionLanguage __lang() {
		return this._lang == null ? ExpressionLanguage.GROOVY : this._lang;
	}

	public TemplatedMapObject template(String key, String template) {

		this.__meta().put(Objects.requireNonNull(key, "The key is required"),
				Objects.requireNonNull(template, "A vlaid template is required"));

		return this;
	}
}
