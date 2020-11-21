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
 * API module with most of the shared classes.
 * 
 * @author Ernest Kiwele
 */
open module com.eussence.mosquito.api {
	exports com.eussence.mosquito.api.data;
	exports com.eussence.mosquito.api.utils;
	exports com.eussence.mosquito.api.http;
	exports com.eussence.mosquito.api;
	exports com.eussence.mosquito.api.command;
	exports com.eussence.mosquito.api.execution;
	exports com.eussence.mosquito.api.qa;
	exports com.eussence.mosquito.api.exception;

	requires transitive com.fasterxml.jackson.annotation;
	requires transitive com.fasterxml.jackson.core;
	requires transitive com.fasterxml.jackson.databind;
	requires java.ws.rs;
	requires static lombok;
	requires org.apache.commons.lang3;
	requires transitive org.codehaus.groovy;
	requires com.fasterxml.jackson.datatype.jsr310;
	requires org.apache.tika.core;
}
