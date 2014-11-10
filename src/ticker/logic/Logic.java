//@author A0114535M

/* Team ID: W15-3J
 * Project Title: Ticker
 * Class: Logic
 * Description: This class passes the user input from UI to the Parser 
 * to process the input. Logic class then receives the processed command 
 * and acts on it. Functions provided include adding task, deleting task,
 * edit an existing task, listing out the tasks in different formats 
 * (e.g priority, time, done and cannot be completed), as well as
 * searching and auto-complete.
 * 
 * Assumptions: 
 * This class assumes that:
 * -the Parser class will pass Logic class valid processed user input 
 * (as an UserInput object) with data at their correct positions.
 * -the Logic class will always be used with classes CRUDManager,
 * TickKIVManager, UndoRedoManager and SearchManager.
 * -the UI using this class knows the key for different task lists.
 */

package ticker.logic;

import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ticker.parser.Parser;
import ticker.parser.UserInput;
import ticker.storage.Storage;
import ticker.common.Task;
import ticker.common.sortByTime;
import ticker.common.sortByPriority;

public class Logic {

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
	private static final String COMMAND_SEARCH_EXPIRED = "searchExpired";
	// Integer key constants for lists used by listTracker
	private static final int KEY_SORTED_TIME = 1;
	private static final int KEY_SORTED_PRIORITY = 2;
	private static final int KEY_TICKED = 3;
	private static final int KEY_KIV = 4;
	private static final int KEY_SEARCH = 5;
	private static final int KEY_FREESLOTS = 6;
	// Other integer constants
	private static final int INIT = 0;
	private static final int OFFSET_INDEX = 1;
	// String constants for type of lists used
	private static final String LIST_TIME = "time";
	private static final String LIST_PRIORITY = "priority";
	private static final String LIST_TICKED = "ticked";
	private static final String LIST_KIV = "kiv";
	private static final String LIST_SEARCH = "search";
	private static final String LIST_FREESLOTS = "free";
	// Feedback messages
	private static final String FEEDBACK_SEARCH_FREESLOTS = "Searching for free slots....";
	private static final String FEEDBACK_SEARCH = "Searching for tasks...";
	private static final String FEEDBACK_HELP = "Help is on the way!";
	private static final String FEEDBACK_CLEAR = "Spick and span!";
	private static final String FEEDBACK_LIST_KIV = "Listing tasks that are kept in view...";
	private static final String FEEDBACK_LIST_TICKED = "Listing ticked tasks...";
	private static final String FEEDBACK_LIST_PRIORITY = "Listing by priority...";
	private static final String FEEDBACK_LIST_TIME = "Listing by time...";
	private static final String FEEDBACK_ERROR_NO_SUCH_LIST = "List does not exist. Please re-enter.";
	private static final String FEEDBACK_ERROR_INDEX_OUT_OF_BOUNDS = "Index out of bounds. No action is performed.";
	private static final String FEEDBACK_ERROR_INVALID_COMMAND = "Invalid command";
	private static final String FEEDBACK_ERROR_MISUSED_TAKE = "Invalid use of take. Please use it only with searching for freeslots.";
	private static final String FEEDBACK_NOTHING_TO_DISPLAY = "Nothing to display";
	// Log messages
	private static final String LOG_SUCCESSFUL_ACTION = "Action proceeded successfully";
	private static final String LOG_NO_COMMANDS_PASSED = "NO COMMANDS PASSED";
	private static final String LOG_PERFORM_ACTION = "Performing an action";
	private static final String LOG_ERROR_UNDOMNG_AND_UNTICK = "Error with UndoManager in untick";
	private static final String LOG_ERROR_UNDOMNG_AND_TICK = "Error with UndoManager in tick";
	private static final String LOG_ERROR_UNDOMNG_AND_KIV = "Error with UndoManager in KIV";
	private static final String LOG_ERROR_UNDOMNG_AND_UNKIV = "Error with UndoManager in unKIV";
	private static final String LOG_ERROR_UNDOMNG_AND_TAKE = "Error with UndoManager in take";
	private static final String LOG_ERROR_UNDOMNG_AND_ADD = "Error with UndoManager in add ";
	private static final String LOG_ERROR_UNDOMNG_AND_DELETE = "Error with UndoManager in delete";
	private static final String LOG_ERROR_UNDOMNG_AND_EDIT = "Error with UndoManager in edit";
	private static final String LOG_ERROR_UNDOMNG_AND_UNDO = "Error with UndoManager in undo";
	private static final String LOG_ERROR_UNDOMNG_AND_REDO = "Error with UndoManager in redo";
	// Other string constants
	private static final String LOGIC = "Logic";
	private static final String EMPTY_STRING = "";
		private static final String PARTITION_STRING = ". ";
	private static final String NEWLINE_STRING = "\n";

