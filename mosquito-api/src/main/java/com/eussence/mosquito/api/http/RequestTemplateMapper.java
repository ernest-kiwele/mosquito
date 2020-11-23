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

package com.eussence.mosquito.api.http;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.eussence.mosquito.api.MapObject;
import com.eussence.mosquito.api.command.CommandLanguage;
import com.eussence.mosquito.api.command.Resolver;
import com.eussence.mosquito.api.utils.Templates;

/**
 * <p>
 * A singleton for mapping request templates to requests, holding
 * factory/mapping objects.
 * </p>
 * 
 * <p>
 * Before an instance of this class is usable, at least the
 * <code>resolverFactory</code> must be set. That instance of
 * <code>java.util.function.Function&lt;CommandLanguage, Resolver&gt;</code> is
 * needed to process template expressions into request attributes. This can be
 * done using the {@link RequestTemplateMapper#setup(Function, Supplier)
 * setup()} method
 * </p>
 * 
 * @author Ernest Kiwele
 */
public class RequestTemplateMapper {

	private static final RequestTemplateMapper instance = new RequestTemplateMapper();

	private RequestTemplateMapper() {
	}

	/**
	 * Get the singleton object of this class.
	 * 
	 * @return A thread-safe instance of {@link RequestTemplateMapper
	 *         RequestTemplateMapper}.
	 */
	public static RequestTemplateMapper instance() {
		return instance;
	}

	private Function<CommandLanguage, Resolver> resolverFactory;
	private Supplier<MapObject> contextSupplier;

	/**
	 * Add a resolver factory that will provide a {@link Resolver resolver} to
	 * process template expressions. If available, a default context can be set,
	 * which will be used across all template executions (in a potentially unsafe
	 * way).
	 * 
	 * @param resolverFactory A factory that will return a {@link Resolver resolver}
	 *                        for the language used in the current template.
	 * @param contextSupplier A supplier that will return the default context if one
	 *                        is available to be used by default across templates.
	 */
	public void setup(Function<CommandLanguage, Resolver> resolverFactory, Supplier<MapObject> contextSupplier) {
		this.resolverFactory = Objects.requireNonNull(resolverFactory);
		this.contextSupplier = Objects.requireNonNull(contextSupplier);
	}

	/**
	 * Process the template and return the corresponding/resulting request object.
	 * 
	 * @param template The template to process
	 * @return A {@link Request request} object resulting from the execution of the
	 *         specified template.
	 */
	public Request toRequest(RequestTemplate template) {
		return this.toRequest(template, Objects.requireNonNull(this.resolverFactory, "No default resolver factory set"),
				Objects.requireNonNull(this.contextSupplier, "No default context available"));
	}

	/**
	 * Process the template and return the resulting request.
	 * 
	 * @param template         The template to process
	 * @param resolverFactory  The factory that will provide the {@link Resolver
	 *                         resolver} to process expressions in the language of
	 *                         this template.
	 * @param contextSuppliert A supplier for the template processing context.
	 * @return A request resulting from the execution of this template.
	 */
	public Request toRequest(RequestTemplate template, Function<CommandLanguage, Resolver> resolverFactory,
			Supplier<MapObject> contextSuppliert) {
		Resolver r = Objects.requireNonNull(resolverFactory.apply(template.getLang()),
				"Resolver factory returned null for language: " + template.getLang());
		var requestBuilder = Request.builder();
		requestBuilder.uri(Templates
				.castString(r.eval(contextSupplier.get(), Templates.multilineQuote(template.getUriTemplate()))));
		requestBuilder.headers(template.getHeaderTemplates()
				.entrySet()
				.stream()
				.map(e -> Map.entry(e.getKey(),
						StringUtils.contains(e.getValue(), "$")
								? Templates.castString(
										r.eval(contextSupplier.get(), Templates.multilineQuote(e.getValue())))
								: e.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
		requestBuilder.parameters(template.getParameterTemplates()
				.entrySet()
				.stream()
				.map(e -> Map.entry(e.getKey(),
						StringUtils.contains(e.getValue(), "$")
								? Templates.castString(
										r.eval(contextSupplier.get(), Templates.multilineQuote(e.getValue())))
								: e.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
		requestBuilder.method(template.getMethod());
		if (template.getMethod()
				.isBodied()) {
			Body.BodyBuilder bodyBuilder = Body.builder()
					.multipart(template.isMultipart());
			if (template.isMultipart()) {
				if (null != template.getPartFiles()) {
					bodyBuilder.parts(template.getPartFiles()
							.stream()
							.map(BodyPart::fromFile)
							.collect(Collectors.toList()));
				}
			} else {
				bodyBuilder.entity(r.eval(contextSupplier.get(), template.getEntityTemplate()))
						.mediaType(template.getMediaType());
			}
			requestBuilder.body(bodyBuilder.build());
		}

		requestBuilder.authType(template.getAuthType());
		if (StringUtils.isNotBlank(template.getAuthCredentialsTemplate())) {
			requestBuilder.authData(AuthData.builder()
					.credentials(((String) r.eval(contextSupplier.get(), template.getAuthCredentialsTemplate()))
							.toCharArray())
					.headerName(template.getAuthHeaderName())
					.build());
		}

		requestBuilder.dataSet(template.getDataSet());

		return requestBuilder.build();
	}
}
