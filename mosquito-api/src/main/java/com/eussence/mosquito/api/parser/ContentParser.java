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

package com.eussence.mosquito.api.parser;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;

import com.eussence.mosquito.api.exception.CheckedExecutable;
import com.eussence.mosquito.api.exception.MosquitoException;

/**
 * Content parsing utilities
 * 
 * @author Ernest Kiwele
 */
public class ContentParser {
	public static final long MEMORY_SIZE_THRESHOLD = 1024 * 1024;

	private static Tika tika = new Tika();
	private static Set<String> TEXT_FORMATS = Set.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_SVG_XML,
			MediaType.APPLICATION_XHTML_XML, MediaType.APPLICATION_XML, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN,
			MediaType.TEXT_XML);

	private ContentParser() {
	}

	public static Object parseFileEntity(String path) {
		var file = new File(Objects.requireNonNull(path));
		if (!file.exists()) {
			throw new MosquitoException("No file found at " + path);
		}

		if (file.length() > MEMORY_SIZE_THRESHOLD) {
			return CheckedExecutable.wrap(() -> new FileInputStream(file));
		}

		if (isTextContent(fileMediaType(file))) {
			return readToText(file);
		}

		return CheckedExecutable.wrap(() -> new FileInputStream(file));
	}

	public static String fileMediaType(String path) {
		return fileMediaType(new File(path));
	}

	public static String fileMediaType(File file) {
		return CheckedExecutable.wrap(() -> tika.detect(file));
	}

	public static String readToText(File path) {
		return CheckedExecutable.wrap(() -> IOUtils.toString(new FileInputStream(path)));
	}

	public static boolean isTextContent(String mediaType) {
		String ct = Objects.requireNonNull(mediaType);

		return ct.startsWith("text/") || TEXT_FORMATS.contains(ct);
	}
}
