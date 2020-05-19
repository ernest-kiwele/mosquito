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

package com.eussence.mosquito.api.http;

/**
 * A request mapper is responsible for converting API requests into target
 * request types.
 * 
 * @param <RQ>
 *            The framework-specific Type of the request object used by the
 *            target HTTP client.
 * 
 * @author Ernest Kiwele
 */
public interface RequestMapper<RQ> {

	/**
	 * Convert the given request to the target HTTP framework request.
	 * 
	 * 
	 * @param request
	 *            The request object supplied by the client.
	 * @return An "Request" instance used in the target HTTP framework.
	 */
	public RQ map(Request request);
}
