package ticker.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class testDate {

	@Test
	public void test() {
		assertEquals(Date.dayOfWeek(new Date(2014,10,14)),2);
		assertEquals(Date.dayOfWeek(new Date(2013,1,1)),2);
		assertEquals(Date.dayOfWeek(new Date(2008,2,29)),5);
		//assertEquals(Date.dayOfWeek(new Date(1899,12,31)),0);
	}

}
