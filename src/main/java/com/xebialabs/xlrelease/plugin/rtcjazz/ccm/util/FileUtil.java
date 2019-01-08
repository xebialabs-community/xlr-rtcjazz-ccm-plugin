// Copyright (c) 2019 XebiaLabs
// 
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.xebialabs.xlrelease.plugin.rtcjazz.ccm.util;

import java.io.File;

/**
 * Tool for file access
 * 
 * @see com.ibm.team.filesystem.rcp.core.internal.compare.ExternalCompareToolsUtil
 * @see com.ibm.team.filesystem.setup.junit.internal.SourceControlContribution
 * 
 * 
 */
public class FileUtil {

	/**
	 * Create a folder
	 * 
	 * @param aFolder
	 */
	public static void createFolderWithParents(File aFolder) {
		if (!aFolder.exists()) {
			aFolder.mkdirs();
		}
	}

	/**
	 * Create a folder
	 * 
	 * @param aFolder
	 */
	public static void createFolderWithParents(String folderName) {
		File aFolder = new File(folderName);
		if (!aFolder.exists()) {
			aFolder.mkdirs();
		}
	}
}
