package ticker.logic;

import java.util.Vector;

import ticker.common.Date;
import ticker.common.DeadlineTask;
import ticker.common.FloatingTask;
import ticker.common.RepeatingTask;
import ticker.common.Task;
import ticker.common.Time;
import ticker.common.TimedTask;

public class CRUManager {
	// CONSTANTS
	// String constants for command types
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_EDIT = "edit";
	// Integer key constants for lists used by listTracker
	private static final int KEY_SORTED_TIME = 1;
	private static final int KEY_SORTED_PRIORITY = 2;
	private static final int KEY_TICKED = 3;
	private static final int KEY_CMI = 4;
	private static final int KEY_SEARCH = 5;
	// String constants for type of lists used
	private static final String TASKS_TIME = "TIME";
	private static final String TASKS_TICKED = "TICKED";
	private static final String TASKS_CMI = "CMI";

	// Instances of other components
	private UndoManager undoMng;
	private Vector<Task> storedTasksByPriority, storedTasksByTime, storedTasksByTicked, storedTasksByCMI;

	CRUManager(Vector<Task> storedTasksByTime, Vector<Task> storedTasksByPriority, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByCMI) {
		this.storedTasksByPriority = storedTasksByPriority;
		this.storedTasksByTime = storedTasksByTime;
		this.storedTasksByTicked = storedTasksByTicked;
		this.storedTasksByCMI = storedTasksByTicked;

		undoMng = UndoManager.getInstance(storedTasksByPriority, storedTasksByTime, storedTasksByTicked, storedTasksByCMI);
	}

	String delete(int index, int listTracker, Vector<Task> current, String currentListName) 
			throws ArrayIndexOutOfBoundsException {
		Event event;
		Task deleted = current.remove(index-1);

		if (listTracker == KEY_SORTED_TIME) {
			storedTasksByPriority.remove(deleted);
			event = new Event(COMMAND_DELETE, deleted, currentListName, index - 1);
			undoMng.add(event);
		}
		else if (listTracker == KEY_SORTED_PRIORITY) {
			storedTasksByTime.remove(deleted);
			event = new Event(COMMAND_DELETE, deleted, currentListName, index - 1);
			undoMng.add(event);
		}
		else if (listTracker == KEY_SEARCH) {
			int indexCounter;
			if (storedTasksByTime.contains(deleted) || storedTasksByPriority.contains(deleted)) {
				indexCounter = 0;
				for (Task task: storedTasksByTime) {
					if (task.equals(deleted)) {
						break;
					}
					indexCounter++;
				}
				storedTasksByTime.remove(deleted);
				storedTasksByPriority.remove(deleted);
				event = new Event(COMMAND_DELETE, deleted, TASKS_TIME, indexCounter);
				undoMng.add(event);
			}
			else if (storedTasksByTicked.contains(deleted)) {
				indexCounter = 0;
				for (Task task: storedTasksByTicked) {
					if (task.equals(deleted)) {
						break;
					}
					indexCounter++;
				}
				storedTasksByTicked.remove(deleted);
				event = new Event(COMMAND_DELETE, deleted, TASKS_TICKED, indexCounter);
				undoMng.add(event);
			}
			else if (storedTasksByCMI.contains(deleted)) {
				indexCounter = 0;
				for (Task task: storedTasksByCMI) {
					if (task.equals(deleted)) {
						break;
					}
					indexCounter++;
				}
				storedTasksByCMI.remove(deleted);
				event = new Event(COMMAND_DELETE, deleted, TASKS_CMI, indexCounter);
				undoMng.add(event);
			}
		}
		else {
			event = new Event(COMMAND_DELETE, deleted, currentListName, index - 1);
			undoMng.add(event);
		}

		return deleted.toString() + " has been removed.\n";


	}
	String add(String description, boolean isRepeating, Date startDate, Date endDate, Time startTime, Time endTime,
			char priority) throws IllegalArgumentException {

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

	String edit(int index, boolean isAppending, String description,boolean isRepeating, Date startDate, Date endDate, Time startTime, Time endTime,
			char priority, int listTracker, Vector<Task> current) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		Task oldTask;
		Task newTask;

		if (description == null || description.equals("")) {
			throw new IllegalArgumentException();
		}

		if (listTracker == KEY_TICKED && listTracker == KEY_CMI) {
			return "Cannot edit from ticked and CMI list.";
		}

		if (listTracker == KEY_SEARCH) {
			oldTask = current.get(index - 1);
			if (storedTasksByTime.contains(oldTask) || storedTasksByPriority.contains(oldTask)) {
				storedTasksByTime.remove(oldTask);
				storedTasksByTime.remove(oldTask);
			}
			else if (storedTasksByTicked.contains(oldTask) || storedTasksByCMI.contains(oldTask)) {
				return "Cannot edit from ticked and CMI list.";
			}
		}
		else {
			oldTask = current.remove(index-1);
		}

		newTask = oldTask.copy();

		// Edit the other Vector<Task>
		if (listTracker == KEY_SORTED_TIME ) {
			storedTasksByPriority.remove(oldTask);
		}
		else if (listTracker == KEY_SORTED_PRIORITY) {
			storedTasksByTime.remove(oldTask);
		}

		if (isAppending) {
			String taskName = oldTask.getDescription();
			taskName += " " + description;
			newTask.setDescription(taskName);

			current.add(index - 1, newTask);
			if (listTracker == KEY_SORTED_TIME ) {
				storedTasksByPriority.add(newTask);
			}
			else if (listTracker == KEY_SORTED_PRIORITY) {
				storedTasksByTime.add(newTask);
			}

			Event event = new Event(COMMAND_EDIT, oldTask, newTask);
			undoMng.add(event);

			return oldTask.getDescription() + " has been updated to " + newTask.getDescription() + ".\n";
		}

		newTask.setDescription(description);
		if (listTracker == KEY_SEARCH) {
			storedTasksByTime.add(newTask);
			storedTasksByPriority.add(newTask);
		}
		else {
			current.add(index - 1, newTask);
			if (listTracker == KEY_SORTED_TIME ) {
				storedTasksByPriority.add(newTask);
			}
			else if (listTracker == KEY_SORTED_PRIORITY) {
				storedTasksByTime.add(newTask);
			}
		}
		return oldTask.getDescription() + " has been updated to " + newTask.getDescription() + ".\n";
	}
}
