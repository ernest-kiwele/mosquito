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
 * A response parser processes HTTP responses into API response types.
 * 
 * @param <RS>
 *            The type of the response object returned by the framework's HTTP
 *            client code.
 * @author Ernest Kiwele
 */
public interface ResponseMapper<RS> {

	/**
	 * Maps a framework-specific HTTP response into a
	 * {@link com.eussence.mosquito.api.http.Response response} object.
	 * 
	 * 
	 * @param response
	 *            The framework-specific response to map.
	 * @return A {@link com.eussence.mosquito.api.http.Response response} instance
	 *         corresponding to the input.
	 */
	public Response map(RS response);
}
