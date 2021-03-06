/**
 * Copyright 2018 eussence.com and contributors Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * OkHttp-based Mosquito HTTP client
 * 
 * @author Ernest Kiwele
 */
open module com.eussence.mosquito.http.okhttp {
	exports com.eussence.mosquito.http.okhttp;

	requires transitive com.eussence.mosquito.api;
	requires com.eussence.mosquito.http;
	requires transitive okhttp3;
	requires okio;
	requires kotlin.stdlib;
	requires java.ws.rs;
	requires org.apache.commons.lang3;
	requires static lombok;

	requires org.apache.commons.io;

	provides com.eussence.mosquito.http.api.HttpDriverFactory
			with com.eussence.mosquito.http.okhttp.OkHttpDriverFactory;
}