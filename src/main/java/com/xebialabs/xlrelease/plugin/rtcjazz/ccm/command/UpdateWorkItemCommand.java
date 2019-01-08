// Copyright (c) 2019 XebiaLabs
// 
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.xebialabs.xlrelease.plugin.rtcjazz.ccm.command;

import com.ibm.team.process.common.advice.TeamOperationCanceledException;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.model.IWorkItemType;

import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.framework.AbstractWorkItemModificationCommand;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.framework.WorkItemException;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.helper.WorkItemTypeHelper;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.parameter.ParameterManager;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.util.WorkItemUtil;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.IWorkItemConstants;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.OperationResult;

/**
 * Command to update a work item, set the provided values and save it. The
 * operation is governed by the process and might fail if required parameters
 * are missing.
 * 
 */
public class UpdateWorkItemCommand extends AbstractWorkItemModificationCommand {

	/**
	 * @param parametermanager
	 */
	public UpdateWorkItemCommand(ParameterManager parametermanager) {
		super(parametermanager);
	}

	@Override
	public String getCommandName() {
		return IWorkItemConstants.COMMAND_UPDATE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.js.team.workitem.commandline.framework.AbstractWorkItemCommand
	 * #setRequiredParameters()
	 */
	public void setRequiredParameters() {
		super.setRequiredParameters();
		// Add the parameters required to perform the operation
		getParameterManager()
				.syntaxAddRequiredParameter(
						IWorkItemConstants.PARAMETER_WORKITEM_ID_PROPERTY,
						IWorkItemConstants.PROPERTY_WORKITEM_ID_PROPERTY_EXAMPLE);
		getParameterManager().syntaxAddSwitch(
				IWorkItemConstants.SWITCH_IGNOREERRORS);
		getParameterManager().syntaxAddSwitch(
				IWorkItemConstants.SWITCH_ENABLE_DELETE_ATTACHMENTS);
		getParameterManager().syntaxAddSwitch(
				IWorkItemConstants.SWITCH_ENABLE_DELETE_APPROVALS);
		getParameterManager().syntaxAddSwitch(
				IWorkItemConstants.SWITCH_ENFORCE_SIZE_LIMITS);
        getParameterManager().syntaxAddSwitch(
                IWorkItemConstants.SWITCH_SUPPRESS_MAIL_NOTIFICATION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.js.team.workitem.commandline.framework.AbstractWorkItemCommand
	 * #process()
	 */
	@Override
	public OperationResult process() throws TeamRepositoryException {
		// Get the parameters such as the work item ID and run the operation
		String wiID = getParameterManager().consumeParameter(
				IWorkItemConstants.PARAMETER_WORKITEM_ID_PROPERTY);
		IWorkItem workItem = WorkItemUtil.findWorkItemByID(wiID,
				IWorkItem.SMALL_PROFILE, getWorkItemCommon(), getMonitor());
		if (workItem == null) {
			throw new WorkItemException(
					"Work item cannot be found ID: " + wiID);
		}
		// Update the work item
		updateWorkItem(workItem);
		return this.getResult();
	}

	/**
	 * Run the update work item operation
	 * 
	 * @param workItem
	 * @return
	 * @throws TeamRepositoryException
	 */
	private boolean updateWorkItem(IWorkItem workItem)
			throws TeamRepositoryException {
		this.setIgnoreErrors(getParameterManager().hasSwitch(
				IWorkItemConstants.SWITCH_IGNOREERRORS));
		this.setSuppressMailNotification(getParameterManager().hasSwitch(
				IWorkItemConstants.SWITCH_SUPPRESS_MAIL_NOTIFICATION));

		ModifyWorkItem operation = new ModifyWorkItem("Updating work Item",
				IWorkItem.FULL_PROFILE);
		try {
			this.appendResultString("Updating work item " + workItem.getId()
					+ ".");
			String workItemTypeID = getParameterManager().consumeParameter(
					IWorkItem.TYPE_PROPERTY);
			if (workItemTypeID != null) {
				IWorkItemType newType = WorkItemTypeHelper
						.findWorkItemTypeByIDAndDisplayName(workItemTypeID,
								workItem.getProjectArea(), getWorkItemCommon(),
								getMonitor());
				if (newType == null) {
					// If we have no type we can't create the work item
					throw new WorkItemException("Work item type "
							+ workItemTypeID + " not found in project area. ");
				}
				IWorkItemType oldType = WorkItemTypeHelper.findWorkItemType(
						workItem.getWorkItemType(), workItem.getProjectArea(),
						getWorkItemCommon(), getMonitor());
				ChangeType changeTypeOperation = new ChangeType(
						"Changing work item type", oldType, newType);
				changeTypeOperation.run(workItem, getMonitor());
			}
			operation.run(workItem, getMonitor());
			this.setSuccess();
			this.appendResultString("ID:[" + workItem.getId() + "]");
		} catch (TeamOperationCanceledException e) {
			throw new WorkItemException("Work item not updated. "
					+ e.getMessage(), e);
		}
		return true;
	}

	@Override
	public String helpSpecificUsage() {
		return "{parameter[:mode]=value}";
	}
}