package ticker.parser;
//@author A0115369B
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import ticker.common.Date;
import ticker.common.Time;
public class ParserTest {
	
	Parser par = new Parser();
	@Test
	public void test() {
	}
	
	@Test
	public void testAdd(){
		//floating task
		assertEquals("add",par.processInput("add Project Meeting").getCommand());
	
		//check whether time and date is parsed correctly
		//format: startTime-endTime    startDate-endDate
		Date date1 = new Date(2014,3,4);
		Date date2 = new Date(2014,5,6);
		Time time1 = new Time(1,0);
		Time time2 = new Time(2,0);
		
		UserInput ui1 = par.processInput("add go shopping 1-2 3/4-5/6");
		assertEquals(ui1.getStartDate(),date1);
		assertEquals(ui1.getEndDate(),date2);
		assertEquals(ui1.getStartTime(),time1);
		assertEquals(ui1.getEndTime(),time2);

		//check whether repeating task is correctly parsed 
		UserInput ui2 = par.processInput("add go shopping 1-2 3/4-5/6 -r -impt");
		assertEquals(ui2.getRepeating(),true);
		//check whether priority is correctly parsed
		assertEquals(ui2.getPriority(),'A'); 
	
		//check if StartDate and EndDate are set to the same day if only one date is given
		UserInput ui3 = par.processInput("add go shopping 11am-2pm 3/4");
		assertEquals(ui3.getStartDate(),date1);
		assertEquals(ui3.getEndDate(),date1);
		//test whether am pm are correctly interpreted
		assertEquals(ui3.getStartTime(),new Time(11,0));
		assertEquals(ui3.getEndTime(),new Time(14,0));
		
		//check whether start date and end date are assumed to be today
		//if the user enters only time.
		UserInput ui4 = par.processInput("add go shopping 11am-2pm");
		assertEquals(ui4.getStartDate(),Date.getCurrentDate());
		assertEquals(ui4.getEndDate(),Date.getCurrentDate());
	}
	
	@Test
	public void testDelete(){
		
		//correct syntax
		assertEquals("delete",par.processInput("delete 1").getCommand());
		assertEquals("delete",par.processInput("del 2").getCommand());
		assertEquals("delete",par.processInput("remove 3").getCommand());
		
		// UPPERCASE and lowercase are both supported
		assertThat(par.processInput("DeLeTE 4").getIndex(),is(4));
		
		//invalid format
		assertEquals("error",par.processInput("delete").getCommand());
		assertEquals("error",par.processInput("delete abc").getCommand());
	}
	
	@Test
	public void testSearch(){
		assertEquals("search",par.processInput("search \"Lecture\"").getCommand());
		assertEquals("Lecture",par.processInput("search Lecture").getDescription());
		
		UserInput searchUserIn = par.processInput("search Project 11/2-11/9");
		assertEquals(searchUserIn.getStartDate(),new Date(2014,11,2));
		assertEquals(searchUserIn.getEndDate(),new Date(2014,11,9));
		assertEquals(searchUserIn.getStartTime(),new Time(0,0));
		assertEquals(searchUserIn.getEndTime(),new Time(23,59));
		
		UserInput searchUserIn2 = par.processInput("search Project -t by next friday");
		assertEquals(searchUserIn2.getStartDate(),Date.getCurrentDate());
		
		UserInput searchUserIn3 = par.processInput("search cs2101 hw");
		assertEquals(searchUserIn3.getStartDate(),null);
		assertEquals(searchUserIn3.getEndDate(),null);
		assertEquals(searchUserIn3.getStartTime(),null);
		assertEquals(searchUserIn3.getEndTime(),null);
		
		//search expired
		UserInput searchUserIn4 = par.processInput("search Assignment -e");
		assertEquals(searchUserIn4.getCommand(),"searchExpired");
		
		//search free slots
		UserInput searchUserIn5 = par.processInput("searchfree 11/10");
		assertEquals(searchUserIn5.getCommand(),"searchfree");
		
		UserInput searchUserIn6 = par.processInput("searchf");
		assertEquals(searchUserIn6.getStartDate(),Date.getCurrentDate());
	}
		
	@Test
	public void testEdit(){
		//edit description only
		assertEquals("edit",par.processInput("edit 1 have lunch").getCommand());
		assertThat(par.processInput("edit 1 have lunch").getIndex(),is(1));
		
		//edit timing for a task using -t
		assertEquals(new Time(13,0),par.processInput("edit 1 have lunch 13-14 ").getStartTime());
		assertEquals(new Time(14,0),par.processInput("edit 1 have lunch 13-14 ").getEndTime());
		assertEquals("have lunch",par.processInput("edit 1 have lunch").getDescription());
		assertEquals('\u0000',par.processInput("edit 1 have lunch").getPriority());
	}
	
	public void testConstructDate(){
		Date date1 = Parser.constructDate("11/1");
		Date date2 = Parser.constructDate("2014/1/11");
		assertEquals(date1,new Date(2014,11,1));
		assertEquals(date2,new Date(2014,1,11));
		assertEquals(Parser.constructDate("Sep/11"),new Date(2014,9,11));
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
		
		//test for KIV (Keep In View)
		assertEquals("kiv",par.processInput("kiv 1").getCommand());
		assertEquals("unkiv",par.processInput("unkiv 1").getCommand());
	}
}
