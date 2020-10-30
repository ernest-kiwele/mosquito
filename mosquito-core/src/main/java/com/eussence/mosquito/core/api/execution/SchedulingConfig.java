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

import java.util.function.Predicate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A set of parameters used to configure the scheduling of request execution.
 * This class defines such attributes as the number of calls the make or the
 * parallelism to aim for.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchedulingConfig {

	public static final Predicate<MosquitoNode> DEFAULT_NODE_SELECTOR = n -> true;
	public static final SchedulingConfig DEFAULT_CONFIG = SchedulingConfig.builder()
			.parallel(false)
			.nodeThreadCount(5)
			.iterations(1)
			.nodeSelector(DEFAULT_NODE_SELECTOR)
			.build();

	private String httpDriverId;
	private boolean parallel;
	@Builder.Default
	private int nodeThreadCount = 5;

	@Builder.Default
	private int iterations = 1;
	private Predicate<MosquitoNode> nodeSelector;
}
