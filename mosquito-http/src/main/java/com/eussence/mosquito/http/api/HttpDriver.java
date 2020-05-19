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

package com.eussence.mosquito.http.api;

import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.Response;

/**
 * An entry point to a specific HTTP client implementation. This constitutes the
 * API for plugging HTTP client implementations.
 * 
 * @author Ernest Kiwele
 */
public interface HttpDriver {

	/**
	 * Run the given HTTP request, convert the outcome into a Response object. This
	 * method will not throw exceptions due to network or protocol-related failures;
	 * only allowed exceptions concern validation.
	 * 
	 * @param request All request information
	 * @return The result of the call as a Response object.
	 */
	Response http(Request request);
}
