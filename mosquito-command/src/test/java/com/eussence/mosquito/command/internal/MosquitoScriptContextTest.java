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

package com.eussence.mosquito.command.internal;

import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.eussence.mosquito.api.data.Environment;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.RequestTemplate;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.api.utils.JsonMapper;

import groovy.lang.Closure;
import groovy.lang.MissingPropertyException;

/**
 * 
 * @author Ernest Kiwele
 */
public class MosquitoScriptContextTest {

	private MosquitoScriptContext context = new MosquitoScriptContext();
	private Response response = Response.builder()
			.status(200)
			.statusReason("OK")
			.build();

	private Object map = Map.of("a", "B", "c", 4);

	private Function<Request, Response> scheduler = r -> response;

	@BeforeEach
	public void init() {
		MosquitoScriptContext.setScheduler(scheduler);
		System.setProperty("a.b.c.d", "A.B.C.D");
	}

	@Test
	void testBasics() {
		Assertions.assertEquals(map.toString(), context.echo(this.map));
		Assertions.assertEquals(JsonMapper.json(this.map), context.json(this.map));
		Assertions.assertEquals(this.map, context.fromJson(JsonMapper.json(this.map)));
		Assertions.assertNotNull(this.context.now());
		Assertions.assertTrue(2 > LocalDate.now()
				.toEpochDay()
				- this.context.today()
						.toEpochDay());
	}

	@Test
	void testRequestConversion() {
		Assertions.assertEquals("https://localhost/", this.context.request(Map.of("uri", "https://localhost/"))
				.getUri());
	}

	@Test
	void testSchedule() {
		Assertions.assertEquals(this.response, this.context.http(Request.builder()
				.build()));
	}

	@Test
	void testClosureCalls() {
		Closure<Object> closure = Mockito.mock(Closure.class);

		var responses = Stream
				.of(this.context.get(closure), this.context.post(closure), this.context.put(closure),
						this.context.patch(closure))
				.collect(Collectors.toList());
		responses.forEach(res -> Assertions.assertEquals(this.response, res));
		Mockito.verify(closure, Mockito.times(responses.size()))
				.call();
		Mockito.verify(closure, Mockito.times(responses.size()))
				.setResolveStrategy(Closure.DELEGATE_FIRST);
		Mockito.verify(closure, Mockito.times(responses.size()))
				.setDelegate(Mockito.isA(Request.RequestBuilder.class));
	}

	@Test
	void testClosureRequestCreation() {
		Closure<Request.RequestBuilder> closure = Mockito.mock(Closure.class);

		var responses = Stream
				.of(this.context.createpost(closure), this.context.createget(closure), this.context.createput(closure),
						this.context.createpatch(closure), this.context.createdelete(closure),
						this.context.createhead(closure), this.context.createoptions(closure),
						this.context.createoptions(closure), this.context.createtrace(closure),
						this.context.createconnect(closure), this.context.create("GET", closure))
				.collect(Collectors.toList());

		responses.forEach(res -> Assertions.assertTrue(res instanceof Request));
		Mockito.verify(closure, Mockito.times(responses.size()))
				.call();
		Mockito.verify(closure, Mockito.times(responses.size()))
				.setResolveStrategy(Closure.DELEGATE_FIRST);
		Mockito.verify(closure, Mockito.times(responses.size()))
				.setDelegate(Mockito.isA(Request.RequestBuilder.class));
	}

	@Test
	void testDirectCalls() {
		var responses = Stream
				.of(this.context.get("http://localhost/"), this.context.post("http://localhost/", new Object()),
						this.context.post("http://localhost/", Map.of("a", "B"), Map.of("head1", "val1")))
				.collect(Collectors.toList());

		responses.forEach(res -> Assertions.assertEquals(this.response, res));
	}

	@Test
	void testObjectCreations() {
		Assertions.assertEquals("something", this.context.assertion(UUID.randomUUID()
				.toString(), "something", "1 == 2 - 1", "You failed")
				.getDescription());

		var environment = Environment.builder()
				.key("key")
				.name("name")
				.production(false)
				.vars(Map.of("a", "b"))
				.build();
		Assertions.assertEquals(JsonMapper.json(environment),
				JsonMapper.json(this.context.env("key", "name", false, Map.of("a", "b"))));

		Assertions.assertEquals(JsonMapper.json(environment.toBuilder()
				.production(true)
				.name("key")
				.build()), JsonMapper.json(this.context.env("key", Map.of("a", "b"))));
	}

	@Test
	void testTemplate() {
		Closure<Object> closure = Mockito.mock(Closure.class);

		this.context.template(closure);

		Mockito.verify(closure, Mockito.times(1))
				.call();
		Mockito.verify(closure, Mockito.times(1))
				.setResolveStrategy(Closure.DELEGATE_FIRST);
		Mockito.verify(closure, Mockito.times(1))
				.setDelegate(Mockito.isA(RequestTemplate.RequestTemplateBuilder.class));
	}

	@Test
	void testSystemTools() {

		this.context.systemVars()
				.entrySet()
				.forEach(entry -> Assertions.assertEquals(entry.getValue(), System.getenv(entry.getKey())));
		System.getenv()
				.forEach((key, value) -> Assertions.assertEquals(value, this.context.systemVar((String) key)));
		Assert.assertEquals("A.B.C.D", this.context.property("a.b.c.d"));
		Assert.assertTrue(this.context.properties()
				.containsKey("a.b.c.d"));
	}

	@Test
	void testPropertyMissing() {
		Assertions.assertThrows(MissingPropertyException.class, () -> this.context.propertyMissing("something"));
	}

	@Test
	void testFileIO() throws Exception {
		var f = Files.createTempFile(UUID.randomUUID()
				.toString(), ".json")
				.toFile();
		var map = Map.of("key", "A");
		this.context.saveJson(map, f.getAbsolutePath());

		Assertions.assertEquals(map, this.context.loadObject(f.getAbsolutePath(), Map.class));
		Assertions.assertEquals("A", this.context.loadEnvironment(f.getAbsolutePath())
				.getKey());
	}
}
