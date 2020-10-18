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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.utils.JsonMapper;
import com.eussence.mosquito.command.wrapper.Ether;
import com.eussence.mosquito.core.api.data.CacheProxy;
import com.eussence.mosquito.core.internal.data.ClusteredCacheProxy;
import com.eussence.mosquito.core.internal.data.LocalCacheProxy;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * The entry point to the Mosquito service call API.
 * 
 * @author Ernest Kiwele
 */
public class Mosquito {

	private Vertx vertx;
	private boolean distributed;

	private Map<String, Object> config = new HashMap<>();

	private String clusterAddress;

	private CacheProxy cacheProxy;

	public static Mosquito instance() {
		Mosquito m = new Mosquito();

		m.distributed = false;

		m.loadConfig();
		m.init();

		return m;
	}

	public void shutdown() {
	}

	private void loadConfig() {
		String configPath = System.getProperty("config.file");
		try {
			if (!StringUtils.isNotBlank(configPath)) {
				configPath = System.getProperty("user.home") + "/.mosquito/mosquito.config.json";
			}

			File f = new File(configPath);
			if (f.exists() && f.isFile()) {
				this.config.putAll(JsonMapper.getObjectMapper()
						.readValue(f, Map.class));
			} else {
				System.out.println("Failed to locate config file. Creating one.");
				File file = new File(System.getProperty("user.home") + "/.mosquito/");
				file.mkdirs();

				Map<String, Object> defaults = this.defaultConfig();
				JsonMapper.getObjectMapper()
						.writeValue(new File(System.getProperty("user.home") + "/.mosquito/mosquito.config.json"),
								defaults);
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not load configuration from specified location: " + e.getMessage(), e);
		}
	}

	private Map<String, Object> defaultConfig() {
		Map<String, Object> c = new HashMap<>();

		c.put("ws.dir", System.getProperty("user.home") + "/.mosquito/workspaces/");
		c.put("label", "_default_");

		return c;
	}

	public static Mosquito distributedMosquito(String address) {
		Mosquito m = new Mosquito();

		m.distributed = true;
		m.clusterAddress = address;

		return m;
	}

	private void init() {
		if (this.distributed) {
			this.initDistributed();
		} else {
			this.initStandalone();
		}
	}

	private void initStandalone() {
		this.vertx = Vertx.vertx();
		this.cacheProxy = LocalCacheProxy.init(vertx);// = this.vertx.sharedData().getLocalMap(SHARED_DATA_MAP);
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

		f.thenApply(vertx -> {
			this.vertx = vertx;
			return this.vertx;
		})
				.thenAccept(vertx -> this.cacheProxy = ClusteredCacheProxy.init(vertx))
				.join();
	}

	public CacheProxy getCacheProxy() {
		return cacheProxy;
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
			throw new RuntimeException("Could not snapshot '" + label + "': " + e.getMessage(), e);
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
			throw new RuntimeException("Could not load ether file for label " + label, e);
		}
	}

	public Ether defaultEther() {
		if (this.config.get("label") != null) {
			Ether ether = this.loadEther((String) this.config.get("label"));

			return ether;
		}

		return new Ether();
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public static void main(String[] args) {
		AtomicLong l = new AtomicLong();
		Stream<Long> stream = Stream.generate(() -> {
			return l.getAndIncrement();
		});

		Stream<String> stringStream = stream.map(lng -> {
			System.out.println(".map function running for " + lng);
			return Long.toString(lng, 16);
		});

		System.out.println(".map() executed");

		stringStream.map(str -> {
			System.out.println(".map function 2 running for " + str);
			return str.toUpperCase();
		})
				.allMatch(str -> str.isEmpty());

	}
}
