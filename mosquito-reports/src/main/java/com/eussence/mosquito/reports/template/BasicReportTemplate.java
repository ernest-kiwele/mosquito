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

package com.eussence.mosquito.reports.template;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.eussence.mosquito.api.execution.ExecutionResult;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

/**
 * 
 * @author Ernest Kiwele
 */
public class BasicReportTemplate {

	private static final BasicReportTemplate instance = new BasicReportTemplate();

	private String basicTemplate = null;

	public static BasicReportTemplate getInstance() {
		return instance;
	}

	public String getDefaultResultReport(ExecutionResult result) {
		try {
			String text = this.getBasicTemplate();

			Map<String, Object> binding = new HashMap<>(Map.of("result", result));

			SimpleTemplateEngine engine = new SimpleTemplateEngine();
			Writable template = engine.createTemplate(text).make(binding);

			return template.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String getBasicTemplate() throws IOException {

		this.basicTemplate = IOUtils.toString(new FileInputStream(
				"/home/ernest/development/workspaces/new-comprehensive/new-workspaces/mosquito/mosquito/mosquito-reports/src/main/resources/basic-report.html"));
		return this.basicTemplate;
	}
}
