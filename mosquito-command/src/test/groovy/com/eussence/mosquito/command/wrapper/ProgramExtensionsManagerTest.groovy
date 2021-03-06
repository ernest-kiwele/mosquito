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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * 
 * @author Ernest Kiwele
 *
 */
class ProgramExtensionsManagerTest {

	@BeforeAll
	static void setup() {
		ProgramExtensionsManager.setupDefaults()
	}

	@Test
	void testMapExtensions() {
		Assertions.assertEquals([c:3, d:4], [a:1,b:2,c:3,d:4,e:5][['c', 'd']])
	}

	@Test
	void testListExtensions() {
		Assertions.assertEquals([2, 3], [1, 2, 3, 4, 5, 6][[1, 2]])
	}
}
