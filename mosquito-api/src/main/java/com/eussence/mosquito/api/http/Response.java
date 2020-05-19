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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An object holding the result of an HTTP call.
 * 
 * @author Ernest Kiwele
 */
public class Response {

	private int status;

	private String statusReason;

	private int length;

	private Body body;

	private String uri;

	private Map<String, String> headers;

	private Map<String, String> cookies;

	private boolean failed;

	private long duration;

	private String errorMessage;

	private Throwable exception;

	public Response(int status, String statusReason, int contentLength, Body rawPayload, String uri,
			Map<String, String> headers, Map<String, String> cookies, long duration) {
		this.status = status;
		this.statusReason = statusReason;
		this.length = contentLength;
		this.body = rawPayload;
		this.uri = uri;
		this.headers = Collections.unmodifiableMap(headers);
		this.cookies = Collections.unmodifiableMap(cookies);

		this.failed = false;
		this.exception = null;
		this.duration = duration;
	}

	public Response(Throwable exception) {
		this.status = 0;
		this.statusReason = null;
		this.length = -1;
		this.body = null;
		this.uri = null;
		this.headers = null;
		this.cookies = null;

		this.failed = true;
		this.exception = exception;
		this.errorMessage = exception.getMessage();
	}

	/**
	 * Get the value of errorMessage
	 * 
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Set the value of errorMessage
	 * 
	 * @param errorMessage
	 *            the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Get the value of status
	 * 
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Get the value of statusReason
	 * 
	 * @return the statusReason
	 */
	public String getStatusReason() {
		return statusReason;
	}

	/**
	 * Get the value of rawPayload
	 * 
	 * @return the rawPayload
	 */
	public Body getBody() {
		return body;
	}

	/**
	 * Get the value of uri
	 * 
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Get the value of headers
	 * 
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * Get the value of length
	 * 
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Get the value of failed
	 * 
	 * @return the failed
	 */
	public boolean isFailed() {
		return failed;
	}

	/**
	 * Get the value of exception
	 * 
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * Get the value of cookies
	 * 
	 * @return the cookies
	 */
	public Map<String, String> getCookies() {
		return cookies;
	}

	/**
	 * Get the value of duration
	 * 
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Set the value of duration
	 * 
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	public Object jsonEntity() {
		return this.body.jsonEntity();
	}

	public static class Builder {

		private int status;

		private String statusReason;

		private int length;

		private Body body;

		private String uri;

		private Map<String, String> headers;

		private Map<String, String> cookies;
		private long duration;

		public Response build() {
			this.defaults();
			return new Response(status, statusReason, length, body, uri, headers, cookies, duration);
		}

		private void defaults() {
			if (status == 0) {
				this.status = -1;
			}

			if (null == headers) {
				this.headers = new HashMap<>();
			}

			if (null == cookies) {
				this.cookies = new HashMap<>();
			}
		}

		public static Response exception(Throwable t) {
			return new Response(t);
		}

		public Builder headers(Map<String, String> headers) {
			this.headers = headers;
			return this;
		}

		public Builder cookies(Map<String, String> cookies) {
			this.cookies = cookies;

			return this;
		}

		public Builder uri(String uri) {
			this.uri = uri;
			return this;
		}

		public Builder body(Body pl) {
			this.body = pl;
			return this;
		}

		public Builder status(int status) {
			this.status = status;
			return this;
		}

		public Builder statusReason(String statusReason) {
			this.statusReason = statusReason;
			return this;
		}

		public Builder length(int length) {
			this.length = length;

			return this;
		}

		public Builder duration(long duration) {
			this.duration = duration;

			return this;
		}
	}
}
