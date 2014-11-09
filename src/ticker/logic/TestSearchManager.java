package ticker.logic;

import static org.junit.Assert.*;

import org.junit.Test;

import ticker.parser.UserInput;
import ticker.ui.TickerUI;

public class TestSearchManager {
	// String constants for command types
		private static final String COMMAND_HELP = "help";
		private static final String COMMAND_UNTICK = "untick";
		private static final String COMMAND_TICK = "tick";
		private static final String COMMAND_REDO = "redo";
		private static final String COMMAND_UNDO = "undo";
		private static final String COMMAND_UNKIV = "unkiv";
		private static final String COMMAND_KIV = "kiv";
		private static final String COMMAND_ADD = "add";
		private static final String COMMAND_EDIT = "edit";
		private static final String COMMAND_LIST = "list";
		private static final String COMMAND_CLEAR = "clear";
		private static final String COMMAND_DELETE = "delete";
		private static final String COMMAND_SEARCH = "search";
		// String constants for type of lists used
		private static final String LIST_TIME = "time";
		private static final String LIST_PRIORITY = "priority";
		private static final String LIST_TICKED = "ticked";
		private static final String LIST_KIV = "kiv";
		private static final String LIST_SEARCH = "search";
		private static final String LIST_FREESLOT = "free";

		TickerUI ui = new TickerUI();
		Logic logic = ui.getLogic();
		UserInput input;
		
	@Test
	public final void testSearchManager() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testSearch() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testSearchExpired() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testSearchForFreeSlots() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testTake() {
		fail("Not yet implemented"); // TODO
	}

}
