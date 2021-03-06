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

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * A dataset defines information to be used in a test. It can optionally contain
 * dynamically resolved information through templating and mapping.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Getter
@Setter
public class Dataset {

	private String id;
	private String name;
	private String description;
	private String comments;

	private String createdBy;

	@Builder.Default
	private Instant dateCreated = Instant.now();

	private String mediaType;
	private String uri;
}
