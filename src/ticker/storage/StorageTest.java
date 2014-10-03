package ticker.storage;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Test;

import tickerPackage.Date;
import tickerPackage.DeadlineTask;
import tickerPackage.FloatingTask;
import tickerPackage.RepeatingTask;
import tickerPackage.Time;
import tickerPackage.TimedTask;

public class StorageTest {

	@Test
	public void testParseTimedTaskIntoJSON() {
		Storage test = new Storage();
		String description = "DO HOMEWORK!!!";
		TimedTask sample  = new TimedTask(description, new Date(2014,12,29), new Time(5,35), new Date(2015,1,1), new Time(13,45));
		JSONObject result = test.parseTimedTaskIntoJSON(sample);
		assertEquals("{\"startTime\":{\"min\":35,\"hour\":5},\"startDate\":{\"month\":12,\"year\":2014,\"date\":29},"
						+ "\"description\":\"DO HOMEWORK!!!\",\"endDate\":{\"month\":1,\"year\":2015,\"date\":1},"
						+ "\"endTime\":{\"min\":45,\"hour\":13}}", result.toString());
	}
	
	@Test
	public void testParseRepeatingTaskIntoJSON() {
		Storage test = new Storage();
		String description = "DO HOMEWORK!!!";
		RepeatingTask sample  = new RepeatingTask(description, new Date(2014,12,29), new Time(5,35), new Time(13,45));
		JSONObject result = test.parseRepeatingTaskIntoJSON(sample);
		assertEquals("{\"startTime\":{\"min\":35,\"hour\":5},\"Date\":{\"month\":12,\"year\":2014,\"date\":29},"
				+ "\"description\":\"DO HOMEWORK!!!\",\"endTime\":{\"min\":45,\"hour\":13}}", result.toString());
	}
	
	@Test
	public void testParseDeadlineTaskIntoJSON() {
		Storage test = new Storage();
		String description = "DO HOMEWORK!!!";
		DeadlineTask sample  = new DeadlineTask(description, new Date(2014,12,29), new Time(5,35));
		JSONObject result = test.parseDeadlineTaskIntoJSON(sample);
		assertEquals("{\"Date\":{\"month\":12,\"year\":2014,\"date\":29},\"description\":\"DO HOMEWORK!!!\","
				+ "\"endTime\":{\"min\":35,\"hour\":5}}", result.toString());
	}
	
	@Test
	public void testParseFloatingTaskIntoJSON() {
		Storage test = new Storage();
		String description = "DO HOMEWORK!!!";
		FloatingTask sample  = new FloatingTask(description);
		JSONObject result = test.parseFloatingTaskIntoJSON(sample);
		assertEquals("{\"description\":\"DO HOMEWORK!!!\"}", result.toString());
	}
	
	@Test
	public void testParseTimeIntoJSON() {
		Storage test = new Storage();
		Time sample  = new Time(11, 59);
		JSONObject result = test.parseTimeIntoJSON(sample);
		assertEquals("{\"min\":59,\"hour\":11}", result.toString());
	}
	
	@Test
	public void testParseDateIntoJSON() {
		Storage test = new Storage();
		Date sample  = new Date(2014, 12, 12);
		JSONObject result = test.parseDateIntoJSON(sample);
		assertEquals("{\"month\":12,\"year\":2014,\"date\":12}", result.toString());
	}

}
