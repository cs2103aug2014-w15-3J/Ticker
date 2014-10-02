package ticker.storage;

import static org.junit.Assert.*;
import Storage;

import org.json.JSONObject;
import org.junit.Test;

import tickerPackage.Date;
import tickerPackage.FloatingTask;
import tickerPackage.Time;

public class StorageTest {

	@Test
	public void testParseFloatingTaskIntoJSON() {
		Storage test = new Storage();
		String description = "DO HOMEWORK!!!";
		FloatingTask sample  = new FloatingTask(description);
		String result = test.parseFloatingTaskIntoJSON(sample);
		assertEquals(result, "{\"description\":\"DO HOMEWORK!!!\"}");
	}
	
	@Test
	public void testParseTimeIntoJSON() {
		Storage test = new Storage();
		String description = "DO HOMEWORK!!!";
		Time sample  = new Time(11, 59);
		JSONObject result = test.parseTimeIntoJSON(sample);
		assertEquals(result.toString(), "{\"min\":59,\"hour\":11}");
	}
	
	@Test
	public void testParseDateIntoJSON() {
		Storage test = new Storage();
		Date sample  = new Date(2014, 12, 12);
		JSONObject result = test.parseDateIntoJSON(sample);
		assertEquals(result.toString(), "{\"month\":12,\"year\":2014,\"date\":12}");
	}

}
