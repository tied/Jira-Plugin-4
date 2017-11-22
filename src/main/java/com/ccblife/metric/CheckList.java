package com.ccblife.metric;

public class CheckList {

	/**
	 * 不合格科目件数/实际审计科目件数
	 * @param openCheckList
	 * @param totalCheckList
	 * @return
	 */
	public String getCheckListRatio(long openCheckList, long totalCheckList){
		return openCheckList + "/" + totalCheckList;
	}

}
