package ticker.storage;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

import tickerPackage.Date;
import tickerPackage.DeadlineTask;
import tickerPackage.FloatingTask;
import tickerPackage.RepeatingTask;
import tickerPackage.Task;
import tickerPackage.Time;
import tickerPackage.TimedTask;

public class StorageTest {

	@Test
	public void testParseTimedTaskIntoJSON() {
		Storage test = new Storage();
		Vector<Task> testing = new Vector<Task>();
		testing.add(new FloatingTask("FUCK"));
		testing.add(new DeadlineTask("FUCKER", new Date(2014,22,23), new Time(41, 12)));
		String result = test.converToJSON(testing);
		assertEquals("", result);
	}
	
	@Test
	public void testParseTimedTaskIntoJSON() {
		Storage test = new Storage();
		String testing = "[{\"id\": 1,\"description\": \"FUCK\"},  	  {"
		                	   + "\"id\": 4,"
		                	    +"\"endDate\": {"
		                	    + "\"year\": 2014,"
		                	     + "\"month\": 22,"
		                	     + "\"date\": 23"
		                	    +"},"
		                	    +"\"endTime\": {"
		                	      +"\"hour\": 41,"
		                	      +"\"minute\": 12"
		                	    +"},"
		                	    +"\"description\": \"FUCKER\""
		                	  +"}"
		                	+"]";
		Vector<Task> result = test.JSONToTasksVector(testing);
		
		assertEquals("", result);
	}
	
}
