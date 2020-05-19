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
 * An object holding all connection-related configuration parameters.
 * 
 * @author Ernest Kiwele
 */
public class ConnectionConfig {

	private boolean validateHostNames;
	private long connectionTimeout = 10000L;
	private long readTimeout;
	private boolean followRedirects;

	public ConnectionConfig() {
	}

	public ConnectionConfig(boolean validateHostNames, long connectionTimeout, long readTimeout,
			boolean followRedirects) {
		this.validateHostNames = validateHostNames;
		this.connectionTimeout = connectionTimeout;
		this.readTimeout = readTimeout;
		this.followRedirects = followRedirects;
	}

	/**
	 * Get the value of validateHostNames
	 * 
	 * @return the validateHostNames
	 */
	public boolean isValidateHostNames() {
		return validateHostNames;
	}

	/**
	 * Get the value of connectionTimeout
	 * 
	 * @return the connectionTimeout
	 */
	public long getConnectionTimeout() {
		return this.connectionTimeout < 1 ? 10000L : this.connectionTimeout;
	}

	/**
	 * Get the value of readTimeout
	 * 
	 * @return the readTimeout
	 */
	public long getReadTimeout() {
		return readTimeout;
	}

	/**
	 * Get the value of followRedirects
	 * 
	 * @return the followRedirects
	 */
	public boolean isFollowRedirects() {
		return followRedirects;
	}
}
