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

package com.eussence.mosquito.core.api.execution;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.CallChainResult;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.RequestTemplate;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.command.wrapper.Ether;

/**
 * An API for submitting calls and jobs to be scheduled in the execution
 * environment (local or cluster)
 * 
 * @author Ernest Kiwele
 */
public interface MosquitoScheduler {

	Response submit(Request request, SchedulingConfig scheduleConfig);

	CompletableFuture<Response> submitAsync(Request request, SchedulingConfig scheduleConfig);

	Collection<Response> submit(RequestTemplate requestTemplate, Ether contextEther, SchedulingConfig scheduleConfig);

	CompletableFuture<Collection<Response>> submitAsync(RequestTemplate requestTemplate, Ether contextEther,
			SchedulingConfig scheduleConfig);

	CallChainResult submit(CallChain callChain, Ether contextEther, SchedulingConfig scheduleConfig);

	CompletableFuture<CallChainResult> submitAsync(CallChain callChain, Ether contextEther,
			SchedulingConfig scheduleConfig);
}
