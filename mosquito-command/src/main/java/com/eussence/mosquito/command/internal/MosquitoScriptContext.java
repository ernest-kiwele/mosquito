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
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;

import com.eussence.mosquito.api.Call;
import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.data.Environment;
import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.api.execution.ExecutionResult;
import com.eussence.mosquito.api.execution.ExecutionSchedule;
import com.eussence.mosquito.api.http.Body;
import com.eussence.mosquito.api.http.HttpMethod;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.api.qa.Assertion;
import com.eussence.mosquito.api.utils.Templates;
import com.eussence.mosquito.reports.template.BasicReportTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;

/**
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

	protected String echo(Object v) {
		return String.valueOf(v);
	}

	protected String json(Object o) {
		try {
			return objectMapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			throw new MosquitoException("Failed to jsonify: " + e.getMessage());
		}
	}

	protected Object fromJson(String o) {
		try {
			return objectMapper.readValue(o, Object.class);
		} catch (Exception e) {
			throw new MosquitoException("Failed to parse JSON: " + e.getMessage());
		}
	}

	protected Call call(Map<String, Object> params) {
		return Templates.safeConvert(params, Call.class);
	}

	protected Request request(Map<String, Object> params) {
		return Templates.safeConvert(params, Request.class);
	}

	protected Response http(Request input) {

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
				.vars(MapObject.instance()
						._map(vars))
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
		System.out.print("Property missing >> " + name);
		throw new MissingPropertyException("I don't know of anything called '" + name + "'");
	}

	protected void report(ExecutionResult result, String htmlPath) {
		try {
			IOUtils.write(BasicReportTemplate.getInstance()
					.getDefaultResultReport(result)
					.getBytes(), new FileOutputStream(htmlPath));
		} catch (Exception ex) {
			throw new MosquitoException("Failed to generate or save report: " + ex.getMessage(), ex);
		}
	}

	protected String dateFormat(Date d) {
		if (null == d) {
			return "";
		}

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(d);
	}

	protected String textFile(String path) {
		try {
			return IOUtils.toString(new File(path).toURI(), Charset.defaultCharset());
		} catch (IOException ioe) {
			throw new MosquitoException("Could not load file from " + path + ": " + ioe.getMessage(), ioe);
		}
	}

	protected Object jsonFile(String path) {
		return this.fromJson(this.textFile(path));
	}

	protected Object loadObject(String path, Class<?> cls) {
		try {
			return objectMapper.readValue(this.textFile(path), cls);
		} catch (Exception e) {
			throw new MosquitoException(e);
		}
	}

	protected Environment loadEnvironment(String path) {
		return (Environment) this.loadObject(path, Environment.class);
	}

	protected CallChain loadChain(String path) {
		return (CallChain) this.loadObject(path, CallChain.class);
	}

	protected ExecutionSchedule loadSchedule(String path) {
		return (ExecutionSchedule) this.loadObject(path, ExecutionSchedule.class);
	}

	protected void saveJson(Object object, String path) {
		try {
			objectMapper.writeValue(new FileOutputStream(path), object);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MosquitoException(e);
		}
	}

	// Date functions
	protected Instant now() {
		return Instant.now();
	}

	protected LocalDate today() {
		return LocalDate.now();
	}
}
