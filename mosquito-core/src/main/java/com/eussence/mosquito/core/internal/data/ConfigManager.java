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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.api.utils.JsonMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Local configuration manager for Mosquito application.
 * 
 * @author Ernest Kiwele
 */
public class ConfigManager {
	private static final ConfigManager instance = new ConfigManager();

	private ConfigManager() {
	}

	public static ConfigManager getInstance() {
		return instance;
	}

	public Map<String, Object> loadConfig() {
		String configPath = System.getProperty("config.file");
		try {
			Map<String, Object> v;
			if (!StringUtils.isNotBlank(configPath)) {
				configPath = System.getProperty("user.home") + "/.mosquito/mosquito.config.json";
			}

			File f = new File(configPath);
			if (f.exists() && f.isFile()) {
				v = JsonMapper.getObjectMapper()
						.readValue(f, new TypeReference<Map<String, Object>>() {
						});
			} else {
				System.out.println("Failed to locate config file. Creating one.");
				File file = new File(System.getProperty("user.home") + "/.mosquito/");
				file.mkdirs();

				v = this.defaultConfig();
				JsonMapper.getObjectMapper()
						.writeValue(new File(System.getProperty("user.home") + "/.mosquito/mosquito.config.json"), v);
			}

			return v;
		} catch (IOException e) {
			throw new MosquitoException("Could not load configuration from specified location: " + e.getMessage(), e);
		}
	}

	public Map<String, Object> defaultConfig() {
		Map<String, Object> c = new HashMap<>();

		c.put("ws.dir", System.getProperty("user.home") + "/.mosquito/workspaces/");
		c.put("label", "_default_");

		return c;
	}
}
