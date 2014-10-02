package ticker.storage;

import static org.junit.Assert.*;

import org.junit.Test;

import tickerPackage.FloatingTask;

public class StorageTest {

	@Test
	public void testParseFloatingTaskIntoJSON() {
		Storage test = new Storage();
		String description = "DO HOMEWORK!!!";
		FloatingTask sample  = new FloatingTask(description);
		String result = test.parseFloatingTaskIntoJSON(sample);
		assertEquals(result, "{\"description\":\"DO HOMEWORK!!!\"}");
	}

}
