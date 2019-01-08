// Copyright (c) 2019 XebiaLabs
// 
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.xebialabs.xlrelease.plugin.rtcjazz.ccm.framework;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.repository.common.TeamRepositoryException;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.OperationResult;

/**
 * Interface to be implemented by all commands to be added to the existing list
 * of commands.
 * 
 */
public interface IWorkItemCommand {

	/**
	 * @return the name of the command that is supported by this class
	 */
	public String getCommandName();

	/**
	 * To initialize a parameter manager.
	 * 
	 * @param params
	 */
	public void initialize();

	/**
	 * Execute an operation. Passes the required interfaces and values.
	 * 
	 * @param monitor
	 *            a progress monitor, may be null.
	 * @return
	 * @throws TeamRepositoryException
	 */
	public OperationResult execute(IProgressMonitor monitor)
			throws TeamRepositoryException;

	/**
	 * Used to print user help on the command.
	 * 
	 * @return a string explaining how the command is used.
	 */
	public String helpUsage();

	/**
	 * Validate the required parameters are supplied
	 */
	public void validateRequiredParameters();
}
