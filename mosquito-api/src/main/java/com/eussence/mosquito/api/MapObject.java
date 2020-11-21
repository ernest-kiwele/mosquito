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

/**
 * A more friendly map to be extended by classes that aren't meant to be much
 * more than a map.
 * 
 * @author Ernest Kiwele
 */
public class MapObject extends HashMap<String, Object> {

	private static final long serialVersionUID = -5460954841313832525L;

	public MapObject toMap(Map<String, Object> m) {
		this.put("_map", m == null ? new HashMap<String, Object>() : m);
		return this;
	}

	public MapObject toMap() {
		return this.toMap(null);
	}

	public static MapObject instance() {
		return new MapObject();
	}

	public boolean contains(String key) {
		return containsKey(key);
	}

	public String getString(String key) {
		return (String) get(key);
	}

	public MapObject add(String key, Object value) {
		put(key, value);
		return this;
	}

	public MapObject set(String key, Object value) {
		return this.add(key, value);
	}

	public MapObject drop(String key) {
		remove(key);
		return this;
	}
}
