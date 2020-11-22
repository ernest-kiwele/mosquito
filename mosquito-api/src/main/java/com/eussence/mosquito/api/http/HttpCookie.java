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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A simple POJO to store cookie data and attributes.
 * 
 * @author Ernest Kiwele
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpCookie {

	private String name;
	private String value;
	private String domain;
	private boolean httpOnly;
	private String expires;
	private String path;
	@Builder.Default
	private Map<String, String> attributes = new HashMap<>();

	public static HttpCookie forHeader(String c) {
		String[] parts = c.split(";");
		String[] nameValuePair = parts[0].split("=");

		Map<String, String> attributes = map(parts);

		var result = HttpCookie.builder()
				.name(nameValuePair[0])
				.value(nameValuePair[1])
				.domain(attributes.get("domain"))
				.httpOnly(attributes.containsKey("httponly"))
				.expires(attributes.get("expires"))
				.path(attributes.get("path"))
				.attributes(attributes)
				.build();

		attributes.remove("path");
		attributes.remove("expires");
		attributes.remove("httponly");
		attributes.remove("domain");

		return result;
	}

	private static Map<String, String> map(String[] m) {
		return Arrays.stream(m)
				.skip(1)
				.map(s -> s.split("="))
				.map(e -> Map.entry(StringUtils.trimToEmpty((e[0]).toLowerCase()), e.length > 1 ? e[1] : ""))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}
}
