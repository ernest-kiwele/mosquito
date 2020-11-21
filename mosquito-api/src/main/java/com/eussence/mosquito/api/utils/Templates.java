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

import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.command.Resolver;
import com.eussence.mosquito.api.data.TemplatedObject;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A processor of template objects.
 * 
 * @author Ernest Kiwele
 */
public class Templates {

	private static final ObjectMapper objectMapper = new ObjectMapper()
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	public static <T extends Object> T getObject(Resolver resolver, MapObject context, TemplatedObject<T> template) {
		return objectMapper.convertValue(resolver.eval(context, template.getTemplates()), template.getRealType());
	}

	public static <T> T safeConvert(Object source, Class<T> target) {
		return objectMapper.convertValue(source, target);
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
