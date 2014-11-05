//@author A0115288B
//TODO recover other test cases

package ticker.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class testDate {

	@Test
	public void test() {
		//test constructor
		Date date1 = new Date(2014, 11, 13);
		Date date2 = new Date(1900, 2, 9);
		
		//test toString
		assertEquals(date1.toString(), "13 Nov");
		assertEquals(date2.toString(), "9 Feb 1900");
		
		//test getMonthStr
		assertEquals(date1.getMonthStr(), "Nov");
		assertEquals(date2.getMonthStr(), "Feb");
		
		//test setMonth, setDate, setYear
		date2.setMonth(9);
		date2.setDate(20);
		date2.setYear(2013);
		assertEquals(date2.toString(), "20 Sep 2013");
		
		//test CompareTo
		assertEquals(date2.compareTo(date1), -1);
		
		
		
		//test dayOfWeek
		assertEquals(Date.dayOfWeek(new Date(2014,10,14)),2);
		assertEquals(Date.dayOfWeek(new Date(2013,1,1)),2);
		assertEquals(Date.dayOfWeek(new Date(2008,2,29)),5);
		assertEquals(Date.dayOfWeek(new Date(1900,1,1)),1);
	}

}
