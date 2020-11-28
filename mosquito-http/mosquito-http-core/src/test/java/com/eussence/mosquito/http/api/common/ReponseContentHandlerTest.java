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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author Ernest Kiwele
 */
public class ReponseContentHandlerTest {

	@Test
	void testExtractContentType() {
		Assertions.assertNull(ReponseContentHandler.extractMediaType(List.of()));
		Assertions.assertEquals(MediaType.TEXT_HTML,
				ReponseContentHandler.extractMediaType(List.of("", MediaType.TEXT_HTML)));
		Assertions.assertNull(ReponseContentHandler.extractMediaType(List.of("")));
	}

	@Test
	void testExtractCharset() {
		Assertions.assertNull(ReponseContentHandler.extractCharSet(List.of()));
		Assertions.assertEquals("utf-16",
				ReponseContentHandler.extractCharSet(List.of("", MediaType.TEXT_HTML + ";charset=utf-16")));
		Assertions.assertNull(ReponseContentHandler.extractCharSet(List.of("")));
	}

	@Test
	void testProcessJsonPayload() {
		var payload = "{\"a\":\"A\"}";
		var headers = Map.of("content-type", List.of(MediaType.APPLICATION_JSON));

		var body = ReponseContentHandler.standardHandler(MediaType.APPLICATION_JSON)
				.process(payload, headers);
		Assertions.assertNotNull(body);
		Assertions.assertEquals(MediaType.APPLICATION_JSON, body.getMediaType());
		Assertions.assertEquals(Map.of("a", "A"), body.getEntity());
	}

	@Test
	void testProcessTextPayload() {
		var payload = "{\"a\":\"A\"}";
		var headers = Map.of("content-type", List.of(MediaType.TEXT_PLAIN));

		var body = ReponseContentHandler.standardHandler(MediaType.TEXT_PLAIN)
				.process(payload, headers);
		Assertions.assertNotNull(body);
		Assertions.assertEquals(MediaType.TEXT_PLAIN, body.getMediaType());
		Assertions.assertEquals("{\"a\":\"A\"}", body.getEntity());
	}
}
