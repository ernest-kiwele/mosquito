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

package com.eussence.mosquito.core.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.api.utils.JsonMapper;
import com.eussence.mosquito.command.wrapper.Ether;
import com.eussence.mosquito.core.api.data.CacheProxy;
import com.eussence.mosquito.core.api.data.ContextInterface;
import com.eussence.mosquito.core.api.execution.MosquitoScheduler;
import com.eussence.mosquito.core.internal.data.ClusteredCacheProxy;
import com.eussence.mosquito.core.internal.data.ConfigManager;
import com.eussence.mosquito.core.internal.data.LocalCacheProxy;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.Builder;
import lombok.Getter;

/**
 * The entry point to the Mosquito service call API.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Getter
public class Mosquito {
	public static final int DEFAULT_QUEUE_CAPACITY = 256;

	private Queue<MosquitoLogEntry> logQueue;
	private MosquitoLogger mosquitoLogger;
	@Builder.Default
	private List<Consumer<MosquitoLogEntry>> logListeners = new ArrayList<>();

	private Vertx vertx;
	private boolean distributed;

	@Builder.Default
	private Map<String, Object> config = new HashMap<>();
	private String clusterAddress;

	private CacheProxy cacheProxy;
	private ContextInterface contextInterface;
	private MosquitoScheduler scheduler;

	public static Mosquito instance() {
		return Mosquito.builder()
				.distributed(false)
				.config(ConfigManager.getInstance()
						.loadConfig())
				.build()
				.init();
	}

	public void shutdown() {
		this.vertx.close();
	}

	public static Mosquito distributedMosquito(String address) {
		return Mosquito.builder()
				.distributed(true)
				.clusterAddress(address)
				.build();
	}

	private Mosquito init() {
		if (this.distributed) {
			this.initDistributed();
		} else {
			this.initStandalone();
		}

		return this;
	}

	private void initStandalone() {
		this.vertx = Vertx.vertx();
		this.cacheProxy = LocalCacheProxy.init(vertx);
		this.logQueue = new ArrayBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
		this.mosquitoLogger = (source, type, message) -> this.logQueue
				.add(MosquitoLogger.logEntry(source, type, message));
	}

	private void initDistributed() {

		CompletableFuture<Vertx> f = new CompletableFuture<>();

		VertxOptions options = new VertxOptions().setClustered(true);

		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				f.complete(res.result());
			} else {
				f.completeExceptionally(
						null == res.cause() ? new RuntimeException("Vertx initialization failed due to unknown cause")
								: res.cause());
			}
		});

		f.thenApply(v -> {
			this.vertx = v;
			return this.vertx;
		})
				.thenAccept(v -> this.cacheProxy = ClusteredCacheProxy.init(v))
				.join();

		// TODO: change to a distributed queue
		this.logQueue = new ArrayBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
		this.mosquitoLogger = (source, type, message) -> this.logQueue
				.add(MosquitoLogger.logEntry(source, type, message));
	}

	private File etherFile(String label) {
		return new File((String) config.computeIfAbsent("ws.dir",
				o -> o == null ? System.getProperty("user.home") + "/.mosquito/workspaces/" : o) + "/" + label);
	}

	public void etherSnapshot(String label, MapObject data) {
		try {
			JsonMapper.getObjectMapper()
					.writeValue(etherFile(label), data);
		} catch (IOException e) {
			throw new MosquitoException("Could not snapshot '" + label + "': " + e.getMessage(), e);
		}
	}

	public Ether loadEther(String label) {
		try {
			File file = this.etherFile(label);
			if (file.exists()) {
				return JsonMapper.getObjectMapper()
						.readValue(etherFile(label), Ether.class);
			} else {
				return new Ether();
			}
		} catch (IOException e) {
			throw new MosquitoException("Could not load ether file for label " + label, e);
		}
	}

	public Ether defaultEther() {
		if (this.config.get("label") != null) {
			return this.loadEther((String) this.config.get("label"));
		}

		return new Ether();
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public MosquitoLogger logger() {
		return this.getMosquitoLogger();
	}

	public CacheProxy getCacheProxy() {
		return cacheProxy;
	}

}
