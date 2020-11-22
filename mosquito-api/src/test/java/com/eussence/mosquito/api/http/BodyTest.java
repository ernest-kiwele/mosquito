package com.eussence.mosquito.api.http;

import java.nio.charset.Charset;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.api.utils.JsonMapper;

public class BodyTest {

	@Test
	void testTextEntity() {
		Assert.assertEquals("", Body.builder()
				.entity(null)
				.build()
				.textEntity());

		Assert.assertEquals("abc", Body.builder()
				.entity("abc")
				.build()
				.textEntity());

		Assert.assertFalse(Body.builder()
				.build()
				.isString());
		Assert.assertTrue(Body.builder()
				.entity("abc")
				.build()
				.isString());
		Assert.assertFalse(Body.builder()
				.entity(Map.of())
				.build()
				.isString());
	}

	@Test
	void testJsonEntity() {
		Assert.assertEquals(Map.of("a", "A"), Body.builder()
				.entity("{\"a\":\"A\"}")
				.build()
				.jsonEntity());
	}

	@Test
	void testBytes() {
		// null entity returns an empty byte array.
		Assert.assertEquals(0, Body.builder()
				.entity(null)
				.build()
				.bytes().length);

		// a byte[] entity should be returned as is.
		byte[] bytes = { 111, 123, 124 };
		Assert.assertEquals(bytes, Body.builder()
				.entity(bytes)
				.build()
				.bytes());

		// with no charset, string.getBytes() should be returned
		var string = "abc123";
		Assert.assertArrayEquals(string.getBytes(), Body.builder()
				.entity(string)
				.build()
				.bytes());

		// with a charset, string.getBytes() should be returned
		var string1 = "abc123";
		Assert.assertArrayEquals(string.getBytes(Charset.defaultCharset()), Body.builder()
				.charSet(Charset.defaultCharset()
						.displayName())
				.entity(string)
				.build()
				.bytes());

		Assertions.assertThrows(MosquitoException.class, () -> Body.builder()
				.charSet("rubbish")
				.entity(string)
				.build()
				.bytes());

		Assertions.assertEquals(9, Body.builder()
				.entity(Map.of("a", "A"))
				.mediaType(MediaType.APPLICATION_JSON)
				.build()
				.bytes().length);

		Assertions.assertThrows(MosquitoException.class, () -> Body.builder()
				.entity(Map.of("a", "A"))
				.mediaType(MediaType.APPLICATION_XML)
				.build()
				.bytes());
	}

	@Test
	void testString() {
		Assert.assertEquals("", Body.builder()
				.entity(null)
				.build()
				.string());

		byte[] bytes = "abcdef".getBytes();
		Assert.assertEquals(new String(bytes), Body.builder()
				.entity(new String(bytes))
				.build()
				.string());

		var string = "abc123";
		Assert.assertEquals(string, Body.builder()
				.entity(string.getBytes())
				.build()
				.string());

		var string1 = "abc123";
		Assert.assertEquals(string1, Body.builder()
				.entity(string1.getBytes())
				.charSet(Charset.defaultCharset()
						.displayName())
				.build()
				.string());

		string1 = "abc123";
		Assertions.assertThrows(MosquitoException.class, () -> Body.builder()
				.charSet(Charset.defaultCharset()
						.displayName())
				.entity(string.getBytes())
				.charSet("rubbish")
				.build()
				.string());

		string1 = "abc123";
		Assertions.assertEquals(JsonMapper.json(Map.of("a", "A")), Body.builder()
				.mediaType(MediaType.APPLICATION_JSON)
				.entity(Map.of("a", "A"))
				.charSet("rubbish")
				.build()
				.string());

		string1 = "abc123";
		Assertions.assertThrows(MosquitoException.class, () -> Body.builder()
				.mediaType(MediaType.APPLICATION_ATOM_XML) // anything but json
				.entity(Map.of("a", "A"))
				.build()
				.string());
	}
}
