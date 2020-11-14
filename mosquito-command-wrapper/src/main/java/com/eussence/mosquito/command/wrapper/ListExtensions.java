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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Extension methods for list types.
 * 
 * @author Ernest Kiwele
 */
public class ListExtensions {
	public static <E> List<E> getAt(List<E> list, List<Integer> keys) {
		return keys.stream()
				.map(list::get)
				.collect(Collectors.toList());
	}
}
