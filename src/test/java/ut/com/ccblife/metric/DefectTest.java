package ut.com.ccblife.metric;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import com.ccblife.metric.Defect;

public class DefectTest {

	private static Defect defect = new Defect();

	@Test
	public void testGetDefectDensity() {
		double defectDensity = defect.getDefectDensity(22, 1, (double) 6928.5, 746);
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		assertThat(decimalFormat.format(defectDensity), is("0.30"));
	}

	@Test
	public void testGetDevDefectDensity()
	{
		double devDefectDensity = defect.getDevDefectDensity(434, 67, 5, (double) 8581.5);
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		assertThat(decimalFormat.format(devDefectDensity), is("5.90"));
	}

	@Test
	public void testGetSitkRatio() {
		float sitRatio = defect.getSitRatio(156, 22, 1);
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		assertThat(decimalFormat.format(sitRatio), is("12.85"));
	}

	@Test
	public void testGetUatRatio() {
		float uatRatio = defect.getUatRatio(22, 1);
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		assertThat(decimalFormat.format(uatRatio), is("4.35"));
	}
	
	@Test
	public void testGetDefectReason() {
		DecimalFormat decimalFormat = new DecimalFormat("##0.00");
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("130-实现技术不足", 10L);
		map.put("120-设计技术不足", 20L);
		map.put("160-失误", 70L);
		HashMap<String, Object> defectReason = (HashMap<String, Object>) defect.getDefectReason(map);
		Set<String> set = defectReason.keySet();
		float value = (float) defectReason.get("130-实现技术不足");
		System.out.println(value);
		assertThat(decimalFormat.format(value), is("0.10"));
	}
}