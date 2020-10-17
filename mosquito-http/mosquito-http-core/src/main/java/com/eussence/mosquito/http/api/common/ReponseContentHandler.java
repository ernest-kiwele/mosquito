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

package com.eussence.mosquito.http.api.common;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import com.eussence.mosquito.api.http.Body;
import com.eussence.mosquito.api.utils.JsonMapper;
import com.eussence.mosquito.http.api.StandardResponseHeaders;

/**
 * A simple interface for objects responsible for processing content.
 * 
 * @author Ernest Kiwele
 */
@FunctionalInterface
public interface ReponseContentHandler {

	ReponseContentHandler jsonHandler = (payload, headers) -> Body.builder()
			.entity(JsonMapper.fromJson((String) payload, Object.class))
			.mediaType(extractMediaType(headers.get(StandardResponseHeaders.CONTENT_TYPE.getHeaderName())))
			.charSet(extractCharSet(headers.get(StandardResponseHeaders.CONTENT_TYPE.getHeaderName())))
			.build();
	ReponseContentHandler textHandler = (payload, headers) -> Body.builder()
			.entity(payload)
			.mediaType(extractMediaType(headers.get(StandardResponseHeaders.CONTENT_TYPE.getHeaderName())))
			.charSet(extractCharSet(headers.get(StandardResponseHeaders.CONTENT_TYPE.getHeaderName())))
			.build();

	static final Map<String, ReponseContentHandler> standardHandlers = Map.of(MediaType.APPLICATION_JSON.toLowerCase(),
			jsonHandler, MediaType.TEXT_PLAIN.toLowerCase(), textHandler, MediaType.TEXT_HTML.toLowerCase(),
			textHandler);

	public static ReponseContentHandler standardHandler(String contentType) {
		return standardHandlers.get(contentType);
	}

	static String extractMediaType(List<String> contentTypeHeader) {
		if (contentTypeHeader.isEmpty()) {
			return null;
		}
		for (String header : contentTypeHeader) {
			if (StringUtils.isNotBlank(header))
				return header.split(";")[0];
		}

		return null;
	}

	static String extractCharSet(List<String> contentTypeHeader) {
		if (contentTypeHeader.isEmpty()) {
			return null;
		}

		for (var header : contentTypeHeader) {
			String[] parts = header.split(";");
			for (String part : parts) {
				if (part.trim()
						.startsWith("charset=")) {
					return part.split("=")[1];
				}
			}
		}

		return null;
	}

	Body process(Object payload, Map<String, List<String>> headers);
}
