package ticker.logic;

// Package Parser
import ticker.parser.Parser;
import ticker.parser.UserInput;
// Package Storage
import ticker.storage.Storage;
// Package UI
import ticker.ui.TickerUI;
// Package Common
import ticker.common.Date;
import ticker.common.Time;
import ticker.common.Task;
import ticker.common.DeadlineTask;
import ticker.common.FloatingTask;
import ticker.common.RepeatingTask;
import ticker.common.TimedTask;
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

public class LogicDuplicate{
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
	// CONSTANTS
	// Integer key constants for lists used by listTracker
	private static final int KEY_SORTED_TIME = 1;
	private static final int KEY_SORTED_PRIORITY = 2;
	private static final int KEY_TICKED = 3;
	private static final int KEY_CMI = 4;
	private static final int KEY_SEARCH = 5;
	// String constants for type of lists used by UndoManager
	private static final String TASKS_TIME = "TIME";
	private static final String TASKS_PRIORITY = "PRIORITY";
	private static final String TASKS_TICKED = "TICKED";
	private static final String TASKS_CMI = "CMI";
	
	// Instances of other components
	private Parser parser;
	private Storage storage;
	private TickerUI UI;
	private UndoManager undoMng;
	private static Logger logger;
	// Tracker to track which list is being displayed
	private static int listTracker;
	private static String currentListName;
	// Pointer to the Vector currently in display
	private static Vector<Task> current;
	// Temporary sorted storages
	private static Vector<Task> sortedTime;
	private static Vector<Task> sortedPriority;
	private static Vector<Task> listTicked; // not sorted
	private static Vector<Task> listCMI; // not sorted
	private static Vector<Task> searchResults;

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
		
		undoMng = UndoManager.getInstance(sortedTime, sortedPriority, listTicked, listCMI);

		searchResults = new Vector<Task>();

		current = sortedTime;
		listTracker = KEY_SORTED_TIME;
		currentListName = TASKS_TIME;

		UI.setList(list());

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
			feedback = this.search(processed.getDescription());
			break;

		case COMMAND_DELETE: 
			try {
				feedback = this.delete(processed.getIndex());
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
				feedback = this.list(processed.getDescription());
			}
			catch (IllegalArgumentException ex) {
				System.out.println("Wrong list name from parser");
				return "List does not exist. Please re-enter.";
			}
			break;


		case COMMAND_EDIT:
			try {
				feedback = this.edit(processed.getIndex(), processed.getAppending(), processed.getDescription());
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
				feedback = this.add(processed.getDescription(), processed.getRepeating(), processed.getStartDate(), 
						processed.getEndDate(), processed.getStartTime(), processed.getEndTime(), processed.getPriority());
			}
			catch (IllegalArgumentException ex) {
				return "Error in input. Either description is missing or date is missing for repeated tasks.";
			}
			break;

		case COMMAND_CMI:
			try {
				feedback = this.cmi(processed.getIndex());
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
				feedback = this.uncmi(processed.getIndex());
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
				sortLists();
				UI.setList(list());
			}
			catch (NullPointerException ex) {
				System.out.println("Error with UndoManager");
			}
			return "undoing action";

		case COMMAND_REDO:
			try {
				undoMng.redo();
				sortLists();
				UI.setList(list());
			}
			catch (NullPointerException ex) {
				System.out.println("Error with UndoManager");
			}
			return "redoing action";
			
		case COMMAND_TICK:
			try {
				feedback = this.tick(processed.getIndex());
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
				feedback = this.untick(processed.getIndex());
			}
			catch (ArrayIndexOutOfBoundsException ex) {
				return "Index out of bounds. Nothing has been unticked.";
			}
			catch (IllegalArgumentException ex) {
				return "Current list: " + currentListName + "Cannot perform command on this list";
			}
			break;


		case COMMAND_HELP:
			feedback = this.help(); 
			break;

