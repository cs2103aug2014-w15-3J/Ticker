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
		assertEquals(date2.compareTo(new Date(2016, 3, 3)), -1);
		assertEquals(date2.compareTo(new Date(2013, 5, 20)), 1);
		assertEquals(date2.compareTo(new Date(2013, 10, 20)), -1);
		assertEquals(date2.compareTo(new Date(2013, 9, 5)), 1);
		assertEquals(date2.compareTo(new Date(2013, 9, 29)), -1);
		assertEquals(date2.compareTo(new Date(2013, 9, 20)), 0);

		//test equals
		assertEquals(date2.equals(date1.getMonthStr()), false);
		assertEquals(date2.equals(date1), false);
		assertEquals(date2.equals(new Date(2013, 9, 20)), true);

		//test isLeapYear
		/* here we use equivalence partition to discuss four different cases:
		 * 1) year can be divided by 400
		 * 2) year can be divided by 4 but not 100
		 * 3) year can be divided by 100 but not 400
		 * 4) year can be divided by neither 4 or 100
		 */
		assertEquals(Date.isLeapYear(2000), true);
		assertEquals(Date.isLeapYear(2012), true);
		assertEquals(Date.isLeapYear(1900), false);
		assertEquals(Date.isLeapYear(2010), false);

		//test dayOfWeek
		assertEquals(Date.dayOfWeek(new Date(2014,10,14)),2);
		assertEquals(Date.dayOfWeek(new Date(2013,1,1)),2);
		assertEquals(Date.dayOfWeek(new Date(2008,2,29)),5);
		assertEquals(Date.dayOfWeek(new Date(1900,1,1)),1);
	}

}
