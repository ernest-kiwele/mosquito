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

package com.eussence.mosquito.http.api;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

/**
 * Some methods to handle HTTP entities.
 * 
 * @author Ernest Kiwele
 */
public class ContentTypeHandler {

	private ContentTypeHandler() {
	}

	public static boolean isTextContentType(String contentType) {
		return contentType.startsWith("text/")
				|| StringUtils.containsAny(contentType, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
	}

	public static String extractMediaType(String contentTypeHeader) {
		return contentTypeHeader.split(";")[0];
	}
}
