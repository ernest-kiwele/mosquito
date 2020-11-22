package com.eussence.mosquito.api.http;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import groovy.lang.Closure;

public class RequestTest {

	@Test
	void testUri() throws Exception {
		var uri = Request.builder()
				.uri("http://localhost/")
				.param("a", "aaa")
				.build();

		Assertions.assertEquals("http://localhost/?a=aaa", uri.uri()
				.toURL()
				.toExternalForm());
		Assertions.assertNull(Request.builder()
				.uri(null)
				.build()
				.uri());
	}

	@Test
	void testApplyParameters() {
		var ai = new AtomicInteger();
		Request.builder()
				.uri("http://localhost/")
				.param("a", "aaa")
				.param("b", "bbb")
				.param("c", "ccc")
				.build()
				.applyParameters((a, b) -> ai.incrementAndGet());

		Assertions.assertEquals(3, ai.get());
	}

	@Test
	void testApplyHeaders() {
		var ai = new AtomicInteger();
		Request.builder()
				.uri("http://localhost/")
				.header("a", "aaa")
				.header("b", "bbb")
				.header("c", "ccc")
				.build()
				.applyHeaders((a, b) -> ai.incrementAndGet());

		Assertions.assertEquals(3, ai.get());
	}

	@Test
	void testCall() {
		var response = Response.builder()
				.build();
		Request.requestHandler = r -> response;

		Assertions.assertEquals(response, Request.builder()
				.build()
				.call());

		Closure<Object> c = Mockito.mock(Closure.class);
		Assertions.assertEquals(response, Request.builder()
				.build()
				.call(c));

		Mockito.verify(c)
				.call();
		Mockito.verify(c)
				.setDelegate(Mockito.isA(Request.RequestBuilder.class));
		Mockito.verify(c)
				.setResolveStrategy(Closure.DELEGATE_FIRST);
	}

	@Test
	void testBuilder() {
		var request = Request.builder();
		Assertions.assertEquals(request, request.uri("http://localhost/"));
		Assertions.assertEquals(request, request.to("http://localhost/to"));
		Assertions.assertEquals(request, request.from("http://localhost/from"));

		Assertions.assertEquals(request, request.header("head", "header-value"));
		Assertions.assertEquals(request, request.header(Map.of("head1", "header-value1")));
		Assertions.assertEquals(request, request.header(null));

		Assertions.assertEquals(request, request.parameter("head", "header-value"));
		Assertions.assertEquals(request, request.params(Map.of("head1", "header-value1")));
		Assertions.assertEquals(request, request.parameter(Map.of("head1", "header-value1")));
		Assertions.assertEquals(request, request.params(null));

		Assertions.assertEquals(request, request.entity(Map.of()));
		Assertions.assertEquals(request, request.mediaType(MediaType.TEXT_PLAIN));

		var r = request.build();
		Assertions.assertEquals("http://localhost/from", r.getUri());
		Assertions.assertEquals("header-value", r.getHeaders()
				.get("head"));
		Assertions.assertEquals("header-value1", r.getParameters()
				.get("head1"));
		Assertions.assertNotNull(r.getBody());
		Assertions.assertEquals(MediaType.TEXT_PLAIN, r.getBody()
				.getMediaType());

		Assertions.assertEquals(MediaType.APPLICATION_JSON, Request.builder()
				.json(Map.of("a", "A"))
				.build()
				.getBody()
				.getMediaType());
		Assertions.assertEquals(Map.of("a", "A"), Request.builder()
				.json(Map.of("a", "A"))
				.build()
				.getBody()
				.getEntity());

		Assertions.assertEquals(MediaType.APPLICATION_JSON, Request.builder()
				.body(null)
				.json(null)
				.build()
				.getBody()
				.getMediaType());
		Assertions.assertNull(Request.builder()
				.body(null)
				.json((Object) null)
				.build()
				.getBody()
				.getEntity());
	}
}
