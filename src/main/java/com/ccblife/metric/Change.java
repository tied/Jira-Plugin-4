package com.ccblife.metric;

public class Change {
	
	/**
	 * 未关闭变更/总变更件数
	 * @param openChange
	 * @param totalChange
	 * @return
	 */
	public String getChangeRatio(long openChange, long totalChange){
		return openChange + "/" + totalChange;
	}

}
