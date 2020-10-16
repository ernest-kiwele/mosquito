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

package com.eussence.mosquito.http.api;

/**
 * An HTTP driver factory is used to discover and expose HTTP drivers. HTTP
 * drivers are pluggable implementations of {@link HttpDriver HttpDriver} that
 * can be discovered at runtime from third-party or user-provided libraries.
 * 
 * Beside creating the HttpDriver implementation, this interface also provides
 * metadata that can be used by the consumer to filter/select among options.
 * 
 * @author Ernest Kiwele
 */
public interface HttpDriverFactory {

	String getProvider();

	String getName();

	String getDescription();

	String getFeaturesDescription();

	HttpDriver getDriver();
}
