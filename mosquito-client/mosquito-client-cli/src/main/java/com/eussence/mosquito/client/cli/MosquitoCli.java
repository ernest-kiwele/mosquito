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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.Parser;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.DefaultParser.Bracket;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp.Capability;

import com.eussence.mosquito.api.command.CommandLanguage;
import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.api.http.Request;
import com.eussence.mosquito.api.http.Response;
import com.eussence.mosquito.api.utils.JsonMapper;
import com.eussence.mosquito.command.internal.GroovyResolver;
import com.eussence.mosquito.command.internal.MosquitoScriptContext;
import com.eussence.mosquito.command.wrapper.Ether;
import com.eussence.mosquito.command.wrapper.ProgramExtensionsManager;
import com.eussence.mosquito.command.wrapper.ResponseWrapper;
import com.eussence.mosquito.core.api.Mosquito;
import com.eussence.mosquito.core.api.SchedulingConfig;
import com.eussence.mosquito.http.api.HttpDriverFactory;
import com.eussence.mosquito.http.driver.HttpDriverFactoryLocator;

/**
 * Entry point to the CLI Mosquito client.
 * 
 * @author Ernest Kiwele
 */
public class MosquitoCli {

	private static final String ETHER_FILE_NAME = "ether.json";
	private static final String HISTORY_FILE_NAME = "history";

	static final MosquitoCli instance = new MosquitoCli();
	private static String[] args;

	private boolean distributed;
	private String clusterAddress;

	private Mosquito mosquito;
	private Ether ether;
	private CommandLanguage lang = CommandLanguage.GROOVY;
	private String prompt;

	private Throwable lastException;

	// jline
	private Terminal terminal;
	private LineReader lineReader;

	private SchedulingConfig scheduleConfig = SchedulingConfig.builder()
			.build();

	private MosquitoCli() {
		MosquitoScriptContext.setScheduler(req -> this.mosquito.getScheduler()
				.submit(req, scheduleConfig));
	}

	public static MosquitoCli getInstance() {
		return instance;
	}

	public void startup(boolean cli) {
		this.mosquito = Mosquito.instance();
		this.bootstrap();
		Request.requestHandler = MosquitoScriptContext::schedule;
		ProgramExtensionsManager.setupDefaults();
//		this.ether.put("scheduler", LocalExecutionScheduler.instance(new ArrayList<>(), 
//				f -> GroovyResolver.getInstance(), DefaultClient.builder()::build));

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

	private void bootstrap() {
		File configDir = this.getConfigDirectory();
		File etherFile = new File(configDir, ETHER_FILE_NAME);

		if (etherFile.exists()) {
			try {
				this.ether = JsonMapper.getObjectMapper()
						.readValue(etherFile, Ether.class);
			} catch (IOException e) {
				System.out.println("|>> Failed to load your session file from '" + etherFile.getAbsolutePath() + ": "
						+ e.getMessage()
						+ "'\n|>> If this file is known to have invalid data, please delete and restart the program");
				System.exit(1);
			}
		}

		if (null == this.ether)
			this.ether = this.mosquito.defaultEther();
	}

	private File getConfigDirectory() {
		File configDirectory = this.configDirectory();
		if (!configDirectory.exists()) {
			configDirectory.mkdir();
		}
		return configDirectory;
	}

	private File configDirectory() {
		return new File(System.getProperty("user.home") + File.separator + ".mosquito");
	}

	public void quit() {
		File etherFile = new File(this.getConfigDirectory(), ETHER_FILE_NAME);
		System.out.println("Storing session information to '" + etherFile.getAbsolutePath() + "'");

		try {
			JsonMapper.getPrettyobjectmapper()
					.writeValue(etherFile, this.ether.putAllFields());
		} catch (IOException e) {
			System.out.println("Failed to write session data: " + e.getMessage());
		}

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

	private void newRequestWrapper(String name) {
		this.ether.newRequest(Objects.requireNonNull(name));
	}

	private void setResponse(Response resp) {
		this.ether.setResponse(ResponseWrapper.of(resp));
	}

	private void setContext(String type) {
		this.ether.setContextType(type);
		this.prompt();
	}

	public String evaluateInput(String command) {
		String output = null;
		switch (this.lang) {
			case GROOVY: {

				Object val;

				try {
					String contextType = (String) this.ether.getContextType();
					String delegate = null;
					String delegatedCommand = command;
					if (contextType != null) {
						if ("request".equals(contextType) && null != this.ether.getRequest()) {
							delegate = "req";
						} else if ("response".equals(contextType) && null != this.ether.getResponse()) {
							delegate = "response";
						}

						if (null != delegate) {
							delegatedCommand = delegate + ".with{" + command + "}";
						}
					}

					val = GroovyResolver.getInstance()
							.eval(delegatedCommand, this.ether.getEnvironments()
									.get(this.ether.get_env()), this.ether.getDataSets(), this.ether.getVars(),
									this.ether.getCallChains(), this.ether.putAllFields());
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

					if (val instanceof Response) {
						this.ether.setResponse(ResponseWrapper.of((Response) val));
						this.ether.setLastRequest(((Response) val).getRequest());
					}

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
		if (null == json || json.length() > 5000) {
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

	private String prompt() {
		this.prompt = new AttributedStringBuilder().append("mosquito:")
				.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE))
				.style(AttributedStyle.BOLD)
				.append("groovy" + (this.ether.getContextType() != null ? "|" + this.ether.getContextType() : ""))
				.style(AttributedStyle.DEFAULT)
				.append("> ")
				.toAnsi();

		return this.prompt;
	}

	private LineReader getLineReader() {

		if (null != this.lineReader) {
			return this.lineReader;
		}

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
		p.setEofOnEscapedNewLine(true);
		p.setQuoteChars(new char[] { '\'', '"' });
		p.setEscapeChars(new char[] { '\\' });
		p.setEofOnUnclosedBracket(Bracket.CURLY, Bracket.ROUND, Bracket.SQUARE);

		parser = p;
		// stuff

		try {
			terminal = builder.build();
		} catch (IOException e1) {
			throw new MosquitoException(e1);
		}

		History history = new DefaultHistory();

		LineReader reader = LineReaderBuilder.builder()
				.history(history)
				.terminal(terminal)
				.completer(completer)
				.parser(parser)
				.variable(LineReader.HISTORY_FILE, new File(this.getConfigDirectory(), HISTORY_FILE_NAME))
				.variable(LineReader.SECONDARY_PROMPT_PATTERN, "> ")
				.variable(LineReader.INDENTATION, 2)
				.option(Option.INSERT_BRACKET, true)
				.build();

		this.lineReader = reader;

		return this.lineReader;
	}

	void error(String line, Terminal terminal, Ether ether) {
		if (null != this.lastException) {
			terminal.writer()
					.println(
							new AttributedStringBuilder().style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
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
	}

	void clearScreen(String line, Terminal terminal, Ether ether) {
		terminal.puts(Capability.clear_screen);
		terminal.flush();
	}

	void trace(String line, Terminal terminal, Ether ether) {
		if (null != this.lastException) {
			String ex = new AttributedStringBuilder().style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
					.append(ExceptionUtils.getStackTrace(this.lastException))
					.toAnsi();

			terminal.writer()
					.println(ex);
		} else {
			terminal.writer()
					.println("[Nothing to show]");
		}

		terminal.flush();
	}

	void driver(String line, Terminal terminal, Ether ether) {
		HttpDriverFactory factory = HttpDriverFactoryLocator.getInstance()
				.getSelectedFactory();

		terminal.writer()
				.println(new AttributedStringBuilder().style(AttributedStyle.DEFAULT)
						.append(StringUtils.rightPad("Driver name:", 15))
						.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA))
						.append(factory.getName()));
		terminal.writer()
				.println(new AttributedStringBuilder().style(AttributedStyle.DEFAULT)
						.append(StringUtils.rightPad("Provided by:", 15))
						.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA))
						.append(factory.getProvider()));
		terminal.writer()
				.println(new AttributedStringBuilder().style(AttributedStyle.DEFAULT)
						.append(StringUtils.rightPad("Description:", 15))
						.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA))
						.append(factory.getDescription()));
		terminal.writer()
				.println(new AttributedStringBuilder().style(AttributedStyle.DEFAULT)
						.append(StringUtils.rightPad("Features:", 15)));
		terminal.writer()
				.println(
						new AttributedStringBuilder().style(AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA))
								.append(factory.getFeaturesDescription()));
		terminal.flush();
	}

