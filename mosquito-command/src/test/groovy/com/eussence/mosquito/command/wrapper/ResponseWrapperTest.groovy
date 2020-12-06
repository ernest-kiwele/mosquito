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

package com.eussence.mosquito.command.wrapper

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import com.eussence.mosquito.api.exception.MosquitoException
import com.eussence.mosquito.api.http.Response

/**
 * 
 * @author Ernest Kiwele
 *
 */
class ResponseWrapperTest {

	@Test
	void testWrapper() {
		def res = ResponseWrapper.of(new Response.ResponseBuilder().uri('http://localhost/').failed(false).status(201).build())

		Assertions.assertEquals(201, res.getStatus())
		Assertions.assertEquals('HTTP', res.summary.result[0..3])
	}

	@Test
	void testWrapperFailedSummary() {
		def res = ResponseWrapper.of(new Response.ResponseBuilder().uri('http://localhost/').failed(true).exception(new MosquitoException('Failure!')).status(201).build())

		Assertions.assertTrue("Request failed: 'Failure!'" == res.summary.result)
	}
}
