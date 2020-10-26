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

package com.eussence.mosquito.core.api;

import java.time.Instant;

/**
 * An object used to send logging events to a manager that dispatches them to
 * various listeners.
 * 
 * @author Ernest Kiwele
 */
@FunctionalInterface
public interface MosquitoLogger {

	/**
	 * Submit a log entry, assumed to occur at the time of submission.
	 * 
	 * @param source  Source of the log entry.
	 * @param type    Type of the log entry's message.
	 * @param message The message of the new entry.
	 */
	void log(String source, String type, String message);

	/**
	 * Generate a new log entry.
	 * 
	 * @param source  The source of the message
	 * @param type    The type of the message
	 * @param message The entry's content
	 * @return The generated log entry.
	 */
	static MosquitoLogEntry logEntry(String source, String type, String message) {
		return MosquitoLogEntry.builder()
				.source(source)
				.type(type)
				.message(message)
				.date(Instant.now())
				.build();
	}
}
