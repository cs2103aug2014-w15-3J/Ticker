package ticker.logic;

import static org.junit.Assert.*;

import org.junit.Test;

import ticker.common.Date;
import ticker.common.Time;
import ticker.parser.UserInput;

//@author A0114535M
/**
 * Description: This class is an automated test to test out add, delete and edit funtions.
 */
public class CRUManagerTest {
	// String constants for command types
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_LIST = "list";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_UNDO = "undo";
	// String constants for type of lists used
	private static final String LIST_TIME = "time";
	private static final String LIST_PRIORITY = "priority";
	private static final String LIST_TICKED = "ticked";
	private static final String LIST_KIV = "kiv";

	@Test
	public final void testCRUManager() {
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
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "2. CompClub: Add actionables on Trello\n", logic.list());

		// Add deadlined task
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("HW: Submit CS2106 v0.5");
		input.setEndDate(new Date(2014, 11, 10));
		input.setEndTime(new Time(23, 59));
		input.setPriority('B');

		assertEquals("HW: Submit CS2106 v0.5 has been added.", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
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
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "3. CompClub: Add actionables on Trello\n"
				+ "4. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());

		// Add floating task with priority A
		input = new UserInput();
		input.setCommand(COMMAND_ADD);
		input.setDescription("Self: Get a haircut");
		input.setPriority('A');

		assertEquals("Self: Get a haircut has been added.", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "3. CompClub: Add actionables on Trello\n"
				+ "4. Self: Get a haircut\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());

		assertEquals("Listing by priority...", logic.list(LIST_PRIORITY));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
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
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		// Test edit priority
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(1);
		input.setPriority('C');

		assertEquals("Self: Get a haircut has been updated.", logic.getOutput(input));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "3. CompClub: Add actionables on Trello\n"
				+ "4. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "5. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n"
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
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

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
				+ "2. CompClub: Man welfare pack booth from 3 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

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
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 10:15 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

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
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 8 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

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
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 23:59\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

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
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 6:00 to 5 Nov, 20:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

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
				+ "2. CompClub: Man welfare pack booth from 1 Nov, 11:30 to 10 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		//Test delete in priority list
		input = new UserInput();
		input.setCommand(COMMAND_DELETE);
		input.setIndex(2);

		assertEquals("CompClub: Man welfare pack booth has been removed.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "3. CompClub: Add actionables on Trello\n"
				+ "4. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "5. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input)); 

		// Test delete in timed list
		assertEquals("Listing by time...", logic.list(LIST_TIME));
		assertEquals("1. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. Self: Get a haircut\n"
				+ "6. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_DELETE);
		input.setIndex(2);

		assertEquals("CompClub: Man welfare pack booth has been removed.", logic.getOutput(input));

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input)); 

		assertEquals("Listing by priority...", logic.list(LIST_PRIORITY));

		// Test edit startDate and endDate of TimedTask
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(2);
		input.setStartDate(new Date(2014, 11, 1));
		input.setEndDate(new Date(2014, 11, 10));

