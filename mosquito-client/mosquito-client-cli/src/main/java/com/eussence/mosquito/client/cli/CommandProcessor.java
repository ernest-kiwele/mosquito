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

package com.eussence.mosquito.client.cli;

import org.jline.terminal.Terminal;

import com.eussence.mosquito.command.wrapper.Ether;

/**
 * A type for processing commands entered by the user. This is also meant to
 * allow for user-defined command processors.
 * 
 * @author Ernest Kiwele
 */
@FunctionalInterface
public interface CommandProcessor {
	void process(String command, Terminal terminal, Ether ether);
}
