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

package com.eussence.mosquito.core.internal.data;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.eussence.mosquito.api.utils.JsonMapper;
import com.eussence.mosquito.core.api.data.CacheProxy;

import io.vertx.core.Vertx;

/**
 * Local cache implementation. Uses the map-based vert.x local data.
 * 
 * @author Ernest Kiwele
 */
public class LocalCacheProxy implements CacheProxy {

	private Map<String, Object> localCache;

	private LocalCacheProxy() {
	}

	public static LocalCacheProxy init(Vertx vertx) {
		LocalCacheProxy cp = new LocalCacheProxy();
		cp.localCache = vertx.sharedData()
				.getLocalMap(CacheProxy.SHARED_DATA_MAP);

		return cp;
	}

	@Override
	public void put(String key, Object val) {
		this.localCache.put(key, JsonMapper.json(val));
	}

	@Override
	public <T> T get(String key, Class<T> valueClass) {
		return JsonMapper.fromJson((String) this.localCache.get(key), valueClass);
	}

	@Override
	public CompletableFuture<Void> putAsync(String key, Object val) {
		this.put(key, val);
		return CompletableFuture.completedFuture(null);
	}
}
