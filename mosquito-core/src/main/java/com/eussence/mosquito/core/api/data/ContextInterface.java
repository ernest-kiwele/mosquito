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

package com.eussence.mosquito.core.api.data;

import java.util.List;

import com.eussence.mosquito.api.data.Dataset;
import com.eussence.mosquito.api.data.Environment;
import com.eussence.mosquito.command.wrapper.Ether;

/**
 * A facade for provkeying data sensitive to execution contexts.
 * 
 * @author Ernest Kiwele
 */
public interface ContextInterface {

	Ether ether(String key);

	List<String> listEthers();

	void persistEther(String key);

	Environment environment(String key);

	List<String> listEnvironments();

	void persistEnvironment(String key);

	Dataset dataset(String key);

	List<Dataset> listDatasets();
}
