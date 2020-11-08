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

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.eussence.mosquito.api.exception.CheckedExecutable;
import com.eussence.mosquito.api.parser.ContentParser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A request body part.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BodyPart {

	private Object entity;
	private String name;
	private String mediaType;
	private String charSet;
	@Builder.Default
	private Map<String, String> headers = new HashMap<>();
	private long size;

	private boolean fromFile;
	private String loadedFromFile;

	public BodyPart header(String n, String value) {
		this.headers.put(Objects.requireNonNull(n), Objects.requireNonNull(value));
		return this;
	}

	public static BodyPart fromFile(String path) {
		File file = new File(Objects.requireNonNull(path));

		String mimeType = ContentParser.fileMediaType(path);
		Object entity = ContentParser.parseFileEntity(path);

		return BodyPart.builder()
				.charSet(Charset.defaultCharset()
						.name())
				.entity(entity)
				.fromFile(true)
				.loadedFromFile(CheckedExecutable.wrap(() -> new File(path).getAbsolutePath()))
				.mediaType(mimeType)
				.name(file.getName())
				.size(file.length())
				.build();
	}
}
