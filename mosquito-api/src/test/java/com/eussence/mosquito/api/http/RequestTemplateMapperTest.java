package com.eussence.mosquito.api.http;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.command.Resolver;

public class RequestTemplateMapperTest {

	@Mock
	private Resolver resolver;
	private final MapObject binding = MapObject.instance();

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		RequestTemplateMapper.instance()
				.setup(l -> this.resolver, () -> binding);
		Mockito.when(this.resolver.eval(Mockito.eq(binding), Mockito.anyString()))
				.thenReturn("efghijk");
		Mockito.when(this.resolver.eval(Mockito.eq(binding), Mockito.matches(".*\\$.*")))
				.thenReturn("parsed");
		Mockito.when(this.resolver.eval(Mockito.eq(binding), Mockito.eq("[:]")))
				.thenReturn(Map.of());
	}

	@Test
	void testToRequest() throws IOException {
		var template = RequestTemplate.builder()
				.get()
				.from("abcdef")
				.build();

		var request = template.toRequest();
		Assertions.assertEquals("efghijk", request.getUri());

		request = template.toBuilder()
				.uri("$efghijk")
				.header("key1", "value1")
				.header("key2", "$value2")
				.param("param1", "value1")
				.param("param2", "$value2")
				.build()
				.toRequest();
		Assertions.assertEquals("parsed", request.getUri());
		Assertions.assertEquals("value1", request.getHeaders()
				.get("key1"));
		Assertions.assertEquals("parsed", request.getHeaders()
				.get("key2"));
		Assertions.assertEquals("value1", request.getParameters()
				.get("param1"));
		Assertions.assertEquals("parsed", request.getParameters()
				.get("param2"));

		// bodied requests
		request = template.toBuilder()
				.post()
				.mediaType(MediaType.APPLICATION_JSON)
				.entityTemplate("[:]")
				.build()
				.toRequest();
		Assertions.assertNotNull(request.getBody()
				.getEntity());
		Assertions.assertTrue(request.getBody()
				.getEntity() instanceof Map);

		// test template with multipart body
		request = template.toBuilder()
				.post()
				.multipart(true)
				.partFiles(List.of(Files.createTempFile(UUID.randomUUID()
						.toString(), ".json")
						.toFile()
						.getAbsolutePath()))
				.mediaType(MediaType.APPLICATION_JSON)
				.entityTemplate("[:]")
				.build()
				.toRequest();
		Assertions.assertTrue(request.getBody()
				.getParts()
				.stream()
				.anyMatch(f -> MediaType.APPLICATION_JSON.equals(f.getMediaType())));
	}
}
