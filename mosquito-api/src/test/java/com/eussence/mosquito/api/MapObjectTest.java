package com.eussence.mosquito.api;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class MapObjectTest {

	@Test
	void testContent() {
		// Not too sure this _map thing shouldn't be deleted
		Assert.assertTrue(MapObject.instance()
				.toMap()
				.contains("_map"));

		Assert.assertTrue(MapObject.instance()
				.add("a", "A")
				.contains("a"));
		Assert.assertEquals("B", MapObject.instance()
				.set("b", "B")
				.getString("b"));
		Assert.assertTrue(MapObject.instance()
				.add("c", "C")
				.drop("c")
				.isEmpty());
	}
}
