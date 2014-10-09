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


	// Temporary sorted storages
	Vector<Task> sortedTime;
	Vector<Task> sortedPriority;
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
			// case "search":

			//TODO: getWHAT?? get index or get number
		case "list":
			feedback = this.list(processed.getIndex()); break;

		case "edit":
			feedback = this.edit(processed.getIndex(), processed.getAppending(), processed.getDescription()); break;
		case "add":
			feedback = this.add(processed.getDescription(), processed.getRepeating(), processed.getStartDate(), processed.getEndDate(), processed.getStartTime(), processed.getEndTime()); break;
			// case "undo":
		case "help":
			feedback = this.help(); break;

			// case "cmi":
			// case "undo":
			// case "redo":
		case "tick":
			feedback = this.tick(processed.getIndex()); break;
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
			sortedTime.remove(deleted);
			sortedPriority.remove(deleted);

			Collections.sort(sortedTime, new sortByTime());
			Collections.sort(sortedPriority, new sortByPriority());

			storage.writeStorageArrayIntoFile(SORTED_TIME, sortedTime);
			storage.writeStorageArrayIntoFile(SORTED_PRIORITY, sortedPriority);
			UI.setList(list());
			return deleted.toString() + " has been removed.\n";
		}

		return "Index out of bounds. Nothing has been deleted.";
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
			if (task instanceof RepeatingTask) {
				RepeatingTask rt = (RepeatingTask) task;
				list += ++i + ". " + rt.toString() + "\n";// TODO: implement repeatingtask
			}
			else if (task instanceof FloatingTask) {
				FloatingTask ft = (FloatingTask) task;
				list += ++i + ". " + ft.toString() + "\n";
			}
			else if (task instanceof DeadlineTask) {
				DeadlineTask dt = (DeadlineTask) task;
				list += ++i + ". " + dt.toString() + "\n";
			}

			else if (task instanceof TimedTask) {
				TimedTask tt = (TimedTask) task;
				list += ++i + ". " + tt.toString() + "\n";
			}

			else {
				list = ++i + ". error in typecasting task\n";
			}
		}
		return list;
	}

	private String list(int listNo) {

		if (listNo == SORTED_TIME) {
			current = sortedTime;
			listTracker = SORTED_TIME;
			return this.list();
		}

		if (listNo == SORTED_PRIORITY) {
			current = sortedPriority;
			listTracker = SORTED_PRIORITY;
			return this.list();
		}

		else {
			return "Non-existent list.\n";
		}

	}

	private String edit(int index, boolean isAppending, String description) {
		// Exception catching

		if (index > 0 && index <= current.size()) {
			Task editTask = current.remove(index - 1);

			// Edit the other Vector<Task>
			if (listTracker == SORTED_TIME ) {
				sortedPriority.remove(editTask);
			}
			else if (listTracker == SORTED_PRIORITY) {
				sortedTime.remove(editTask);
			}

			if (isAppending) {
				String taskName = editTask.getDescription();
				taskName += " " + description;
				editTask.setDescription(taskName);

				// TODO: to implement sort function so there will not be need to keep index at the same place
				current.add(index - 1, editTask);
				if (listTracker == SORTED_TIME ) {
					sortedPriority.add(editTask);
				}
				else if (listTracker == SORTED_PRIORITY) {
					sortedTime.add(editTask);
				}

				storage.writeStorageArrayIntoFile(SORTED_TIME, sortedTime);
				storage.writeStorageArrayIntoFile(SORTED_PRIORITY, sortedPriority);
				UI.setList(list());
				return "Index " + index + " has been updated to " + current.get(index - 1) + ".\n";
			}

			editTask.setDescription(description);
			current.add(index - 1, editTask);
			if (listTracker == SORTED_TIME ) {
				sortedPriority.add(editTask);
			}
			else if (listTracker == SORTED_PRIORITY) {
				sortedTime.add(editTask);
			}

			Collections.sort(sortedTime, new sortByTime());
			Collections.sort(sortedPriority, new sortByPriority());

			storage.writeStorageArrayIntoFile(SORTED_TIME, sortedTime);
			storage.writeStorageArrayIntoFile(SORTED_PRIORITY, sortedPriority);

			UI.setList(list());
			return "Index " + index + " has been updated to " + current.get(index - 1) + ".\n";
		}

		return"Index out of bounds. Nothing has been edited.\n";

	}


	private String add(String description, boolean isRepeating, Date startDate, Date endDate,
			Time startTime, Time endTime) {
		// TODO check with kexin whether tasks are correctly allocated

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

		Collections.sort(sortedTime, new sortByTime());
		Collections.sort(sortedPriority, new sortByPriority());

		storage.writeStorageArrayIntoFile(SORTED_TIME, sortedTime);
		storage.writeStorageArrayIntoFile(SORTED_PRIORITY, sortedPriority);

		UI.setList(list());
		return description + " has been added.\n";
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

			Collections.sort(sortedTime, new sortByTime());
			Collections.sort(sortedPriority, new sortByPriority());

			storage.writeStorageArrayIntoFile(SORTED_TIME, sortedTime);
			storage.writeStorageArrayIntoFile(SORTED_PRIORITY, sortedPriority);
			UI.setList(list());
			return ticked.toString() + " is done!\n";
		}

		return "Index out of bounds. Nothing has been ticked.";
	}
}


// TODO: 
// -implement switch current
