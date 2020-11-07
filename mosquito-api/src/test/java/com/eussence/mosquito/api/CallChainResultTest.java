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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eussence.mosquito.api.qa.AssertionResult;

/**
 * @author Ernest Kiwele
 */
public class CallChainResultTest {

	@Test
	public void isSucceeded() {
		var chainResult = CallChainResult.builder()
				.succeeded(false)
				.assertionResults(List.of(AssertionResult.builder()
						.succeeded(true)
						.build(),
						AssertionResult.builder()
								.succeeded(false)
								.build()))
				.build();

		Assertions.assertFalse(chainResult::isSucceeded);
		Assertions.assertFalse(chainResult.toBuilder()
				.assertionResults(List.of(AssertionResult.builder()
						.succeeded(true)
						.build(),
						AssertionResult.builder()
								.succeeded(true)
								.build()))
				.build()
				.isSucceeded());

		Assertions.assertTrue(chainResult.toBuilder()
				.succeeded(true)
				.assertionResults(List.of(AssertionResult.builder()
						.succeeded(true)
						.build(),
						AssertionResult.builder()
								.succeeded(true)
								.build()))
				.build()
				.isSucceeded());

		Assertions.assertTrue(chainResult.toBuilder()
				.succeeded(true)
				.assertionResults(null)
				.build()
				.isSucceeded());
	}

	@Test
	public void testGetChildResults() {
		var result = CallChainResult.builder()
				.build();

		Assertions.assertNotNull(result.getChildResults());
		Assertions.assertTrue(result.getChildResults()
				.isEmpty());

		result = CallChainResult.builder()
				.callResults(null)
				.build();

		Assertions.assertNotNull(result.getChildResults());
		Assertions.assertTrue(result.getChildResults()
				.isEmpty());

		result = CallChainResult.builder()
				.callResults(Map.of("abc", CallResult.builder()
						.build()))
				.build();

		Assertions.assertEquals(Collections.unmodifiableMap(new HashMap<String, CallResult>())
				.getClass(),
				result.getChildResults()
						.getClass());
		Assertions.assertEquals(1, result.getChildResults()
				.size());
	}

	@Test
	public void testIsSuccessful() {
		var chainResult = CallChainResult.builder()
				.succeeded(false)
				.callResults(Map.of("a", CallResult.builder()
						.assertionResults(List.of(AssertionResult.builder()
								.succeeded(false)
								.build()))
						.build()))
				.build();

		Assertions.assertFalse(chainResult::isSuccessful);
		Assertions.assertFalse(chainResult.toBuilder()
				.callResults(Map.of("a", CallResult.builder()
						.assertionResults(List.of(AssertionResult.builder()
								.succeeded(false)
								.build()))
						.build()))
				.build()
				.isSuccessful());

		Assertions.assertTrue(chainResult.toBuilder()
				.succeeded(true)
				.callResults(Map.of("a", CallResult.builder()
						.assertionResults(List.of(AssertionResult.builder()
								.succeeded(true)
								.build()))
						.build()))
				.build()
				.isSucceeded());

		Assertions.assertTrue(chainResult.toBuilder()
				.succeeded(true)
				.callResults(null)
				.build()
				.isSucceeded());

		Assertions.assertTrue(chainResult.toBuilder()
				.succeeded(true)
				.callResults(Map.of())
				.build()
				.isSucceeded());
	}
}
