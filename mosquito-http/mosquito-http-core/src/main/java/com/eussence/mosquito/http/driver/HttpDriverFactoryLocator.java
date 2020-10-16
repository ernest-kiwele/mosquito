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

package com.eussence.mosquito.http.driver;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.http.api.HttpDriver;
import com.eussence.mosquito.http.api.HttpDriverFactory;

/**
 * The loader of service implementations for {@link HttpDriver HttpDriver}.
 * 
 * @author Ernest Kiwele
 */
public class HttpDriverFactoryLocator {

	private static final HttpDriverFactoryLocator instance = new HttpDriverFactoryLocator();

	private HttpDriverFactory selectedFactory;
	private HttpDriver selectedDriver;

	private HttpDriverFactoryLocator() {
		this.selectedFactory = this.getAny()
				.orElseThrow(() -> new MosquitoException("Could not locate any HTTP driver"));
		this.selectedDriver = this.selectedFactory.getDriver();
	}

	public static HttpDriverFactoryLocator getInstance() {
		return instance;
	}

	public HttpDriver getSelectedDriver() {
		return selectedDriver;
	}

	public HttpDriverFactory getSelectedFactory() {
		return selectedFactory;
	}

	public Set<HttpDriverFactory> listServices() {
		ServiceLoader<HttpDriverFactory> serviceLoader = ServiceLoader.load(HttpDriverFactory.class);
		Set<HttpDriverFactory> all = new LinkedHashSet<>();

		for (HttpDriverFactory driver : serviceLoader) {
			all.add(driver);
		}

		return all;
	}

	public Optional<HttpDriverFactory> getAny() {
		return this.listServices()
				.stream()
				.sorted(Comparator.comparing(HttpDriverFactory::getName))
				.findFirst();
	}

	public Optional<HttpDriverFactory> findAnyByProvider(String providerName) {
		Objects.requireNonNull(providerName, "Provider name is null");

		return this.listServices()
				.stream()
				.filter(s -> providerName.equalsIgnoreCase(s.getProvider()))
				.findAny();
	}

	public Optional<HttpDriverFactory> findByName(String name) {
		Objects.requireNonNull(name, "Name is null");

		return this.listServices()
				.stream()
				.filter(s -> name.equalsIgnoreCase(s.getName()))
				.findAny();
	}
}
