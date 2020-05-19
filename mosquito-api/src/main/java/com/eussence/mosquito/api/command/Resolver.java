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

package com.eussence.mosquito.api.command;

import java.util.Map;

import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.ExpressionLanguage;
import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.data.Dataset;
import com.eussence.mosquito.api.data.Environment;
import com.eussence.mosquito.api.data.Vars;

/**
 * A resolver is a language-specific expression evaluator that processes
 * templates and runs scripts.
 * 
 * @author Ernest Kiwele
 */
public interface Resolver {

	ExpressionLanguage getExpressionLanguage();

	Object eval(MapObject context, String template);

	Map<String, Object> eval(MapObject context, Map<String, String> templates);

	Map<String, Object> exec(MapObject context, String script);

	Object eval(String template, Environment environment, Map<String, Dataset> datasets, Map<String, Vars> vars,
			Map<String, CallChain> callChains, MapObject outerContext);
}
