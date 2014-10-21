package ticker.logic;

import ticker.ui.TickerUI;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestLogic {
	TickerUI ui = new TickerUI();

	@Test
	public void test() {
		// Add floating tasks
		assertEquals("aaa has been added.\n", ui.getLogic().getLogic("add \"aaa\""));
		// Add scheduled tasks
		assertEquals("aaa has been added.\n", ui.getLogic().getLogic("add \"aaa\" 22/10-23/10 -st 4:00 -et 13:00"));
		// Add deadlined tasks
		assertEquals("aaa has been added.\n", ui.getLogic().getLogic("add \"aaa\" -23/10 -et 13:00"));
		assertEquals("aaa has been added.\n", ui.getLogic().getLogic("add \"aaa\" 23/10 -et 13:00"));
		// Adding invalid command (This is testing the position of the dash "-" in relation to the date. If "-" is after date, it's starting time, if "-" is
		// after it's ending time. Starting time and ending time creates an error due to ambiguity in the user wanting a scheduled task or deadline task
		assertEquals("invalid command", ui.getLogic().getLogic("add \"aaa\" 23/10- -et 13:00"));
		assertEquals("invalid command", ui.getLogic().getLogic("add aaa"));
		
			
		// Get into Tick list
		assertEquals("Listing ticked tasks...", ui.getLogic().getLogic("list tick"));
			
		// Get into sortedPriority list
		assertEquals("Listing by priority...", ui.getLogic().getLogic("list priority"));
			
		// Get into cmi list
		assertEquals("Listing tasks that cannot be done...", ui.getLogic().getLogic("list cmi"));
		
		// Search function
		assertEquals("No search results", ui.getLogic().getLogic("search \"aaa\""));
		
	}

}
