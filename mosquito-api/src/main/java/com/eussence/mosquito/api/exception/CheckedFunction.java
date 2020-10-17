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

package com.eussence.mosquito.api.exception;

import java.util.function.Function;

/**
 * A checked function allows running code that can throw a checked exception
 * with a lambda expression.
 * 
 * @author Ernest Kiwele
 */
@FunctionalInterface
public interface CheckedFunction<T, U> {
	U apply(T input) throws Exception;

	public static <I, O> Function<I, O> wrap(CheckedFunction<I, O> e) {
		return i -> {
			try {
				return e.apply(i);
			} catch (Exception ex) {
				throw new MosquitoException(ex);
			}
		};
	}
}
