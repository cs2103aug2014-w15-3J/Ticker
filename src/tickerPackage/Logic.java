package tickerPackage;

import java.util.Collections;
import java.util.Vector;

import ticker.storage.*;


public class Logic{
	// Instances of other components
	Parser parser;
	Storage storage;
	TickerUI UI;

	// Pointer to the Vector currently in display
	Vector<Task> current;

	private static final int SORTED_TIME = 1;
	private static final int SORTED_PRIORITY = 2;
	private static final int TICKED = 3;
	private static final int CMI = 4;


	// Temporary sorted storages
	Vector<Task> sortedTime;
	Vector<Task> sortedPriority;
	Vector<Task> listTicked; // not sorted
	Vector<Task> listCMI; // not sorted
	Vector<Task> searchResults;

	// Tracker to track what Vector is being used

	static int listTracker;

	// HashMaps to be added in later
	public Logic() {

	}

	public Logic(TickerUI UI){
		// Creating 1-1 dependency with UI
		this.UI = UI;

		// Instantiating sub-components
		parser = new Parser();
		storage = new Storage();

		sortedTime = storage.restoreDataFromFile(SORTED_TIME);
		sortedPriority = storage.restoreDataFromFile(SORTED_PRIORITY);
		listTicked = storage.restoreDataFromFile(TICKED);
		listCMI = storage.restoreDataFromFile(CMI);

		searchResults = new Vector<Task>();

		current = sortedTime;
		listTracker = SORTED_TIME;

		UI.setList(list());

	}


	public String getLogic(String input) {
		String feedback;
		UserInput processed = parser.processInput(input);  // double check parser method

		switch (processed.getCommand()) {
		case "delete": 
			feedback = this.delete(processed.getIndex()); break;
		case "clear":
			feedback = this.clear(); break;
			// case "search":
		case "list":
			feedback = this.list(processed.getDescription()); break;
		case "edit":
			feedback = this.edit(processed.getIndex(), processed.getAppending(), processed.getDescription()); break;
		case "add":
			feedback = this.add(processed.getDescription(), processed.getRepeating(), processed.getStartDate(), processed.getEndDate(), processed.getStartTime(), processed.getEndTime()); break;
		case "cmi":
			feedback = this.cmi(processed.getIndex()); break;
			// case "undo":
			// case "redo":
		case "tick":
			feedback = this.tick(processed.getIndex()); break;
		case "help":
			feedback = this.help(); break;
		default:
			feedback = "invalid command";
			break;
		}
		return feedback;
	}


	private String delete(int index) {
		// Exception catching
		if (index > 0 && index <= current.size()) {
			Task deleted = current.remove(index-1);

			if (listTracker == SORTED_TIME) {
				sortedPriority.remove(deleted);
				sortLists();

			}
			if (listTracker == SORTED_PRIORITY) {
				sortedTime.remove(deleted);
				sortLists();
			}

			storeLists();
			
			UI.setList(list());
			return deleted.toString() + " has been removed.\n";
		}

		return "Index out of bounds. Nothing has been deleted.";
	}

	/**
	 * 
	 */
	private void storeLists() {
		storage.writeStorageArrayIntoFile(SORTED_TIME, sortedTime);
		storage.writeStorageArrayIntoFile(SORTED_PRIORITY, sortedPriority);
		storage.writeStorageArrayIntoFile(TICKED, listTicked);
		storage.writeStorageArrayIntoFile(CMI, listCMI);
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

		switch (listTracker) {
		case SORTED_TIME:
			current = sortedTime; break;
		case SORTED_PRIORITY:
			current = sortedPriority; break;
		// TODO: decide whether the view should still stay in Ticked and CMI
		case TICKED:
			current = listTicked; break;
		case CMI:
			current = listCMI; break;
		default:
		}

		UI.setList(list());

		storeLists();

		return "Spick and span!";
	}

	private boolean search(String str) {
		// TODO Auto-generated method stub
		System.out.println("search");
		return false;
	}

	// TODO: Add identifying method to Parser so that user can list in either Time or Array
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

