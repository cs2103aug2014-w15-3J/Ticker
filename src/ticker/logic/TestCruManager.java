package ticker.logic;

import static org.junit.Assert.*;

import org.junit.Test;

import ticker.parser.UserInput;
import ticker.ui.TickerUI;

public class TestCruManager {
	// String constants for command types
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_LIST = "list";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_DELETE = "delete";
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
	public final void testCRUManager() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testAdd() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testDelete() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testEdit() {
		fail("Not yet implemented"); // TODO
	}

}
