package tickerPackage;

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
		// TODO Transfer data from storage

		// Creating 1-1 dependency with UI
		this.UI = UI;

		// Instantiating sub-components
		parser = new Parser();
		storage = new Storage();
		
		sortedTime = storage.restoreDataFromFile(SORTED_TIME);
		sortedPriority = storage.restoreDataFromFile(SORTED_PRIORITY);

		// STUB:
		sortedTime = new Vector<Task>();
		sortedPriority = new Vector<Task>();
		searchResults = new Vector<Task>();

		current = sortedTime;
		listTracker = SORTED_TIME;

	}

	// TODO: need UI API to call UI for command input
	public String getLogic(String input) {
		String feedback;
		UserInput processed = parser.processInput(input);  // double check parser method

		switch (processed.getCommand()) {
		case "delete": 
			feedback = this.delete(processed.getIndex()); break;
			// case "search":
		case "list":
			feedback = this.list(); break;
		case "edit":
			feedback = this.edit(processed.getIndex(), processed.getAppending(), processed.getDescription()); break;
		case "add":
			feedback = this.add(processed.getDescription(), processed.getRepeating(), processed.getStartDate(), processed.getEndDate(), processed.getStartTime(), processed.getEndTime()); break;
					// case "undo":
		default:
			feedback = "invalid command";
			break;
		}
		return feedback;
	}


	public String delete(int index) {
		// Exception catching
		if (index > 0 && index <= current.size()) {
			Task deleted = current.remove(index-1);
			sortedTime.remove(deleted);
			sortedPriority.remove(deleted);
			UI.setList(list());
			return deleted.toString() + " has been removed.\n";
		}

		return "Index out of bounds. Nothing has been deleted.";
	}

	public boolean search(String str) {
		// TODO Auto-generated method stub
		System.out.println("search");
		return false;
	}

	public String list() {
		if (current == null) {
			return "Nothing to display.\n";
		}
		int i = 0;
		String list = "";
		for (Task task: current) {
			if (task instanceof FloatingTask) {
				FloatingTask ft = (FloatingTask) task;
				list += ++i + ". " + ft.toString() + "\n";
			}
			else if (task instanceof DeadlineTask) {
				DeadlineTask dt = (DeadlineTask) task;
				list += ++i + ". " + task.toString() + " " + dt.getEndDate() + " " + dt.getEndTime() + "\n";
			}
			
			else if (task instanceof TimedTask) {
				TimedTask tt = (TimedTask) task;
				list += ++i + ". " + task.toString() + " " + tt.getStartDate() + " " + tt.getStartTime() 
										+ " " + tt.getEndDate() + " " + tt.getEndTime() + "\n";
			}
			else if (task instanceof RepeatingTask) {
				RepeatingTask rt = (RepeatingTask) task;
				// TODO: implement repeatingtask
			}
			else {
				list = ++i + ". error in typecasting task\n";
			}
		}
		return list;
	}

	public String edit(int index, boolean isAppending, String description) {
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

				
				UI.setList(list());
				return "Index " + index + " has been updated to " + current.get(index) + ".\n";
			}

			editTask.setDescription(description);
			current.add(index - 1, editTask);
			if (listTracker == SORTED_TIME ) {
				sortedPriority.add(editTask);
			}
			else if (listTracker == SORTED_PRIORITY) {
				sortedTime.add(editTask);
			}

			
			UI.setList(list());
			return "Index " + index + " has been updated to " + current.get(index - 1) + ".\n";
		}

		return"Index out of bounds. Nothing has been edited.\n";

	}


	public String add(String description, Boolean isRepeating, Date startDate, Date endDate,
			Time startTime, Time endTime) {
		// TODO priority is missing
		// TODO check with kexin whether tasks are correctly allocated
		// TODO how to implement repeating tasks

		Task newTask;

		if (startDate == null && startTime == null) {
			// Creation of floating tasks
			if (endDate == null && endTime == null) {
				newTask = new FloatingTask(description);
			}
			// Creation of deadline tasks
			else {
				newTask = new DeadlineTask(description, endDate, endTime);
			}

		}
		// Creation of timed tasks
		else {
			newTask = new TimedTask(description, startDate, startTime, endDate, endTime);
		}

		// TODO: implementation of search
		sortedTime.add(newTask);
		storage.writeStorageArrayIntoFile(SORTED_TIME, sortedTime);
		sortedPriority.add(newTask);
		storage.writeStorageArrayIntoFile(SORTED_PRIORITY, sortedPriority);
		
		UI.setList(list());
		return description + " has been added.\n";
	}
}


// TODO: 
// -sort the different vectors
// -how to implement repeating tasks
// -implement switch current
// -modify storage after every action