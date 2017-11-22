package ut.com.ccblife.metric;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.ccblife.metric.CheckList;

public class CheckListTest {
	
	private CheckList checkList = new CheckList();

	@Test
	public void testGetCheckListyRatio() {
		String checkListRatio = checkList.getCheckListRatio(10, 100);
		assertThat(checkListRatio, is("10/100"));
	}
}
