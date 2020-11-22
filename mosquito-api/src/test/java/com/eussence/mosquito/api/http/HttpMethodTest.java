package com.eussence.mosquito.api.http;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class HttpMethodTest {

	@Test
	void testLookup() {
		Assert.assertFalse(HttpMethod.of("get")
				.isEmpty());
		Assert.assertTrue(HttpMethod.of("nonexistent")
				.isEmpty());
	}
}
