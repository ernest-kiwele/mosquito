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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;

import com.eussence.mosquito.api.Call;
import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.CallChainResult;
import com.eussence.mosquito.api.ExpressionLanguage;
import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.data.Dataset;
import com.eussence.mosquito.api.data.Environment;
import com.eussence.mosquito.api.data.Vars;
import com.eussence.mosquito.api.execution.ExecutionResult;
import com.eussence.mosquito.api.execution.ExecutionSchedule;
import com.eussence.mosquito.api.http.ClientBinding;
import com.eussence.mosquito.api.http.HttpMethod;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.api.qa.Assertion;
import com.eussence.mosquito.api.utils.Templates;
import com.eussence.mosquito.http.api.DefaultClient;
import com.eussence.mosquito.reports.template.BasicReportTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import groovy.lang.MissingPropertyException;
import groovy.lang.Script;

/**
 * 
 * @author Ernest Kiwele
 */
public class MosquitoScriptContext extends Script {

	private static ClientBinding<?, ?, ?> client = DefaultClient.builder()
			.build();

	private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	@Override
	public Object run() {
		return "Ran!";
	}

	public String echo(Object v) {
		return String.valueOf(v);
	}

	public String json(Object o) {
		try {
			return objectMapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to jsonify: " + e.getMessage());
		}
	}

	public Object fromJson(String o) {
		try {
			return objectMapper.readValue(o, Object.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse JSON: " + e.getMessage());
		}
	}

	public Call call(String key, String uri) {
		Call c = new Call().key(key);
		c.getRequestTemplate()
				.set("uri", uri);

		return c;
	}

	public Call call(Map<String, Object> params) {
		return Templates.safeConvert(params, Call.class);
	}

	public Request request(Map<String, Object> params) {
		return Templates.safeConvert(params, Request.class);
	}

	public Response http(Map<String, Object> input) {
		return client.send(request(input));
	}

	public Response get(Object uri) {
		return this.http(MapObject.instance()
				.add("uri", uri)
				.add("method", HttpMethod.GET)
				._map());
	}

	public Map<String, List<String>> head(Object uri) {
		return this.http(MapObject.instance()
				.add("uri", uri)
				.add("method", HttpMethod.HEAD)
				._map())
				.getHeaders();
	}

	public Response post(String uri, Object entity) {
		return this.post(uri, entity, Map.of());
	}

	public Response post(String uri, Object entity, Map<String, Object> headers) {
		return this.http(MapObject.instance()
				.add("uri", uri)
				.add("method", HttpMethod.POST)
				.add("headers", headers)
				.add("body", MapObject.instance()
						.add("entity", entity)
						.add("mediaType", MediaType.APPLICATION_JSON)
						._map())
				._map());
	}

	public CallChain chain(String key, String name, String description, String comments, Map<String, Call> calls,
			List<Assertion> assertions, ExpressionLanguage expressionLanguage, boolean scriptMode, String script,
			String createdBy, Date dateCreated) {
		return new CallChain(key, name, description, comments, calls, assertions, expressionLanguage, scriptMode,
				script, createdBy, dateCreated);
	}

	// public CallChain chain(Map<String, Object> params) {
	// new CallChain(params);
	// }
	//
	// public Assertion assertion(Map<String, Object> params) {
	// new Assertion(params);
	// }

	public Assertion assertion(String id, String description, String booleanExpression, String messageTemplate) {
		return new Assertion(id, description, booleanExpression, messageTemplate);
	}

	public ExecutionSchedule schedule(String id, boolean assertionsRun, boolean metricsCollected,
			Map<String, CallChain> callChains, Environment environment, Map<String, Dataset> datasets,
			Map<String, Vars> vars, Map<String, CallChainResult> callChainResults) {

		return new ExecutionSchedule(id, callChains.values(), environment, datasets, vars);
	}

	public Environment environment(String key, String name, boolean production, Map<String, Object> vars) {
		return Environment.builder()
				.key(key)
				.name(name)
				.production(production)
				.vars(MapObject.instance()
						._map(vars))
				.build();
	}

	public Environment env(String key, String name, boolean production, Map<String, Object> vars) {
		return this.env(key, name, production, vars);
	}

	public Environment environment(String key, Map<String, Object> vars) {
		return this.environment(key, key, true, vars);
	}

	public Environment env(String key, Map<String, Object> vars) {
		return environment(key, vars);
	}

	public Map<String, Object> systemVars() {
		return System.getenv()
				.entrySet()
				.stream()
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	public String systemVar(String var) {
		return System.getenv(var);
	}

	public String property(String name) {
		return System.getProperty(name);
	}

	public Properties properties() {
		return System.getProperties();
	}

	public Object propertyMissing(String name) {
		throw new MissingPropertyException("I don't know of anything called '" + name + "'");
	}

	public void report(ExecutionResult result, String htmlPath) {
		try {
			IOUtils.write(BasicReportTemplate.getInstance()
					.getDefaultResultReport(result), new FileOutputStream(htmlPath));
			System.out.println("[Report exported to $htmlPath]");
		} catch (Exception ex) {
			System.err.println("Failed to generate or save report: " + ex.getMessage());
			ex.printStackTrace();
			throw new RuntimeException("Failed to generate or save report: " + ex.getMessage(), ex);
		}
	}

	public String dateFormat(Date d) {
		if (null == d) {
			return "";
		}

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(d);
	}

	public String textFile(String path) {
		try {
			return IOUtils.toString(new FileInputStream(path));
		} catch (IOException ioe) {
			throw new RuntimeException("Could not load file from $path: ${ioe.message}", ioe);
		}
	}

	public Object jsonFile(String path) {
		return this.fromJson(this.textFile(path));
	}

	public Object loadObject(String path, Class<?> cls) {
		try {
			return objectMapper.readValue(this.textFile(path), cls);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public Environment loadEnvironment(String path) {
		return (Environment) this.loadObject(path, Environment.class);
	}

	public CallChain loadChain(String path) {
		return (CallChain) this.loadObject(path, CallChain.class);
	}

	public ExecutionSchedule loadSchedule(String path) {
		return (ExecutionSchedule) this.loadObject(path, ExecutionSchedule.class);
	}

	public void saveJson(Object object, String path) {
		try {
			objectMapper.writeValue(new FileOutputStream(path), object);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
