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

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.command.CommandLanguage;
import com.eussence.mosquito.api.command.Resolver;
import com.eussence.mosquito.api.data.Dataset;
import com.eussence.mosquito.api.data.Environment;
import com.eussence.mosquito.api.data.Vars;
import com.eussence.mosquito.api.exception.MosquitoException;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * A built-in resolver for Groovy scripts and templates.
 * 
 * @author Ernest Kiwele
 */
public class GroovyResolver implements Resolver {

	private static final GroovyResolver instance = new GroovyResolver();

	private GroovyResolver() {
		if (null != instance) {
			throw new IllegalStateException("GroovyResolver cannot be created multiple times");
		}
	}

	public static GroovyResolver getInstance() {
		return instance;
	}

	@Override
	public CommandLanguage getExpressionLanguage() {
		return CommandLanguage.GROOVY;
	}

	@Override
	public Object eval(String template, Environment environment, Map<String, Dataset> datasets, Map<String, Vars> vars,
			Map<String, CallChain> callChains, MapObject outerContext) {

		try {
			MapObject context = outerContext;

			context.add("env", environment.getVars())
					.add("data", datasets)
					.add("vars", vars)
					.add("chains", callChains);

			// overwriting...
			context.putAll(vars);
			context.putAll(datasets);
			context.putAll(environment.getVars());

			this.setOnContext(context);
			GroovyShell groovyShell = new GroovyShell(this.getCompilerConfiguration());
			Script script = groovyShell.parse(template);

			Binding binding = new Binding(context);
			script.setBinding(binding);

			return script.run();
		} catch (CompilationFailedException e) {
			throw new MosquitoException("Invalid template: " + e.getMessage(), e);
		}
	}

	@Override
	public Object eval(MapObject context, String template) {

		try {
			System.out.println("Evaluating string: " + template + " \nwith context: " + context);

			this.setOnContext(context);
			GroovyShell groovyShell = new GroovyShell(this.getCompilerConfiguration());
			Script script = groovyShell.parse(template);

			Binding binding = new Binding(context);
			script.setBinding(binding);

			return script.run();
		} catch (CompilationFailedException e) {
			throw new RuntimeException("Invalid template: " + e.getMessage(), e);
		}
	}

	@Override
	public Map<String, Object> eval(MapObject context, Map<String, String> templates) {
		this.setOnContext(context);
		return templates.entrySet()
				.stream()
				.map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), this.eval(context, entry.getValue())))
				.peek(System.out::println)
				.collect(Collectors.toMap(entry -> (String) entry.getKey(), AbstractMap.SimpleEntry::getValue));
	}

	@Override
	public Map<String, Object> exec(MapObject context, String scriptText) {

		try {
			this.setOnContext(context);
			GroovyShell groovyShell = new GroovyShell(this.getCompilerConfiguration());
			Script script = groovyShell.parse(scriptText);

			Binding binding = new Binding(context);
			script.setBinding(binding);

			script.run();
		} catch (CompilationFailedException e) {
			throw new RuntimeException("Invalid template: " + e.getMessage(), e);
		}

		return context;
	}

	private CompilerConfiguration getCompilerConfiguration() {

		CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
		compilerConfiguration.setScriptBaseClass(MosquitoScriptContext.class.getName());

		return compilerConfiguration;
	}

	private void setOnContext(MapObject map) {
		map.put("__context", map);
		map.put("resolver", this);
	}
}
