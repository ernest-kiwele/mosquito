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

package com.eussence.mosquito.command.internal.factory;

import com.eussence.mosquito.api.Call;
import com.eussence.mosquito.api.ExpressionLanguage;

/**
 * A simple factory creating request objects.
 * 
 * @author Ernest Kiwele
 */
class CallFactory {

	public Call call() {
		return new Call();
	}

	public Call call(String key, String uri) {
		Call call = call().key(key);
		call.getRequestTemplate().set("_lang", ExpressionLanguage.GROOVY.name());
		call.getRequestTemplate().set("uri", uri);
		return call;
	}
}
