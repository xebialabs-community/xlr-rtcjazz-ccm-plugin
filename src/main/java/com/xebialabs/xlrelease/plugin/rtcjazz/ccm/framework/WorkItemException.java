// Copyright (c) 2019 XebiaLabs
// 
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.xebialabs.xlrelease.plugin.rtcjazz.ccm.framework;

/**
 * Exception used internally to wrap issues to throw and process them.
 * 
 */
public class WorkItemException extends RuntimeException {

	Throwable ex = null;

	/**
	 * Base constructor
	 */
	public WorkItemException() {
		super();
	}

	/**
	 * Just throw with a simple message.
	 * 
	 * @param message
	 */
	public WorkItemException(String message) {
		super(message);
	}

	/**
	 * Just throw another throwable
	 * 
	 * @param throwable
	 */
	public WorkItemException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * Constructor to warp an exception into another one and provide both info.
	 * 
	 * @param message
	 * @param throwable
	 */
	public WorkItemException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7933361626497401499L;

}
