package com.ccblife.metric;

public class Risk {
	
	/**
	 * 未关闭风险/总风险件数
	 * @param openRisk
	 * @param totalRisk
	 * @return
	 */
	public String getRiskRatio(long openRisk, long totalRisk){
		return openRisk + "/" + totalRisk;
	}	
}
