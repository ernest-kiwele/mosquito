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

package com.eussence.mosquito.api.data;

import com.eussence.mosquito.api.AuthType;
import com.eussence.mosquito.api.MapObject;

/**
 * A dynamically resolvable template of auth data.
 * 
 * @author Ernest Kiwele
 */
public class AuthDataTemplate extends MapObject {

	private static final long serialVersionUID = -8003685348289891361L;

	private AuthType authType = AuthType.BASIC_AUTH;

	private String usernameTemplate;
	private String passwordTemplate;
	private String tokenHeader;
	private String bearerTokenTemplate;

	/**
	 * Get the value of authType
	 * 
	 * @return the authType
	 */
	public AuthType getAuthType() {
		return authType;
	}

	/**
	 * Set the value of authType
	 * 
	 * @param authType
	 *            the authType to set
	 */
	public void setAuthType(AuthType authType) {
		this.authType = authType;
	}

	/**
	 * Get the value of usernameTemplate
	 * 
	 * @return the usernameTemplate
	 */
	public String getUsernameTemplate() {
		return usernameTemplate;
	}

	/**
	 * Set the value of usernameTemplate
	 * 
	 * @param usernameTemplate
	 *            the usernameTemplate to set
	 */
	public void setUsernameTemplate(String usernameTemplate) {
		this.usernameTemplate = usernameTemplate;
	}

	/**
	 * Get the value of passwordTemplate
	 * 
	 * @return the passwordTemplate
	 */
	public String getPasswordTemplate() {
		return passwordTemplate;
	}

	/**
	 * Set the value of passwordTemplate
	 * 
	 * @param passwordTemplate
	 *            the passwordTemplate to set
	 */
	public void setPasswordTemplate(String passwordTemplate) {
		this.passwordTemplate = passwordTemplate;
	}

	/**
	 * Get the value of tokenHeader
	 * 
	 * @return the tokenHeader
	 */
	public String getTokenHeader() {
		return tokenHeader;
	}

	/**
	 * Set the value of tokenHeader
	 * 
	 * @param tokenHeader
	 *            the tokenHeader to set
	 */
	public void setTokenHeader(String tokenHeader) {
		this.tokenHeader = tokenHeader;
	}

	/**
	 * Get the value of bearerTokenTemplate
	 * 
	 * @return the bearerTokenTemplate
	 */
	public String getBearerTokenTemplate() {
		return bearerTokenTemplate;
	}

	/**
	 * Set the value of bearerTokenTemplate
	 * 
	 * @param bearerTokenTemplate
	 *            the bearerTokenTemplate to set
	 */
	public void setBearerTokenTemplate(String bearerTokenTemplate) {
		this.bearerTokenTemplate = bearerTokenTemplate;
	}
}
