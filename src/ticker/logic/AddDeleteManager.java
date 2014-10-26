package ticker.logic;

import java.util.Vector;

import ticker.common.Date;
import ticker.common.DeadlineTask;
import ticker.common.FloatingTask;
import ticker.common.RepeatingTask;
import ticker.common.Task;
import ticker.common.Time;
import ticker.common.TimedTask;

public class AddDeleteManager {
	// CONSTANTS
	// String constants for command types
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DELETE = "delete";
	// Integer key constants for lists used by listTracker
	private static final int KEY_SORTED_TIME = 1;
	private static final int KEY_SORTED_PRIORITY = 2;

	// Instances of other components
	private UndoManager undoMng;
	private Vector<Task> storedTasksByPriority, storedTasksByTime;
	
	AddDeleteManager(Vector<Task> storedTasksByPriority, Vector<Task> storedTasksByTime, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByCMI) {
		this.storedTasksByPriority = storedTasksByPriority;
		this.storedTasksByTime = storedTasksByTime;
		
		undoMng = UndoManager.getInstance(storedTasksByPriority, storedTasksByTime, storedTasksByTicked, storedTasksByCMI);
	}
	
	String delete(int index, int listTracker, Vector<Task> current, String currentListName) throws ArrayIndexOutOfBoundsException {
		// Exception catching

		Task deleted = current.remove(index-1);

		if (listTracker == KEY_SORTED_TIME) {
			storedTasksByPriority.remove(deleted);
		}
		if (listTracker == KEY_SORTED_PRIORITY) {
			storedTasksByTime.remove(deleted);
		}

		Event event = new Event(COMMAND_DELETE, deleted, currentListName, index - 1);
		undoMng.add(event);

		return deleted.toString() + " has been removed.\n";


	}
	String add(String description, boolean isRepeating, Date startDate, Date endDate,
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

		storedTasksByTime.add(newTask);
		storedTasksByPriority.add(newTask);

		Event event = new Event(COMMAND_ADD, newTask);
		undoMng.add(event);


		return description + " has been added.\n";
	}


}
