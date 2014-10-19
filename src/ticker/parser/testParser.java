package ticker.parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class testParser {
	
	Parser par = new Parser();
	@Test
	public void test() {
		
		assertEquals("add",par.processInput("add \"Project Meeting\"").getCommand());
		assertEquals("delete",par.processInput("delete 1").getCommand());
		assertEquals("search",par.processInput("search \"Lecture\"").getCommand());
		assertEquals("redo",par.processInput("redo").getCommand());
		assertEquals("undo",par.processInput("undo").getCommand());
	
	}
}
