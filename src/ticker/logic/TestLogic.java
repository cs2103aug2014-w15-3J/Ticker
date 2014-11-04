//@author A0114535M

/* Team ID: W15-3J
 * Name: Li Jia'En, Nicholette
 * Matric Number: A0114535M
 * Project Title: CE1 TextBuddy
 * Purpose: This class receives text commands from the user and edits a textfile. 
 * The commands are for add, display, delete, clear and exit.
 * Assumptions: 
 * This program assumes that:
 * -the user knows the format for each command
 * -the user input lines in the textfile is not numbered.
 * -(option c) the file is saved to disk when the user exit the program
 */

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

	TickerUI ui = new TickerUI();
	Logic logic = ui.getLogic();
	UserInput input;

	@Test
	public void test() {
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
		assertEquals("", logic.list());

		// Add floating task
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("CompClub: Add actionables on Trello");
		input.setPriority('B');

		assertEquals("CompClub: Add actionables on Trello has been added.", logic.getOutput(input));
		assertEquals("1. CompClub: Add actionables on Trello\n", logic.list());

		// Test undo
		input = new UserInput();
		input.setCommand(COMMAND_UNDO);

		assertEquals("Undoing action", logic.getOutput(input));
		assertEquals("", logic.list());

		// Test undo again
		input = new UserInput();
		input.setCommand(COMMAND_UNDO);

		assertEquals("You have reached the last undo", logic.getOutput(input));
		assertEquals("", logic.list());

		// Test redo
		input = new UserInput();
		input.setCommand(COMMAND_REDO);

		assertEquals("Redoing action", logic.getOutput(input));
		assertEquals("1. CompClub: Add actionables on Trello\n", logic.list());

		// Test redo again
		input = new UserInput();
		input.setCommand(COMMAND_REDO);

		assertEquals("You have reached the last redo", logic.getOutput(input));
		assertEquals("1. CompClub: Add actionables on Trello\n", logic.list());		

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
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov 11:30 to 5 Nov, 14:00\n"
				+ "2. CompClub: Add actionables on Trello\n", logic.list());

		// Add deadlined task
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("HW: Submit CS2106 v0.5");
		input.setEndDate(new Date(2014, 11, 10));
		input.setEndTime(new Time(23, 59));
		input.setPriority('B');

		assertEquals("HW: Submit CS2106 v0.5 has been added.", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. CompClub: Add actionables on Trello\n", logic.list());

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
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. CompClub: Add actionables on Trello\n"
				+ "4. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());

		// Add floating task with priority A
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: Get a haircut");
		input.setPriority('A');

		assertEquals("Self: Get a haircut has been added.", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. CompClub: Add actionables on Trello\n"
				+ "4. Self: Get a haircut\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());
		assertEquals("Listing by priority...", logic.list(LIST_PRIORITY));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());

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
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n", logic.list());

		// Test edit priority
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(1);
		input.setPriority('C');

		assertEquals("Self: Get a haircut has been updated.", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. CompClub: Add actionables on Trello\n"
				+ "4. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "5. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n"
				+ "6. Self: Get a haircut\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit description
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(1);
		input.setDescription("Self: Get some ice-cream");

		assertEquals("Self: Get a haircut has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get some ice-cream\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startDate
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(2);
		input.setStartDate(new Date(2014, 11, 3));

		assertEquals("CompClub: Man welfare pack booth has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 3 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startTime
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(2);
		input.setStartTime(new Time(10, 15));

		assertEquals("CompClub: Man welfare pack booth has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 10:15 to 5 Nov, 2014, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit endDate
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(2);
		input.setEndDate(new Date(2014, 11, 8));

		assertEquals("CompClub: Man welfare pack booth has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 8 Nov, 2014, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit endTime
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(2);
		input.setEndTime(new Time(23, 59));

		assertEquals("CompClub: Man welfare pack booth has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 23:59\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startTime and endTime
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(2);
		input.setStartTime(new Time(6,0));
		input.setEndTime(new Time(20, 0));

		assertEquals("CompClub: Man welfare pack booth has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 6:00 to 5 Nov, 2014, 20:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startDate and endDate
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(2);
		input.setStartDate(new Date(2014, 11, 1));
		input.setEndDate(new Date(2014, 11, 10));

		assertEquals("CompClub: Man welfare pack booth has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 1 Nov, 2014, 11:30 to 10 Nov, 2014, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		//Test delete in priority list
		input = new UserInput();
		input.setCommand(COMMAND_DELETE);
		input.setIndex(2);

		assertEquals("CompClub: Man welfare pack booth has been removed.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. CompClub: Add actionables on Trello\n"
				+ "4. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "5. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input)); 

		// Test edit startDate and endDate
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(2);
		input.setStartDate(new Date(2014, 11, 1));
		input.setEndDate(new Date(2014, 11, 10));

		assertEquals("CompClub: Man welfare pack booth has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 1 Nov, 2014, 11:30 to 10 Nov, 2014, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test tick
		input = new UserInput();
		input.setCommand(COMMAND_TICK);
		input.setIndex(4);

		assertEquals("CompClub: Add actionables on Trello is done!", logic.getOutput(input));
		assertEquals("Listing ticked tasks...", logic.list(LIST_TICKED));
		assertEquals("1. CompClub: Add actionables on Trello\n", logic.list());

		// Test delete from time list
		input = new UserInput();
		input.setCommand(COMMAND_LIST);
		input.setDescription(LIST_TIME);

		assertEquals("Listing by time...", logic.getOutput(input));
		assertEquals("1. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. Self: Get a haircut\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_DELETE);
		input.setIndex(4);

		assertEquals("Self: Get a haircut has been removed.", logic.getOutput(input));
		assertEquals("1. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);

		assertEquals("Undoing action", logic.getOutput(input));


		// Test KIV
		input = new UserInput();
		input.setCommand(COMMAND_KIV);
		input.setIndex(4);

		assertEquals("Self: Get a haircut will be kept in view.", logic.getOutput(input));
		assertEquals("Listing tasks that are kept in view...", logic.list(LIST_KIV));
		assertEquals("1. Self: Get a haircut\n", logic.list());

		// Test illegal list type
		input = new UserInput();
		input.setCommand(COMMAND_LIST);
		input.setDescription("size");

		assertEquals("List does not exist. Please re-enter.", logic.getOutput(input));

		// Test delete from KIV
		input = new UserInput();
		input.setCommand(COMMAND_DELETE);
		input.setIndex(1);

		assertEquals("Self: Get a haircut has been removed.", logic.getOutput(input));
		assertEquals("", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test search for description
		input = new UserInput();
		input.setCommand(COMMAND_SEARCH);
		input.setDescription("CompClub");

		assertEquals("Searching for tasks...", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "3. \\***TICKED***\\\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. \\***KIV***\\\n", logic.list());

		// Test Search for startDate
		input = new UserInput();
		input.setCommand(COMMAND_SEARCH);
		input.setStartDate(new Date(2014, 11, 5));

		assertEquals("Searching for tasks...", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "4. \\***TICKED***\\\n"
				+ "5. \\***KIV***\\\n", logic.list());

		// Test tick for current Search list
		input = new UserInput();
		input.setCommand(COMMAND_TICK);
		input.setIndex(3);

		assertEquals("<Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting is done!", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. \\***TICKED***\\\n"
				+ "4. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "5. \\***KIV***\\\n", logic.list());

		// Test undo for current Search list
		input = new UserInput();
		input.setCommand(COMMAND_UNDO);

		assertEquals("Undoing action", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "4. \\***TICKED***\\\n"
				+ "5. \\***KIV***\\\n", logic.list());

		// Test redo for current Search list
		input = new UserInput();
		input.setCommand(COMMAND_REDO);

		assertEquals("Redoing action", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. \\***TICKED***\\\n"
				+ "4. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "5. \\***KIV***\\\n", logic.list());

		// Test untick for current Search list
		input = new UserInput();
		input.setCommand(COMMAND_UNTICK);
		input.setIndex(4);

		assertEquals("<Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting is back to undone.", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "4. \\***TICKED***\\\n"
				+ "5. \\***KIV***\\\n", logic.list());

		// Test KIV for current Search list
		input = new UserInput();
		input.setCommand(COMMAND_KIV);
		input.setIndex(3);

		assertEquals("<Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting will be kept in view.", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. \\***TICKED***\\\n"
				+ "4. \\***KIV***\\\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());


		// Test unKIV for current Search list
		input = new UserInput();
		input.setCommand(COMMAND_UNKIV);
		input.setIndex(5);

		assertEquals("<Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting is back to undone.", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "4. \\***TICKED***\\\n"
				+ "5. \\***KIV***\\\n", logic.list());


		// Test search for startDate and startTime
		input = new UserInput();
		input.setCommand(COMMAND_SEARCH);
		input.setStartDate(new Date(2014, 11, 5));
		input.setStartTime(new Time(11, 30));

		assertEquals("Searching for tasks...", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "4. \\***TICKED***\\\n"
				+ "5. \\***KIV***\\\n", logic.list());

		// Test search for startDate and startTime
		input = new UserInput();
		input.setCommand(COMMAND_SEARCH);
		input.setStartDate(new Date(2014, 11, 5));
		input.setStartTime(new Time(11, 30));

		assertEquals("Searching for tasks...", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "3. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "4. \\***TICKED***\\\n"
				+ "5. \\***KIV***\\\n", logic.list());

		// Test search for endDate
		input = new UserInput();
		input.setCommand(COMMAND_SEARCH);
		input.setEndDate(new Date(2014, 11, 6));

		assertEquals("Searching for tasks...", logic.getOutput(input));
		assertEquals("1. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "3. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "4. \\***TICKED***\\\n"
				+ "5. \\***KIV***\\\n", logic.list());

		// Test search for endDate and endTime
		input = new UserInput();
		input.setCommand(COMMAND_SEARCH);
		input.setEndDate(new Date(2014, 11, 5));
		input.setEndTime(new Time(15, 0));

		assertEquals("Searching for tasks...", logic.getOutput(input));
		assertEquals("1. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "3. \\***TICKED***\\\n"
				+ "4. \\***KIV***\\\n", logic.list());

		// Test search for startDate and endDate
		input = new UserInput();
		input.setCommand(COMMAND_SEARCH);
		input.setStartDate(new Date(2014, 11, 2));
		input.setEndDate(new Date(2014, 11, 11));

		assertEquals("Searching for tasks...", logic.getOutput(input));
		assertEquals("1. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "5. \\***TICKED***\\\n"
				+ "6. \\***KIV***\\\n", logic.list());

		// Test search priority
		input = new UserInput();
		input.setCommand(COMMAND_SEARCH);
		input.setPriority('A');

		assertEquals("Searching for tasks...", logic.getOutput(input));
		assertEquals("1. \\***TICKED***\\\n"
				+ "2. \\***KIV***\\\n"
				+ "3. Self: Get a haircut\n", logic.list());

		// Delete from tick list
		input = new UserInput();
		input.setCommand(COMMAND_LIST);
		input.setDescription(LIST_TIME);

		assertEquals("Listing by time...", logic.getOutput(input));
		assertEquals("1. Self: Watch running man from 3 Nov, 2014, 20:00 to 3 Nov, 2014, 21:30\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 2014, 11:30 to 5 Nov, 2014, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov, 2014\n"
				+ "4. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());

		// Clear everything and create new cases
		assertEquals("Spick and span!", logic.clear());
		assertEquals("Listing ticked tasks...", logic.list(LIST_TICKED));
		assertEquals("Spick and span!", logic.clear());
		assertEquals("Listing tasks that are kept in view...", logic.list(LIST_KIV));
		assertEquals("Spick and span!", logic.clear());

		// Populate list with floating tasks
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: buy chocolates");

		assertEquals("Self: buy chocolates has been added.", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: play games");

		assertEquals("Self: play games has been added.", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: watch funny videos");

		assertEquals("Self: watch funny videos has been added.", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: play tchoukball");

		assertEquals("Self: play tchoukball has been added.", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: play with cats");

		assertEquals("Self: play with cats has been added.", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: go shopping");

		assertEquals("Self: go shopping has been added.", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_TICK);
		input.setIndex(1);

		assertEquals("Self: buy chocolates is done!", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_TICK);
		input.setIndex(1);

		assertEquals("Self: go shopping is done!", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_KIV);
		input.setIndex(1);

		assertEquals("Self: play games will be kept in view.", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_KIV);
		input.setIndex(1);

		assertEquals("Self: play tchoukball will be kept in view.", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_SEARCH);
		input.setDescription("SELF");

		assertEquals("Searching for tasks...", logic.getOutput(input));
		assertEquals("1. Self: play with cats\n"
				+ "2. Self: watch funny videos\n"
				+ "3. \\***TICKED***\\\n"
				+ "4. Self: go shopping\n"
				+ "5. Self: buy chocolates\n"
				+ "6. \\***KIV***\\\n"
				+ "7. Self: play tchoukball\n"
				+ "8. Self: play games\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_DELETE);
		input.setIndex(2);

		assertEquals("Self: watch funny videos has been removed.", logic.getOutput(input));
		assertEquals("1. Self: play with cats\n"
				+ "2. \\***TICKED***\\\n"
				+ "3. Self: go shopping\n"
				+ "4. Self: buy chocolates\n"
				+ "5. \\***KIV***\\\n"
				+ "6. Self: play tchoukball\n"
				+ "7. Self: play games\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_DELETE);
		input.setIndex(4);

		assertEquals("Self: buy chocolates has been removed.", logic.getOutput(input));
		assertEquals("1. Self: play with cats\n"
				+ "2. \\***TICKED***\\\n"
				+ "3. Self: go shopping\n"
				+ "4. \\***KIV***\\\n"
				+ "5. Self: play tchoukball\n"
				+ "6. Self: play games\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_DELETE);
		input.setIndex(6);

		assertEquals("Self: play games has been removed.", logic.getOutput(input));
		assertEquals("1. Self: play with cats\n"
				+ "2. \\***TICKED***\\\n"
				+ "3. Self: go shopping\n"
				+ "4. \\***KIV***\\\n"
				+ "5. Self: play tchoukball\n", logic.list());

		// Set corner case for search dates without times but occur on the stated date (searched with time)
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: watch anime");
		input.setStartDate(new Date(2014, 11, 10));

		assertEquals("Self: watch anime has been added.", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_SEARCH);
		input.setStartDate(new Date(2014, 11, 10));
		input.setStartTime(new Time(15,0));

		assertEquals("Searching for tasks...", logic.getOutput(input));
		assertEquals("1. Self: watch anime from 10 Nov, 2014\n"
				+ "2. \\***TICKED***\\\n"
				+ "3. \\***KIV***\\\n", logic.list());

		// Set corner case for search dates with endDates without timings that occur on the startDate
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: go shop for clothes");
		input.setEndDate(new Date(2014, 11, 10));

		assertEquals("Self: go shop for clothes has been added.", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_SEARCH);
		input.setStartDate(new Date(2014, 11, 10));
		input.setStartTime(new Time(15,0));

		assertEquals("Searching for tasks...", logic.getOutput(input));
		assertEquals("1. Self: go shop for clothes deadline 10 Nov, 2014\n"
				+ "2. Self: watch anime from 10 Nov, 2014\n"
				+ "3. \\***TICKED***\\\n"
				+ "4. \\***KIV***\\\n", logic.list());

		// Set corner case for search dates with endDates without timings that occur on the endDate

		input = new UserInput();
		input.setCommand(COMMAND_SEARCH);
		input.setEndDate(new Date(2014, 11, 10));
		input.setEndTime(new Time(15,0));

		assertEquals("Searching for tasks...", logic.getOutput(input));
		assertEquals("1. Self: go shop for clothes deadline 10 Nov, 2014\n"
				+ "2. Self: watch anime from 10 Nov, 2014\n"
				+ "3. \\***TICKED***\\\n"
				+ "4. \\***KIV***\\\n", logic.list());

		// Set corner case for search dates with startDates and startTime occuring with searched endDate and endTime
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: go eat desserts");
		input.setStartDate(new Date(2014, 11, 10));
		input.setStartTime(new Time(16,0));

		assertEquals("Self: go eat desserts has been added.", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_SEARCH);
		input.setEndDate(new Date(2014, 11, 10));
		input.setEndTime(new Time(16,0));

		assertEquals("Searching for tasks...", logic.getOutput(input));
		assertEquals("1. Self: go eat desserts from 10 Nov, 2014, 16:00\n"
				+ "2. Self: go shop for clothes deadline 10 Nov, 2014\n"
				+ "3. Self: watch anime from 10 Nov, 2014\n"
				+ "4. \\***TICKED***\\\n"
				+ "5. \\***KIV***\\\n", logic.list());

		// Get error for empty string
		input = new UserInput();
		input.setCommand(COMMAND_ADD);

		assertEquals("Error in input. Either description is missing or date is missing for repeated tasks.", logic.getOutput(input));
		assertEquals("Listing by time...", logic.list(LIST_TIME));
		assertEquals("1. Self: go eat desserts from 10 Nov, 2014, 16:00\n"
				+ "2. Self: go shop for clothes deadline 10 Nov, 2014\n"
				+ "3. Self: watch anime from 10 Nov, 2014\n"
				+ "4. Self: play with cats\n", logic.list());

		// Set deadlined task as repeat
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(2);
		input.setRepeating(true);

		assertEquals("Self: go shop for clothes has been updated.", logic.getOutput(input));
		assertEquals("1. Self: go eat desserts from 10 Nov, 2014, 16:00\n"
				+ "2. Self: watch anime from 10 Nov, 2014\n"
				+ "3. Self: play with cats\n"
				+ "4. <Monday> Self: go shop for clothes\n", logic.list());
	}
}