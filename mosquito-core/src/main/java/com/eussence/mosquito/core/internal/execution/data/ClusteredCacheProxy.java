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

package com.eussence.mosquito.core.internal.execution.data;

import java.util.concurrent.CompletableFuture;

import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.api.utils.JsonMapper;
import com.eussence.mosquito.core.api.data.CacheProxy;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;

/**
 * A proxy to a distributed cache
 * 
 * @author Ernest Kiwele
 */
public class ClusteredCacheProxy implements CacheProxy {

	private ClusteredCacheProxy() {
	}

	private AsyncMap<Object, Object> clusterCache;

	public static ClusteredCacheProxy init(Vertx vertx) {
		ClusteredCacheProxy cp = new ClusteredCacheProxy();
		CompletableFuture<Void> f = new CompletableFuture<>();

		vertx.sharedData()
				.getClusterWideMap(SHARED_DATA_MAP, res -> {
					cp.clusterCache = res.result();
					f.complete(null);
				});

		f.join();
		return cp;
	}

	@Override
	public void put(String key, Object val) {
		CompletableFuture<Void> f = new CompletableFuture<>();
		this.clusterCache.put(key, val, res -> {
			if (res.succeeded()) {
				f.complete(null);
			} else {
				f.completeExceptionally(res.cause());
			}
		});
		f.exceptionally(ex -> {
			throw new MosquitoException(ex);
		})
				.join();
	}

	@Override
	public <T> T get(String key, Class<T> valueClass) {
		CompletableFuture<T> f = new CompletableFuture<>();

		this.clusterCache.get(key, res -> {
			if (res.succeeded()) {
				f.complete(JsonMapper.fromJson((String) res.result(), valueClass));
			} else {
				f.completeExceptionally(res.cause());
			}
		});

		return f.join();
	}

	@Override
	public CompletableFuture<Void> putAsync(String key, Object val) {

		CompletableFuture<Void> f = new CompletableFuture<>();

		this.clusterCache.put(key, val, res -> {
			if (res.succeeded()) {
				f.complete(null);
			} else {
				f.completeExceptionally(res.cause());
			}
		});

		return f;
	}
}