		assertEquals("CompClub: Man welfare pack booth has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 1 Nov, 11:30 to 10 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startDate and endDate of DeadlineTask
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(3);
		input.setStartDate(new Date(2014, 11, 1));
		input.setEndDate(new Date(2014, 11, 10));

		assertEquals("HW: Submit CS2106 v0.5 has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. HW: Submit CS2106 v0.5 from 1 Nov, 0:00 to 10 Nov, 23:59\n"
				+ "3. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startDate and endDate of FloatingTask
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(4);
		input.setStartDate(new Date(2014, 11, 1));
		input.setEndDate(new Date(2014, 11, 10));

		assertEquals("CompClub: Add actionables on Trello has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Add actionables on Trello from 1 Nov, 0:00 to 10 Nov, 23:59\n"
				+ "3. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "4. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startDate and endDate of RepeatingTask
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(5);
		input.setStartDate(new Date(2014, 11, 1));
		input.setEndDate(new Date(2014, 11, 10));

		assertEquals("CompClub: Pcell meeting has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Saturday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startDate on deadline task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(3);
		input.setStartDate(new Date(2014, 11, 9));

		assertEquals("HW: Submit CS2106 v0.5 has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 from 9 Nov, 0:00 to 10 Nov, 23:59\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startDate on floating task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(4);
		input.setStartDate(new Date(2014, 11, 10));

		assertEquals("CompClub: Add actionables on Trello has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. CompClub: Add actionables on Trello from 10 Nov, 0:00\n"
				+ "4. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startDate on repeating task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(5);
		input.setStartDate(new Date(2014, 11, 4));

		assertEquals("CompClub: Pcell meeting has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Tuesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startDate on scheduled task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(6);
		input.setStartDate(new Date(2014, 11, 2));

		assertEquals("Self: Watch running man has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 2 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit endDate on deadline task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(3);
		input.setEndDate(new Date(2014, 11, 11));

		assertEquals("HW: Submit CS2106 v0.5 has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 11 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit endDate on floating task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(4);
		input.setEndDate(new Date(2014, 11, 11));

		assertEquals("CompClub: Add actionables on Trello has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello deadline 23:59, 11 Nov\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit endDate on scheduled task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(5);
		input.setStartDate(new Date(2014, 11, 11));

		assertEquals("CompClub: Pcell meeting has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Tuesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));
		
		// Test edit startTime and endTime of TimedTask
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(2);
		input.setStartTime(new Time(0, 0));
		input.setEndTime(new Time(23, 59));

		assertEquals("CompClub: Man welfare pack booth has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 0:00 to 5 Nov, 23:59\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startTime and endTime of DeadlineTask
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(3);
		input.setStartTime(new Time(0, 0));
		input.setEndTime(new Time(23, 59));

		assertEquals("HW: Submit CS2106 v0.5 has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 from 10 Nov, 0:00 to 10 Nov, 23:59\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startTime and endTime of FloatingTask 
		// Test date varies according to current date
		/*
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(4);
		input.setStartTime(new Time(0, 0));
		input.setEndTime(new Time(23, 59));

		assertEquals("CompClub: Add actionables on Trello has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. CompClub: Add actionables on Trello from 10 Nov, 0:00 to 10 Nov, 23:59\n"
				+ "4. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));
		*/
		
		// Test edit startTime and endTime of RepeatingTask
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(5);
		input.setStartTime(new Time(0, 0));
		input.setEndTime(new Time(23, 59));

		assertEquals("CompClub: Pcell meeting has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 0:00 to 23:59) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startTime on deadline task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(3);
		input.setStartTime(new Time(0, 0));

		assertEquals("HW: Submit CS2106 v0.5 has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 from 10 Nov, 0:00 to 10 Nov, 23:59\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));
		
		// Start date on floating task is dependent on current date
		// Test edit startTime on floating task
		/*
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(4);
		input.setStartTime(new Time(0, 0));

		assertEquals("CompClub: Add actionables on Trello has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. CompClub: Add actionables on Trello from 10 Nov, 0:00\n"
				+ "4. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));
		*/
		
		// Test edit startTime on repeating task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(5);
		input.setStartTime(new Time(0, 0));

		assertEquals("CompClub: Pcell meeting has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 0:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit startTime on scheduled task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(6);
		input.setStartTime(new Time(0, 0));

		assertEquals("Self: Watch running man has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 0:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit endTime on deadline task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(3);
		input.setEndTime(new Time(20, 59));

		assertEquals("HW: Submit CS2106 v0.5 has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. HW: Submit CS2106 v0.5 deadline 20:59, 10 Nov\n"
				+ "4. CompClub: Add actionables on Trello\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));
		
		// EndDate depends on the current date
		// Test edit endTime on floating task
		/*
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(4);
		input.setEndTime(new Time(23, 59));

		assertEquals("CompClub: Add actionables on Trello has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. CompClub: Add actionables on Trello deadline 23:59, 10 Nov\n"
				+ "4. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));
		*/

		// Test edit repeating on deadline task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(3);
		input.setRepeating(true);

		assertEquals("HW: Submit CS2106 v0.5 has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. CompClub: Add actionables on Trello\n"
				+ "4. <Monday> (end at 23:59) HW: Submit CS2106 v0.5\n"
				+ "5. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit repeating on floating task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(4);
		input.setRepeating(true);

		assertEquals("Invalid edit to repeated task. Missing date", logic.getOutput(input));

		// Test edit repeating on repeating task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(5);
		input.setRepeating(true);

		assertEquals("CompClub: Pcell meeting has been updated.", logic.getOutput(input));
		assertEquals("1. Self: Get a haircut\n"
				+ "2. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "3. CompClub: Pcell meeting from 5 Nov, 16:00 to, 18:00\n"
				+ "4. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "5. CompClub: Add actionables on Trello\n"
				+ "6. Self: Watch running man from 3 Nov, 20:00 to 3 Nov, 21:30\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

		// Test edit repeating on scheduled task
		input = new UserInput();
		input.setCommand(COMMAND_EDIT);
		input.setIndex(6);
		input.setRepeating(true);

		assertEquals("Self: Watch running man has been updated.", logic.getOutput(input));
		assertEquals("Listing by time...", logic.list(LIST_TIME));
		assertEquals("1. CompClub: Man welfare pack booth from 5 Nov, 11:30 to 5 Nov, 14:00\n"
				+ "2. HW: Submit CS2106 v0.5 deadline 23:59, 10 Nov\n"
				+ "3. CompClub: Add actionables on Trello\n"
				+ "4. Self: Get a haircut\n"
				+ "5. <Monday> (from 20:00 to 21:30) Self: Watch running man\n"
				+ "6. <Wednesday> (from 16:00 to 18:00) CompClub: Pcell meeting\n", logic.list());

		input = new UserInput();
		input.setCommand(COMMAND_UNDO);
		assertEquals("Undoing action", logic.getOutput(input));

	}
}
