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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.MapObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A once-off object used to send information to local or remote objects
 * expected to run call chains.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionSchedule {

	@Builder.Default
	private String id = UUID.randomUUID()
			.toString();

	private String description;
	private String comments;

	@Builder.Default
	private MapObject details = MapObject.instance();

	private CallChain callChain;
	private List<Map<String, Object>> datasets;
	private MapObject context;
}
