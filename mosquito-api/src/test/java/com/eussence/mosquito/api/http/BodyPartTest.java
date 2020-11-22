package com.eussence.mosquito.api.http;

import java.io.File;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BodyPartTest {

	@Test
	void testFile() throws Exception {
		File f = File.createTempFile(UUID.randomUUID()
				.toString(), ".json");
		BodyPart part = BodyPart.fromFile(f.getAbsolutePath());

		Assertions.assertEquals(MediaType.APPLICATION_JSON, part.getMediaType());
		Assertions.assertEquals(0, part.getSize());
		Assertions.assertEquals(f.getName(), part.getName());
		Assertions.assertTrue(part.isFromFile());

		Assertions.assertEquals(part, part.header("a", "aaa"));
		Assertions.assertEquals("aaa", part.getHeaders()
				.get("a"));
	}
}
