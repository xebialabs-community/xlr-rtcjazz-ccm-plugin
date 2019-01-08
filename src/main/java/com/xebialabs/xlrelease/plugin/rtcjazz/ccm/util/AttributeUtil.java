// Copyright (c) 2019 XebiaLabs
// 
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.xebialabs.xlrelease.plugin.rtcjazz.ccm.util;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.IWorkItemCommon;
import com.ibm.team.workitem.common.model.IAttribute;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.framework.WorkItemException;

/**
 * Utility class to collect attribute related API
 * 
 */
public class AttributeUtil {

	/**
	 * Find the attribute type (IAttribute) from the process
	 * 
	 * @param attributeID
	 *            - the ID of the attribute we are working at
	 * @param projectArea
	 *            - must not be null
	 * @param monitor
	 *            - an IProgressMonitor or null
	 * @return the IAttribute found or null, in case it can not be found
	 * @throws TeamRepositoryException
	 */
	/**
	 * @param attributeID
	 * @param teamRepository
	 * @param projectAreaHandle
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IAttribute resolveAttribute(String attributeID,
			IProjectAreaHandle projectAreaHandle, IProgressMonitor monitor)
			throws TeamRepositoryException {
		if (projectAreaHandle == null) {
			throw new WorkItemException(
					"Resolve Attribute: project area handle must not be null");
		}
		ITeamRepository teamRepository = (ITeamRepository) projectAreaHandle
				.getOrigin();
		IWorkItemCommon workItemCommon = (IWorkItemCommon) teamRepository
				.getClientLibrary(IWorkItemCommon.class);
		return workItemCommon.findAttribute(projectAreaHandle, attributeID,
				monitor);
	}
}
