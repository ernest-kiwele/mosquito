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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A type associated with node metadata for machines belonging to a Mosquito
 * cluster.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MosquitoNode {
	private String hostName;
	private String ipV4;
	private String osName;
	private String osVersion;
	private int httpPort;
	private String javaVersion;
	private String javaVendor;

	private long systemMemory;
	private long cpuCores;
}
