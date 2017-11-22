package com.ccblife.metric;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Defect {

	/**
	 * 缺陷密度
	 * @param uatDefect
	 * @param prdDefect
	 * @param devEffort
	 * @param qaEffort
	 * @return
	 */
	public double getDefectDensity(long uatDefect, long prdDefect, double devEffort, double qaEffort) {
		if ((devEffort + qaEffort) != 0)
			return (double) (100 * (uatDefect + prdDefect)) / (devEffort + qaEffort);
		else
			return 0;
	}
	
	/**
	 * 开发缺陷密度
	 * @param sitDefect
	 * @param uatDefect
	 * @param prdDefect
	 * @param devEffort
	 * @return
	 */
	public double getDevDefectDensity(long sitDefect, long uatDefect, long prdDefect, double devEffort) {
		if (devEffort != 0)
			return (double) (100 * (sitDefect + uatDefect + prdDefect)) / devEffort;
		else
			return 0;
	}

	
	/**
	 * SIT缺陷遗漏率
	 * @param sitDefect
	 * @param uatDefect
	 * @param prdDefect
	 * @return
	 */
	public float getSitRatio(long sitDefect, long uatDefect, long prdDefect) {
		if ((sitDefect + uatDefect + prdDefect) != 0)
			return (float) (100 * (uatDefect + prdDefect)) / (float) (sitDefect + uatDefect + prdDefect);
		else
			return 0;
	}

	/**
	 * UAT缺陷遗漏率
	 * @param uatDefect
	 * @param prdDefect
	 * @return
	 */
	public float getUatRatio(long uatDefect, long prdDefect) {
		if ((uatDefect + prdDefect) != 0)
			return (float) (100 * prdDefect) / (float) (uatDefect + prdDefect);
		else
			return 0;
	}

	/**
	 * 未关闭缺陷比例
	 * @param openDefect
	 * @param totalDefect
	 * @return
	 */
	public String getDefectRatio(long openDefect, long totalDefect) {
		return openDefect + "/" + totalDefect;
	}

	public Map<String, Object> getDefectReason(Map<String, Object> map) {
		long total = 0;
		HashMap<String, Object> percentMap = new HashMap<String, Object>();
		Set<String> set = map.keySet();
		for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
			String key = iter.next();
			long value = (long) map.get(key);
			total = total + value;
			System.out.println(key + "====" + value);
		}
		if (total == 0) {
			percentMap.put("N/A", 0);
		} else {
			for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
				String key = iter.next();
				long value = (long) map.get(key);
				float percent = (float) value / (float) total;
				percentMap.put(key, percent);
				System.out.println(key + "-----" + percent);
			}
		}
		return percentMap;
	}
}
