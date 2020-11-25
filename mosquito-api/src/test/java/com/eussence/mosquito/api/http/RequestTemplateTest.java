/**
 * Copyright 2018 eussence.com and contributors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eussence.mosquito.api.http;

import java.util.Map;
import java.util.function.Function;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.command.CommandLanguage;
import com.eussence.mosquito.api.command.Resolver;
import com.eussence.mosquito.api.http.RequestTemplate.RequestTemplateBuilder;

import groovy.lang.Closure;

public class RequestTemplateTest {

	private static Response response = Response.builder()
			.build();

	@BeforeAll
	public static void setup() {
		Request.requestHandler = r -> response;
	}

	@Test
	void testBuilder() {
		var builder = RequestTemplate.builder()
				.uri("http://localhost/")
				.from("http://localhost/from/")
				.to("http://localhost/to/")
				.header("header1", "Header1")
				.header(Map.of("header2", "Header2"))
				.param(Map.of("param1", "Param1"))
				.param("param2", "Param2")
				.params(Map.of("param1", "Param12"))
				.mediaType(MediaType.APPLICATION_XML)
				.json("[key:'value']")
				.get()
				.put()
				.delete()
				.patch()
				.head()
				.options()
				.trace()
				.connect()
				.method("options")
				.post();

		var template = builder.build();

		Assertions.assertEquals(HttpMethod.POST, template.getMethod());
		Assertions.assertEquals("http://localhost/to/", template.getUriTemplate());
		Assertions.assertEquals("Header1", template.getHeaderTemplates()
				.get("header1"));
		Assertions.assertEquals("Header2", template.getHeaderTemplates()
				.get("header2"));
		Assertions.assertEquals("Param2", template.getParameterTemplates()
				.get("param2"));
		Assertions.assertEquals("Param12", template.getParameterTemplates()
				.get("param1"));
		Assertions.assertEquals(MediaType.APPLICATION_JSON, template.getMediaType());
		Assertions.assertEquals(HttpMethod.POST, template.getMethod());
	}

	@Test
	void testCall() {
		Resolver resolver = Mockito.mock(Resolver.class);
		Function<CommandLanguage, Resolver> resolverFactory = lang -> resolver;
		var context = MapObject.instance();
		RequestTemplateMapper.instance()
				.setup(resolverFactory, () -> context);
		var request = RequestTemplate.builder()
				.lang(CommandLanguage.JAVASCRIPT)
				.from("$url")
				.build();
		Closure<Object> filler = Mockito.mock(Closure.class);

		request.call(filler);

		Mockito.verify(filler)
				.setDelegate(Mockito.isA(RequestTemplateBuilder.class));
		Mockito.verify(filler)
				.setResolveStrategy(Closure.DELEGATE_FIRST);
		Mockito.verify(filler)
				.call();
		Mockito.verify(resolver)
				.eval(context, "\"\"\"$url\"\"\"");
	}

	@Test
	void testRequestCall() {
		Resolver resolver = Mockito.mock(Resolver.class);
		Function<CommandLanguage, Resolver> resolverFactory = Mockito.mock(Function.class);
		Mockito.when(resolverFactory.apply(CommandLanguage.GROOVY))
				.thenReturn(resolver);
		RequestTemplateMapper.instance()
				.setup(resolverFactory, () -> MapObject.instance());
		var request = RequestTemplate.builder()
				.lang(CommandLanguage.GROOVY)
				.build();
		request.call();
		Mockito.verify(resolverFactory)
				.apply(CommandLanguage.GROOVY);
	}
}
