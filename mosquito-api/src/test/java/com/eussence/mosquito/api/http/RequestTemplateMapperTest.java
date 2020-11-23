package com.eussence.mosquito.api.http;

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
	}

	@Test
	void testToRequest() {
		var template = RequestTemplate.builder()
				.get()
				.from("abcdef")
				.build();

		var request = template.toRequest();
		Assertions.assertEquals("efghijk", request.getUri());
	}
}
