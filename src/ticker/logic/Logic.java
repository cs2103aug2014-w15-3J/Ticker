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

// TODO: make logic.java an interface
// TODO: make UI an observer
// TODO: check description by end of project

/*
 * Class: Logic
 * Description: Passes the user input from UI to the Parser to process the input. Logic then receives the
 * processed command and acts on it. Functions provided include adding task, deleting task, edit an existing task,
 * listing out the tasks in different formats (e.g priority, time, done and cannot be completed), as well as
 * searching and auto-complete.
 */

public class Logic{
	// CONSTANTS
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
	// Integer key constants for lists used by listTracker
	private static final int KEY_SORTED_TIME = 1;
	private static final int KEY_SORTED_PRIORITY = 2;
	private static final int KEY_TICKED = 3;
	private static final int KEY_CMI = 4;
	private static final int KEY_SEARCH = 5;
	// String constants for type of lists used
	private static final String TASKS_TIME = "TIME";
	private static final String TASKS_PRIORITY = "PRIORITY";
	private static final String TASKS_TICKED = "TICKED";
	private static final String TASKS_CMI = "CMI";
	private static final String TASKS_SEARCH = "SEARCH";

	// Instances of other components
	private Parser parser;
	private Storage storage;
	private TickerUI UI;
	private UndoManager undoMng;
	private CRUManager cruMng;
	private TickCMIManager tickCMIMng;
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
	private static Vector<Task> listCMI; // not sorted
	private static Vector<Task> searchResults;
	// Store existing (current) search request
	private static UserInput searchRequest;

	public Logic() {
	}

