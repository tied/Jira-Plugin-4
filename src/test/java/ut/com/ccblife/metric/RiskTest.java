package ut.com.ccblife.metric;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import com.ccblife.metric.Risk;

public class RiskTest {
	
	private Risk risk = new Risk();

	@Test
	public void testGetRiskRatio() {
		String riskRatio = risk.getRiskRatio(10, 100);
		assertThat(riskRatio, is("10/100"));
	}
}
