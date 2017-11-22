package com.ccblife.report;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.web.bean.StatisticMapWrapper;
import com.atlassian.query.Query;
import com.ccblife.jira.IssueInfo;
import com.ccblife.jira.ProjectInfo;
import com.ccblife.metric.Change;
import com.ccblife.metric.CheckList;
import com.ccblife.metric.Defect;
import com.ccblife.metric.Risk;

import webwork.action.ActionContext;

public class ProjectReport extends AbstractReport {

	@Override
	public String generateReportHtml(ProjectActionSupport projectActionSupport, Map map) throws Exception{
		Map<String, Object> velocityParams = generateVelocityParams(projectActionSupport, map);
		return descriptor.getHtml("view", velocityParams);
	}
	
	@Override
	public String generateReportExcel(ProjectActionSupport projectActionSupport, Map map) throws Exception {
		Map<String, Object> velocityParams = generateVelocityParams(projectActionSupport, map);
		return this.descriptor.getHtml("excel", velocityParams);
	}

	private Map<String, Object> generateVelocityParams(ProjectActionSupport projectActionSupport, Map map) throws SearchException {
		
		// 获得项目ID
		String projectId = ParameterUtils.getStringParam(map, "selectedProjectId");
		// 获得生产缺陷件数
		Long prdDefect = ParameterUtils.getLongParam(map, "prdDefect");
		// 获得开发工作量
		Double devEffort = ParameterUtils.getDoubleParam(map, "devEffort");
		// 获得测试工作量
		Double qaEffort = ParameterUtils.getDoubleParam(map, "qaEffort");
		// 获得项目状态报告截止日期
		Date endDate = ParameterUtils.getDateParam(map, "endDate",
				ComponentAccessor.getJiraAuthenticationContext().getLocale());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String strEndDate = formatter.format(endDate);

		System.out.println("PRD Defect: " + prdDefect);
		System.out.println("DEV Effort: " + devEffort);
		System.out.println("QA Effort: " + qaEffort);
		System.out.println("End Date: " + strEndDate);
		System.out.println("Project ID: " + projectId);

		String sitJql = "project = " + projectId
				+ " AND issuetype = Bug AND 缺陷发现阶段 in (PRD, SIT, UAT) AND (缺陷原因分类 not in (152-已知历史问题, 210-集成测试问题, 220-用户测试问题, 310-部署发布问题, 320-环境问题, 410-需求规格不佳, 420-开发规范不佳, 530-提交人失误) OR 缺陷原因分类 is EMPTY) AND (resolution not in (重复问题) OR resolution is EMPTY) AND 缺陷发现阶段 = SIT AND created <="
				+ strEndDate;
		String uatJql = "project = " + projectId
				+ " AND issuetype = Bug AND 缺陷发现阶段 in (PRD, SIT, UAT) AND (缺陷原因分类 not in (152-已知历史问题, 210-集成测试问题, 220-用户测试问题, 310-部署发布问题, 320-环境问题, 410-需求规格不佳, 420-开发规范不佳, 530-提交人失误) OR 缺陷原因分类 is EMPTY) AND (resolution not in (重复问题) OR resolution is EMPTY) AND 缺陷发现阶段 = UAT AND created <="
				+ strEndDate;
		String openDefectJql = "project = " + projectId + " AND issuetype = Bug AND status != 关闭 AND created <="
				+ strEndDate;
		String totalDefectJql = "project = " + projectId + " AND issuetype = Bug AND created <=" + strEndDate;
		String openRiskJql = "project = " + projectId + " AND issuetype = 风险 AND status != 关闭 AND created <="
				+ strEndDate;
		String totalRiskJql = "project = " + projectId + " AND issuetype = 风险  AND created <=" + strEndDate;
		String openChangeJql = "project = " + projectId + " AND issuetype = 变更 AND status != 关闭 AND created <="
				+ strEndDate;
		String totalChangeJql = "project = " + projectId + " AND issuetype = 变更  AND created <=" + strEndDate;
		String openCheckListJql = "project = " + projectId + " AND issuetype = 审计  AND status != 关闭 AND created <="
				+ strEndDate;
		String totalCheckListJql = "project = " + projectId + " AND issuetype = 审计  AND created <=" + strEndDate;

		
		long sitDefect = getJqlCount(sitJql);
		long uatDefect = getJqlCount(uatJql);
		System.out.println("SIT Defect: " + sitDefect);
		System.out.println("UAT Defect: " + uatDefect);

		// 计算缺陷密度、开发缺陷密度、SIT缺陷遗留率、UAT缺陷遗漏率
		Defect defect = new Defect();
		double defectDensity = defect.getDefectDensity(uatDefect, prdDefect, devEffort, qaEffort);
		double devDefectDensity = defect.getDevDefectDensity(sitDefect, uatDefect, prdDefect, devEffort);
		float sitRatio = defect.getSitRatio(sitDefect, uatDefect, prdDefect);
		float uatRatio = defect.getUatRatio(uatDefect, prdDefect);

		// 计算未关闭缺陷/总缺陷件数
		long openDefect = getJqlCount(openDefectJql);
		long totalDefect = getJqlCount(totalDefectJql);
		String defectRatio = defect.getDefectRatio(openDefect, totalDefect);

		// 计算未关闭风险/总风险
		Risk risk = new Risk();
		long openRisk = getJqlCount(openRiskJql);
		long totalRisk = getJqlCount(totalRiskJql);
		String riskRatio = risk.getRiskRatio(openRisk, totalRisk);

		// 计算未关闭变更/总变更
		Change change = new Change();
		long openChange = getJqlCount(openChangeJql);
		long totalChange = getJqlCount(totalChangeJql);
		String changeRatio = change.getChangeRatio(openChange, totalChange);

		// 计算未关闭变更/总变更
		CheckList checkList = new CheckList();
		long openCheckList = getJqlCount(openCheckListJql);
		long totalCheckList = getJqlCount(totalCheckListJql);
		String checkListRatio = checkList.getCheckListRatio(openCheckList, totalCheckList);

		// 项目信息、项目经理、项目监理
		ProjectInfo projectInfo = new ProjectInfo();
		String projectNo = projectInfo.getProjectNo(projectId);
		String projectManagerName = projectInfo.getProjectManagerName(projectId);
		String projectSqaName = projectInfo.getProjectSqaName(projectId);

		// 计算缺陷原因前3分布
		IssueInfo issueInfo = new IssueInfo();
		StatisticMapWrapper<Object, Integer> data = issueInfo.issueStatistics(totalDefectJql);
		ArrayList<Object> defectReasonList =new ArrayList<>(3);
		ArrayList<Integer> defectPercentList =new ArrayList<>(3);
		int i = 0;
		for (Map.Entry<Object, Integer> entry : data.entrySet()) {
			defectReasonList.add(i, entry.getKey());
			defectPercentList.add(i, data.getPercentage(entry.getKey()));
			i = i + 1;
			if(i >= 3)
			{
				break;
			}
		}
		
		// 返回显示的值
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		Map<String, Object> velocityParams = new HashMap<String, Object>();
		velocityParams.put("report", this);
		velocityParams.put("action", projectActionSupport);
		
		velocityParams.put("selectedProjectId", projectId);
		velocityParams.put("prdDefect", prdDefect);
		velocityParams.put("devEffort", devEffort);
		velocityParams.put("qaEffort", qaEffort);
		velocityParams.put("endDate", strEndDate);

		velocityParams.put("projectNo", projectNo);
		velocityParams.put("projectManagerName", projectManagerName);
		velocityParams.put("projectSqaName", projectSqaName);
		velocityParams.put("defectDensity", BigDecimal.valueOf(defectDensity).setScale(2,BigDecimal.ROUND_HALF_UP));
		velocityParams.put("devDefectDensity", BigDecimal.valueOf(devDefectDensity).setScale(2,BigDecimal.ROUND_HALF_UP));
		velocityParams.put("sitRatio", BigDecimal.valueOf(sitRatio).setScale(2,BigDecimal.ROUND_HALF_UP));
		velocityParams.put("uatRatio", BigDecimal.valueOf(uatRatio).setScale(2,BigDecimal.ROUND_HALF_UP));
		velocityParams.put("defectRatio", defectRatio);
		velocityParams.put("riskRatio", riskRatio);
		velocityParams.put("changeRatio", changeRatio);
		velocityParams.put("checkListRatio", checkListRatio);
		velocityParams.put("defectReasonList0", defectReasonList.get(0));
		velocityParams.put("defectPercentList0", decimalFormat.format(defectPercentList.get(0)) + "%");
		velocityParams.put("defectReasonList1", defectReasonList.get(1));
		velocityParams.put("defectPercentList1", decimalFormat.format(defectPercentList.get(1)) + "%");
		velocityParams.put("defectReasonList2", defectReasonList.get(2));
		velocityParams.put("defectPercentList2", decimalFormat.format(defectPercentList.get(2)) + "%");
		
		//为了前台显示，作为判断条件，但是不显示
		velocityParams.put("openDefect", openDefect);
		velocityParams.put("openCheckList", openCheckList);
		velocityParams.put("openChange", openChange);
		velocityParams.put("openRisk", openRisk);
		
		return velocityParams;
	}
	
