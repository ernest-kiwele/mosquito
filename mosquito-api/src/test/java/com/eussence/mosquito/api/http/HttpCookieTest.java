package com.eussence.mosquito.api.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HttpCookieTest {

	@Test
	void testFromHeaders() {
		var cookie = HttpCookie.forHeader(
				"prov=17963160-40be-432a-b3f2-eecd7ebdd4f3; expires=Fri, 01 Jan 2055 00:00:00 GMT; domain=company.com; path=/; secure; samesite=none; httponly");

		Assertions.assertTrue(cookie.isHttpOnly());
		Assertions.assertEquals("prov", cookie.getName());
		Assertions.assertEquals("17963160-40be-432a-b3f2-eecd7ebdd4f3", cookie.getValue());
		Assertions.assertEquals("/", cookie.getPath());
		Assertions.assertEquals("company.com", cookie.getDomain());
		Assertions.assertEquals(2, cookie.getAttributes()
				.size());
	}
}
