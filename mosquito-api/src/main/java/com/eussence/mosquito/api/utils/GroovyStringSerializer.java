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

package com.eussence.mosquito.api.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import groovy.lang.GString;

/**
 * A GString serializer is needed as the default Jackson serialization is coming
 * up with funnies (treating Gstrings as objects rather than strings).
 * 
 * @author Ernest Kiwele
 */
public class GroovyStringSerializer extends StdSerializer<GString> {

	private static final long serialVersionUID = 1L;

	public GroovyStringSerializer() {
		super(GString.class);
	}

	@Override
	public void serialize(GString value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(value.toString());
	}
}
