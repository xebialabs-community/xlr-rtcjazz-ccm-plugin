// Copyright (c) 2019 XebiaLabs
// 
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.xebialabs.xlrelease.plugin.rtcjazz.ccm;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Map;

import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.command.CreateWorkItemCommand;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.command.UpdateWorkItemCommand;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.parameter.ParameterList;
import com.xebialabs.xlrelease.plugin.rtcjazz.ccm.parameter.ParameterManager;

/*
 * Represents an RTC Repository.  Work item actions are implemented here.
 */

public class RtcRepository 
{
    private String serverUrl;
	private String username;
    private String password;

    public RtcRepository(String serverUrl, String username, String password)
    {
        this.serverUrl = serverUrl;
        this.username = username;
		this.password = password;
    }

    public boolean checkConnection()
    {
		IProgressMonitor monitor = new SysoutProgressMonitor();
		TeamPlatform.startup();
        try 
        {     
            login(monitor);
        } 
        catch (TeamRepositoryException ex)
        {
            return false;
        }
        finally 
        {
			TeamPlatform.shutdown();
        }
        return true;
    }

	public String createWorkItem(String projectArea, String type, Map<String,String> parameters) throws TeamRepositoryException 
	{
		// create list for parameters and populate
		ParameterList arguments = new ParameterList();

		arguments.addParameterValue(IWorkItemConstants.PARAMETER_PROJECT_AREA_NAME_PROPERTY, projectArea);
		arguments.addParameterValue(IWorkItemConstants.PARAMETER_WORKITEM_TYPE_PROPERTY, type);
		arguments.addParameterValue(IWorkItemConstants.PARAMETER_REPOSITORY_URL_PROPERTY, this.serverUrl);
		arguments.addParameterValue(IWorkItemConstants.PARAMETER_USER_ID_PROPERTY, this.username);
		arguments.addParameterValue(IWorkItemConstants.PARAMETER_PASSWORD_PROPERTY, this.password);

		for ( String key : parameters.keySet() )
		{
			arguments.addParameterValue(key, parameters.get(key));
		}
	
        CreateWorkItemCommand cmd = new CreateWorkItemCommand(new ParameterManager(arguments));
		OperationResult result = cmd.process();

		return result.getResultString();
	}

	public String updateWorkItem(String projectArea, String workitemID, Map<String,String> parameters) throws TeamRepositoryException 
	{
		// create list for parameters and populate
		ParameterList arguments = new ParameterList();

		arguments.addParameterValue(IWorkItemConstants.PARAMETER_PROJECT_AREA_NAME_PROPERTY, projectArea);
		arguments.addParameterValue(IWorkItemConstants.PARAMETER_WORKITEM_ID_PROPERTY, workitemID);
		arguments.addParameterValue(IWorkItemConstants.PARAMETER_REPOSITORY_URL_PROPERTY, this.serverUrl);
		arguments.addParameterValue(IWorkItemConstants.PARAMETER_USER_ID_PROPERTY, this.username);
		arguments.addParameterValue(IWorkItemConstants.PARAMETER_PASSWORD_PROPERTY, this.password);

		for ( String key : parameters.keySet() )
		{
			arguments.addParameterValue(key, parameters.get(key));
		}
	
        UpdateWorkItemCommand cmd = new UpdateWorkItemCommand(new ParameterManager(arguments));
		OperationResult result = cmd.process();

		return result.getResultString();
	}

    private ITeamRepository login(IProgressMonitor monitor) throws TeamRepositoryException 
    {
		ITeamRepository repository = TeamPlatform.getTeamRepositoryService().getTeamRepository(this.serverUrl);

        repository.registerLoginHandler(new ITeamRepository.ILoginHandler() 
        {
            public ILoginInfo challenge(ITeamRepository repository) 
            {
                return new ILoginInfo() 
                {
                    public String getUserId() 
                    {
						return username;
					}
                    public String getPassword() 
                    {
						return password;                        
					}
				};
			}
        });
        
		monitor.subTask("Contacting " + repository.getRepositoryURI() + "...");
		repository.login(monitor);
		monitor.subTask("Connected");
		return repository;
	}
}
