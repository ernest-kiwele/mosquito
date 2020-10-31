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

package com.eussence.mosquito.api;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import com.eussence.mosquito.api.qa.AssertionResult;

/**
 * A result of a test at various levels.
 * 
 * @author Ernest Kiwele
 */
public interface Result {

	boolean isSuccessful();

	List<AssertionResult> getAssertionResults();

	Map<String, Result> getChildResults();

	Instant getStartDate();

	Instant getEndDate();

	default long getDuration() {
		if (null == this.getStartDate() || null == this.getEndDate()) {
			return -1;
		}

		return ChronoUnit.MILLIS.between(this.getStartDate(), this.getEndDate());
	}
}
