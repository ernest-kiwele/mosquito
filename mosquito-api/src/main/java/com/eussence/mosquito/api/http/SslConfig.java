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
 * SSL configuration applicable to an HTTP request.
 * 
 * @author Ernest Kiwele
 */
public class SslConfig {

	private final boolean checkHosts;

	public SslConfig(boolean checkHosts) {
		this.checkHosts = checkHosts;
	}

	/**
	 * Get the value of checkHosts
	 * 
	 * @return the checkHosts
	 */
	public boolean isCheckHosts() {
		return checkHosts;
	}
}
