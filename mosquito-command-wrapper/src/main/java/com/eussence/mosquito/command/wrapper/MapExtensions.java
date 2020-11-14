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

package com.eussence.mosquito.command.wrapper;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Mixins for map classes.
 * 
 * @author Ernest Kiwele
 */
public class MapExtensions {
	public static <K, V> Map<K, V> getAt(Map<K, V> map, Collection<K> keys) {
		return map.entrySet()
				.stream()
				.filter(e -> keys.contains(e.getKey()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}
}