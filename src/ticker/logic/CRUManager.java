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

		return deleted.getDescription() + " has been removed.";


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

		addBackRemovedTask(newTask);

		Event event = new Event(COMMAND_ADD, newTask);
		undoMng.add(event);


		return description + " has been added.";
	}

	String edit(int index, boolean isAppending, String description,boolean isRepeating, Date startDate, Date endDate, Time startTime, Time endTime,
			char priority, int listTracker, Vector<Task> current) throws ArrayIndexOutOfBoundsException{
		Task oldTask;
		Task newTask;

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

		// Edits task description by adding words behind
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
		}
		// Edit description of task
		else if (description != null && !description.equals("")) {
			newTask.setDescription(description);
		}

		newTask.setRepeat(isRepeating);
		// Edit startDate only
		if (startDate != null && endDate == null) {
			// Edited task can update startDate without worrying of endDate being earlier than startDate
			if (newTask.getEndDate() == null) {
				newTask.setStartDate(startDate);
			}
			else {
				// Error: Edited task will end up with earlier endDate than startDate
				if (newTask.getEndDate().compareTo(startDate) == -1) {
					addBackRemovedTask(oldTask);
					return "Invalid edit on starting date";
				}
				newTask.setStartDate(startDate);
			}
		}
		// Edit endDate only
		if (endDate != null && startDate == null) {
			// Edited task can update endDate without worrying of endDate being earlier than startDate
			if (newTask.getStartDate() == null) {
				newTask.setEndDate(endDate);
			}
			else {
				// Error: Edited task will end up with earlier endDate than startDate
				if (newTask.getStartDate().compareTo(endDate) == 1) {
					addBackRemovedTask(oldTask);
					return "Invalid edit on ending date";
				}
				newTask.setEndDate(endDate);
			}
		}
		// Edit startDate and endDate
		if (startDate != null && endDate != null) {
			// Error: endDate is earlier than startDate
			if (startDate.compareTo(endDate) == 1) {
				addBackRemovedTask(oldTask);
				return "Invalid edit on dates";
			}
			else {
				newTask.setStartDate(startDate);
				newTask.setEndDate(endDate);
			}
		}

		// Edit startTime only
		if (startTime != null && endTime == null) {
			// Edited task can update startTime without worrying of endTime being earlier than startTime
			if (newTask.getEndTime() == null) {
				newTask.setStartTime(startTime);
			}
			else {
				// Error: Edited task will end up with earlier endTime than startTime
				if (newTask.getEndTime().compareTo(startTime) == -1) {
					addBackRemovedTask(oldTask);
					return "Invalid edit on starting time";
				}
				newTask.setStartTime(startTime);
			}
		}
		// Edit endTime only
		if (endTime != null && startTime == null) {
			// Edited task can update endTime without worrying of endTime being earlier than startTime
			if (newTask.getStartTime() == null) {
				newTask.setEndTime(endTime);
			}
			else {
				// Error: Edited task will end up with earlier endTime than startTime
				if (newTask.getStartTime().compareTo(endTime) == 1) {
					addBackRemovedTask(oldTask);
					return "Invalid edit on ending time";
				}
				newTask.setEndTime(endTime);
			}
		}
		// Edit startTime and endTime
		if (startTime != null && endTime != null) {
			// Error: endTime is earlier than startTime
			if (startTime.compareTo(endTime) == 1) {
				addBackRemovedTask(oldTask);
				return "Invalid edit on both timings";
			}
			else {
				newTask.setStartTime(startTime);
				newTask.setEndTime(endTime);
			}
		}
		
		if (priority != '\u0000') {
			newTask.setPriority(priority);
		}

		if (listTracker == KEY_SEARCH) {
			addBackRemovedTask(newTask);
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
		Event event = new Event(COMMAND_EDIT, oldTask, newTask);
		undoMng.add(event);
		return oldTask.getDescription() + " has been updated.";
	}

	/**
	 * @param oldTask
	 */
	private void addBackRemovedTask(Task oldTask) {
		storedTasksByTime.add(oldTask);
		storedTasksByPriority.add(oldTask);
	}
}
