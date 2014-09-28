package tickerPackage;

import java.util.Vector;

class Logic{
	// Pointer to the Vector currently in display
	Vector<Task> current;

	// Temporary sorted storages
	Vector<Task> sortedTime;
	Vector<Task> sortedPriority;
	Vector<Task> searchResults;

	// HashMaps to be added in later

	public Logic(){
		// TODO Transfer data from storage

		// STUB:
		sortedTime = new Vector<Task>();
		sortedPriority = new Vector<Task>();
		searchResults = new Vector<Task>();

		current = sortedTime;

	}	

	public boolean delete(int index) {
		// Exception catching
		if (index >= 0 && index < current.size()) {
			Task deleted = current.remove(index);
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
			System.out.printf("%d. %s\n", i, task.toString());
		}
		return true;
	}

	public void edit(int index, boolean isAppending, String description) {
		// Exception catching
		if (index >= 0 && index < current.size()) {
			Task editTask = current.remove(index);

			if (isAppending) {
				String taskName = editTask.getDescription();
				taskName += " " + description;
				editTask.setDescription(taskName);

				current.add(index, editTask);

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