	void drivers(String line, Terminal terminal, Ether ether) {
		var services = HttpDriverFactoryLocator.getInstance()
				.listServices();

		services.forEach(factory -> {
			terminal.writer()
					.println(new AttributedStringBuilder()
							.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA))
							.append("\"" + factory.getName() + "\"")
							.style(AttributedStyle.DEFAULT)
							.append(StringUtils.rightPad(", provided by ", 15))
							.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA))
							.append("\"" + factory.getProvider() + "\""));
		});
		terminal.flush();
	}

	void setContextCommand(String line, Terminal terminal, Ether ether) {
		String[] parts = line.trim()
				.split(" +");
		if (parts.length < 2 || !(parts[1].equalsIgnoreCase("request") || parts[1].equalsIgnoreCase("response"))) {
			terminal.writer()
					.println("|>> Usage: set-context request|response");
			terminal.flush();
			return;
		}

		this.setContext(parts[1]);
	}

	void modeRequest(String line, Terminal terminal, Ether ether) {
		this.setContext("request");
	}

	void modeResponse(String line, Terminal terminal, Ether ether) {
		this.setContext("response");
	}

	void newRequest(String line, Terminal terminal, Ether ether) {
		String[] parts = line.trim()
				.split(" +");
		if (parts.length < 2) {
			terminal.writer()
					.println("|>> Usage: new-request <request-name>");
			terminal.flush();
		}

		this.newRequestWrapper(parts[1]);
	}

	void send(String command, Terminal terminal, Ether ether) {
		if (!this.ether.getContextType()
				.equals("request")) {
			terminal.writer()
					.println("|>> That can only be done in 'request' mode");
			terminal.flush();
			return;
		}

		String output = this.runCommand("http(req.request)");
		if (StringUtils.isNotBlank(output)) {
			terminal.writer()
					.println(output);
			terminal.flush();
		}
	}

	private void processNonBuiltInCommand(String line) {
		String output = this.runCommand(line);
		if (StringUtils.isNotBlank(output)) {
			terminal.writer()
					.println(output);
			terminal.flush();
		}
	}

	private void commandPrompt() throws Exception {

		LineReader reader = this.getLineReader();

		while (2 < 3) { // well, Sonarqube complained about while true, so here we go.

			final String line;
			try {
				line = StringUtils.trimToEmpty(reader.readLine(this.prompt == null ? this.prompt() : this.prompt, null,
						(MaskingCallback) null, null));
			} catch (UserInterruptException e) {
				// Ignore
				continue;
			} catch (EndOfFileException e) {
				quit();
				break;
			}

			BuiltInCommand.of(line)
					.or(() -> BuiltInCommand.match(line))
					.ifPresentOrElse(bic -> bic.getProcessor()
							.process(line, terminal, ether), () -> this.processNonBuiltInCommand(line));
		}
	}
}
