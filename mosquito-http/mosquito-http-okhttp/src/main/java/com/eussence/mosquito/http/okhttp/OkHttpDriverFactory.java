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

package com.eussence.mosquito.http.okhttp;

import com.eussence.mosquito.http.api.HttpDriver;
import com.eussence.mosquito.http.api.HttpDriverFactory;

/**
 * 
 * @author Ernest Kiwele
 *
 */
public class OkHttpDriverFactory implements HttpDriverFactory {

	@Override
	public String getProvider() {
		return "com.eussence";
	}

	@Override
	public String getName() {
		return "OkHttp Driver";
	}

	@Override
	public String getDescription() {
		return "An HTTP client based on OkHttp";
	}

	@Override
	public String getFeaturesDescription() {
		return "Supports all defined HTTP operations";
	}

	@Override
	public HttpDriver getDriver() {
		return OkHttpDriver.getInstance();
	}
}
