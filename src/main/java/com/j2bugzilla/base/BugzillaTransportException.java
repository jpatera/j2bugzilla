/*
 * Copyright 2018 Jan Patera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.j2bugzilla.base;

/**
 * <code>BugzillaTransportException</code> is thrown if the exception received from XML-RPC request is of type XmlRpcTransportException.
 * It can have many reasons (authentication, timeout, wrong URL, ...), for the concrete XML-RPC transport error
 * processing see {@link XmlExceptionHandler#handleFault(org.apache.xmlrpc.XmlRpcException)} .
 * <P>
 * This exception always indicates that that further subsequent Bugzilla remote procedure calls will fail.
 * <P>
 * BugzillaTransportException extends BugzillaException. It would be better for both to implement a common interface instead,
 * but I did not want to change the current j2bugzilla API so implemented it this way.
 * <P>
 * BugzillaTransportException will always be a wrapper for a nested <code>Exception</code> which
 * indicates the cause of the error.
 * @author Jan Patera
 *
 */
public class BugzillaTransportException extends BugzillaException {

	private int status;

	private static final long serialVersionUID = 3474040741426882110L;

	/**
	 * Public constructor which calls super()
	 * @param message A custom error message describing the issue
	 * @param cause The root cause of the exception
	 */
	public BugzillaTransportException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@link BugzillaTransportException} with the specified summary and cause.
	 * @param message A customized error message describing the issue
	 * @param status The error code of the HTTP request.
	 * @param cause The nested cause, typically a {@link org.apache.xmlrpc.XmlRpcException XmlRpcException}.
	 */
	public BugzillaTransportException(String message, int status, Throwable cause) {
		this(message, cause);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
