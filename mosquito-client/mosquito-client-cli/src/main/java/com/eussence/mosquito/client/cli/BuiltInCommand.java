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

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A set of built-in commands with their processors.
 * 
 * @author Ernest Kiwele
 */
public enum BuiltInCommand {

	QUIT("quit", Set.of("quit", "exit"), (a, b, c) -> MosquitoCli.instance.quit()),
	ERROR("error", Set.of("error"), MosquitoCli.instance::error),
	CLS("cls", Set.of("cls", "clear"), MosquitoCli.instance::clearScreen),
	TRACE("trace", Set.of("trace"), MosquitoCli.instance::trace),
	DRIVER("driver", Set.of("driver", "driver-info"), MosquitoCli.instance::driver),
	DRIVERS("drivers", Set.of("drivers", "list-drivers"), MosquitoCli.instance::drivers),
	SET_CONTEXT("setcontext", Set.of("set-context", "set context"), MosquitoCli.instance::setContextCommand),
	MODE_REQUEST("moderequest", Set.of("moderequest", "mode-request", "mode request"),
			MosquitoCli.instance::modeRequest),
	MODE_RESPONSE("moderesponse", Set.of("moderesponse", "mode-response", "mode response"),
			MosquitoCli.instance::modeResponse),
	SEND("send", Set.of("send", "go"), MosquitoCli.instance::send),
	NEW_REQUEST("new-request", Set.of("new-request", "new request"), MosquitoCli.instance::newRequest);

	private final String bundlePrefix;
	private final Set<String> synonyms;
	private final CommandProcessor processor;

	private static final Map<String, BuiltInCommand> mapping = new ConcurrentHashMap<>();

	private BuiltInCommand(String bundlePrefix, Set<String> synonyms, CommandProcessor processor) {
		this.processor = processor;
		this.bundlePrefix = bundlePrefix;
		this.synonyms = synonyms;
	}

	/**
	 * Returns this command's execution logic.
	 */
	public CommandProcessor getProcessor() {
		return processor;
	}

	/**
	 * The unique value used to reference this command in bundles. Affixes are added
	 * to this value in actual texts.
	 * 
	 * @return This command's bundle key.
	 */
	public String getBundlePrefix() {
		return bundlePrefix;
	}

	/**
	 * Returns a set of all texts supported as commands to be executed by this
	 * handler.
	 * 
	 * @return This command's alternative texts.
	 */
	public Set<String> getSynonyms() {
		return synonyms;
	}

	/**
	 * Find the Built-in command object for the given user-provided command.
	 * 
	 * @param command The command to search.
	 * @return The built-in command definition if found, null otherwise.
	 */
	public static Optional<BuiltInCommand> of(String command) {
		return Optional.ofNullable(mapping.computeIfAbsent(command.trim()
				.toLowerCase(),
				n -> Arrays.stream(values())
						.filter(v -> v.synonyms.contains(n)) // assumes all synonyms here are lowercase
						.findAny()
						.orElse(null)));
	}

	/**
	 * Attempts to find a command that matches the beginning of the given user
	 * input.
	 * 
	 * @param command The user-input command to try to match with a built-in command
	 * @return The matched command or null if none is found.
	 */
	public static Optional<BuiltInCommand> match(String command) {
		return Arrays.stream(values())
				.filter(v -> v.synonyms.stream()
						.anyMatch(command::startsWith))
				.findAny();
	}
}
