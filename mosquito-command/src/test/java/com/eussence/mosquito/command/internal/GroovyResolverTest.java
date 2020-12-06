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

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.command.CommandLanguage;
import com.eussence.mosquito.api.data.Dataset;
import com.eussence.mosquito.api.data.Environment;
import com.eussence.mosquito.api.exception.MosquitoException;

/**
 * 
 * @author Ernest Kiwele
 *
 */
public class GroovyResolverTest {

	private GroovyResolver resolver = GroovyResolver.getInstance();

	private Environment environment = Environment.builder()
			.vars(Map.of("a", "A", "one", 1))
			.build();
	private Map<String, Dataset> datasets = Map.of();
	private Map<String, Object> vars = Map.of("key", "value");
	private Map<String, CallChain> callChains = Map.of();
	private MapObject outerContext = MapObject.instance();

	@Test
	void testBasics() {
		Assertions.assertEquals(CommandLanguage.GROOVY, resolver.getExpressionLanguage());
		Assertions.assertTrue(resolver == GroovyResolver.getInstance());
	}

	@Test
	void testEval() {
		Assertions.assertEquals(2,
				this.resolver.eval("one + 1", environment, datasets, vars, callChains, outerContext));
	}

	@Test
	void testEvalBadCode() {
		Assertions.assertThrows(MosquitoException.class,
				() -> this.resolver.eval("one + 1rubbish", environment, datasets, vars, callChains, outerContext));
	}

	@Test
	void testEvalWithContext() {
		Assertions.assertEquals("This is Yes!",
				this.resolver.eval(outerContext.add("yes", "Yes"), "\"This is $yes!\".toString()"));
		Assertions.assertThrows(MosquitoException.class,
				() -> this.resolver.eval(outerContext.add("yes", "Yes"), "\"is ${notExists(yes)!\".toString()"));
	}

	@Test
	void testEvalCollection() {
		var templates = Map.of("first", "one + 33", "second", "'abc' + one");
		Assertions.assertEquals(Map.of("first", 34, "second", "abc1"),
				this.resolver.eval(this.outerContext.add("one", 1), templates));
	}

	@Test
	void testExec() {
		this.resolver.exec(this.outerContext.add("one", 1), "result = one + 33");
		Assertions.assertEquals(34, this.outerContext.get("result"));

		Assertions.assertThrows(MosquitoException.class,
				() -> this.resolver.exec(this.outerContext.add("one", 1), "result -.-one + 33"));
	}
}
