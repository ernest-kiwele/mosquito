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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.Parser;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp.Capability;

import com.eussence.mosquito.api.CallChain;
import com.eussence.mosquito.api.data.Dataset;
import com.eussence.mosquito.api.data.Environment;
import com.eussence.mosquito.api.data.Vars;
import com.eussence.mosquito.api.execution.LocalExecutionScheduler;
import com.eussence.mosquito.api.utils.JsonMapper;
import com.eussence.mosquito.command.internal.GroovyResolver;
import com.eussence.mosquito.core.api.Ether;
import com.eussence.mosquito.core.api.Mosquito;
import com.eussence.mosquito.http.api.DefaultClient;

/**
 * Entry point to the CLI Mosquito client.
 * 
 * @author Ernest Kiwele
 */
public class MosquitoCli {

	private static final MosquitoCli instance = new MosquitoCli();
	private static String[] args;

	private boolean distributed;
	private String clusterAddress;

	private Mosquito mosquito;
	private Ether ether;
	private String lang = "groovy";

	private Throwable lastException;

	private MosquitoCli() {
	}

	public static MosquitoCli getInstance() {
		return instance;
	}

	public void startup(boolean cli) {
		this.mosquito = Mosquito.instance();
		this.ether = this.mosquito.defaultEther();
		this.ether.put("scheduler", LocalExecutionScheduler.instance(new ArrayList<>(),
				f -> GroovyResolver.getInstance(), DefaultClient.builder()::build));

		try {
			if (cli)
				commandPrompt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		this.mosquito.shutdown();
	}

	public static void main(String[] args) throws Exception {
		MosquitoCli.args = args;

		if (args.length != 0) {
			MosquitoCli.instance.runScript(args);
		} else {
			MosquitoCli.instance.startup(true);
		}
	}

	private void runScript(String[] args) {
		String script = null;

		for (String a : args) {
			if (a.startsWith("--script")) {
				String[] p = a.split("=");
				if (p.length != 2) {
					System.out.println("Expected --script=/path/to/script.groovy");
					System.exit(1);
				}

				script = p[1];
			}
		}

		if (StringUtils.isAnyBlank(script)) {
			System.out.println("Parameters must supply a valid script path");
			System.exit(1);
		}

		try {
			MosquitoCli.instance.startup(false);
			String input = IOUtils.toString(new FileInputStream(script));
			this.runCommand(input);
			System.out.println("Script execution completed.");
			System.exit(0);
		} catch (Exception ex) {
			System.err.println("Execution failed with error: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public void quit() {
		System.out.println("Goodbye!");
		System.exit(0);
	}

	public String runCommand(String command) {
		return this.evaluateInput(command);
	}

	public void runShellCommand(String command) {
		String[] input = command.split(" +");

		switch (input[0]) {
			case "$quit": {
				this.quit();
			}
			case "$echo": {
				System.out.println(String.join(" ", input));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public String evaluateInput(String command) {
		String output = null;
		switch (this.lang) {
			case "groovy": {

				Object val;

				try {
					// val = GroovyResolver.getInstance().eval(this.ether, command);

					val = GroovyResolver.getInstance()
							.eval(command,
									(Environment) ((Map<String, Object>) this.ether.get("environments"))
											.get(this.ether.get("_env")),
									(Map<String, Dataset>) this.ether.get("datasets"),
									(Map<String, Vars>) this.ether.get("vars"),
									(Map<String, CallChain>) this.ether.get("callChains"), this.ether);
				} catch (groovy.lang.MissingPropertyException | groovy.lang.MissingMethodException ex) {
					val = new AttributedStringBuilder()
							.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA))
							.append("[ERROR]: Unknown attribute: ")
							.style(AttributedStyle.BOLD)
							.append(ex.getMessage())
							.toAnsi();
					lastException = ex;
				} catch (Exception ex) {
					val = new AttributedStringBuilder().style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
							.append("[ERROR]: Unexpected error > " + ex.getMessage())
							.toAnsi();
					lastException = ex;
				} catch (StackOverflowError so) {
					val = new AttributedStringBuilder().style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
							.append("[SEVERE]: You did something really bad. The computer is laughing to tears! Google 'StackOverflow error'")
							.toAnsi();
					lastException = so;
				} catch (Throwable t) {
					System.out.println("[DEADLY]: Now something lethal happened. Quitting while I still can");
					System.exit(1);
					return null; // to compile
				}

				if (null != val) {
					if (val instanceof String) {
						output = (String) val;
					} else if (val instanceof Number) {
						output = "" + val;
					} else {
						try {
							output = this.highlightJson(JsonMapper.jsonPretty(val));
						} catch (Throwable t) {
							lastException = t;
							output = "[Failed to represent output as JSON: " + t.getMessage() + "]"
									+ "\n[Object.toString() == ]\n" + val;
						}
					}
				}
			}
		}

		return output;
	}

	private String highlightJson(String json) {
		if (null == json || json.length() > 2000) {
			return json;
		}

		String[] marked = json.replaceAll("(\".*?\")\\s*:", "~$1^ :")
				.replaceAll("(?<=:\\s*)(\".*?\")(?=\\s*,?)", "`$1^")
				.split("(?<=(~|`|\\^))|(?=(~|`|\\^))");
		AttributedStringBuilder b = new AttributedStringBuilder();

		for (String s : marked) {
			if ("~".equals(s.trim())) {
				b = b.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA));
			} else if ("`".equals(s.trim())) {
				b = b.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
			} else if ("^".equals(s.trim())) {
				b = b.style(AttributedStyle.DEFAULT);
			} else {
				b = b.append(s)
						.style(AttributedStyle.DEFAULT);
			}
		}

		return b.toAnsi();
	}

	private void commandPrompt() throws Exception {

		String prompt = new AttributedStringBuilder().append("mosquito:")
				.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE))
				.style(AttributedStyle.BOLD)
				.append("groovy")
				.style(AttributedStyle.DEFAULT)
				.append("> ")
				.toAnsi();

		String rightPrompt = null;
		Character mask = null;
		String trigger = null;

		TerminalBuilder builder = TerminalBuilder.builder();

		Completer completer = (reader, line, candidates) -> {
			if (line.wordIndex() == 0) {
				candidates.add(new Candidate("println"));
			} else if (line.words()
					.get(0)
					.equals("quit")) {
				if (line.words()
						.get(line.wordIndex() - 1)
						.equals("Option1")) {
					candidates.add(new Candidate("Param1"));
					candidates.add(new Candidate("Param2"));
				} else {
					if (line.wordIndex() == 1)
						candidates.add(new Candidate("Option1"));
					if (!line.words()
							.contains("Option2"))
						candidates.add(new Candidate("Option2"));
					if (!line.words()
							.contains("Option3"))
						candidates.add(new Candidate("Option3"));
				}
			}
		};

		Parser parser = null;
		DefaultParser p = new DefaultParser();
		p.setEofOnUnclosedQuote(true);
		parser = p;
		// stuff

		Terminal terminal;
		try {
			terminal = builder.build();
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}

		LineReader reader = LineReaderBuilder.builder()
				.terminal(terminal)
				.completer(completer)
				.parser(parser)
				.build();

		while (true) {
			String line = null;
			try {
				line = reader.readLine(prompt, rightPrompt, (MaskingCallback) null, null);
			} catch (UserInterruptException e) {
				// Ignore
			} catch (EndOfFileException e) {
				quit();
			}

			if (line == null)
				continue;

			line = line.trim();

			if ((trigger != null) && (line.compareTo(trigger) == 0))
				line = reader.readLine("password> ", mask);

			if (!StringUtils.startsWithAny(line, "quit", "exit", "cls", "error", "trace")) {
				String output = this.runCommand(line);
				if (StringUtils.isNotBlank(output)) {
					terminal.writer()
							.println(output);
					terminal.flush();
				}

				continue;
			}

			if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
				this.quit();
			} else if ("cls".equalsIgnoreCase(line)) {
				terminal.puts(Capability.clear_screen);
				terminal.flush();
			} else if ("error".equalsIgnoreCase(line)) {
				if (null != this.lastException) {
					terminal.writer()
							.println(new AttributedStringBuilder()
									.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
									.style(AttributedStyle.BOLD)
									.append(this.lastException.getClass()
											.getName() + ": ")
									.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA))
									.append(this.lastException.getMessage())
									.toAnsi());
				} else {
					terminal.writer()
							.println("[Nothing to show]");
				}
				terminal.flush();
			} else if ("trace".equalsIgnoreCase(line)) {
				if (null != this.lastException) {
					String ex = new AttributedStringBuilder()
							.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
							.append(ExceptionUtils.getStackTrace(this.lastException))
							.toAnsi();

					terminal.writer()
							.println(ex);
				} else {
					terminal.writer()
							.println("[Nothing to show]");
				}
				terminal.flush();
			} else {
				terminal.writer()
						.println("I didn't get that... try again!");
				terminal.flush();
			}

			// ParsedLine pl = reader.getParser().parse(line, 0);
		}
	}
}
