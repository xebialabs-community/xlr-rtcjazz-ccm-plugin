// Copyright (c) 2019 XebiaLabs
// 
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.xebialabs.xlrelease.plugin.rtcjazz.ccm.framework;

import com.ibm.team.links.common.IReference;
import com.ibm.team.links.common.registry.IEndPointDescriptor;

/**
 * Class to manage references and pass them through the hierarchy
 * 
 */
public class ReferenceData {

	private IEndPointDescriptor endpoint = null;
	private IReference reference = null;

	/**
	 * Create a reference
	 * 
	 * @param endPoint
	 *            the endpoint to be used
	 * @param reference
	 *            the reference to the item
	 */
	public ReferenceData(IEndPointDescriptor endPoint, IReference reference) {
		this.endpoint = endPoint;
		this.reference = reference;
	}

	/**
	 * @return the endpoint descriptor
	 */
	public IEndPointDescriptor getEndPointDescriptor() {
		return this.endpoint;
	}

	/**
	 * @return the reference
	 */
	public IReference getReference() {
		return this.reference;
	}
}
