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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	private static final ObjectMapper objectMapper = new ObjectMapper();

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
		if (!MediaType.APPLICATION_JSON.equals(this.mediaType)) {
			System.out.println("Trying to parse '" + this.mediaType + "' as JSON");
		}

		try {
			return objectMapper.readValue(this.bytes(), Object.class);
		} catch (IOException e) {
			throw new RuntimeException("Failed to make JSON from entity: " + e.getMessage(), e);
		}
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
					throw new RuntimeException(
							"Unable to get bytes from string entity using character set " + this.charSet, e);
				}
			} else {
				return ((String) entity).getBytes();
			}
		} else if (MediaType.APPLICATION_JSON.equals(this.mediaType)) {
			try {
				return objectMapper.writeValueAsBytes(this.entity);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Could not represent the entity as JSON bytes: " + e.getMessage(), e);
			}
		}

		throw new IllegalStateException("Failed to convert class " + this.entity.getClass()
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
					throw new RuntimeException(
							"Unable to get bytes from string entity using character set " + this.charSet, e);
				}
			} else {
				return new String((byte[]) entity);
			}
		} else if (MediaType.APPLICATION_JSON.equals(this.mediaType)) {
			try {
				return objectMapper.writeValueAsString(this.entity);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Could not represent the entity as JSON: " + e.getMessage(), e);
			}
		}

		throw new IllegalStateException("Failed to convert class " + this.entity.getClass()
				.getName() + " to String");
	}
}
