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

import java.util.concurrent.CompletableFuture;

/**
 * An concrete cache proxy provides access to data meant to be cached and used
 * during execution of call chains.
 * 
 * @author Ernest Kiwele
 */
public interface CacheProxy {

	void put(String key, Object val);

	<T> T get(String key, Class<T> valueClass);

	CompletableFuture<Void> putAsync(String key, Object val);

	<T> CompletableFuture<T> getAsync(String key);
}
