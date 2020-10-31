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

package com.eussence.mosquito.core.api.execution.standalone;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.eussence.mosquito.api.Call;
import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.CallChainResult;
import com.eussence.mosquito.api.CallResult;
import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.command.CommandLanguage;
import com.eussence.mosquito.api.command.Resolver;
import com.eussence.mosquito.api.execution.ExecutionEvent;
import com.eussence.mosquito.api.execution.ExecutionResult;
import com.eussence.mosquito.api.execution.ExecutionSchedule;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.api.qa.Assertion;
import com.eussence.mosquito.api.qa.AssertionResult;
import com.eussence.mosquito.http.api.HttpDriver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A scheduler plans and orchestrates the execution of call chains.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandaloneSchedule {

	protected boolean runAssertions;
	protected String executionId;
	protected boolean collectMetrics;
	@Builder.Default
	protected Collection<Consumer<ExecutionEvent>> eventConsumers = new ArrayList<>();
	protected Function<CommandLanguage, Resolver> resolverFactory;
	protected HttpDriver client;

	public void registerEventConsumer(Consumer<ExecutionEvent> listener) {
		this.eventConsumers.add(listener);
	}

	protected Resolver resolver(CommandLanguage lang) {
		CommandLanguage l = null == lang ? CommandLanguage.GROOVY : lang;
		return this.resolverFactory.apply(l);
	}

	protected Request getRequestForCall(Call call, CommandLanguage lang, MapObject context) {

		return call.getRequestTemplate()
				.toRequest(resolverFactory, context);
	}

	protected AssertionResult runAssertion(Call call, Assertion assertion, Request request, Response response,
			MapObject context, Resolver resolver) {

		var resultBuilder = AssertionResult.builder()
				.id(assertion.getId())
				.assertion(assertion);

		if (response.isFailed()) {
			return resultBuilder.build();
		}

		try {
			MapObject c = MapObject.instance()
					.add("request", request)
					.add("response", response);
			boolean succeeded = Boolean
					.parseBoolean(String.valueOf(resolver.eval(c, assertion.getBooleanExpression())));
			resultBuilder = resultBuilder.succeeded(succeeded)
					.error(false);

			if (!succeeded && StringUtils.isNotBlank(assertion.getMessageTemplate())) {
				resultBuilder = resultBuilder
						.expectationMessage(String.valueOf(resolver.eval(c, assertion.getMessageTemplate())));
			}
		} catch (Exception ex) {
			resultBuilder = resultBuilder.expectationMessage("[Execution error: " + ex.getMessage() + "]")
					.succeeded(false)
					.error(true)
					.errorMessage(ex.getMessage())
					.stackTrace(ExceptionUtils.getStackTrace(ex));
		}

		return resultBuilder.build();
	}

	protected Map<String, AssertionResult> runAssertions(Call call, Request request, Response response,
			MapObject context, Resolver resolver) {

		return call.getAssertions()
				.stream()
				.map(ass -> this.runAssertion(call, ass, request, response, context, resolver))
				.collect(Collectors.toMap(AssertionResult::getId, Function.identity(), (ar1, ar2) -> ar1));
	}

	protected CallResult executeCall(Call call, CallChain callChain, MapObject context, Resolver resolver) {

		Request request = this.getRequestForCall(call, callChain.getExpressionLanguage(), context);

		var start = Instant.now();
		Response response = this.client.http(request);
		var end = Instant.now();

		context.add(call.getKey(), response);

		var resultBuilder = CallResult.builder()
				.startDate(start)
				.endDate(end)
				.key(call.getKey())
				.executed(true)
				.request(request)
				.response(response)
				.assertionsExecuted(runAssertions);

		if (!response.isFailed() && this.runAssertions) {
			resultBuilder = resultBuilder.assertions(call.getAssertions())
					.assertionResults(this.runAssertions(call, request, response, context, resolver)
							.values()
							.stream()
							.collect(Collectors.toList()));
		}

		return resultBuilder.build();
	}

	protected CallChainResult executeCallChain(CallChain callChain, MapObject context) {

		Resolver resolver = this.resolver(callChain.getExpressionLanguage());
		return CallChainResult.builder()
				.key(callChain.getKey())
				.startDate(Instant.now())
				.callResults(callChain.getCalls()
						.values()
						.stream()
						.sorted(Comparator.comparing(Call::getKey, String::compareTo))
						.map(call -> this.executeCall(call, callChain, context, resolver))
						.collect(Collectors.toMap(CallResult::getKey, Function.identity(), (cr1, cr2) -> cr1)))
				.endDate(Instant.now())
				.build();
	}

	public ExecutionResult execute(ExecutionSchedule schedule) {

		return ExecutionResult.builder()
				.startDate(Instant.now())
				.id(schedule.getId())
				.assertionsRun(this.runAssertions)
				.metricsCollected(this.collectMetrics)
				.datasets(schedule.getDatasets())
				.callChainResults(this.executeCallChain(schedule.getCallChain(), schedule.getContext()))
				.endDate(Instant.now())
				.build();
	}
}