	// ATTRIBUTES
	// Log error messages
	protected static Logger logger;
	// Singleton pattern
	private static Logic theOne;
	// Observer pattern
	private static Vector<Observer> observerList;
	// Instances of other components
	private Parser parser;
	private Storage storage;
	private UndoManager undoMng;
	private CRUManager cruMng;
	private TickKIVManager tickKivMng;
	private SearchManager searchMng;
	// Variables to track which list is being displayed
	private Integer listTracker;
	private String currentListName;
	private Vector<Task> current;
	// Temporary sorted storages
	private Vector<Task> storedTasksByTime;
	private Vector<Task> storedTasksByPriority;
	private Vector<Task> storedTasksByTicked; // not sorted
	private Vector<Task> storedTasksByKiv; // not sorted
	private Vector<Task> searchResults;
	private Vector<Task> freeslotsResults;
	// Store existing (current) search request
	private UserInput searchRequest;
	private UserInput freeslotsRequest;

	// Construct dependency with UI
	public Logic() {

		getLogger();
		instantiateParserAndStorage();

		initialiseStorageFiles();
		retrieveStoredFiles();

		instantiateLogicManagers();

		instantiateSearchResults();

		setCurrentAsTime();

		updateObservers();

	}

	/**
	 * This method is for UI to request to be an observer of Logic.
	 *
	 * @param UI	Observer UI.
	 * @return The one instance of Logic.
	 */
	public static Logic getInstance(Observer UI) {
		if (theOne == null) {
			observerList = new Vector<Observer>();
			observerList.add(UI);
			theOne = new Logic();
		} else {
			observerList.add(UI);
		}

		return theOne;
	}

	/**
	 * This method is for observer to call logic and for logic to pass the
	 * string to parser to process user input
	 *
	 * @param input		Name of user input string
	 * @return Message from the command operation.
	 */
	public String getLogic(String input) {
		// Crash the program if Logic is constructed without observer, missing
		// dependency
		assert (observerList.isEmpty() != true);

		String feedback;

		UserInput processed = parser.processInput(input);
		feedback = getOutput(processed);

		return feedback;
	}

