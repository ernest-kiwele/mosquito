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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.UUID;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.eussence.mosquito.api.exception.CheckedRunnable;
import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.utils.JsonMapper;
import com.eussence.mosquito.command.internal.MosquitoScriptContext;
import com.eussence.mosquito.command.wrapper.Ether;

/**
 * 
 * @author Ernest Kiwele
 *
 */
public class MosquitoCliTest {

	@Mock
	private Terminal terminal;
	@Mock
	private LineReader lineReader;
	@Mock
	private OutputStream output;

	@InjectMocks
	private MosquitoCli cli = MosquitoCli.getInstance();

	@BeforeAll
	static void setup() throws Exception {
		var dir = Files.createTempDirectory(UUID.randomUUID()
				.toString())
				.toFile();
		System.setProperty("user.home", dir.getAbsolutePath());

		var ether = Files.createTempFile("", "ether.json")
				.toFile();
		CheckedRunnable.wrap(() -> JsonMapper.getObjectMapper()
				.writeValue(ether, new Ether()))
				.run();

	}

	@BeforeEach
	public void init() throws Exception {
		MockitoAnnotations.openMocks(this);
		Mockito.when(this.lineReader.readLine(Mockito.anyString()))
				.thenReturn("drivers", "driver-info", "mode none", "mode request", "new-request", "mode response",
						"trace", "cls", "clear", "send", "error", "quit");
		Mockito.when(this.terminal.writer())
				.thenReturn(new PrintWriter(output));

		cli.lastException = new MosquitoException("Something wrong");
	}

	@Test
	void testBasics() throws Exception {
		MosquitoCli.main(new String[0]);
		Assertions.assertNotNull(MosquitoScriptContext.getScheduler());
		Assertions.assertNotNull(Request.requestHandler);
	}

	@Test
	void testCalls() throws Exception {
		MosquitoCli.main(new String[0]);
		Mockito.verify(this.terminal, Mockito.atLeastOnce())
				.writer();
		Mockito.verify(this.terminal, Mockito.atLeastOnce())
				.flush();
	}

	@Test
	void testCommand() throws Exception {
		Mockito.when(this.lineReader.readLine(Mockito.anyString()))
				.thenReturn("1+23", "quit");

		MosquitoCli.main(new String[0]);

		Mockito.verify(this.terminal, Mockito.atLeastOnce())
				.writer();
		Mockito.verify(this.terminal, Mockito.atLeastOnce())
				.flush();
	}
}
