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

import com.eussence.mosquito.api.AuthType
import com.eussence.mosquito.api.http.Body
import com.eussence.mosquito.api.http.HttpMethod

/**
 * 
 * @author Ernest Kiwele
 */
class RequestWrapperTest {

	@Test
	void testMethods() {
		RequestWrapper wrapper = new RequestWrapper()

		Assertions.assertEquals(HttpMethod.GET, wrapper.get().getMethod())
		Assertions.assertEquals(HttpMethod.POST, wrapper.post().getMethod())
		Assertions.assertEquals(HttpMethod.PUT, wrapper.put().getMethod())
		Assertions.assertEquals(HttpMethod.HEAD, wrapper.head().getMethod())
		Assertions.assertEquals(HttpMethod.OPTIONS, wrapper.options().getMethod())
		Assertions.assertEquals(HttpMethod.PATCH, wrapper.patch().getMethod())
		Assertions.assertEquals(HttpMethod.DELETE, wrapper.delete().getMethod())
	}

	@Test
	void testHeaders() {
		RequestWrapper wrapper = new RequestWrapper()

		Assertions.assertEquals('abc', wrapper.header('v', 'abc').headers.v)
		Assertions.assertEquals('abc', wrapper.headers(['w': 'abc']).headers.w)
	}

	@Test
	void testBasics() {
		RequestWrapper wrapper = new RequestWrapper()

		wrapper.uri('http://localhost/')
		wrapper.method HttpMethod.PUT
		Assertions.assertEquals 'http://localhost/', wrapper.uri
		Assertions.assertEquals HttpMethod.PUT, wrapper.method
		wrapper.parameters(['p1': 'P1'])
		Assertions.assertEquals 'P1', wrapper.parameters['p1']

		wrapper.body(Body.builder().build())
		wrapper.entity([:])

		Assertions.assertEquals([:], wrapper.payload)

		wrapper.basicAuth('user', 'pw')
		Assertions.assertEquals(AuthType.BASIC_AUTH, wrapper.authType)

		wrapper.bearer("abcd")
		Assertions.assertEquals('Authorization', wrapper.authData.headerName)
	}
}
