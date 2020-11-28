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

package com.eussence.mosquito.http.driver;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eussence.mosquito.http.api.HttpDriverFactory;

/**
 * 
 * @author Ernest Kiwele
 */
public class HttpDriverFactoryLocatorTest {

	@Test
	void testGetAny() {
		Assertions.assertTrue(HttpDriverFactoryLocator.getInstance()
				.getAny()
				.isPresent());
	}

	@Test
	void testGetDefault() {
		Assertions.assertNotNull(HttpDriverFactoryLocator.getInstance()
				.getSelectedDriver());
		Assertions.assertNotNull(HttpDriverFactoryLocator.getInstance()
				.getSelectedFactory());
	}

	@Test
	void testListDrivers() {
		Set<HttpDriverFactory> list = HttpDriverFactoryLocator.getInstance()
				.listServices();
		Assertions.assertFalse(list.isEmpty());

		list.forEach(factory -> {
			Assertions.assertNotNull(HttpDriverFactoryLocator.getInstance()
					.findById(factory.getId()));
			Assertions.assertNotNull(HttpDriverFactoryLocator.getInstance()
					.findByName(factory.getName()));
			Assertions.assertNotNull(HttpDriverFactoryLocator.getInstance()
					.findAnyByProvider(factory.getProvider()));
		});
	}
}