	public Logic(TickerUI UI){
		// Creating 1-1 dependency with UI
		this.UI = UI;

		// Instantiating sub-components
		parser = new Parser();
		storage = new Storage();
		logger = Logger.getLogger("Logic");

		sortedTime = storage.restoreDataFromFile(KEY_SORTED_TIME);
		sortedPriority = storage.restoreDataFromFile(KEY_SORTED_PRIORITY);
		listTicked = storage.restoreDataFromFile(KEY_TICKED);
		listCMI = storage.restoreDataFromFile(KEY_CMI);

		cruMng = new CRUManager(sortedTime, sortedPriority, listTicked, listCMI);
		tickCMIMng = new TickCMIManager(sortedTime, sortedPriority, listTicked, listCMI);
		searchMng = new SearchManager(sortedTime, sortedPriority, listTicked, listCMI);
		undoMng = UndoManager.getInstance(sortedTime, sortedPriority, listTicked, listCMI);

		searchResults = new Vector<Task>();

		current = sortedTime;
		listTracker = KEY_SORTED_TIME;
		currentListName = TASKS_TIME;

		//checkForTaskExpiry();
		UI.setList(current);

	}

	
	public String getLogic(String input) {
		// Crash the program if Logic is contructed without TickerUI, missing dependency
		assert(UI != null);

		String feedback = "";
		String command = "";
		UserInput processed = parser.processInput(input);

		logger.log(Level.INFO, "Performing an action");

		try {
			command = processed.getCommand();
		}

		catch (NullPointerException ep) {
			logger.log(Level.WARNING, "NO COMMANDS PASSED");
			System.out.println("Parser just sent a null command");
		}

		switch(command){
		case COMMAND_SEARCH: 
			try {
				searchRequest = processed;
				searchResults.removeAllElements();
				searchResults = searchMng.search(processed.getDescription(), processed.getRepeating(), processed.getStartDate(), 
						processed.getEndDate(), processed.getStartTime(), processed.getEndTime(), processed.getPriority());
				
				//checkForTaskExpiry();
				
				listTracker = KEY_SEARCH;
				current = searchResults;
				currentListName = TASKS_SEARCH;
				
				UI.setList(current);
				
				feedback = "searching for tasks...";
			}
			catch (Exception e) {
				System.out.println("error in search");
			}
			
			break;

		case COMMAND_DELETE: 
			try {
				feedback = cruMng.delete(processed.getIndex(), listTracker, current, currentListName);

				//checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
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
				//checkForTaskExpiry();
				feedback = this.list(processed.getDescription());
			}
			catch (IllegalArgumentException ex) {
				System.out.println("Wrong list name from parser");
				return "List does not exist. Please re-enter.";
			}
			break;


		case COMMAND_EDIT:
			try {
				if (listTracker == KEY_CMI || listTracker == KEY_TICKED || listTracker == KEY_SEARCH) {
					return "Cannot perform edit in this list";
				}

				feedback = cruMng.edit(processed.getIndex(), processed.getAppending(),
						processed.getDescription(), listTracker, current);
				//checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
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

				if (listTracker == KEY_CMI || listTracker == KEY_TICKED || listTracker == KEY_SEARCH) {
					listTracker = KEY_SORTED_TIME;
					current = sortedTime;
					currentListName = TASKS_TIME;
				}

				//checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
			}
			catch (IllegalArgumentException ex) {
				return "Error in input. Either description is missing or date is missing for repeated tasks.";
			}
			break;

		case COMMAND_CMI:
			try {
				feedback = tickCMIMng.cmi(processed.getIndex(), listTracker, current, currentListName);

				//checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
			}
			catch (ArrayIndexOutOfBoundsException ex) {
				return "Index out of bounds. Nothing has been marked as cannot do.";
			}
			catch (IllegalArgumentException ex) {
				return "Current list: " + currentListName + "Cannot perform command on this list";
			}
			break;

		case COMMAND_UNCMI:
			try {
				feedback = tickCMIMng.uncmi(processed.getIndex(), listTracker, current);

				//checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
			}
			catch (ArrayIndexOutOfBoundsException ex) {
				return "Index out of bounds. Nothing has been unmarked as cannot do.";
			}
			catch (IllegalArgumentException ex) {
				return "Current list: " + currentListName + "Cannot perform command on this list";
			}
			break;

		case COMMAND_UNDO:
			try {
				undoMng.undo();
				
				if (listTracker == KEY_SEARCH) {
					searchResults.removeAllElements();
					searchResults = searchMng.search(searchRequest.getDescription(), searchRequest.getRepeating(), searchRequest.getStartDate(), 
							searchRequest.getEndDate(), searchRequest.getStartTime(), searchRequest.getEndTime(), searchRequest.getPriority());
					
				}

				//checkForTaskExpiry();
				sortLists();
				UI.setList(current);
			}
			catch (NullPointerException ex) {
				System.out.println("Error with UndoManager");
			}
			return "undoing action";

		case COMMAND_REDO:
			try {
				undoMng.redo();
				
				if (listTracker == KEY_SEARCH) {
					searchResults.removeAllElements();
					searchResults = searchMng.search(searchRequest.getDescription(), searchRequest.getRepeating(), searchRequest.getStartDate(), 
							searchRequest.getEndDate(), searchRequest.getStartTime(), searchRequest.getEndTime(), searchRequest.getPriority());
					
				}

				//checkForTaskExpiry();
				sortLists();
				UI.setList(current);
			}
			catch (NullPointerException ex) {
				System.out.println("Error with UndoManager");
			}
			return "redoing action";

		case COMMAND_TICK:
			try {
				feedback = tickCMIMng.tick(processed.getIndex(), listTracker, current);

				//checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
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
				feedback = tickCMIMng.untick(processed.getIndex(), listTracker, current);

				//checkForTaskExpiry();
				sortLists();
				storeLists();
				UI.setList(current);
			}
			catch (ArrayIndexOutOfBoundsException ex) {
				return "Index out of bounds. Nothing has been unticked.";
			}
			catch (IllegalArgumentException ex) {
				return "Current list: " + currentListName + "Cannot perform command on this list";
			}
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
	 * 
	 */
	/*private void checkForTaskExpiry() {
		for (Task timeTask: sortedTime) {
			timeTask.isExpired();
		}
		for (Task priorityTask: sortedTime) {
			priorityTask.isExpired();
		}
		for (Task cmiTask: listCMI) {
			cmiTask.isExpired();
		}
		for (Task tickedTask: listTicked) {
			tickedTask.isExpired();
		}
	}*/



	/**
	 * 
	 */
	private void storeLists() {
		storage.writeStorageArrayIntoFile(KEY_SORTED_TIME, sortedTime);
		storage.writeStorageArrayIntoFile(KEY_SORTED_PRIORITY, sortedPriority);
		storage.writeStorageArrayIntoFile(KEY_TICKED, listTicked);
		storage.writeStorageArrayIntoFile(KEY_CMI, listCMI);
	}

	/**
	 * 
	 */
	private void sortLists() {
		Collections.sort(sortedTime, new sortByTime());
		Collections.sort(sortedPriority, new sortByPriority());
	}

	private String clear() {
		current.removeAllElements();
		
		if (listTracker == KEY_SORTED_TIME || listTracker == KEY_SORTED_PRIORITY) {
			sortedTime.removeAllElements();
			sortedPriority.removeAllElements();
		}

		storeLists();
		UI.setList(current);

		return "Spick and span!";
	}

	/*private String list() {
		if (current == null) {
			return "Nothing to display.\n";
		}
		int i = 0;
		String list = "";
		for (Task task: current) {

			list += ++i + ". " + task.toString() + "\n";
		}
		return list;
	}*/

	/*private String listSearch() {
		if (current == null) {
			return "Nothing to display.\n";
		}
		// int i = 0;
		String list = "";
		for (Task task: searchResults) {

			// list += ++i + ". " + task.toString() + "\n";
			list += task.toString() + "\n";
		}
		return list;
	}*/

	private String list(String listType) throws IllegalArgumentException {
		switch (listType) {
		case "time":
			current = sortedTime;
			listTracker = KEY_SORTED_TIME;
			currentListName = TASKS_TIME;
			UI.setList(current);
			return "Listing by time...";
		case "priority":
			current = sortedPriority;
			listTracker = KEY_SORTED_PRIORITY;
			currentListName = TASKS_PRIORITY;
			UI.setList(current);
			return "Listing by priority...";
		case "ticked":
			current = listTicked;
			listTracker = KEY_TICKED;
			currentListName = TASKS_TICKED;
			UI.setList(current);
			return "Listing ticked tasks...";
		case COMMAND_CMI:
			current = listCMI;
			listTracker = KEY_CMI;
			currentListName = TASKS_CMI;
			UI.setList(current);
			return "Listing tasks that cannot be done...";
		default:
			throw new IllegalArgumentException();
		}
	}
}

//TODO: 
//-Do exception handling for tick and cmi, cannot do certain commands
//-refactor the code and make it neat