package ticker.logic;

import static org.junit.Assert.*;

import org.junit.Test;

import ticker.parser.UserInput;
import ticker.ui.TickerUI;

public class TestTickKivManager {
	// String constants for command types
	private static final String COMMAND_UNTICK = "untick";
	private static final String COMMAND_TICK = "tick";
	private static final String COMMAND_UNKIV = "unkiv";
	private static final String COMMAND_KIV = "kiv";
	private static final String COMMAND_ADD = "add";
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
	public final void testTickKIVManager() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testTick() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testUntick() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testKiv() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testUnkiv() {
		fail("Not yet implemented"); // TODO
	}

}
