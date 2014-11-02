package ticker.logic;

import ticker.common.Date;
import ticker.common.Time;
import ticker.parser.UserInput;
import ticker.ui.TickerUI;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestLogic {
	// String constants for command types
	private static final String COMMAND_HELP = "help";
	private static final String COMMAND_UNTICK = "untick";
	private static final String COMMAND_TICK = "tick";
	private static final String COMMAND_REDO = "redo";
	private static final String COMMAND_UNDO = "undo";
	private static final String COMMAND_UNCMI = "uncmi";
	private static final String COMMAND_CMI = "cmi";
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_LIST = "list";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_SEARCH = "search";
	// String constants for type of lists used
	private static final String TASKS_TIME = "time";
	private static final String TASKS_PRIORITY = "priority";
	private static final String TASKS_TICKED = "ticked";
	private static final String TASKS_CMI = "cmi";
	private static final String TASKS_SEARCH = "search";
	
	TickerUI ui = new TickerUI();
	Logic logic = ui.getLogic();
	UserInput input;

	@Test
	public void test() {
		// Clear the list before every testing
		assertEquals("Spick and span!", logic.clear());
		assertEquals("", logic.list());

		// Add floating task
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("CompClub: Add actionables on Trello");

		assertEquals("CompClub: Add actionables on Trello has been added.\n", logic.getOutput(input));
		assertEquals("1. CompClub: Add actionables on Trello\n", logic.list());

		// Add scheduled task
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("CompClub: Man welfare pack booth");
		input.setStartDate(new Date(2014, 11, 5));
		input.setStartTime(new Time(11, 30));
		input.setEndDate(new Date(2014, 11, 5));
		input.setEndTime(new Time(14, 0));
		
		assertEquals("CompClub: Man welfare pack booth has been added.\n", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. CompClub: Add actionables on Trello\n", logic.list());
		
		// Add deadlined task
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("HW: Submit CS2106 v0.5");
		input.setEndDate(new Date(2014, 11, 10));
		input.setEndTime(new Time(23, 59));
		
		assertEquals("HW: Submit CS2106 v0.5 has been added.\n", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. CompClub: Add actionables on Trello\n", logic.list());
		
		// Add repeated task
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("CompClub: Pcell meeting");
		input.setStartDate(new Date(2014, 11, 5));
		input.setStartTime(new Time(16, 0));
		input.setEndTime(new Time(18, 0));
		input.setRepeating(true);
		
		assertEquals("CompClub: Pcell meeting has been added.\n", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. CompClub: Add actionables on Trello\n"
				+ "4. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());
		
		// Add floating task with priority A
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: Get a haircut");
		input.setPriority('A');
		
		assertEquals("Self: Get a haircut has been added.\n", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. CompClub: Add actionables on Trello\n"
				+ "4. Self: Get a haircut\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());
		assertEquals("Listing by priority...", logic.list(TASKS_PRIORITY));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());
		

		/*
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
		assertEquals("Displaying search results", ui.getLogic().getLogic("search \"aaa\""));
		*/


	}


}