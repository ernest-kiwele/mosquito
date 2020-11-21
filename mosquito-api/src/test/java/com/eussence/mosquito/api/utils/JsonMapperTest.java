package com.eussence.mosquito.api.utils;

import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;

public class JsonMapperTest {

	@Test
	void testToJson() {
		Assert.assertEquals("{\"a\":\"A\"}", JsonMapper.json(Map.of("a", "A")));
		Assert.assertArrayEquals("{\"a\":\"A\"}".getBytes(), JsonMapper.jsonBytes(Map.of("a", "A")));
		Assert.assertEquals(Map.of("a", "A"), JsonMapper.fromJson(JsonMapper.jsonPretty(Map.of("a", "A")), Map.class));
	}

	@Test
	void testFromJson() {
		Assert.assertEquals(Map.of("a", "A"), JsonMapper.fromJson("{\"a\":\"A\"}", Map.class));
		Assert.assertEquals(Map.of("a", "A"),
				JsonMapper.fromJson("{\"a\":\"A\"}", new TypeReference<Map<String, Object>>() {
				}));
		Assert.assertEquals(Map.of("a", "A"), JsonMapper.fromJson("{\"a\":\"A\"}".getBytes(), Map.class));
	}
}