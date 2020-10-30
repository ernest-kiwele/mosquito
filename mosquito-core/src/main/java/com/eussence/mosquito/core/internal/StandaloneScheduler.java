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

package com.eussence.mosquito.core.internal;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.CallChainResult;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.RequestTemplate;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.command.wrapper.Ether;
import com.eussence.mosquito.core.api.execution.SchedulingConfig;
import com.eussence.mosquito.http.api.HttpDriver;

/**
 * A scheduler designed to run calls off the local machine using only resources
 * available on one node.
 * 
 * @author Ernest Kiwele
 */
public class StandaloneScheduler extends AbstractMosquitoScheduler {

	private static final StandaloneScheduler instance = new StandaloneScheduler();

	public static StandaloneScheduler getInstance() {
		return instance;
	}

	@Override
	protected Collection<Response> schedule(Iterable<Request> requests, SchedulingConfig scheduleConfig) {
		return StreamSupport.stream(requests.spliterator(), scheduleConfig.isParallel())
				.map(it -> this.getDriver(this.getDriverOrDefault(scheduleConfig.getHttpDriverId()))
						.http(it))
				.collect(Collectors.toList());
	}

	@Override
	protected CompletableFuture<Collection<Response>> scheduleAsync(Iterable<Request> requests,
			SchedulingConfig scheduleConfig) {
		HttpDriver driver = this.getDriver(this.getDriverOrDefault(scheduleConfig.getHttpDriverId()));

		ExecutorService es;
		if (scheduleConfig.isParallel()) {
			es = Executors.newFixedThreadPool(Math.max(scheduleConfig.getNodeThreadCount(), 5));
			return CompletableFuture.supplyAsync(() -> StreamSupport.stream(requests.spliterator(), false)
					.map(req -> CompletableFuture.supplyAsync(() -> driver.asyncHttp(req), es))
					.map(CompletableFuture::join)
					.collect(Collectors.toList())
					.stream()
					.map(CompletableFuture::join)
					.collect(Collectors.toList()));
		}

		return CompletableFuture.supplyAsync(() -> StreamSupport.stream(requests.spliterator(), false)
				.map(req -> CompletableFuture.supplyAsync(() -> driver.asyncHttp(req)))
				.map(CompletableFuture::join)
				.collect(Collectors.toList())
				.stream()
				.map(CompletableFuture::join)
				.collect(Collectors.toList()));
	}

	@Override
	protected CallChainResult schedule(Map<String, List<Request>> requests, CallChain chain,
			SchedulingConfig scheduleConfig) {
		return null;
	}

	@Override
	protected CompletableFuture<CallChainResult> scheduleAsync(Map<String, List<Request>> requests, CallChain chain,
			SchedulingConfig scheduleConfig) {
		return null;
	}

	@Override
	protected List<Request> resolveRequestTemplate(RequestTemplate template, Ether contextEther) {
		return null;
	}
}
