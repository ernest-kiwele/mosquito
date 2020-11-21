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

package com.eussence.mosquito.command.wrapper

import groovy.transform.CompileStatic

/**
 * A linked hash map with a size cap. Credit for idea: SO, https://stackoverflow.com/a/5601377/5761558
 * @author Ernest Kiwele
 */
@CompileStatic
class CappedHashMap<T> extends LinkedHashMap<String, T>{

	int sizeCap = 10

	static <T> CappedHashMap<T> instance(int size) {
		new CappedHashMap<>(sizeCap: size)
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<String, T> eldest) {
		return super.removeEldestEntry(eldest)
	}
}
