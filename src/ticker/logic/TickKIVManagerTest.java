package ticker.logic;

import static org.junit.Assert.*;

import org.junit.Test;

import ticker.common.Date;
import ticker.common.Time;
import ticker.parser.UserInput;

//@author A0114535M
public class TickKIVManagerTest {
	// String constants for command types
	private static final String COMMAND_UNTICK = "untick";
	private static final String COMMAND_TICK = "tick";
	private static final String COMMAND_UNKIV = "unkiv";
	private static final String COMMAND_KIV = "kiv";
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_LIST = "list";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_REDO = "redo";
	private static final String COMMAND_UNDO = "undo";
	// String constants for type of lists used
	private static final String LIST_TIME = "time";
	private static final String LIST_PRIORITY = "priority";
	private static final String LIST_TICKED = "ticked";
	private static final String LIST_KIV = "kiv";
	private static final String LIST_SEARCH = "search";
	private static final String LIST_FREESLOT = "free";

	@Test
	public final void testTickKIVManager() {
		UIForLogicTesting ui = new UIForLogicTesting();
		Logic logic = ui.getLogic();
		UserInput input;

		// Clear the list before every testing
		input = new UserInput();
		input.setCommand(COMMAND_CLEAR);
		assertEquals("Spick and span!", logic.getOutput(input));
		input = new UserInput();
		input.setCommand(COMMAND_LIST);
		input.setDescription(LIST_TICKED);
		assertEquals("Listing ticked tasks...", logic.getOutput(input));
		assertEquals("Spick and span!", logic.clear());
		assertEquals("Listing tasks that are kept in view...", logic.list(LIST_KIV));
		assertEquals("Spick and span!", logic.clear());
		assertEquals("Listing by time...", logic.list(LIST_TIME));
		assertEquals("Spick and span!", logic.clear());
		assertEquals("Nothing to display", logic.list());

		// Add floating task
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("CompClub: Add actionables on Trello");
		input.setPriority('B');

		assertEquals("CompClub: Add actionables on Trello has been added.", logic.getOutput(input));

		// Add scheduled task
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("CompClub: Man welfare pack booth");
		input.setStartDate(new Date(2014, 11, 5));
		input.setStartTime(new Time(11, 30));
		input.setEndDate(new Date(2014, 11, 5));
		input.setEndTime(new Time(14, 0));
		input.setPriority('B');

		assertEquals("CompClub: Man welfare pack booth has been added.", logic.getOutput(input));

		// Add deadlined task
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("HW: Submit CS2106 v0.5");
		input.setEndDate(new Date(2014, 11, 10));
		input.setEndTime(new Time(23, 59));
		input.setPriority('B');

		assertEquals("HW: Submit CS2106 v0.5 has been added.", logic.getOutput(input));

		// Add repeated task
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("CompClub: Pcell meeting");
		input.setStartDate(new Date(2014, 11, 5));
		input.setStartTime(new Time(16, 0));
		input.setStartDate(new Date(2014, 11, 5));
		input.setEndTime(new Time(18, 0));
		input.setRepeating(true);
		input.setPriority('B');

		assertEquals("CompClub: Pcell meeting has been added.", logic.getOutput(input));

		// Add floating task with priority A
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: Get a haircut");
		input.setPriority('A');

		assertEquals("Self: Get a haircut has been added.", logic.getOutput(input));

		// Add scheduled task with priority C
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: Watch running man");
		input.setStartDate(new Date(2014, 11, 3));
		input.setStartTime(new Time(20, 0));
		input.setEndDate(new Date(2014, 11, 3));
		input.setEndTime(new Time(21, 30));
		input.setPriority('C');

		assertEquals("Self: Watch running man has been added.", logic.getOutput(input));
		assertEquals("1. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. Self: Get a haircut\n"
				+ "6. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());
		
		input = new UserInput();
		input.setCommand(COMMAND_TICK);
		input.setIndex(4);

		assertEquals("CompClub: Add actionables on Trello is done!", logic.getOutput(input));
		assertEquals("Listing ticked tasks...", logic.list(LIST_TICKED));
		assertEquals("1. CompClub: Add actionables on Trello\n", logic.list());
		
		input = new UserInput();
		input.setCommand(COMMAND_UNTICK);
		input.setIndex(1);

		assertEquals("CompClub: Add actionables on Trello is back to undone.", logic.getOutput(input));
		assertEquals("Nothing to display", logic.list());
		
		assertEquals("Listing by time...", logic.list(LIST_TIME));
		
		input = new UserInput();
		input.setCommand(COMMAND_KIV);
		input.setIndex(4);

		assertEquals("CompClub: Add actionables on Trello is kept in view.", logic.getOutput(input));
		assertEquals("Listing tasks that are kept in view...", logic.list(LIST_KIV));
		assertEquals("1. CompClub: Add actionables on Trello\n", logic.list());
		
		input = new UserInput();
		input.setCommand(COMMAND_UNKIV);
		input.setIndex(1);

		assertEquals("CompClub: Add actionables on Trello is back to undone.", logic.getOutput(input));
		assertEquals("Nothing to display", logic.list());
		
		
	}
}