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
 * Core module with basic API and context management classes.
 * 
 * @author Ernest Kiwele
 */
module com.eussence.mosquito.core {
	exports com.eussence.mosquito.core.api.data;
	exports com.eussence.mosquito.core.api;

	requires com.eussence.mosquito.api;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.eussence.mosquito.http;
	requires org.apache.commons.lang3;
	requires vertx.core;

	requires static lombok;
	requires com.eussence.mosquito.command.wrapper;
}
