// Copyright (c) 2019 XebiaLabs
// 
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.xebialabs.xlrelease.plugin.rtcjazz.ccm.command;

import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.advice.TeamOperationCanceledException;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemHandle;
import com.ibm.team.workitem.common.model.IWorkItemType;

import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.framework.AbstractWorkItemModificationCommand;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.framework.WorkItemException;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.helper.WorkItemTypeHelper;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.parameter.ParameterManager;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.util.ProcessAreaUtil;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.util.WorkItemUtil;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.IWorkItemConstants;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.OperationResult;

/**
 * Command to create a work item, set the provided values and save it. The
 * operation is governed by the process and might fail if required parameters
 * are missing.
 * 
 */
public class CreateWorkItemCommand extends AbstractWorkItemModificationCommand {

	/**
	 * @param parametermanager
	 */
	public CreateWorkItemCommand(ParameterManager parametermanager) {
		super(parametermanager);
	}

	@Override
	public String getCommandName() {
		return IWorkItemConstants.COMMAND_CREATE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.js.team.workitem.commandline.framework.
	 * AbstractWorkItemCommandLineCommand#setRequiredParameters()
	 */
	public void setRequiredParameters() {
		super.setRequiredParameters();
		// Add the parameters required to perform the operation
		// getParameterManager().syntaxCommand()
		getParameterManager().syntaxAddRequiredParameter(
						IWorkItemConstants.PARAMETER_PROJECT_AREA_NAME_PROPERTY,
						IWorkItemConstants.PARAMETER_PROJECT_AREA_NAME_PROPERTY_EXAMPLE);
		getParameterManager().syntaxAddRequiredParameter(
						IWorkItemConstants.PARAMETER_WORKITEM_TYPE_PROPERTY,
						IWorkItemConstants.PARAMETER_WORKITEM_TYPE_PROPERTY_EXAMPLE);
		getParameterManager().syntaxAddSwitch(
				IWorkItemConstants.SWITCH_IGNOREERRORS);
		getParameterManager().syntaxAddSwitch(
				IWorkItemConstants.SWITCH_ENABLE_DELETE_ATTACHMENTS);
		getParameterManager().syntaxAddSwitch(
				IWorkItemConstants.SWITCH_ENABLE_DELETE_APPROVALS);
		getParameterManager().syntaxAddSwitch(
				IWorkItemConstants.SWITCH_ENFORCE_SIZE_LIMITS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.js.team.workitem.commandline.framework.
	 * AbstractWorkItemCommandLineCommand#process()
	 */
	@Override
	public OperationResult process() throws TeamRepositoryException {
		// Get the parameters such as project area name and Attribute Type and
		// run the operation
		String projectAreaName = getParameterManager()
				.consumeParameter(
						IWorkItemConstants.PARAMETER_PROJECT_AREA_NAME_PROPERTY)
				.trim();
		// Find the project area
		IProjectArea projectArea = ProcessAreaUtil.findProjectAreaByFQN(
				projectAreaName, getProcessClientService(), getMonitor());
		if (projectArea == null) {
			throw new WorkItemException("Project Area not found: "
					+ projectAreaName);
		}

		String workItemTypeID = getParameterManager().consumeParameter(
				IWorkItemConstants.PARAMETER_WORKITEM_TYPE_PROPERTY)
				.trim();
		// Find the work item type
		IWorkItemType workItemType = WorkItemTypeHelper.findWorkItemType(
				workItemTypeID, projectArea.getProjectArea(),
				getWorkItemCommon(), getMonitor());
		// Create the work item
		createWorkItem(workItemType);
		return this.getResult();
	}

	/**
	 * Create the work item and set the required attribute values.
	 * 
	 * @param workItemType
	 * @return
	 * @throws TeamRepositoryException
	 */
	private boolean createWorkItem(IWorkItemType workItemType)
			throws TeamRepositoryException {

		ModifyWorkItem operation = new ModifyWorkItem("Creating Work Item");
		this.setIgnoreErrors(getParameterManager().hasSwitch(
			IWorkItemConstants.SWITCH_IGNOREERRORS));
		this.setSuppressMailNotification(getParameterManager().hasSwitch(
			IWorkItemConstants.SWITCH_SUPPRESS_MAIL_NOTIFICATION));

        IWorkItemHandle handle;
		try 
		{
			handle = operation.run(workItemType, getMonitor());
		} 
		catch (TeamOperationCanceledException e) 
		{
			throw new WorkItemException("Work item not created. " + e.getMessage(), e);
		}

		if (handle == null) 
		{
			throw new WorkItemException("Work item not created, cause unknown.");
		} 
		else 
		{
			IWorkItem workItem = WorkItemUtil.resolveWorkItem(handle, IWorkItem.SMALL_PROFILE, getWorkItemCommon(), getMonitor());
			this.appendResultString("ID:[" + workItem.getId() + "]");
			this.setSuccess();
		}

        return true;
	}

	@Override
	public String helpSpecificUsage() {
		return "{parameter[:mode]=value}";
	}
}