	/**
	 * 验证数据的数据
	 */
	@Override
	public void validate(ProjectActionSupport action, Map map) {
		super.validate(action, map);
		// 获得项目ID
		String projectId = ParameterUtils.getStringParam(map, "selectedProjectId");
		// 获得生产缺陷件数
		String prdDefect = ParameterUtils.getStringParam(map, "prdDefect");
		// 获得开发工作量
		String devEffort = ParameterUtils.getStringParam(map, "devEffort");
		// 获得测试工作量
		String qaEffort = ParameterUtils.getStringParam(map, "qaEffort");
		// 获得项目状态报告截止日期
		String endDate = ParameterUtils.getStringParam(map, "endDate");
		System.out.println(prdDefect);
		//验证报告截止日期输入是否正确
		if ("".equals(endDate)) {
			action.addError("endDate", action.getText("report.endDate.is.required"));
		} else {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
				formatter.parse(endDate);
			} catch (ParseException e) {
				action.addError("endDate", action.getText("report.endDate.is.wrong"));
			}
		}
		
		// 验证生产缺陷件数
		if ("".equals(prdDefect)) {
			action.addError("prdDefect", action.getText("report.prdDefect.is.required"));
		} else {
			try {
				Long.parseLong(prdDefect);
			} catch (NumberFormatException e) {
				action.addError("prdDefect", action.getText("report.prdDefect.is.wrong"));
			}
		}
		
		// 验证开发工作量
		if ("".equals(devEffort)) {
			action.addError("devEffort", action.getText("report.devEffort.is.required"));
		} else {
			try {
				Float.parseFloat(devEffort);
			} catch (NumberFormatException e) {
				action.addError("devEffort", action.getText("report.devEffort.is.wrong"));
			}
		}
		
		//验证测试工作量
		if ("".equals(qaEffort)) {
			action.addError("qaEffort", action.getText("report.qaEffort.is.required"));
		} else {
			try {
				Float.parseFloat(qaEffort);
			} catch (NumberFormatException e) {
				action.addError("qaEffort", action.getText("report.qaEffort.is.wrong"));
			}
		}

	}

	/**
	 * 支持Excel下载
	 */
	@Override
	public boolean isExcelViewSupported() {
		return true;
	}
	
	@Override
	public boolean showReport() {
		return true;
	}

	/**
	 * 计算JQL件数
	 * 
	 * @param jql
	 * @return
	 * @throws SearchException
	 */
	private long getJqlCount(String jql) throws SearchException {
		User user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
		SearchService.ParseResult parseResult = searchService.parseQuery(user, jql);
		Query query = parseResult.getQuery();
		return searchService.searchCount(user, query);
	}

}
