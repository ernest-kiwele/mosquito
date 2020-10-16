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

import com.eussence.mosquito.http.api.HttpDriver;
import com.eussence.mosquito.http.api.HttpDriverFactory;

/**
 * Default HTTP driver implemented using JDK-provided HTTP client APIs.
 * 
 * @author Ernest Kiwele
 */
public class StandardHttpDriverFactory implements HttpDriverFactory {

	public static final String PROVIDER_NAME = "com.eussence";

	@Override
	public String getProvider() {
		return PROVIDER_NAME;
	}

	@Override
	public String getName() {
		return "Standard HTTP Driver";
	}

	@Override
	public String getDescription() {
		return "Driver implemented using java.net.http module";
	}

	@Override
	public String getFeaturesDescription() {
		return "Supports all methods and content types, but does not support multipart requests";
	}

	@Override
	public HttpDriver getDriver() {
		return StandardHttpDriver.getInstance();
	}

}