		default:
			feedback = "invalid command";
			break;
		}

		logger.log(Level.INFO, "Action proceeded successfully");
		return feedback;
	}

	private String search(String key) {
		SearchManager searchMng = new SearchManager();
		Vector<Task> searchResultsTime = searchMng.search(sortedTime, key);
		Vector<Task> searchResultsTicked = searchMng.search(listTicked, key);
		Vector<Task> searchResultsCMI = searchMng.search(listCMI, key);
		
		for (Task searchTime: searchResultsTime) {
			searchResults.add(searchTime);
		}
		
		searchResults.add(new Task("\\***TICKED***\\", null, null, null, null, 'B', false));

		for (Task searchTicked: searchResultsTicked) {
			searchResults.add(searchTicked);
		}
		
		searchResults.add(new Task("\\***CMI***\\", null, null, null, null, 'B', false));
		
		for (Task searchCMI: searchResultsCMI) {
			searchResults.add(searchCMI);
		}
		
		if (searchResults.isEmpty()) {
			return "No search results";
		}

		UI.setList(listSearch());

		return "Displaying search results";

	}
	private String delete(int index) throws ArrayIndexOutOfBoundsException {
		// Exception catching
		if (index <= 0 || index > current.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		Task deleted = current.remove(index-1);

		if (listTracker == KEY_SORTED_TIME) {
			sortedPriority.remove(deleted);
			sortLists();

		}
		if (listTracker == KEY_SORTED_PRIORITY) {
			sortedTime.remove(deleted);
			sortLists();
		}

		storeLists();
		//TODO: add index of deleted to parameter
		Event event = new Event(COMMAND_DELETE, deleted, currentListName, index - 1);
		undoMng.add(event);

		UI.setList(list());
		return deleted.toString() + " has been removed.\n";


	}

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
		sortedTime = new Vector<Task>();
		sortedPriority = new Vector<Task>();
		listTicked = new Vector<Task>();
		listCMI = new Vector<Task>();
		searchResults = new Vector<Task>();

		switch (listTracker) {
		case KEY_SORTED_TIME:
			current = sortedTime; 
			currentListName = TASKS_TIME; 
			break;
		case KEY_SORTED_PRIORITY:
			current = sortedPriority; 
			currentListName = TASKS_PRIORITY;
			break;
		case KEY_TICKED:
			current = listTicked;
			currentListName = TASKS_TICKED;
			break;
		case KEY_CMI:
			current = listCMI; 
			currentListName = TASKS_CMI;
			break;
		case KEY_SEARCH:
			current = sortedTime; 
			currentListName = TASKS_TIME;
			break;
		default:
		}

		UI.setList(list());

		storeLists();

		return "Spick and span!";
	}

	private String list() {
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
	
	private String listSearch() {
		if (current == null) {
			return "Nothing to display.\n";
		}
		int i = 0;
		String list = "";
		for (Task task: searchResults) {

			// list += ++i + ". " + task.toString() + "\n";
			list += task.toString() + "\n";
		}
		return list;
	}

	private String list(String listType) throws IllegalArgumentException {
		switch (listType) {
		case "time":
			current = sortedTime;
			listTracker = KEY_SORTED_TIME;
			currentListName = TASKS_TIME;
			UI.setList(list());
			return "Listing by time...";
		case "priority":
			current = sortedPriority;
			listTracker = KEY_SORTED_PRIORITY;
			currentListName = TASKS_PRIORITY;
			UI.setList(list());
			return "Listing by priority...";
		case "ticked":
			current = listTicked;
			listTracker = KEY_TICKED;
			currentListName = TASKS_TICKED;
			UI.setList(list());
			return "Listing ticked tasks...";
		case COMMAND_CMI:
			current = listCMI;
			listTracker = KEY_CMI;
			currentListName = TASKS_CMI;
			UI.setList(list());
			return "Listing tasks that cannot be done...";
		default:
			throw new IllegalArgumentException();
		}

	}

	private String edit(int index, boolean isAppending, String description) 
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException{
		// Exception catching

		if (index <= 0 || index > current.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		if (description == null || description.equals("")) {
			throw new IllegalArgumentException();
		}

		Task oldTask = current.remove(index - 1);
		Task newTask = oldTask;

		// Edit the other Vector<Task>
		if (listTracker == KEY_SORTED_TIME ) {
			sortedPriority.remove(oldTask);
		}
		else if (listTracker == KEY_SORTED_PRIORITY) {
			sortedTime.remove(oldTask);
		}

		if (isAppending) {
			String taskName = oldTask.getDescription();
			taskName += " " + description;
			newTask.setDescription(taskName);

			current.add(index - 1, newTask);
			if (listTracker == KEY_SORTED_TIME ) {
				sortedPriority.add(newTask);
				sortLists();
			}
			else if (listTracker == KEY_SORTED_PRIORITY) {
				sortedTime.add(newTask);
				sortLists();
			}

			Event event = new Event(COMMAND_EDIT, oldTask, newTask);
			undoMng.add(event);

			storeLists();

			UI.setList(list());
			return oldTask.getDescription() + " has been updated to " + newTask.getDescription() + ".\n";
		}

		newTask.setDescription(description);
		current.add(index - 1, newTask);
		if (listTracker == KEY_SORTED_TIME ) {
			sortedPriority.add(newTask);
			sortLists();
		}
		else if (listTracker == KEY_SORTED_PRIORITY) {
			sortedTime.add(newTask);
			sortLists();
		}

		storeLists();

		UI.setList(list());
		return oldTask.getDescription() + " has been updated to " + newTask.getDescription() + ".\n";
	}


	private String add(String description, boolean isRepeating, Date startDate, Date endDate,
			Time startTime, Time endTime, char priority) throws IllegalArgumentException {

		if (description == null || description.equals("")) {
			throw new IllegalArgumentException();
		}

		Task newTask;

		// Creation of RepeatingTask
		if (isRepeating) {
			if (startDate != null) {
				newTask = new RepeatingTask(description, startDate, startTime, endTime, priority, isRepeating);
			}
			else if (endDate != null) {
				newTask = new RepeatingTask(description, endDate, startTime, endTime, priority, isRepeating);
			}
			else {
				throw new IllegalArgumentException();
			}

		}

		else if (startDate == null && startTime == null) {
			// Creation of floating tasks
			if (endDate == null && endTime == null) {
				newTask = new FloatingTask(description, priority, false);
			}
			// Creation of deadline tasks
			else {
				newTask = new DeadlineTask(description, endDate, endTime, priority, false);
			}

		}
		// Creation of timed tasks
		else {
			newTask = new TimedTask(description, startDate, startTime, endDate, endTime, priority, false);
		}

		sortedTime.add(newTask);
		sortedPriority.add(newTask);

		Event event = new Event(COMMAND_ADD, newTask);
		undoMng.add(event);

		sortLists();
		storeLists();

		UI.setList(list());
		return description + " has been added.\n";
	}

	private String cmi(int index) throws ArrayIndexOutOfBoundsException {
		// Exception catching
		if (index <= 0 || index > current.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}

		if (listTracker == KEY_TICKED) {
			throw new IllegalArgumentException();
		}

		Task cmi = current.remove(index-1);
		// Add to the front so the latest additions are on top
		listCMI.add(0, cmi);
		sortedTime.remove(cmi);
		sortedPriority.remove(cmi);

		Event event = new Event(COMMAND_CMI, cmi, TASKS_TIME, TASKS_CMI);
		undoMng.add(event);

		sortLists();
		storeLists();
		UI.setList(list());
		return cmi.toString() + " cannot be done!\n";

	}

	private String uncmi(int index) throws ArrayIndexOutOfBoundsException {
		// Exception catching
		if (index <= 0 || index > current.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}

		if (listTracker != KEY_CMI) {
			throw new IllegalArgumentException();
		}

		Task uncmi = current.remove(index-1);
		// Add to the front so the latest additions are on top
		sortedTime.add(uncmi);
		sortedPriority.add(uncmi);

		Event event = new Event(COMMAND_UNCMI, uncmi, TASKS_TIME, TASKS_CMI);
		undoMng.add(event);

		sortLists();
		storeLists();
		UI.setList(list());
		return uncmi.toString() + "is back to undone!\n";

	}

	private String help() {
		// TODO: check through helpList again!
		String helpList = "";
		helpList += "HELP FOR USING TICKER\n";
		helpList += "-to add a task: add \"<task name>\" -st <start time> -sd <start date in dd/mm/yy format> "
				+ "-et <end time> -ed <end date in dd/mm/yy format.\n";
		helpList += "-to set a task to repeat, add the flag: -r\n";
		helpList += "-to set a priority for a task, add the flag: to be continued\n";
		helpList += "-to delete a task: delete <index of task>\n";
		helpList += "-to edit a task: to be continued\n";
		helpList += "-to sort the tasks according to time and date: list to be continued\n";
		helpList += "-to sort the tasks according to priority: list to be continued\n";
		helpList += "-to undo the last command: undo\n";
		helpList += "-to redo the last undo: redo\n";

		UI.setList(helpList);
		return "Help is on the way!\n";
	}

	private String tick(int index) {
		// Exception catching
		if (index <= 0 || index > current.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}

		if (listTracker == KEY_CMI) {
			System.out.println("Error in listTracker");
			throw new IllegalArgumentException();
		}

		Task ticked = current.remove(index-1);
		sortedTime.remove(ticked);
		sortedPriority.remove(ticked);
		listTicked.add(0, ticked);

		Event event = new Event(COMMAND_TICK, ticked, TASKS_TIME, TASKS_TICKED);
		undoMng.add(event);

		sortLists();
		storeLists();

		UI.setList(list());
		return ticked.toString() + " is done!\n";
	}

	private String untick(int index) {
		// Exception catching
		if (index <= 0 || index > current.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}

		if (listTracker != KEY_TICKED) {
			throw new IllegalArgumentException();
		}

		Task unticked = current.remove(index-1);
		sortedTime.add(unticked);
		sortedPriority.add(unticked);

		Event event = new Event(COMMAND_UNTICK, unticked, TASKS_TIME, TASKS_TICKED);
		undoMng.add(event);

		sortLists();
		storeLists();

		UI.setList(list());
		return unticked.toString() + " is back to undone\n";
	}
}

//TODO: 
//-Do exception handling for tick and cmi, cannot do certain commands
//-refactor the code and make it neat