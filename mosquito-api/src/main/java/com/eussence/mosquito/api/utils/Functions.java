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

import java.util.function.Predicate;

/**
 * A collection of functional utilities.
 * 
 * @author Ernest Kiwele
 */
public class Functions {

	private Functions() {
	}

	public static <T> void conditional(Predicate<T> test, T target, Runnable when, Runnable otherwise) {
		if (test.test(target)) {
			when.run();
		} else {
			otherwise.run();
		}
	}
}
