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

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * A test for all exception types, including related functional interfaces.
 * 
 * @author Ernest Kiwele
 */
public class MosquitoExceptionTest {

	@Test
	void testBasics() {
		var e = MosquitoException.supplier("abc");
		Assert.assertEquals("abc", e.get()
				.getMessage());

		var ex = new Exception("def");
		Assert.assertEquals("abc", new MosquitoException("abc", ex).getMessage());
		Assert.assertEquals("def", new MosquitoException("abc", ex).getCause()
				.getMessage());
		Assert.assertEquals(ex, new MosquitoException("abc", ex).getCause());
		Assert.assertEquals(ex, new MosquitoException(ex).getCause());
	}

	@Test
	void testCheckedFunction() throws Exception {
		CheckedFunction<String, Integer> cf = String::length;
		Assert.assertEquals(Integer.valueOf(4), cf.apply("abcd"));
		Assertions.assertThrows(MosquitoException.class, () -> CheckedFunction.wrap((String s) -> s.length())
				.apply(null));
		Assert.assertEquals(Integer.valueOf(4), CheckedFunction.wrap(cf)
				.apply("abcd"));
	}

	@Test
	void testCheckedExecutable() throws Exception {
		CheckedExecutable<Integer> ce = () -> 42, cd;
		Assert.assertEquals(Integer.valueOf(42), CheckedExecutable.wrap(ce));
		cd = () -> 42 / (2 - 2);
		Assertions.assertThrows(MosquitoException.class, () -> CheckedExecutable.wrap(cd));
	}

	@Test
	void testCheckedRunnable() {
		CheckedRunnable cr = () -> System.out.println(42 / 0);
		Assertions.assertThrows(MosquitoException.class, () -> CheckedRunnable.wrap(cr)
				.run());

		var sb = new StringBuilder("abc");
		CheckedRunnable.wrap(() -> sb.delete(0, sb.length()))
				.run();
		Assert.assertTrue(sb.toString()
				.isEmpty());
	}
}
