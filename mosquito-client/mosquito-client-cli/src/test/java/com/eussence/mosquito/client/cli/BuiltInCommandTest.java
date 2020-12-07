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

package com.eussence.mosquito.client.cli;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author Ernest Kiwele
 *
 */
public class BuiltInCommandTest {

	@Test
	void testFind() {
		Arrays.asList(BuiltInCommand.values())
				.forEach(comm -> {
					Assertions.assertEquals(comm, BuiltInCommand.of(comm.getSynonyms()
							.iterator()
							.next()
							.toLowerCase())
							.get());
					Assertions.assertNotNull(comm.getProcessor());
					Assertions.assertNotNull(comm.getBundlePrefix());
				});
	}

	@Test
	void testMatch() {
		Arrays.asList(BuiltInCommand.values())
				.stream()
				.flatMap(f -> f.getSynonyms()
						.stream()
						.map(s -> Map.entry(f, s)))
				.forEach(comm -> Assertions.assertNotNull(BuiltInCommand.match(comm.getValue())
						.get()));
	}
}
