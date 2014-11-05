package ticker.storage;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

import ticker.common.Date;
import ticker.common.DeadlineTask;
import ticker.common.FloatingTask;
import ticker.common.RepeatingTask;
import ticker.common.Task;
import ticker.common.Time;
import ticker.common.TimedTask;

//@author A0116673A

public class StorageTest {

	@Test
	public void testConvertToJSON() {
		Storage test = new Storage();
		Vector<Task> tasks = new Vector<Task>();
		String expected = "[{\"id\":1,\"description\":\"Buy milk from NTUC\",\"priority\":\"B\",\"isRepeating\":false,\"isExpired\":false},"
				+ "{\"id\":4,\"description\":\"Finish OP2 slides\",\"endDate\":{\"year\":2014,\"month\":11,\"date\":7},\"endTime\":{\"hour\":11,"
				+ "\"minute\":30},\"priority\":\"A\",\"isRepeating\":false,\"isExpired\":false},{\"id\":2,\"description\":\"CS2103 V0.4 Demo\","
				+ "\"startDate\":{\"year\":2014,\"month\":11,\"date\":5},\"startTime\":{\"hour\":15,\"minute\":0},\"endDate\":{\"year\":2014,"
				+ "\"month\":11,\"date\":5},\"endTime\":{\"hour\":14,\"minute\":0},\"priority\":\"A\",\"isRepeating\":false,\"isExpired\":false},"
				+ "{\"id\":3,\"day\":4,\"description\":\"Post Lecture Reflection\",\"startDate\":{\"year\":2014,\"month\":11,\"date\":6},\"priority\""
				+ ":\"B\",\"isRepeating\":true,\"isExpired\":false}]"; 
		
		// This covers the 4 different types of tasks 
		tasks.add(new FloatingTask("Buy milk from NTUC", 'B', false));
		tasks.add(new DeadlineTask("Finish OP2 slides", new Date(2014, 11, 7), new Time(11, 30), 'A', false));
		tasks.add(new TimedTask("CS2103 V0.4 Demo", new Date(2014, 11, 5), new Time(15, 0), new Date(2014, 11, 5), new Time(14, 0), 'A', false));
		tasks.add(new RepeatingTask("Post Lecture Reflection", new Date(2014, 11, 6), null, null, 'B', true));
		
		String result = test.convertToJSON(tasks);
		assertEquals(expected, result);
	}
	
	@Test
	public void testJSONToTaskVector() {
		Storage test = new Storage();
		Vector<Task> tasks;
		
		//data set with 4 different types of tasks
		String data = "[{\"id\":1,\"description\":\"Buy milk from NTUC\",\"priority\":\"B\",\"isRepeating\":false,\"isExpired\":false},"
				+ "{\"id\":4,\"description\":\"Finish OP2 slides\",\"endDate\":{\"year\":2014,\"month\":11,\"date\":7},\"endTime\":{\"hour\":11,"
				+ "\"minute\":30},\"priority\":\"A\",\"isRepeating\":false,\"isExpired\":false},{\"id\":2,\"description\":\"CS2103 V0.4 Demo\","
				+ "\"startDate\":{\"year\":2014,\"month\":11,\"date\":5},\"startTime\":{\"hour\":15,\"minute\":0},\"endDate\":{\"year\":2014,"
				+ "\"month\":11,\"date\":5},\"endTime\":{\"hour\":14,\"minute\":0},\"priority\":\"A\",\"isRepeating\":false,\"isExpired\":false},"
				+ "{\"id\":3,\"day\":4,\"description\":\"Post Lecture Reflection\",\"startDate\":{\"year\":2014,\"month\":11,\"date\":6},\"priority\""
				+ ":\"B\",\"isRepeating\":true,\"isExpired\":false}]"; 
		
		tasks = test.JSONToTasksVector(data);
		
		assertEquals(true, tasks.contains(new FloatingTask("Buy milk from NTUC", 'B', false)));
		assertEquals(true, tasks.contains(new DeadlineTask("Finish OP2 slides", new Date(2014, 11, 7), new Time(11, 30), 'A', false)));
		assertEquals(true, tasks.contains(new TimedTask("CS2103 V0.4 Demo", new Date(2014, 11, 5), new Time(15, 0), new Date(2014, 11, 5), new Time(14, 0), 'A', false)));
		assertEquals(true, tasks.contains(new RepeatingTask("Post Lecture Reflection", new Date(2014, 11, 6), null, null, 'B', true)));
	}
	
}
