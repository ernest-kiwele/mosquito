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

import java.util.Date;

import com.eussence.mosquito.api.MapObject;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An environment allows parameters and configurations to be isolated while
 * logic setup is reused. An environment can define the setup that
 * differentiates, for example, the URIs for production vs non-production
 * servers.
 * 
 * @author Ernest Kiwele
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Environment {

	private String key;
	private String name;
	private String description;
	private String comments;

	@Builder.Default
	private boolean production = true;

	private boolean shared;
	private String scope;

	@Builder.Default
	private MapObject vars = new MapObject();

	private String createdBy;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private Date dateCreated;
}
