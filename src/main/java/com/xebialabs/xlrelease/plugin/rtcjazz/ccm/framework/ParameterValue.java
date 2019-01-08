// Copyright (c) 2019 XebiaLabs
// 
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.xebialabs.xlrelease.plugin.rtcjazz.ccm.framework;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.process.common.IProjectAreaHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.workitem.common.model.IAttribute;

import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.util.AttributeUtil;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.util.StringUtil;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.parameter.ParameterIDMapper;

/**
 * Internal class to manage parameter data and allow easy access to the
 * parameter names, values and modes
 * 
 */
public class ParameterValue {
	// default mode different behaviour for simple and multi value types
	private String mode = MODE_DEFAULT;
	private String attributeID = null;
	private String value = null;
	private IAttribute theAttribute = null;
	private IProjectAreaHandle fProjectAreaHandle;
	private IProgressMonitor fMonitor;
	public static final String POSTFIX_PARAMETER_MANIPULATION_MODE = ":";
	public static final String MODE_REMOVE = "remove";
	public static final String MODE_ADD = "add";
	public static final String MODE_SET = "set";
	// Update Modes
	public static final String MODE_DEFAULT = "default";

	/**
	 * Constructor to create the value
	 * 
	 * @param parameter
	 * @param value
	 * @throws WorkItemException
	 */
	public ParameterValue(String parameter, String value,
			IProjectAreaHandle projectAreaHandle, IProgressMonitor monitor)
			throws WorkItemException {
		analyzeValue(parameter, value, projectAreaHandle, monitor);
	}

	/**
	 * @param parameter
	 * @param value
	 * @param projectAreaHandle
	 * @param monitor
	 * @throws WorkItemException
	 */
	private void analyzeValue(String parameter, String value,
			IProjectAreaHandle projectAreaHandle, IProgressMonitor monitor)
			throws WorkItemException {
		fProjectAreaHandle = projectAreaHandle;
		fMonitor = monitor;
		if (parameter == null) {
			throw new WorkItemException(
					"Parameter can not be null: ");
		}
		this.value = value;
		List<String> propertyData = StringUtil.splitStringToList(parameter,
				POSTFIX_PARAMETER_MANIPULATION_MODE);
		this.attributeID = ParameterIDMapper.getAlias(propertyData.get(0));
		if (propertyData.size() == 2) {
			String foundMode = propertyData.get(1);
			if (MODE_ADD.equals(foundMode) || MODE_REMOVE.equals(foundMode)
					|| MODE_SET.equals(foundMode)) {
				mode = foundMode;
			} else {
				throw new WorkItemException(
						"Parameter update mode not recognizeable: " + parameter
								+ " Value: " + value);
			}
		}
	}

	/**
	 * @return the attribute ID
	 */
	public String getAttributeID() {
		return this.attributeID;
	}

	/**
	 * @return the parameter value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @return the IAttribute needed to get and set values
	 * @throws TeamRepositoryException
	 */
	public IAttribute getIAttribute() throws TeamRepositoryException {
		if (this.theAttribute == null) 
		{
			this.theAttribute = AttributeUtil.resolveAttribute(this.attributeID, fProjectAreaHandle, fMonitor);
		}
		return this.theAttribute;
	}

	/**
	 * @return true if the parameter is provided in default mode
	 */
	public boolean isDefault() {
		return mode.equals(MODE_DEFAULT);
	}

	/**
	 * @return true if the parameter is provided in add mode
	 */
	public boolean isAdd() {
		return mode.equals(MODE_ADD);
	}

	/**
	 * @return true if the parameter is provided in remove mode
	 */
	public boolean isRemove() {
		return mode.equals(MODE_REMOVE);
	}

	/**
	 * @return true if the parameter is provided in set mode
	 */
	public boolean isSet() {
		return mode.equals(MODE_SET);
	}

	/**
	 * To override the value to be able to use if for encoded item types
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Get the display name. The parameter must be a real attribute to have a
	 * display name.
	 * 
	 * @return
	 * @throws TeamRepositoryException
	 */
	public String getDisplayName() throws TeamRepositoryException {
		IAttribute attribute = getIAttribute();
		if (attribute == null) {
			throw new WorkItemException("Attribute not found: "
					+ this.attributeID);
		}
		return getIAttribute().getDisplayName();
	}
}