	/**
	 * This method gets the feedback from the command operation and updates the
	 * UI display if applicable
	 *
	 * @param processed		Name of UserInput object with processed user input returned by
	 *            			Parser object.
	 * @return Message from the command operation.
	 */
	protected String getOutput(UserInput processed) {

		logger.log(Level.INFO, LOG_PERFORM_ACTION);

		String feedback = EMPTY_STRING;
		String command = extractCommand(processed);

		switch (command) {
		case COMMAND_TAKE :
			try {
				if (listTracker != KEY_FREESLOTS) {
					return FEEDBACK_ERROR_MISUSED_TAKE;
				}
				feedback = performTake(processed);
			} catch (IllegalArgumentException iae) {
				logger.log(Level.WARNING, LOG_ERROR_UNDOMNG_AND_TAKE);
			}
			break;

		case COMMAND_SEARCH_FREESLOTS :
			feedback = performSearchFreeslots(processed);
			break;

		case COMMAND_SEARCH :
			feedback = performSearch(processed);
			break;

		case COMMAND_SEARCH_EXPIRED:
			feedback = performSearchExpired(processed);
			break;

		case COMMAND_DELETE :
			try {
				feedback = performDelete(processed);
			} catch (ArrayIndexOutOfBoundsException OOBE) {
				return FEEDBACK_ERROR_INDEX_OUT_OF_BOUNDS;
			} catch (IllegalArgumentException iae) {
				logger.log(Level.WARNING, LOG_ERROR_UNDOMNG_AND_DELETE);
			}
			break;

		case COMMAND_CLEAR :
			feedback = performClear();
			break;

		case COMMAND_LIST :
			try {
				feedback = performList(processed);
			} catch (IllegalArgumentException iae) {
				return FEEDBACK_ERROR_NO_SUCH_LIST;
			}
			break;

		case COMMAND_EDIT :
			try {
				feedback = performEdit(processed);
			} catch (ArrayIndexOutOfBoundsException oobe) {
				return FEEDBACK_ERROR_INDEX_OUT_OF_BOUNDS;
			} catch (IllegalArgumentException iae) {
				logger.log(Level.WARNING, LOG_ERROR_UNDOMNG_AND_EDIT);
			}
			break;

		case COMMAND_ADD :
			try {
				feedback = performAdd(processed);
			} catch (IllegalArgumentException iae) {
				logger.log(Level.WARNING, LOG_ERROR_UNDOMNG_AND_ADD);
			}
			break;

		case COMMAND_KIV :
			try {
				feedback = performKiv(processed);
			} catch (ArrayIndexOutOfBoundsException oobe) {
				return FEEDBACK_ERROR_INDEX_OUT_OF_BOUNDS;
			} catch (IllegalArgumentException ex) {
				logger.log(Level.WARNING, LOG_ERROR_UNDOMNG_AND_KIV);
			}
			break;

		case COMMAND_UNKIV :
			try {
				feedback = performUnkiv(processed);
			} catch (ArrayIndexOutOfBoundsException oobe) {
				return FEEDBACK_ERROR_INDEX_OUT_OF_BOUNDS;
			} catch (IllegalArgumentException ex) {
				logger.log(Level.WARNING, LOG_ERROR_UNDOMNG_AND_UNKIV);
			}
			break;

		case COMMAND_UNDO :
			try {
				feedback = performUndo();
			} catch (NullPointerException npe) {
				logger.log(Level.WARNING, LOG_ERROR_UNDOMNG_AND_UNDO);
			}
			return feedback;

		case COMMAND_REDO :
			try {
				feedback = performRedo();
			} catch (NullPointerException npe) {
				logger.log(Level.WARNING, LOG_ERROR_UNDOMNG_AND_REDO);
			}
			return feedback;

		case COMMAND_TICK :
			try {
				feedback = performTick(processed);
			} catch (ArrayIndexOutOfBoundsException oobe) {
				return FEEDBACK_ERROR_INDEX_OUT_OF_BOUNDS;
			} catch (IllegalArgumentException ex) {
				logger.log(Level.WARNING, LOG_ERROR_UNDOMNG_AND_TICK);
			}
			break;

		case COMMAND_UNTICK :
			try {
				feedback = performUntick(processed);
			} catch (ArrayIndexOutOfBoundsException oobe) {
				return FEEDBACK_ERROR_INDEX_OUT_OF_BOUNDS;
			} catch (IllegalArgumentException ex) {
				logger.log(Level.WARNING, LOG_ERROR_UNDOMNG_AND_UNTICK);
			}
			break;

		case COMMAND_HELP :
			feedback = performHelp();
			break;

		default:
			feedback = FEEDBACK_ERROR_INVALID_COMMAND;
			break;
		}

		logger.log(Level.INFO, LOG_SUCCESSFUL_ACTION);
		return feedback;
	}

	/**
	 * This method sets the help instructions in the observers.
	 * 
	 * @return Feedback from action.
	 */
	private String performHelp() {
		String feedback;
		feedback = FEEDBACK_HELP;
		setHelp();
		return feedback;
	}

	/**
	 * This method unticks a task.
	 * 
	 * @param processed		Processed user input.
	 * @return Feedback from the action.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds the boundaries of task list.
	 * @throws IllegalArgumentException			If event is created wrongly.
	 */
	private String performUntick(UserInput processed)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		String feedback;
		feedback = tickKivMng
				.untick(processed.getIndex(), listTracker, current);

		if (listTracker == KEY_SEARCH) {
			searchResults.removeAllElements();
			searchResults = searchMng.search(searchRequest.getDescription(),
					searchRequest.getRepeating(), searchRequest.getStartDate(),
					searchRequest.getEndDate(), searchRequest.getStartTime(),
					searchRequest.getEndTime(), searchRequest.getPriority());
		}

