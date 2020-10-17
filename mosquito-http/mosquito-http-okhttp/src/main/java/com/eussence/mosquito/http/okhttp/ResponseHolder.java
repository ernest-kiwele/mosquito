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

package com.eussence.mosquito.http.okhttp;

import lombok.Builder;
import lombok.Data;
import okhttp3.Response;

/**
 * A simple structure meant to read the response body content off a response.
 * 
 * @author Ernest Kiwele
 */
@Data
@Builder
public class ResponseHolder<T> {
	private T payload;
	private Response response;
}
