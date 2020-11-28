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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author Ernest Kiwele
 */
public class ContentTypeHandlerTest {

	@Test
	void testTextContent() {
		Assertions.assertTrue(ContentTypeHandler.isTextContentType(MediaType.TEXT_HTML));
		Assertions.assertTrue(ContentTypeHandler.isTextContentType("text/unknown"));
		Assertions.assertTrue(ContentTypeHandler.isTextContentType(MediaType.APPLICATION_JSON));
		Assertions.assertFalse(ContentTypeHandler.isTextContentType(MediaType.APPLICATION_OCTET_STREAM));
	}

	@Test
	void testExtractContentType() {
		Assertions.assertEquals(MediaType.TEXT_HTML, ContentTypeHandler.extractMediaType(MediaType.TEXT_HTML));
		Assertions.assertEquals(MediaType.TEXT_HTML,
				ContentTypeHandler.extractMediaType(MediaType.TEXT_HTML + ";charset=utf-8"));
	}
}
