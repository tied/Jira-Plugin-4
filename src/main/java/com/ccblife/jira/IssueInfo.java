package com.ccblife.jira;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.statistics.FilterStatisticsValuesGenerator;
import com.atlassian.jira.issue.statistics.StatisticsMapper;
import com.atlassian.jira.web.bean.StatisticAccessorBean;
import com.atlassian.jira.web.bean.StatisticMapWrapper;
import com.atlassian.query.Query;

public class IssueInfo {

	public StatisticMapWrapper<Object, Integer> issueStatistics(String jql) {
		StatisticMapWrapper<Object, Integer> data = null;
		User user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
		SearchService.ParseResult parseResult = searchService.parseQuery(user, jql);
		Query query = parseResult.getQuery();
		SearchRequest searchRequest = new SearchRequest(query);
		FilterStatisticsValuesGenerator generator = new FilterStatisticsValuesGenerator();
		//计算【缺陷原因分类】字段的统计值
		StatisticsMapper mapper = generator.getStatsMapper("customfield_10270");
		StatisticAccessorBean statsBean = new StatisticAccessorBean(
				ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(), searchRequest);
		try {
			data = statsBean.getWrapper(mapper,
					StatisticAccessorBean.OrderBy.TOTAL, StatisticAccessorBean.Direction.DESC);
		} catch (SearchException e) {
			e.printStackTrace();
		}
		return data;
	}

}