	private String list(String listType) {

		if (listType.equals("time")) {
			current = sortedTime;
			listTracker = SORTED_TIME;
			UI.setList(list());
			return "Listing by time..";
		}

		if (listType.equals("priority")) {
			current = sortedPriority;
			listTracker = SORTED_PRIORITY;
			UI.setList(list());
			return "Listing by priority..";
		}

		else {
			return "Non-existent list.";
		}

	}

	private String edit(int index, boolean isAppending, String description) {
		// Exception catching

		if (index > 0 && index <= current.size()) {
			Task oldTask = current.remove(index - 1);
			Task newTask = oldTask;

			// Edit the other Vector<Task>
			if (listTracker == SORTED_TIME ) {
				sortedPriority.remove(oldTask);
			}
			else if (listTracker == SORTED_PRIORITY) {
				sortedTime.remove(oldTask);
			}

			if (isAppending) {
				String taskName = oldTask.getDescription();
				taskName += " " + description;
				newTask.setDescription(taskName);

				current.add(index - 1, newTask);
				if (listTracker == SORTED_TIME ) {
					sortedPriority.add(newTask);
					sortLists();
				}
				else if (listTracker == SORTED_PRIORITY) {
					sortedTime.add(newTask);
					sortLists();
				}

				storeLists();
				
				UI.setList(list());
				return oldTask.getDescription() + " has been updated to " + newTask.getDescription() + ".\n";
			}

			newTask.setDescription(description);
			current.add(index - 1, newTask);
			if (listTracker == SORTED_TIME ) {
				sortedPriority.add(newTask);
				sortLists();
			}
			else if (listTracker == SORTED_PRIORITY) {
				sortedTime.add(newTask);
				sortLists();
			}

			storeLists();

			UI.setList(list());
			return oldTask.getDescription() + " has been updated to " + newTask.getDescription() + ".\n";
		}

		return"Index out of bounds. Nothing has been edited.\n";

	}


	private String add(String description, boolean isRepeating, Date startDate, Date endDate,
			Time startTime, Time endTime) {

		Task newTask;

		// Creation of RepeatingTask
		if (isRepeating) {
			// TODO: set priority
			if (startDate != null) {
				newTask = new RepeatingTask(description, startDate, startTime, endTime, 0, isRepeating);
			}
			else if (endDate != null) {
				newTask = new RepeatingTask(description, endDate, startTime, endTime, 0, isRepeating);
			}
			else {
				return "No date in repeating task.\n";
			}

		}

		else if (startDate == null && startTime == null) {
			// Creation of floating tasks
			if (endDate == null && endTime == null) {
				// TODO: set priority
				newTask = new FloatingTask(description, 0, false);
			}
			// Creation of deadline tasks
			else {
				// TODO: set priority
				newTask = new DeadlineTask(description, endDate, endTime, 0, false);
			}

		}
		// Creation of timed tasks
		else {
			// TODO: set priority
			newTask = new TimedTask(description, startDate, startTime, endDate, endTime, 0, false);
		}

		// TODO: implementation of search
		sortedTime.add(newTask);
		sortedPriority.add(newTask);

		sortLists();

		storeLists();

		UI.setList(list());
		return description + " has been added.\n";
	}

	private String cmi(int index) {
		// Exception catching
		if (index > 0 && index <= current.size()) {
			Task cmi = current.remove(index-1);
			// Add to the front so the latest additions are on top
			listCMI.add(0, cmi);
			sortedTime.remove(cmi);
			sortedPriority.remove(cmi);

			sortLists();
			storeLists();
			UI.setList(list());
			return cmi.toString() + " cannot be done!\n";
		}

		return "Index out of bounds. Nothing has been CMI-ed.";
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
		if (index > 0 && index <= current.size()) {
			Task ticked = current.remove(index-1);
			sortedTime.remove(ticked);
			sortedPriority.remove(ticked);
			listTicked.add(0, ticked);

			sortLists();
			storeLists();

			UI.setList(list());
			return ticked.toString() + " is done!\n";
		}

		return "Index out of bounds. Nothing has been ticked.";
	}
}

