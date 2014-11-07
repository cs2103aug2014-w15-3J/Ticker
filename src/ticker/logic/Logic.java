//@author A0114535M

/* Team ID: W15-3J
 * Name: Li Jia'En, Nicholette
 * Matric Number: A0114535M
 * 
 * Project Title: Ticker
 * Class: Logic
 * Description: This class passes the user input from UI to the Parser to process the input. Logic class then receives the
 * processed command and acts on it. Functions provided include adding task, deleting task, edit an existing task,
 * listing out the tasks in different formats (e.g priority, time, done and cannot be completed), as well as
 * searching and auto-complete.
 * 
 * Assumptions: 
 * This program assumes that:
 * -the Parser class will pass Logic class valid processed user input (as an UserInput object) with data at their correct
 * positions.
 * -the Logic class will always be used with classes CRUDManager, TickKIVManager, UndoRedoManager and SearchManager.
 */

package ticker.logic;

// Package Parser
import ticker.parser.Parser;
import ticker.parser.UserInput;
// Package Storage
import ticker.storage.Storage;
// Package UI
import ticker.ui.TickerUI;
// Package Common
import ticker.common.Task;
import ticker.common.sortByTime;
import ticker.common.sortByPriority;

// Package Java util
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: make UI an observer
// TODO: check description by end of project

public class Logic{
	// CONSTANTS
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
	private static final String COMMAND_SEARCH_FREESLOTS = "searchfree";
	private static final String COMMAND_TAKE = "take";
	// Integer key constants for lists used by listTracker
	private static final int KEY_SORTED_TIME = 1;
	private static final int KEY_SORTED_PRIORITY = 2;
	private static final int KEY_TICKED = 3;
	private static final int KEY_KIV = 4;
	private static final int KEY_SEARCH = 5;
	private static final int KEY_FREESLOTS = 6;
	// String constants for type of lists used
	private static final String LIST_TIME = "time";
	private static final String LIST_PRIORITY = "priority";
	private static final String LIST_TICKED = "ticked";
	private static final String LIST_KIV = "kiv";
	private static final String LIST_SEARCH = "search";
	private static final String LIST_FREESLOTS = "free";

	// Instances of other components
	private Parser parser;
	private Storage storage;
	private TickerUI UI;
	private UndoManager undoMng;
	private CRUManager cruMng;
	private TickKIVManager tickKIVMng;
	private SearchManager searchMng;
	private static Logger logger;
	// Tracker to track which list is being displayed
	private static Integer listTracker;
	private static String currentListName;
	// Pointer to the Vector currently in display
	private static Vector<Task> current;
	// Temporary sorted storages
	private static Vector<Task> sortedTime;
	private static Vector<Task> sortedPriority;
	private static Vector<Task> listTicked; // not sorted
	private static Vector<Task> listKIV; // not sorted
	private static Vector<Task> searchResults;
	// Store existing (current) search request
	private static UserInput searchRequest;

	/**
	 * This method instantiate a Logic object while creating dependency with TickerUI
	 *
	 * @param UI	Name of TickerUI.
	 */
	public Logic(TickerUI UI){
		this.UI = UI;

		// Instantiating sub-components
		parser = new Parser();
		storage = new Storage();
		logger = Logger.getLogger("Logic");

		sortedTime = storage.restoreDataFromFile(KEY_SORTED_TIME);
		sortedPriority = storage.restoreDataFromFile(KEY_SORTED_PRIORITY);
		listTicked = storage.restoreDataFromFile(KEY_TICKED);
		listKIV = storage.restoreDataFromFile(KEY_KIV);

		cruMng = new CRUManager(sortedTime, sortedPriority, listTicked, listKIV);
		tickKIVMng = new TickKIVManager(sortedTime, sortedPriority, listTicked, listKIV);
		searchMng = new SearchManager(sortedTime, sortedPriority, listTicked, listKIV);
		undoMng = UndoManager.getInstance(sortedTime, sortedPriority, listTicked, listKIV);

		searchResults = new Vector<Task>();

		current = sortedTime;
		listTracker = KEY_SORTED_TIME;
		currentListName = LIST_TIME;

		checkForTaskExpiry();
		UI.setList(current);
		UI.setNextView(listTracker);

	}

