package tickerPackage;

import java.util.Vector;
import ticker.storage.*;

class Logic{
	// Instances of other components
	Parser parser;
	Storage storage;
	TickerUI UI;
	
	// Pointer to the Vector currently in display
	Vector<Task> current;

	// Temporary sorted storages
	Vector<Task> sortedTime;
	Vector<Task> sortedPriority;
	Vector<Task> searchResults;

	// HashMaps to be added in later

	public Logic(TickerUI UI){
		// TODO Transfer data from storage
		
		// Creating 1-1 dependency with UI
		this.UI = UI;
		
		// Instantiating sub-components
		parser = new Parser();
		storage = new Storage();

		// STUB:
		sortedTime = new Vector<Task>();
		sortedPriority = new Vector<Task>();
		searchResults = new Vector<Task>();

		current = sortedTime;
		this.run();

	}
	
	// TODO: need UI API to call UI for command input
	private void run() {
		/*
		String input = UI.getInput();
		while(input != ...........) { what will UI return logic when there is no input
			UserInput processed = parser.processInput(input);  // double check parser method
			
			switch (processed.getCommand()) {
				case "delete": 
					this.delete(processed.getIndex()); break;
				// case "search":
				case "list":
					this.list(); break;
				case "edit":
					this.edit(processed.getIndex(), processed.getAppending(), processed.getDescription()); break;
				case "add":
					this.delete(processed.getDescription(), processed.getRepeating(), processed,getStartDate(), 
									processed.getEndDate(), processed.getStartTime(), processed.getEndTime()); break;
				// case "undo":
				default:
					System.out.println("invalid command");
					break;
			}
			
			if (UI.hasNext()) {
				input = UI.getInput();
			}
		}
		*/
	
	}

	public boolean delete(int index) {
		// Exception catching
		if (index > 0 && index <= current.size()) {
			Task deleted = current.remove(index-1);
			sortedTime.remove(deleted);
			sortedPriority.remove(deleted);
			System.out.printf("%s has been removed.\n", deleted);
			return true;
		}

		System.out.println("Index out of bounds. Nothing has been deleted.");
		return false;
	}

	public boolean search(String str) {
		// TODO Auto-generated method stub
		System.out.println("search");
		return false;
	}

	public boolean list() {
		if (current == null) {
			System.out.printf("Nothing to display.\n");
			return false;
		}
		int i = 1;
		for (Task task: current) {
			System.out.printf("%d. %s\n", i++, task.toString());
		}
		return true;
	}

	public void edit(int index, boolean isAppending, String description) {
		// Exception catching
		if (index >= 0 && index < current.size()) {
			Task editTask = current.remove(index - 1);

			if (isAppending) {
				String taskName = editTask.getDescription();
				taskName += " " + description;
				editTask.setDescription(taskName);

				current.add(index - 1, editTask);

				System.out.printf("Index %d has been updated to %s.\n", index, current.get(index));
				return;
			}

			editTask.setDescription(description);
			current.add(index, editTask);

			System.out.printf("Index %d has been updated to %s.\n", index, current.get(index));
			return;
		}

		System.out.println("Index out of bounds. Nothing has been edited.\n");

	}


	public void add(String description, Boolean isRepeating, Date startDate, Date endDate,
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
		
		System.out.printf("%s has been added.\n", description);
	}
}


// TODO: 
// -sort the different vectors
// -how to implement repeating tasks
// -implement switch current