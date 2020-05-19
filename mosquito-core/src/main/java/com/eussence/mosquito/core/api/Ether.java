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

import java.util.HashMap;

import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.data.Environment;
import com.eussence.mosquito.http.api.DefaultClient;

/**
 * The ether is the execution context from which all commands are evaluated. It
 * holds contextual and environment data.
 * 
 * @author Ernest Kiwele
 */
public class Ether extends MapObject {

	private static final long serialVersionUID = -7825203213429363914L;

	{
		put("cache", Mosquito.instance().getCacheProxy());
		put("_label", Mosquito.instance().getConfig().get("label"));
		put("callChains", new HashMap<String, CallChain>());
		put("environments", MapObject.instance().add("_dev", Environment.builder().key("dev").name("Development")
				.vars(MapObject.instance().add("hello", "World")).build()));
		put("datasets", new HashMap<>());
		put("vars", new HashMap<>());
		put("currentChain", "default");

		put("client", DefaultClient.builder().build());
		put("_env", "_dev");
	}

	public static enum EtherKey {

		LIVE_CHAIN("live.chain");

		private final String key;

		private EtherKey(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}

	public void snapshot() {
		Mosquito.instance().etherSnapshot(this.getString("_label"), this);
	}

	public void load(String label) {
		Ether ether = Mosquito.instance().loadEther(label);

		this.putAll(ether);

		this.put("callChains", ether.get("callChains"));
		this.put("environments", ether.get("environments"));
		this.put("datasets", ether.get("datasets"));
		this.put("vars", ether.get("vars"));

		this.put("currentChain", ether.get("currentChain"));
		this.put("_label", label);
	}
}
