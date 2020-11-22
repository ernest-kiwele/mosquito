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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import com.eussence.mosquito.api.exception.MosquitoException;
import com.eussence.mosquito.api.utils.JsonMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A request body.
 * 
 * @author Ernest Kiwele
 */
@Builder
@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
public class Body {

	private Object entity;
	@Builder.Default
	private String mediaType = MediaType.APPLICATION_JSON;
	private String charSet;

	@Builder.Default
	private boolean multipart = false;
	@Builder.Default
	private List<BodyPart> parts = new ArrayList<>();

	public String textEntity() {
		if (entity instanceof String)
			return (String) entity;

		return Optional.ofNullable(entity)
				.map(Object::toString)
				.orElseGet(() -> "");
	}

	public boolean isString() {
		return entity instanceof String;
	}

	public Object jsonEntity() {
		return JsonMapper.fromJson(this.bytes(), Object.class);
	}

	public byte[] bytes() {
		if (null == this.entity) {
			return new byte[0];
		} else if (entity instanceof byte[]) {
			return (byte[]) entity;
		} else if (entity instanceof String) {
			if (StringUtils.isNotBlank(charSet)) {
				try {
					return ((String) entity).getBytes(this.charSet);
				} catch (UnsupportedEncodingException e) {
					throw new MosquitoException(
							"Unable to get bytes from string entity using character set " + this.charSet, e);
				}
			} else {
				return ((String) entity).getBytes();
			}
		} else if (MediaType.APPLICATION_JSON.equals(this.mediaType)) {
			return JsonMapper.jsonBytes(this.entity);
		}

		throw new MosquitoException("Failed to convert class " + this.entity.getClass()
				.getName() + " to byte array");
	}

	public String string() {
		if (null == this.entity) {
			return "";
		} else if (entity instanceof String) {
			return (String) entity;
		} else if (entity instanceof byte[]) {
			if (StringUtils.isNotBlank(charSet)) {
				try {
					return new String((byte[]) entity, this.charSet);
				} catch (UnsupportedEncodingException e) {
					throw new MosquitoException(
							"Unable to get bytes from string entity using character set " + this.charSet, e);
				}
			} else {
				return new String((byte[]) entity);
			}
		} else if (MediaType.APPLICATION_JSON.equals(this.mediaType)) {
			return JsonMapper.json(this.entity);
		}

		throw new MosquitoException("Failed to convert class " + this.entity.getClass()
				.getName() + " to String");
	}

	public Body part(BodyPart part) {
		this.parts.add(Objects.requireNonNull(part));
		return this;
	}
}
