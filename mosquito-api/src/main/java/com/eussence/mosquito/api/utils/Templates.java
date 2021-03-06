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

package com.eussence.mosquito.api.utils;

/**
 * A processor of template objects.
 * 
 * @author Ernest Kiwele
 */
public class Templates {

	private Templates() {
	}

	public static <T> T safeConvert(Object source, Class<T> target) {
		return JsonMapper.getObjectMapper()
				.convertValue(source, target);
	}

	public static String singleQuote(String s) {
		return s == null ? null : "'" + s + "'";
	}

	public static String multilineQuote(String s) {
		return s == null ? null : "\"\"\"" + s + "\"\"\"";
	}

	public static String castString(Object o) {
		return null == o ? null : (o instanceof String ? (String) o : o.toString());
	}
}
