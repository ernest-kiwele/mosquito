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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A dynamically resolvable template of auth data.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthDataTemplate extends MapObject {

	private static final long serialVersionUID = -8003685348289891361L;

	@Builder.Default
	private AuthType authType = AuthType.BASIC_AUTH;

	private String usernameTemplate;
	private String passwordTemplate;
	private String tokenHeader;
	private String bearerTokenTemplate;
}