	/**
	 * This method is for UI to call logic and for logic to pass the string to parser to process user input
	 *
	 * @param input		Name of user input string
	 * @return    		Message from the command operation.
	 */
	public String getLogic(String input) {
		// Crash the program if Logic is contructed without TickerUI, missing dependency
		assert(UI != null);

		UserInput processed = parser.processInput(input);
		return getOutput(processed);
	}

	/**
	 * This method gets the feedback from the command operation and updates the UI display if applicable
	 *
	 * @param processed 	Name of UserInput object with processed user input returned by Parser object.
	 * @return    			Message from the command operation.
	 */
	protected String getOutput(UserInput processed) {

		String feedback = "";
		String command = "";
		logger.log(Level.INFO, "Performing an action");

		try {
			command = processed.getCommand();
		}

		catch (NullPointerException ep) {
			logger.log(Level.WARNING, "NO COMMANDS PASSED");
			System.out.println("Parser just sent a null command");
		}

		switch(command){
		case COMMAND_TAKE:
			try {
				if (listTracker != KEY_FREESLOTS) {
					return "Invalid use of take. Please use it only with searching for freeslots.";
				}
				feedback = searchMng.take(processed.getIndex(), processed.getDescription());
			}
			catch (Exception e) {
				
			}
			break;
			
		case COMMAND_SEARCH_FREESLOTS:
			//try {
				searchRequest = processed;
				searchResults.removeAllElements();
				
				searchResults = searchMng.searchForFreeSlots(processed.getStartDate(), processed.getStartTime(), 
						processed.getEndDate(), processed.getEndTime());
				
				listTracker = KEY_FREESLOTS;
				current = searchResults;
				currentListName = LIST_FREESLOTS;
				
				UI.setList(current);
				UI.setNextView(listTracker);
				
				feedback = "Searching for free slots....";
			
			//}
			/*catch (Exception e) {
				System.out.println("error in search for free timeslots");
			}*/
			break;
			
		case COMMAND_SEARCH: 
			try {
				searchRequest = processed;
				searchResults.removeAllElements();
				
				searchResults = searchMng.search(processed.getDescription(), processed.getRepeating(), processed.getStartDate(), 
						processed.getEndDate(), processed.getStartTime(), processed.getEndTime(), processed.getPriority());
				
				checkForTaskExpiry();

				listTracker = KEY_SEARCH;
				current = searchResults;
				currentListName = LIST_SEARCH;

				UI.setList(current);
				UI.setNextView(listTracker);
				
				feedback = "Searching for tasks...";
								
			}
			catch (Exception e) {
				System.out.println("error in search");
			}

			break;

		case COMMAND_DELETE: 
			try {
				feedback = cruMng.delete(processed.getIndex(), listTracker, current, currentListName);

				checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
				UI.setNextView(listTracker);
			}
			catch (ArrayIndexOutOfBoundsException ex) {
				return "Index out of bounds. Nothing has been deleted.";
			}
			break;

		case COMMAND_CLEAR:
			feedback = this.clear(); 
			break;

		case COMMAND_LIST:
			try {
				checkForTaskExpiry();
				feedback = this.list(processed.getDescription());
			}
			catch (IllegalArgumentException ex) {
				System.out.println("Wrong list name from parser");
				return "List does not exist. Please re-enter.";
			}
			break;


		case COMMAND_EDIT:
			try {

				feedback = cruMng.edit(processed.getIndex(), processed.getDescription(), processed.getRepeating(), processed.getStartDate(), 
						processed.getEndDate(), processed.getStartTime(), processed.getEndTime(), processed.getPriority(), listTracker, current);

				if (listTracker == KEY_SEARCH) {
					searchResults.removeAllElements();
					searchResults = searchMng.search(searchRequest.getDescription(), searchRequest.getRepeating(), searchRequest.getStartDate(), 
							searchRequest.getEndDate(), searchRequest.getStartTime(), searchRequest.getEndTime(), searchRequest.getPriority());
				}

				checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
				UI.setNextView(listTracker);
			}
			catch (ArrayIndexOutOfBoundsException ex) {
				return "Index out of bounds. Nothing has been edited.";
			}
			catch (IllegalArgumentException ex) {
				return "Task description is empty. Please re-enter.";
			}
			break;

		case COMMAND_ADD:
			try {
				feedback = cruMng.add(processed.getDescription(), processed.getRepeating(), processed.getStartDate(), 
						processed.getEndDate(), processed.getStartTime(), processed.getEndTime(), processed.getPriority());

				if (listTracker == KEY_KIV || listTracker == KEY_TICKED || listTracker == KEY_SEARCH || listTracker == KEY_FREESLOTS) {
					listTracker = KEY_SORTED_TIME;
					current = sortedTime;
					currentListName = LIST_TIME;
				}

				checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
				UI.setNextView(listTracker);
			}
			catch (IllegalArgumentException ex) {
				return "Error in input. Either description is missing or date is missing for repeated tasks.";
			}
			break;

		case COMMAND_KIV:
			try {
				feedback = tickKIVMng.kiv(processed.getIndex(), listTracker, current, currentListName);

				if (listTracker == KEY_SEARCH) {
					searchResults.removeAllElements();
					searchResults = searchMng.search(searchRequest.getDescription(), searchRequest.getRepeating(), searchRequest.getStartDate(), 
							searchRequest.getEndDate(), searchRequest.getStartTime(), searchRequest.getEndTime(), searchRequest.getPriority());
				}

				checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
				UI.setNextView(listTracker);
			}
			catch (ArrayIndexOutOfBoundsException ex) {
				return "Index out of bounds. Nothing has been marked as cannot do.";
			}
			/*catch (IllegalArgumentException ex) {
				return "Current list: " + currentListName + "Cannot perform command on this list";
			}*/
			break;

		case COMMAND_UNKIV:
			try {
				feedback = tickKIVMng.unkiv(processed.getIndex(), listTracker, current);

				if (listTracker == KEY_SEARCH) {
					searchResults.removeAllElements();
					searchResults = searchMng.search(searchRequest.getDescription(), searchRequest.getRepeating(), searchRequest.getStartDate(), 
							searchRequest.getEndDate(), searchRequest.getStartTime(), searchRequest.getEndTime(), searchRequest.getPriority());
				}

				checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
				UI.setNextView(listTracker);
			}
			catch (ArrayIndexOutOfBoundsException ex) {
				return "Index out of bounds. Nothing has been unmarked as cannot do.";
			}
			/*catch (IllegalArgumentException ex) {
				return "Current list: " + currentListName + "Cannot perform command on this list";
			}*/
			break;

		case COMMAND_UNDO:
			try {
				feedback = undoMng.undo();

				if (listTracker == KEY_SEARCH) {
					searchResults.removeAllElements();
					searchResults = searchMng.search(searchRequest.getDescription(), searchRequest.getRepeating(), searchRequest.getStartDate(), 
							searchRequest.getEndDate(), searchRequest.getStartTime(), searchRequest.getEndTime(), searchRequest.getPriority());
				}

				checkForTaskExpiry();
				sortLists();
				UI.setList(current);
				UI.setNextView(listTracker);
			}
			catch (NullPointerException ex) {
				System.out.println("Error with UndoManager");
			}
			return feedback;

		case COMMAND_REDO:
			try {
				feedback = undoMng.redo();

				if (listTracker == KEY_SEARCH) {
					searchResults.removeAllElements();
					searchResults = searchMng.search(searchRequest.getDescription(), searchRequest.getRepeating(), searchRequest.getStartDate(), 
							searchRequest.getEndDate(), searchRequest.getStartTime(), searchRequest.getEndTime(), searchRequest.getPriority());

				}

				checkForTaskExpiry();
				sortLists();
				UI.setList(current);
				UI.setNextView(listTracker);
			}
			catch (NullPointerException ex) {
				System.out.println("Error with UndoManager");
			}
			return feedback;

		case COMMAND_TICK:
			try {
				feedback = tickKIVMng.tick(processed.getIndex(), listTracker, current);

				if (listTracker == KEY_SEARCH) {
					searchResults.removeAllElements();
					searchResults = searchMng.search(searchRequest.getDescription(), searchRequest.getRepeating(), searchRequest.getStartDate(), 
							searchRequest.getEndDate(), searchRequest.getStartTime(), searchRequest.getEndTime(), searchRequest.getPriority());
				}

				checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
				UI.setNextView(listTracker);
			}
			catch (ArrayIndexOutOfBoundsException ex) {
				return "Index out of bounds. Nothing has been ticked.";
			}
			/*catch (IllegalArgumentException ex) {
				return "Current list: " + currentListName + "Cannot perform command on this list";
			}*/
			break;

		case COMMAND_UNTICK:
			try {
				feedback = tickKIVMng.untick(processed.getIndex(), listTracker, current);

				if (listTracker == KEY_SEARCH) {
					searchResults.removeAllElements();
					searchResults = searchMng.search(searchRequest.getDescription(), searchRequest.getRepeating(), searchRequest.getStartDate(), 
							searchRequest.getEndDate(), searchRequest.getStartTime(), searchRequest.getEndTime(), searchRequest.getPriority());
				}

				checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
				UI.setNextView(listTracker);
			}
			catch (ArrayIndexOutOfBoundsException ex) {
				return "Index out of bounds. Nothing has been unticked.";
			}
			//catch (IllegalArgumentException ex) {
			//return "Current list: " + currentListName + "Cannot perform command on this list";
			//}
			break;


		case COMMAND_HELP:
			feedback = "help is on the way!";
			UI.setHelp();
			break;

		default:
			feedback = "invalid command";
			break;
		}

		logger.log(Level.INFO, "Action proceeded successfully");
		return feedback;
	}

