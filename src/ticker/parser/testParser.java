package ticker.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import ticker.common.Date;
import ticker.common.Time;
public class testParser {
	
	Parser par = new Parser();
	@Test
	public void test() {
		
	}
	
	@Test
	public void testAdd(){
		
		assertEquals("add",par.processInput("add \"Project Meeting\"").getCommand());
		assertEquals("delete",par.processInput("delete 1").getCommand());
		assertEquals("search",par.processInput("search \"Lecture\"").getCommand());
		assertEquals("redo",par.processInput("redo").getCommand());
		assertEquals("undo",par.processInput("undo").getCommand());
	
		Date date1 = new Date(2014,4,3);
		Date date2 = new Date(2014,6,5);
		Time time1 = new Time(1,0);
		Time time2 = new Time(2,0);
		UserInput ui1 = par.processInput("add \"blah blah blah\" 1-2 3/4-5/6");
		assertEquals(ui1.startDate,date1);
		assertEquals(ui1.endDate,date2);
		assertEquals(ui1.startTime,time1);
		assertEquals(ui1.endTime,time2);
	}
	
	public void testDelete(){
		assertEquals("delete",par.processInput("delete 1").getCommand());
		assertEquals("error",par.processInput("delete 1").getCommand());
	}
	
}
