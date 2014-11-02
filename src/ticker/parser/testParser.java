package ticker.parser;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

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
		//floating task
		assertEquals("add",par.processInput("add \"Project Meeting\"").getCommand());
	
		//check whether time and date is parsed correctly
		//format: startTime-endTime    startDate-endDate
		Date date1 = new Date(2014,3,4);
		Date date2 = new Date(2014,5,6);
		Time time1 = new Time(1,0);
		Time time2 = new Time(2,0);
		UserInput ui1 = par.processInput("add \"blah blah blah\" 1-2 3/4-5/6");
		assertEquals(ui1.getStartDate(),date1);
		assertEquals(ui1.getEndDate(),date2);
		assertEquals(ui1.getStartTime(),time1);
		assertEquals(ui1.getEndTime(),time2);
		
		//check whether repeating task is correctly parsed 
		UserInput ui2 = par.processInput("add \"blah blah blah\" 1-2 3/4-5/6 -r -impt");
		assertEquals(ui2.getRepeating(),true);
		//check whether priority is correctly parsed
		assertEquals(ui2.getPriority(),'A'); 
	}
	
	@Test
	public void testDelete(){
		
		//correct syntax
		assertEquals("delete",par.processInput("delete 1").getCommand());
		assertEquals("delete",par.processInput("del 2").getCommand());
		assertEquals("delete",par.processInput("remove 3").getCommand());
		assertThat(par.processInput("DeLeTE 4").getIndex(),is(4));// UPPERCASE and lowercase are both supported
		
		//invalid format
		assertEquals("error",par.processInput("delete").getCommand());
		assertEquals("error",par.processInput("delete abc").getCommand());
	}
	
	@Test
	public void testSearch(){
		assertEquals("search",par.processInput("search \"Lecture\"").getCommand());
		assertEquals("Lecture",par.processInput("search Lecture").getDescription());
	}
	
	@Test
	public void testOthers(){
		//test undo redo
		assertEquals("redo",par.processInput("redo").getCommand());
		assertEquals("undo",par.processInput("undo").getCommand());
		//test list command
		assertEquals("priority",par.processInput("list p").getDescription());
		assertEquals("time",par.processInput("list time").getDescription());
		//test tick untick
		assertEquals("tick",par.processInput("tick 1").getCommand());
		assertEquals("tick",par.processInput("done 1").getCommand());
		assertThat(par.processInput("done 1").getIndex(),is(1));
		assertEquals("untick",par.processInput("untick 1").getCommand());
		//test for CMI (Cannot Make It)
		assertEquals("cmi",par.processInput("cmi 1").getCommand());
		assertEquals("uncmi",par.processInput("uncmi 1").getCommand());
	}
	
	@Test
	public void testEdit(){
		//edit description only
		assertEquals("edit",par.processInput("edit 1 \"have lunch\"").getCommand());
		assertThat(par.processInput("edit 1 \"have lunch\"").getIndex(),is(1));
		//edit timing for a task using -t
		assertEquals("editt",par.processInput("edit 1 -t \"have lunch\" 13-14 ").getCommand());
		assertEquals(new Time(13,0),par.processInput("edit 1 -t \"have lunch\" 13-14 ").getStartTime());
		assertEquals(new Time(14,0),par.processInput("edit 1 -t \"have lunch\" 13-14 ").getEndTime());
		assertEquals("have lunch",par.processInput("edit 1 have lunch").getDescription());
	}
	
	public void testConstructDate(){
		Date date1 = Parser.constructDate("11/1");
		Date date2 = Parser.constructDate("2014/1/11");
		assertEquals(date1,new Date(2014,11,1));
		assertEquals(date2,new Date(2014,1,11));
		assertEquals(Parser.constructDate("Sep/11"),new Date(2014,9,11));
	}
}
