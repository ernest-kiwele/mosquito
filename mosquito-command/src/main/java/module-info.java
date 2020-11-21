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
 * Execution of the command line.
 * 
 * @author Ernest Kiwele
 */
module com.eussence.mosquito.command {
	exports com.eussence.mosquito.command.internal;
	exports com.eussence.mosquito.command.api;

	requires transitive com.eussence.mosquito.api;
	requires com.eussence.mosquito.http;
	requires com.eussence.mosquito.reports;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires java.ws.rs;
	requires org.apache.commons.io;
	requires org.codehaus.groovy;
	requires vertx.core;
	requires org.apache.commons.lang3;

	exports com.eussence.mosquito.command.wrapper;

	requires static lombok;
}
