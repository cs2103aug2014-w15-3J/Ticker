package tickerPackage;

import java.util.Vector;
import ticker.storage.*;

public class Logic{
	// Instances of other components
	Parser parser;
	//Storage storage;
	TickerUI UI;

	// Pointer to the Vector currently in display
	Vector<Task> current;

	// Temporary sorted storages
	Vector<Task> sortedTime;
	Vector<Task> sortedPriority;
	Vector<Task> searchResults;

	// HashMaps to be added in later
	public Logic() {
		
	}

	public Logic(TickerUI UI){
		// TODO Transfer data from storage

		// Creating 1-1 dependency with UI
		this.UI = UI;

		// Instantiating sub-components
		parser = new Parser();
		//storage = new Storage();

		// STUB:
		sortedTime = new Vector<Task>();
		sortedPriority = new Vector<Task>();
		searchResults = new Vector<Task>();

		current = sortedTime;

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
			list += ++i + ". " + task.toString() + "\n";
		}
		return list;
	}

	public String edit(int index, boolean isAppending, String description) {
		// Exception catching
		if (index >= 0 && index < current.size()) {
			Task editTask = current.remove(index - 1);

			if (isAppending) {
				String taskName = editTask.getDescription();
				taskName += " " + description;
				editTask.setDescription(taskName);

				current.add(index - 1, editTask);
				
				UI.setList(list());
				return "Index " + index + " has been updated to " + current.get(index) + ".\n";
			}

			editTask.setDescription(description);
			current.add(index, editTask);
			
			UI.setList(list());
			return "Index " + index + " has been updated to " + current.get(index) + ".\n";
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
		sortedPriority.add(newTask);
		
		UI.setList(list());
		return description + " has been added.\n";
	}
}


// TODO: 
// -sort the different vectors
// -how to implement repeating tasks
// -implement switch current
// -modify storage after every action