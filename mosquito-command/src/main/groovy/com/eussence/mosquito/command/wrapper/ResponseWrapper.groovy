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

import com.eussence.mosquito.api.http.Response

import groovy.transform.CompileStatic

/**
 * A command-friendly wrapper for responses.
 * 
 * @author Ernest Kiwele
 */
@CompileStatic
class ResponseWrapper {

	@Delegate
	Response response

	static ResponseWrapper of(Response response) {
		new ResponseWrapper(response: response)
	}

	Map<String, Object> getSummary() {
		def r = [:]
		if(response.failed) {
			r['result'] = "Request failed: '${response.exception.message}'"
		} else {
			r['result'] = "HTTP ${response.status} - ${response.statusReason} in ${response.duration} milliseconds"
			r['cookies'] = response.cookies?.size()
			r['headers'] = response.headers?.size()
			r['payload'] = response.length > 0 ? "${response.length}-byte ${response.body?.mediaType} content" : 'No payload'
		}

		r
	}
}