	/**
	 * This method checks for expired tasks and updates their attribute isExpired.
	 */
	private void checkForTaskExpiry() {
		for (Task timeTask: sortedTime) {
			timeTask.isExpired();
		}
		for (Task priorityTask: sortedTime) {
			priorityTask.isExpired();
		}
		for (Task kivTask: listKIV) {
			kivTask.isExpired();
		}
		for (Task tickedTask: listTicked) {
			tickedTask.isExpired();
		}
	}

	/**
	 * This method writes the lists into storage.
	 */
	private void storeLists() {
		storage.writeStorageArrayIntoFile(KEY_SORTED_TIME, sortedTime);
		storage.writeStorageArrayIntoFile(KEY_SORTED_PRIORITY, sortedPriority);
		storage.writeStorageArrayIntoFile(KEY_TICKED, listTicked);
		storage.writeStorageArrayIntoFile(KEY_KIV, listKIV);
	}

	/**
	 * This method sorts the time and priority lists.
	 */
	private void sortLists() {
		Collections.sort(sortedTime, new sortByTime());
		Collections.sort(sortedPriority, new sortByPriority());
	}

	/**
	 * This method clears the current list.
	 * 
	 * @return     Message from the operation clear().
	 */
	protected String clear() {
		current.removeAllElements();

		if (listTracker == KEY_SORTED_TIME || listTracker == KEY_SORTED_PRIORITY) {
			sortedTime.removeAllElements();
			sortedPriority.removeAllElements();
		}

		storeLists();
		UI.setList(current);
		UI.setNextView(listTracker);

		return "Spick and span!";
	}

