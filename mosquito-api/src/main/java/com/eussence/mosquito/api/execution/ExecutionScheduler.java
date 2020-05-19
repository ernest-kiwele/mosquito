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

package com.eussence.mosquito.api.execution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.eussence.mosquito.api.Call;
import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.CallChainResult;
import com.eussence.mosquito.api.CallResult;
import com.eussence.mosquito.api.ExpressionLanguage;
import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.command.Resolver;
import com.eussence.mosquito.api.http.ClientBinding;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.api.qa.Assertion;
import com.eussence.mosquito.api.qa.AssertionResult;
import com.eussence.mosquito.api.utils.Templates;

/**
 * A scheduler plans and orchestrates the execution of call chains.
 * 
 * @author Ernest Kiwele
 */
public abstract class ExecutionScheduler {

	protected boolean runAssertions;
	protected String executionId;
	protected boolean collectMetrics;
	protected Collection<Consumer<ExecutionEvent>> eventConsumers = new ArrayList<>();
	protected Function<ExpressionLanguage, Resolver> resolverFactory;
	protected Supplier<ClientBinding<?, ?, ?>> clientFactory;

	protected ExecutionScheduler(boolean runAssertions, String executionId, boolean collectMetrics,
			Collection<Consumer<ExecutionEvent>> eventConsumers,
			Function<ExpressionLanguage, Resolver> resolverProvider, Supplier<ClientBinding<?, ?, ?>> clientFactory) {
		this.runAssertions = runAssertions;
		this.executionId = executionId;
		this.collectMetrics = collectMetrics;
		this.eventConsumers = eventConsumers;
		this.resolverFactory = resolverProvider;
		this.clientFactory = clientFactory;
	}

	public void registerEventConsumer(Consumer<ExecutionEvent> listener) {
		this.eventConsumers.add(listener);
	}

	protected Resolver resolver(ExpressionLanguage lang) {
		ExpressionLanguage l = null == lang ? ExpressionLanguage.GROOVY : lang;

		return this.resolverFactory.apply(l);
	}

	protected Request getRequestForCall(Call call, ExpressionLanguage lang, MapObject context) {

		return Templates.safeConvert(this.resolver(lang).eval(context, call.getRequestTemplate().getTemplates()),
				Request.class);
	}

	protected AssertionResult runAssertion(Call call, Assertion assertion, Request request, Response response,
			MapObject context, Resolver resolver) {

		AssertionResult result = new AssertionResult();

		result.setId(assertion.getId());
		result.setAssertion(assertion);

		if (response.isFailed()) {
			return result;
		}

		try {
			MapObject c = MapObject.instance().add("request", request).add("response", response);
			result.setSucceeded(Boolean.valueOf(String.valueOf(resolver.eval(c, assertion.getBooleanExpression()))));
			result.setError(false);

			if (!result.isSucceeded() && StringUtils.isNotBlank(assertion.getMessageTemplate())) {
				result.setExpectationMessage(String.valueOf(resolver.eval(c, assertion.getMessageTemplate())));
			}
		} catch (Exception ex) {
			result.setExpectationMessage("[Execution error: " + ex.getMessage() + "]");
			result.setSucceeded(false);
			result.setError(true);
			result.setErrorMessage(ex.getMessage());
			result.setStackTrace(ExceptionUtils.getStackTrace(ex));
		}

		return result;
	}

	protected Map<String, AssertionResult> runAssertions(Call call, Request request, Response response,
			MapObject context, Resolver resolver) {

		return call.getAssertions().stream()
				.map(ass -> this.runAssertion(call, ass, request, response, context, resolver))
				.collect(Collectors.toMap(AssertionResult::getId, Function.identity(), (ar1, ar2) -> ar1));
	}

	protected CallResult executeCall(Call call, CallChain callChain, MapObject context, Resolver resolver) {
		CallResult result = new CallResult();

		Request request = this.getRequestForCall(call, callChain.getExpressionLanguage(), context);

		Date start = new Date();
		Response response = this.client().send(request);
		Date end = new Date();

		context.add(call.getKey(), response);

		result.setStartDate(start);
		result.setEndDate(end);
		result.setKey(call.getKey());
		result.setExecuted(true);
		result.setRequest(request);
		result.setResponse(response);

		result.setAssertionsExecuted(runAssertions);
		if (!response.isFailed()) {

			if (this.runAssertions) {
				result.setAssertions(call.getAssertions());
				result.setAssertionResults(this.runAssertions(call, request, response, context, resolver).values()
						.stream().collect(Collectors.toList()));
			}
		}

		return result;
	}

	protected CallChainResult executeCallChain(CallChain callChain, MapObject context) {
		CallChainResult result = new CallChainResult();
		result.setKey(callChain.getKey());
		result.setStartDate(new Date());

		Resolver resolver = this.resolver(callChain.getExpressionLanguage());

		result.setCallResults(
				callChain.getCalls().values().stream().sorted(Comparator.comparing(Call::getKey, String::compareTo))
						.map(call -> this.executeCall(call, callChain, context, resolver))
						.collect(Collectors.toMap(CallResult::getKey, Function.identity(), (cr1, cr2) -> cr1)));
		result.setEndDate(new Date());
		return result;
	}

	protected abstract ClientBinding<?, ?, ?> client();

	public abstract ExecutionResult execute(ExecutionSchedule schedule);
}
