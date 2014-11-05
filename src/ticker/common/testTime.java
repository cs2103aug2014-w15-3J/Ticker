//@author A0115288B

package ticker.common;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

public class testTime {
	
	@Test
	public void test() {
		//test constructor
		Time time1 = new Time(5, 29);
		Time time2 = new Time(23, 59);
		
		//test setHour, setMinute, getHour and getMinute
		assertEquals(time1.getHour(), 5);
		time1.setMinute(1);
		assertEquals(time1.getMinute(), 1);
		time2.setHour(13);
		assertEquals(time2.getHour(), 13);
		
		//test toString
		assertEquals(time1.toString(), "5:01");
		assertEquals(time2.toString(), "13:59");
		
		//test compareTo
		assertEquals(time1.compareTo(new Time(10, 34)), -1);
		assertEquals(time1.compareTo(new Time(5, 22)), -1);
		assertEquals(time2.compareTo(time2), 0);
		assertEquals(time2.compareTo(new Time(12, 4)), 1);
		
		assertEquals(time2.smallerThan(time1), false);
		assertEquals(time1.smallerThan(time2), true);
		
		//test equals
		assertEquals(time2.equals(time1.getHour()), false);
		assertEquals(time2.equals(time1), false);
		assertEquals(time2.equals(time2), true);
	}

}
