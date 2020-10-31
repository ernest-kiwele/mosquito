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

package com.eussence.mosquito.core.internal.execution;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.command.CommandLanguage;
import com.eussence.mosquito.api.command.Resolver;
import com.eussence.mosquito.api.data.Dataset;
import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.api.execution.ExecutionResult;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.RequestTemplate;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.command.internal.GroovyResolver;
import com.eussence.mosquito.command.wrapper.Ether;
import com.eussence.mosquito.core.api.MosquitoScheduler;
import com.eussence.mosquito.core.api.SchedulingConfig;
import com.eussence.mosquito.http.api.HttpDriver;
import com.eussence.mosquito.http.driver.HttpDriverFactoryLocator;
import com.eussence.mosquito.http.driver.StandardHttpDriverFactory;

/**
 * A collection of reusable scheduler methods/operations/data.
 * 
 * @author Ernest Kiwele
 */
public abstract class AbstractMosquitoScheduler implements MosquitoScheduler {

	private Map<String, HttpDriver> drivers = new ConcurrentHashMap<>();

	protected Function<CommandLanguage, Resolver> resolverFactory = lang -> lang == CommandLanguage.GROOVY
			? GroovyResolver.getInstance()
			: null;

	protected HttpDriver getDriver(String id) {
		return this.drivers.computeIfAbsent(id, n -> HttpDriverFactoryLocator.getInstance()
				.findByName(n)
				.orElseThrow(MosquitoException.supplier("No driver found by name '" + n + "'"))
				.getDriver());
	}

	protected String getDriverOrDefault(String n) {
		return StringUtils.firstNonBlank(n, StandardHttpDriverFactory.STANDARD_DRIVER_ID);
	}

	@Override
	public Response submit(Request request, SchedulingConfig scheduleConfig) {
		return this.getDriver(this.getDriverOrDefault(scheduleConfig.getHttpDriverId()))
				.http(request);
	}

	@Override
	public CompletableFuture<Response> submitAsync(Request request, SchedulingConfig scheduleConfig) {
		return this.getDriver(this.getDriverOrDefault(scheduleConfig.getHttpDriverId()))
				.asyncHttp(request);
	}

	@Override
	public Collection<Response> submit(RequestTemplate requestTemplate, Ether contextEther,
			SchedulingConfig scheduleConfig) {
		return this.schedule(this.resolveRequestTemplate(requestTemplate, contextEther), scheduleConfig);
	}

	@Override
	public CompletableFuture<Collection<Response>> submitAsync(RequestTemplate requestTemplate, Ether contextEther,
			SchedulingConfig scheduleConfig) {
		return this.scheduleAsync(this.resolveRequestTemplate(requestTemplate, contextEther), scheduleConfig);
	}

	@Override
	public ExecutionResult submit(CallChain callChain, Ether contextEther, SchedulingConfig scheduleConfig) {

		var requests = callChain.getCalls()
				.entrySet()
				.stream()
				.map(e -> Map.entry(e.getKey(), this.resolveRequestTemplate(e.getValue()
						.getRequestTemplate(), contextEther)))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		return this.schedule(requests, callChain, scheduleConfig);
	}

	@Override
	public CompletableFuture<ExecutionResult> submitAsync(CallChain callChain, Ether contextEther,
			SchedulingConfig scheduleConfig) {
		var requests = callChain.getCalls()
				.entrySet()
				.stream()
				.map(e -> Map.entry(e.getKey(), this.resolveRequestTemplate(e.getValue()
						.getRequestTemplate(), contextEther)))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		return this.scheduleAsync(requests, callChain, scheduleConfig);
	}

	protected List<Map<String, Object>> getDataSet(Dataset dateSet) {
		// TODO: Implement resolution of data set content:
		return List.of();
	}

	protected abstract Collection<Response> schedule(Iterable<Request> requests, SchedulingConfig scheduleConfig);

	protected abstract CompletableFuture<Collection<Response>> scheduleAsync(Iterable<Request> requests,
			SchedulingConfig scheduleConfig);

	protected abstract ExecutionResult schedule(Map<String, List<Request>> requests, CallChain chain,
			SchedulingConfig scheduleConfig);

	protected abstract CompletableFuture<ExecutionResult> scheduleAsync(Map<String, List<Request>> requests,
			CallChain chain, SchedulingConfig scheduleConfig);

	protected abstract List<Request> resolveRequestTemplate(RequestTemplate template, Ether contextEther);
}
