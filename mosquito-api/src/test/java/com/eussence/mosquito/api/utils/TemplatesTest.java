package com.eussence.mosquito.api.utils;

import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import lombok.Data;

public class TemplatesTest {

	@Data
	public static class A {
		private String a;

		public String toString() {
			return "a = " + a;
		}
	}

	@Test
	void testConvert() {
		Assert.assertEquals("A", Templates.safeConvert(Map.of("a", "A"), A.class).a);
	}

	@Test
	void testQuotes() {
		Assert.assertEquals("'A'", Templates.singleQuote("A"));
		Assert.assertNull(null, Templates.singleQuote(null));
		Assert.assertEquals("\"\"\"A\"\"\"", Templates.multilineQuote("A"));
		Assert.assertNull(null, Templates.multilineQuote(null));
	}

	@Test
	void testCast() {
		Assert.assertEquals("a", Templates.castString("a"));

		var a = new A();
		a.a = "B";
		Assert.assertEquals("a = B", Templates.castString(a));
		Assert.assertEquals(null, Templates.castString(null));
	}
}
