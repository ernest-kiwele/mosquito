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

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;

import com.eussence.mosquito.api.data.Environment;
import com.eussence.mosquito.api.exception.CheckedExecutable;
import com.eussence.mosquito.api.exception.CheckedRunnable;
import com.eussence.mosquito.api.http.Body;
import com.eussence.mosquito.api.http.HttpMethod;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.RequestTemplate;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.api.qa.Assertion;
import com.eussence.mosquito.api.utils.JsonMapper;
import com.eussence.mosquito.api.utils.Templates;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;

/**
 * Base class for Groovy (and perhaps other) scripts interpreted by mosquito-cli
 * or other interfaces.
 * 
 * @author Ernest Kiwele
 */
public class MosquitoScriptContext extends Script {

	private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
	private static Function<Request, Response> httpScheduler;

	@Override
	public Object run() {
		return "Ran!";
	}

	public static void setScheduler(Function<Request, Response> sched) {
		httpScheduler = sched;
	}

	public static Function<Request, Response> getScheduler() {
		return httpScheduler;
	}

	protected String echo(Object v) {
		return String.valueOf(v);
	}

	protected String json(Object o) {
		return JsonMapper.json(o);
	}

	protected Object fromJson(String o) {
		return JsonMapper.fromJson(o, Object.class);
	}

	protected Request request(Map<String, Object> params) {
		return Templates.safeConvert(params, Request.class);
	}

	protected Response http(Request input) {
		return schedule(input);
	}

	public static Response schedule(Request input) {
		var resp = httpScheduler.apply(input);

		resp.setRequest(input);

		return resp;
	}

	private Response requestDelegate(Closure<Object> closure, HttpMethod method) {
		var builder = Request.builder()
				.method(method);

		closure.setResolveStrategy(Closure.DELEGATE_FIRST);
		closure.setDelegate(builder);
		closure.call();

		return this.http(builder.build());
	}

	public Response get(Closure<Object> closure) {
		return this.requestDelegate(closure, HttpMethod.GET);
	}

	public Response post(Closure<Object> closure) {
		return this.requestDelegate(closure, HttpMethod.POST);
	}

	public Response put(Closure<Object> closure) {
		return this.requestDelegate(closure, HttpMethod.PUT);
	}

	public Response patch(Closure<Object> closure) {
		return this.requestDelegate(closure, HttpMethod.PATCH);
	}

	public Request createpost(Closure<Request.RequestBuilder> closure) {
		return this.create(HttpMethod.POST, closure);
	}

	public Request createget(Closure<Request.RequestBuilder> closure) {
		return this.create(HttpMethod.GET, closure);
	}

	public Request createput(Closure<Request.RequestBuilder> closure) {
		return this.create(HttpMethod.PUT, closure);
	}

	public Request createpatch(Closure<Request.RequestBuilder> closure) {
		return this.create(HttpMethod.PATCH, closure);
	}

	public Request createdelete(Closure<Request.RequestBuilder> closure) {
		return this.create(HttpMethod.PATCH, closure);
	}

	public Request createhead(Closure<Request.RequestBuilder> closure) {
		return this.create(HttpMethod.HEAD, closure);
	}

	public Request createoptions(Closure<Request.RequestBuilder> closure) {
		return this.create(HttpMethod.OPTIONS, closure);
	}

	public Request createconnect(Closure<Request.RequestBuilder> closure) {
		return this.create(HttpMethod.CONNECT, closure);
	}

	public Request createtrace(Closure<Request.RequestBuilder> closure) {
		return this.create(HttpMethod.TRACE, closure);
	}

	public Request create(String method, Closure<Request.RequestBuilder> closure) {
		return this.create(HttpMethod.of(method)
				.orElse(HttpMethod.GET), closure);
	}

	public Request create(HttpMethod method, Closure<Request.RequestBuilder> closure) {
		var builder = Request.builder()
				.method(method);

		closure.setResolveStrategy(Closure.DELEGATE_FIRST);
		closure.setDelegate(builder);
		closure.call();

		return builder.build();
	}

	public Response get(GString uri) {
		return this.get(uri.toString());
	}

	public Response get(String uri) {
		return this.http(Request.builder()
				.uri(uri)
				.method(HttpMethod.GET)
				.build());
	}

	protected Response post(String uri, Object entity) {
		return this.post(uri, entity, Map.of());
	}

	protected Response post(String uri, Object entity, Map<String, String> headers) {
		return this.http(Request.builder()
				.uri(uri)
				.method(HttpMethod.POST)
				.headers(headers)
				.body(Body.builder()
						.entity(entity)
						.mediaType(MediaType.APPLICATION_JSON)
						.build())
				.build());
	}

	protected Assertion assertion(String id, String description, String booleanExpression, String messageTemplate) {
		return new Assertion(id, description, booleanExpression, messageTemplate);
	}

	protected Environment environment(String key, String name, boolean production, Map<String, Object> vars) {
		return Environment.builder()
				.key(key)
				.name(name)
				.production(production)
				.vars(vars)
				.build();
	}

	protected Environment env(String key, String name, boolean production, Map<String, Object> vars) {
		return this.environment(key, name, production, vars);
	}

	protected Environment environment(String key, Map<String, Object> vars) {
		return this.environment(key, key, true, vars);
	}

	protected Environment env(String key, Map<String, Object> vars) {
		return environment(key, vars);
	}

	protected RequestTemplate template(Closure<Object> filler) {
		var builder = RequestTemplate.builder();

		filler.setDelegate(builder);
		filler.setResolveStrategy(Closure.DELEGATE_FIRST);
		filler.call();

		return builder.build();
	}

	protected Map<String, Object> systemVars() {
		return System.getenv()
				.entrySet()
				.stream()
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	protected String systemVar(String var) {
		return System.getenv(var);
	}

	protected String property(String name) {
		return System.getProperty(name);
	}

	protected Properties properties() {
		return System.getProperties();
	}

	public Object propertyMissing(String name) {
		throw new MissingPropertyException("I don't know of anything called '" + name + "'");
	}

	protected String textFile(String path) {
		return CheckedExecutable.wrap(() -> IOUtils.toString(new File(path).toURI(), Charset.defaultCharset()));
	}

	protected Object loadObject(String path, Class<?> cls) {
		return CheckedExecutable.wrap(() -> objectMapper.readValue(this.textFile(path), cls));
	}

	protected Environment loadEnvironment(String path) {
		return (Environment) this.loadObject(path, Environment.class);
	}

	protected void saveJson(Object object, String path) {
		CheckedRunnable.wrap(() -> objectMapper.writeValue(new FileOutputStream(path), object))
				.run();
	}

	// Date functions
	protected Instant now() {
		return Instant.now();
	}

	protected LocalDate today() {
		return LocalDate.now();
	}
}