		maintainLists();
		updateObservers();
		return feedback;
	}

	/**
	 * This method ticks a task.
	 * 
	 * @param processed		Processed user input.
	 * @return Feedback from the action.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds the boundaries of task list.
	 * @throws IllegalArgumentException			If event is created wrongly.
	 */
	private String performTick(UserInput processed)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		String feedback;
		feedback = tickKivMng.tick(processed.getIndex(), listTracker, current);

		if (listTracker == KEY_SEARCH) {
			searchResults.removeAllElements();
			searchResults = searchMng.search(searchRequest.getDescription(),
					searchRequest.getRepeating(), searchRequest.getStartDate(),
					searchRequest.getEndDate(), searchRequest.getStartTime(),
					searchRequest.getEndTime(), searchRequest.getPriority());
		} else if (listTracker == KEY_FREESLOTS) {
			freeslotsResults.removeAllElements();
			freeslotsResults = searchMng.searchForFreeSlots(
					freeslotsRequest.getStartDate(),
					freeslotsRequest.getStartTime(),
					freeslotsRequest.getEndDate(),
					freeslotsRequest.getEndTime());

			current = freeslotsResults;
		}

		maintainLists();
		updateObservers();
		return feedback;
	}

	/**
	 * This method redo the undid action.
	 * 
	 * @return Feedback from the action.
	 */
	private String performRedo() {
		String feedback;
		feedback = undoMng.redo();

		if (listTracker == KEY_SEARCH) {
			searchResults.removeAllElements();
			searchResults = searchMng.search(searchRequest.getDescription(),
					searchRequest.getRepeating(), searchRequest.getStartDate(),
					searchRequest.getEndDate(), searchRequest.getStartTime(),
					searchRequest.getEndTime(), searchRequest.getPriority());
		} else if (listTracker == KEY_FREESLOTS) {
			freeslotsResults.removeAllElements();
			freeslotsResults = searchMng.searchForFreeSlots(
					freeslotsRequest.getStartDate(),
					freeslotsRequest.getStartTime(),
					freeslotsRequest.getEndDate(),
					freeslotsRequest.getEndTime());

			current = freeslotsResults;
		}

		maintainLists();
		updateObservers();
		return feedback;
	}

	/**
	 * This method undo the last action.
	 * 
	 * @return Feedback from the action.
	 */
	private String performUndo() {
		String feedback;
		feedback = undoMng.undo();
		maintainLists();

		if (listTracker == KEY_SEARCH) {
			searchResults.removeAllElements();
			searchResults = searchMng.search(searchRequest.getDescription(),
					searchRequest.getRepeating(), searchRequest.getStartDate(),
					searchRequest.getEndDate(), searchRequest.getStartTime(),
					searchRequest.getEndTime(), searchRequest.getPriority());
		} else if (listTracker == KEY_FREESLOTS) {
			freeslotsResults.removeAllElements();
			freeslotsResults = searchMng.searchForFreeSlots(
					freeslotsRequest.getStartDate(),
					freeslotsRequest.getStartTime(),
					freeslotsRequest.getEndDate(),
					freeslotsRequest.getEndTime());

			current = freeslotsResults;
		}

		updateObservers();
		return feedback;
	}

	/**
	 * This method unkiv a task.
	 * 
	 * @param processed		Processed user input.
	 * @return Feedback from the action.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds the boundaries of task list.
	 * @throws IllegalArgumentException			If event is created wrongly.
	 */
	private String performUnkiv(UserInput processed)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		String feedback;
		feedback = tickKivMng.unkiv(processed.getIndex(), listTracker, current);

		if (listTracker == KEY_SEARCH) {
			searchResults.removeAllElements();
			searchResults = searchMng.search(searchRequest.getDescription(),
					searchRequest.getRepeating(), searchRequest.getStartDate(),
					searchRequest.getEndDate(), searchRequest.getStartTime(),
					searchRequest.getEndTime(), searchRequest.getPriority());
		}

		maintainLists();
		updateObservers();
		return feedback;
	}

	/**
	 * This method kiv a task.
	 * 
	 * @param processed		Processed user input.
	 * @return Feedback from the action.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds the boundaries of task list.
	 * @throws IllegalArgumentException			If event is created wrongly.
	 */
	private String performKiv(UserInput processed)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		String feedback;
		feedback = tickKivMng.kiv(processed.getIndex(), listTracker, current,
				currentListName);

		if (listTracker == KEY_SEARCH) {
			searchResults.removeAllElements();
			searchResults = searchMng.search(searchRequest.getDescription(),
					searchRequest.getRepeating(), searchRequest.getStartDate(),
					searchRequest.getEndDate(), searchRequest.getStartTime(),
					searchRequest.getEndTime(), searchRequest.getPriority());
		} else if (listTracker == KEY_FREESLOTS) {
			freeslotsResults.removeAllElements();
			freeslotsResults = searchMng.searchForFreeSlots(
					freeslotsRequest.getStartDate(),
					freeslotsRequest.getStartTime(),
					freeslotsRequest.getEndDate(),
					freeslotsRequest.getEndTime());

			current = freeslotsResults;
		}

		maintainLists();
		updateObservers();
		return feedback;
	}

	/**
	 * This method adds a task.
	 * 
	 * @param processed		Processed user input.
	 * @return Feedback from the action.
	 * @throws IllegalArgumentException		If event is created wrongly.
	 */
	private String performAdd(UserInput processed)
			throws IllegalArgumentException {
		String feedback;
		feedback = cruMng.add(processed.getDescription(),
				processed.getRepeating(), processed.getStartDate(),
				processed.getEndDate(), processed.getStartTime(),
				processed.getEndTime(), processed.getPriority());

		if (listTracker == KEY_KIV || listTracker == KEY_TICKED
				|| listTracker == KEY_SEARCH) {
			setCurrentAsTime();
		} else if (listTracker == KEY_FREESLOTS) {
			freeslotsResults.removeAllElements();
			freeslotsResults = searchMng.searchForFreeSlots(
					freeslotsRequest.getStartDate(),
					freeslotsRequest.getStartTime(),
					freeslotsRequest.getEndDate(),
					freeslotsRequest.getEndTime());

			current = freeslotsResults;
		}

		maintainLists();
		updateObservers();
		return feedback;
	}

	/**
	 * This method edits a task.
	 * 
	 * @param processed		Processed user input.
	 * @return Feedback from the action.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds the boundaries of task list.
	 * @throws IllegalArgumentException			If event is created wrongly.
	 */
	private String performEdit(UserInput processed)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		String feedback;
		feedback = cruMng.edit(processed.getIndex(),
				processed.getDescription(), processed.getRepeating(),
				processed.getStartDate(), processed.getEndDate(),
				processed.getStartTime(), processed.getEndTime(),
				processed.getPriority(), listTracker, current);

		if (listTracker == KEY_SEARCH) {
			searchResults.removeAllElements();
			searchResults = searchMng.search(searchRequest.getDescription(),
					searchRequest.getRepeating(), searchRequest.getStartDate(),
					searchRequest.getEndDate(), searchRequest.getStartTime(),
					searchRequest.getEndTime(), searchRequest.getPriority());
		} else if (listTracker == KEY_FREESLOTS) {
			freeslotsResults.removeAllElements();
			freeslotsResults = searchMng.searchForFreeSlots(
					freeslotsRequest.getStartDate(),
					freeslotsRequest.getStartTime(),
					freeslotsRequest.getEndDate(),
					freeslotsRequest.getEndTime());

			current = freeslotsResults;
		}

		maintainLists();
		updateObservers();
		return feedback;
	}

	/**
	 * This method lists a specified tasklist.
	 * 
	 * @param processed		Processed user input.
	 * @return Feedback from the action.
	 * @throws IllegalArgumentException		If list name requested is invalid.
	 */
	private String performList(UserInput processed)
			throws IllegalArgumentException {
		String feedback;
		checkForTaskExpiry();
		feedback = this.list(processed.getDescription());
		return feedback;
	}

	/**
	 * This method clears the current list.
	 * 
	 * @return Feedback from the action.
	 */
	private String performClear() {
		String feedback;
		feedback = this.clear();
		return feedback;
	}

	/**
	 * This method deletes a task.
	 * 
	 * @param processed		Processed user input.
	 * @return Feedback from the action.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds the boundaries of task list.
	 * @throws IllegalArgumentException			If event is created wrongly.
	 */
	private String performDelete(UserInput processed)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		String feedback;
		feedback = cruMng.delete(processed.getIndex(), listTracker, current,
				currentListName);

		if (listTracker == KEY_FREESLOTS) {
			freeslotsResults.removeAllElements();
			freeslotsResults = searchMng.searchForFreeSlots(
					freeslotsRequest.getStartDate(),
					freeslotsRequest.getStartTime(),
					freeslotsRequest.getEndDate(),
					freeslotsRequest.getEndTime());

			current = freeslotsResults;
		}

		maintainLists();
		updateObservers();
		return feedback;
	}

	/**
	 * This method takes a freeslot.
	 * 
	 * @param processed		Processed user input.
	 * @return Feedback from the action.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds the boundaries of task list.
	 * @throws IllegalArgumentException			If event is created wrongly.
	 */
	private String performTake(UserInput processed)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		String feedback;
		feedback = searchMng.take(processed.getIndex(),
				processed.getDescription());
		return feedback;
	}

	/**
	 * This method searches for expired tasks.
	 * 
	 * @param processed		Processed user input.
	 * @return Feedback from the action.
	 */
	private String performSearchExpired(UserInput processed) {
		String feedback;
		searchRequest = deepCopyUserInput(processed);
		searchResults.removeAllElements();
		searchResults = searchMng.searchExpired(processed.getDescription(),
				processed.getRepeating(), processed.getStartDate(),
				processed.getEndDate(), processed.getStartTime(),
				processed.getEndTime(), processed.getPriority());

		setCurrentAsSearch();
		updateObservers();

		feedback = FEEDBACK_SEARCH;
		return feedback;
	}

	/**
	 * This method searches for tasks.
	 * 
	 * @param processed		Processed user input.
	 * @return Feedback from the action.
	 */
	private String performSearch(UserInput processed) {
		String feedback;
		searchRequest = deepCopyUserInput(processed);
		searchResults.removeAllElements();
		searchResults = searchMng.search(processed.getDescription(),
				processed.getRepeating(), processed.getStartDate(),
				processed.getEndDate(), processed.getStartTime(),
				processed.getEndTime(), processed.getPriority());

		setCurrentAsSearch();
		updateObservers();

		feedback = FEEDBACK_SEARCH;
		return feedback;
	}

	/**
	 * This method searches for freeslots.
	 * 
	 * @param processed		Processed user input.
	 * @return Feedback from the action.
	 */
	private String performSearchFreeslots(UserInput processed) {
		String feedback;
		freeslotsRequest = deepCopyUserInput(processed);
		freeslotsResults.removeAllElements();
		freeslotsResults = searchMng.searchForFreeSlots(
				processed.getStartDate(), processed.getStartTime(),
				processed.getEndDate(), processed.getEndTime());

		setCurrentAsSearchFreeslots();
		checkForTaskExpiry(current);
		updateObservers();

		feedback = FEEDBACK_SEARCH_FREESLOTS;
		return feedback;
	}

	/**
	 * This method maintains any updates in the lists.
	 */
	private void maintainLists() {
		sortLists();
		storeLists();
	}

	/**
	 * This method tries to get command from the processed user input.
	 * 
	 * @param processed		Processed user input.
	 * @return User input command.
	 */
	private String extractCommand(UserInput processed) {
		String command = EMPTY_STRING;
		try {
			command = processed.getCommand();
		} catch (NullPointerException npe) {
			logger.log(Level.WARNING, LOG_NO_COMMANDS_PASSED);
		}
		return command;
	}

	/**
	 * This method deep copies a UserInput object.
	 * 
	 * @param processed		UserInput object to be duplicated. 
	 * @return Copied UserInput
	 */
	private UserInput deepCopyUserInput(UserInput input) {
		UserInput copied = new UserInput();
		copied.setCommand(input.getCommand());
		copied.setDescription(input.getDescription());
		copied.setRepeating(input.getRepeating());
		copied.setStartDate(input.getStartDate());
		copied.setStartTime(input.getStartTime());
		copied.setEndDate(input.getEndDate());
		copied.setEndTime(input.getEndTime());
		copied.setPriority(input.getPriority());

		return copied;
	}

	/**
	 * This method sets the observers to display help instructions.
	 */
	private void setHelp() {
		for (Observer observer : observerList) {
			observer.setHelp();
		}
	}

	/**
	 * This method clears the current list.
	 * 
	 * @return Message from the operation clear().
	 */
	protected String clear() {

		if (listTracker == KEY_SORTED_TIME
				|| listTracker == KEY_SORTED_PRIORITY) {
			storedTasksByTime.removeAllElements();
			storedTasksByPriority.removeAllElements();
		} else {
			current.removeAllElements();
		}

		storeLists();
		updateObservers();

		return FEEDBACK_CLEAR;
	}

	/**
	 * This method lists the current task list in string form. This This is used
	 * by TestLogic class for testing without TickerUI.
	 *
	 * @return List of tasks in string format.
	 */
	protected String list() {
		if (current.isEmpty()) {
			return FEEDBACK_NOTHING_TO_DISPLAY;
		}

		String list = EMPTY_STRING;
		for (int i = INIT; i < current.size(); i++) {
			list += i + OFFSET_INDEX + PARTITION_STRING + current.get(i).toString()
					+ NEWLINE_STRING;
		}
		return list;
	}

	/**
	 * This method displays the list requested by the user
	 *
	 * @param listType	Name of list that the user wants displayed
	 * @return Feedback message for listing a list type
	 * @throws IllegalArgumentException		If list name is unidentifiable.
	 */
	protected String list(String listType) throws IllegalArgumentException {
		switch (listType) {
		case LIST_TIME :
			sortLists();
			setCurrentAsTime();
			updateObservers();
			return FEEDBACK_LIST_TIME;
		case LIST_PRIORITY :
			sortLists();
			setCurrentAsPriority();
			updateObservers();
			return FEEDBACK_LIST_PRIORITY;
		case LIST_TICKED :
			sortLists();
			setCurrentAsTicked();
			updateObservers();
			return FEEDBACK_LIST_TICKED;
		case COMMAND_KIV :
			sortLists();
			setCurrentAsKiv();
			updateObservers();
			return FEEDBACK_LIST_KIV;
		default :
			throw new IllegalArgumentException();
		}
	}

	/**
	 * This method creates instances of Parser and Storage.
	 */
	private void instantiateParserAndStorage() {
		parser = new Parser();
		storage = new Storage();
		logger = Logger.getLogger(LOGIC);
	}

	/**
	 * This method creates instances of Parser and Storage.
	 */
	private void getLogger() {
		logger = Logger.getLogger(LOGIC);
	}

	/**
	 * This method initialises files in Storage.
	 */
	private void initialiseStorageFiles() {
		try {
			storage.initFile();
		} catch (IllegalStateException ise) {
			isFileCorrupted(true);
		}
	}

	/**
	 * This method restores the last saved files if there is any.
	 */
	private void retrieveStoredFiles() {
		try {
			storedTasksByTime = storage.restoreDataFromFile(KEY_SORTED_TIME);
			storedTasksByPriority = storage
					.restoreDataFromFile(KEY_SORTED_PRIORITY);
			storedTasksByTicked = storage.restoreDataFromFile(KEY_TICKED);
			storedTasksByKiv = storage.restoreDataFromFile(KEY_KIV);
		} catch (IllegalStateException ise) {
			isFileCorrupted(true);
		}
	}

	/**
	 * This method creates instances of the logic managers.
	 */
	private void instantiateLogicManagers() {
		cruMng = new CRUManager(storedTasksByTime, storedTasksByPriority,
				storedTasksByTicked, storedTasksByKiv);
		tickKivMng = new TickKIVManager(storedTasksByTime,
				storedTasksByPriority, storedTasksByTicked, storedTasksByKiv);
		searchMng = new SearchManager(storedTasksByTime, storedTasksByPriority,
				storedTasksByTicked, storedTasksByKiv);
		undoMng = UndoManager.getInstance(storedTasksByTime,
				storedTasksByPriority, storedTasksByTicked, storedTasksByKiv);
	}

	/**
	 * This method creates instances of the search results.
	 */
	private void instantiateSearchResults() {
		searchResults = new Vector<Task>();
		freeslotsResults = new Vector<Task>();
	}

	/**
	 * This method notifies observer UI(s) that the storage files have been
	 * corrupted.
	 *
	 * @param isCorrupted		The state of whether the file is corrupted
	 */
	private void isFileCorrupted(boolean isCorrupted) {
		for (Observer observer : observerList) {
			observer.isFileCorrupted(isCorrupted);
		}
	}

	/**
	 * This method checks for expired tasks and updates their attribute
	 * isExpired.
	 */
	private void checkForTaskExpiry() {
		checkForTaskExpiryInListTime();
		checkForTaskExpiryInListPriority();
		checkForTaskExpiryInListKiv();
		checkForTaskExpiryInListTicked();
	}

	/**
	 * This method checks for expired tasks in list ticked.
	 */
	private void checkForTaskExpiryInListTicked() {
		if (!storedTasksByTicked.isEmpty()) {
			for (Task tickedTask : storedTasksByTicked) {
				tickedTask.isExpired();
			}
		}
	}

	/**
	 * This method checks for expired tasks in list KIV.
	 */
	private void checkForTaskExpiryInListKiv() {
		if (!storedTasksByKiv.isEmpty()) {
			for (Task kivTask : storedTasksByKiv) {
				kivTask.isExpired();
			}
		}
	}

	/**
	 * This method checks for expired tasks in list by priority.
	 */
	private void checkForTaskExpiryInListPriority() {
		if (!storedTasksByPriority.isEmpty()) {
			for (Task priorityTask : storedTasksByPriority) {
				priorityTask.isExpired();
			}
		}
	}

	/**
	 * This method checks for expired tasks in list by time.
	 */
	private void checkForTaskExpiryInListTime() {
		if (!storedTasksByTime.isEmpty()) {
			for (Task timeTask : storedTasksByTime) {
				timeTask.isExpired();
			}
		}
	}

	/**
	 * This is an overloaded method that checks for expired tasks in only one
	 * task list and updates their attribute isExpired.
	 * 
	 * @param taskList	Tasklist to be checked for expired task.
	 */
	private void checkForTaskExpiry(Vector<Task> taskList) {
		for (Task task : taskList) {
			task.isExpired();
		}
	}

	/**
	 * This method updates the observers of the new display.
	 */
	private void updateObservers() {
		checkForTaskExpiry();
		updateList(current);
		updateDisplayKey(listTracker);
	}

	/**
	 * This method updates observer UI(s) on the tasks being displayed.
	 *
	 * @param taskList	Tasklist to be displayed.
	 */
	private void updateList(Vector<Task> taskList) {
		for (Observer observer : observerList) {
			observer.setList(taskList);
		}
	}

	/**
	 * This method updates observer UI(s) of the task display key.
	 *
	 * @param displayKey	Key of the displayed tasklist.
	 */
	private void updateDisplayKey(int displayKey) {
		for (Observer observer : observerList) {
			observer.setNextView(displayKey);
		}
	}

	/**
	 * This method sets the tasklist sorted by time to be displayed.
	 */
	private void setCurrentAsTime() {
		listTracker = KEY_SORTED_TIME;
		current = storedTasksByTime;
		currentListName = LIST_TIME;
	}

	/**
	 * This method sets the tasklist sorted by priority to be displayed.
	 */
	private void setCurrentAsPriority() {
		listTracker = KEY_SORTED_PRIORITY;
		current = storedTasksByPriority;
		currentListName = LIST_PRIORITY;
	}

	/**
	 * This method sets the tasklist containing ticked tasks to be displayed.
	 */
	private void setCurrentAsTicked() {
		listTracker = KEY_TICKED;
		current = storedTasksByTicked;
		currentListName = LIST_TICKED;
	}

	/**
	 * This method sets the tasklist containing kiv-ed tasks to be displayed.
	 */
	private void setCurrentAsKiv() {
		listTracker = KEY_KIV;
		current = storedTasksByKiv;
		currentListName = LIST_KIV;
	}

	/**
	 * This method sets the search tasklist to be displayed.
	 */
	private void setCurrentAsSearch() {
		listTracker = KEY_SEARCH;
		current = searchResults;
		currentListName = LIST_SEARCH;
	}

	/**
	 * This method sets the search freeslots tasklist to be displayed.
	 */
	private void setCurrentAsSearchFreeslots() {
		listTracker = KEY_FREESLOTS;
		current = freeslotsResults;
		currentListName = LIST_FREESLOTS;
	}

	/**
	 * This method writes the lists into storage.
	 */
	private void storeLists() {
		storage.writeStorageArrayIntoFile(KEY_SORTED_TIME, storedTasksByTime);
		storage.writeStorageArrayIntoFile(KEY_SORTED_PRIORITY,
				storedTasksByPriority);
		storage.writeStorageArrayIntoFile(KEY_TICKED, storedTasksByTicked);
		storage.writeStorageArrayIntoFile(KEY_KIV, storedTasksByKiv);
	}

	/**
	 * This method sorts the time and priority lists.
	 */
	private void sortLists() {
		Collections.sort(storedTasksByTime, new sortByTime());
		Collections.sort(storedTasksByPriority, new sortByPriority());
	}
}
