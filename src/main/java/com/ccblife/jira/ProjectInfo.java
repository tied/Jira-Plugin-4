package com.ccblife.jira;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
public class ProjectInfo {
	
	/**
	 * 获得项目编号
	 * @param projectId
	 * @return
	 */
	public String getProjectNo(String projectId)
	{
		Project project = ComponentAccessor.getProjectManager().getProjectObj(new Long(projectId));
		String projectName = project.getName();
		return StringUtils.substringBefore(projectName, "（");
	}
	
	/**
	 * 获得项目经理，如果有多个取第一个项目经理
	 * @param projectId
	 * @return
	 */
	public String getProjectManagerName(String projectId)
	{
		Set <ApplicationUser> userSetForProjectRole = getUserSetForProjectRole(projectId, "项目经理");
		String projectManagerName = "";
		for (ApplicationUser user : userSetForProjectRole)
		{
			projectManagerName = user.getDisplayName();
			break;
		}
		return projectManagerName;
	}
	/**
	 * 获得项目监理，如果有多个取第一个项目监理
	 * @param projectId
	 * @return
	 */
	
	public String getProjectSqaName(String projectId)
	{
		Set <ApplicationUser> userSetForProjectRole = getUserSetForProjectRole(projectId, "项目监理");
		String projectSqaName = "";
		for (ApplicationUser user : userSetForProjectRole)
		{
			projectSqaName = user.getDisplayName();
			break;
		}
		return projectSqaName;
	}
	
	/**
	 * 根据项目ID和项目角色名称获得项目角色用户名称
	 * @param projectId
	 * @param projectRoleName
	 * @return
	 */
	private Set<ApplicationUser> getUserSetForProjectRole(String projectId, String projectRoleName)
	{
		Project project = ComponentAccessor.getProjectManager().getProjectObj(new Long(projectId));
		ProjectRoleManager projectRoleManager = ComponentAccessor.getComponentOfType(ProjectRoleManager.class);
		ProjectRole projectRole = projectRoleManager.getProjectRole(projectRoleName);
		ProjectRoleActors projectRoleActors = projectRoleManager.getProjectRoleActors(projectRole, project);
		return projectRoleActors.getApplicationUsers();
	}
}