	/**
	 * This method lists the current task list in string form. This
	 * This is used by TestLogic class for testing without TickerUI.
	 *
	 * @return     List of tasks in string format.
	 */
	protected String list() {
		if (current == null) {
			return "Nothing to display.\n";
		}
		int i = 0;
		String list = "";
		for (Task task: current) {

			list += ++i + ". " + task.toString() + "\n";
		}
		return list;
	}

	/**
	 * This method displays the list requested by the user
	 *
	 * @param listType		Name of list that the user wants displayed
	 * @return     			Feedback message for listing a list type
	 * @throws IllegalArgumentException  If list name is unidentifiable.
	 */
	protected String list(String listType) throws IllegalArgumentException {
		switch (listType) {
		case LIST_TIME:
			current = sortedTime;
			listTracker = KEY_SORTED_TIME;
			currentListName = LIST_TIME;
			UI.setList(current);
			UI.setNextView(listTracker);
			return "Listing by time...";
		case LIST_PRIORITY:
			current = sortedPriority;
			listTracker = KEY_SORTED_PRIORITY;
			currentListName = LIST_PRIORITY;
			UI.setList(current);
			UI.setNextView(listTracker);
			return "Listing by priority...";
		case LIST_TICKED:
			current = listTicked;
			listTracker = KEY_TICKED;
			currentListName = LIST_TICKED;
			UI.setList(current);
			UI.setNextView(listTracker);
			return "Listing ticked tasks...";
		case COMMAND_KIV:
			current = listKIV;
			listTracker = KEY_KIV;
			currentListName = LIST_KIV;
			UI.setList(current);
			UI.setNextView(listTracker);
			return "Listing tasks that are kept in view...";
		default:
			throw new IllegalArgumentException();
		}
	}
}

//TODO: 
//-refactor the code and make it neat