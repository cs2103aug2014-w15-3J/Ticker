//@author A0114535M

/* Team ID: W15-3J
 * Name: Li Jia'En, Nicholette
 * Matric Number: A0114535M
 * Project Title: Ticker
 * Class: CRUManager
 * Description: This class adds, delete and edits task.
 * Assumptions: 
 * This program assumes that:
 * -this class will be called by Logic class.
 * -the Logic class will always be used with classes CRUDManager, TickKIVManager, UndoRedoManager and SearchManager.
 */

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
	private static final int KEY_KIV = 4;
	private static final int KEY_SEARCH = 5;
	private static final int KEY_FREESLOTS = 6;
	// String constants for type of lists used
	private static final String TASKS_TIME = "time";
	private static final String TASKS_TICKED = "ticked";
	private static final String TASKS_KIV = "kiv";
	// String constants for stamps
	private static final String FREESLOT_STAMP = "\\***FREE***\\";

	// Instances of other components
	private UndoManager undoMng;
	private Vector<Task> storedTasksByPriority, storedTasksByTime, storedTasksByTicked, storedTasksByKIV;

	CRUManager(Vector<Task> storedTasksByTime, Vector<Task> storedTasksByPriority, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByKIV) {
		this.storedTasksByPriority = storedTasksByPriority;
		this.storedTasksByTime = storedTasksByTime;
		this.storedTasksByTicked = storedTasksByTicked;
		this.storedTasksByKIV = storedTasksByKIV;

		undoMng = UndoManager.getInstance(storedTasksByPriority, storedTasksByTime, storedTasksByTicked, storedTasksByKIV);
	}

	/**
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
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
			if (startDate != null && endDate != null && startTime != null && endTime != null && 
					(endDate.compareTo(startDate) == -1 || endTime.compareTo(startTime) == -1)) {
				return "Invalid ending date or time.";
			}

			if (startDate != null && endDate != null && startTime == null && endTime == null && 
					(endDate.compareTo(startDate) == -1)) {
				return "Invalid ending date.";
			}
			newTask = new TimedTask(description, startDate, startTime, endDate, endTime, priority, false);
		}

		// Check whether there's an exact task already inside the list
		if (storedTasksByPriority.contains(newTask) || storedTasksByTicked.contains(newTask) || storedTasksByKIV.contains(newTask)) {
			return "Task already exists.";
		}

		addTaskIntoUndone(newTask);

		Event event = new Event(COMMAND_ADD, newTask);
		undoMng.add(event);


		return description + " has been added.";
	}

	/**
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
	String delete(int index, int listTracker, Vector<Task> current, String currentListName) 
			throws ArrayIndexOutOfBoundsException {
		Event event;
		Task deleted;

		if (listTracker == KEY_SORTED_TIME) {
			deleted = current.remove(index - 1);
			storedTasksByPriority.remove(deleted);
			event = new Event(COMMAND_DELETE, deleted, currentListName, index - 1);
			undoMng.add(event);
		}
		else if (listTracker == KEY_SORTED_PRIORITY) {
			deleted = current.remove(index - 1);
			storedTasksByTime.remove(deleted);
			event = new Event(COMMAND_DELETE, deleted, currentListName, index - 1);
			undoMng.add(event);
		}
		else if (listTracker == KEY_SEARCH) {
			int indexCounter;
			Task tickedPartition = new Task("\\***TICKED***\\", null, null, null, null, 'B', false);
			Task kivPartition = new Task("\\***KIV***\\", null, null, null, null, 'B', false);
			int tickedPartitionIndex = current.indexOf(tickedPartition);
			int kivPartitionIndex = current.indexOf(kivPartition);

			if ((index - 1) < tickedPartitionIndex) {
				deleted = current.remove(index - 1);
			}
			else if ((index - 1) >= tickedPartitionIndex && (index - 1) <= (kivPartitionIndex - 2)) {
				deleted = current.remove(index);
			}
			else {
				deleted = current.remove(index + 1);
			}

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
			else if (storedTasksByKIV.contains(deleted)) {
				indexCounter = 0;
				for (Task task: storedTasksByKIV) {
					if (task.equals(deleted)) {
						break;
					}
					indexCounter++;
				}
				storedTasksByKIV.remove(deleted);
				event = new Event(COMMAND_DELETE, deleted, TASKS_KIV, indexCounter);
				undoMng.add(event);
			}
		}
		else if (listTracker == KEY_FREESLOTS) {
			deleted = current.get(index - 1);
			if (deleted.getDescription() == FREESLOT_STAMP) {
				return "Cannot delete freeslot.";
			}
			else {
				current.remove(index - 1);
				int actualIndex = storedTasksByTime.indexOf(deleted);
				storedTasksByTime.remove(deleted);
				storedTasksByPriority.remove(deleted);
				event = new Event(COMMAND_DELETE, deleted, TASKS_TIME, actualIndex);
				undoMng.add(event);
			}
		}
		// For ticked list and KIV list
		else {
			deleted = current.remove(index - 1);
			event = new Event(COMMAND_DELETE, deleted, currentListName, index - 1);
			undoMng.add(event);
		}

		return deleted.getDescription() + " has been removed.";
	}



	// Resetting repeated task will make it a timedTask
	/**
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
	Task remakeTask(Task task) throws IllegalArgumentException {
		String description = task.getDescription();
		boolean isRepeating = false;
		Date startDate = task.getStartDate();
		Date endDate = task.getEndDate();
		Time startTime = task.getStartTime();
		Time endTime = task.getEndTime();
		char priority = task.getPriority();

		if (description == null || description.equals("")) {
			throw new IllegalArgumentException();
		}

		Task newTask;

		newTask = new TimedTask(description, startDate, startTime, endDate, endTime, priority, false);

		return newTask;
	}

	/**
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
	String edit(int index, String description,boolean isRepeating, Date startDate, Date endDate, Time startTime, Time endTime,
			char priority, int listTracker, Vector<Task> current) throws ArrayIndexOutOfBoundsException{
		Task oldTask;
		Task newTask;

		if (listTracker == KEY_TICKED) {
			return "Cannot edit from ticked list.";
		}
		else if (listTracker == KEY_KIV) { 
			return "Cannot edit from KIV list.";
		}

		else if (listTracker == KEY_SEARCH) {
			Task tickedPartition = new Task("\\***TICKED***\\", null, null, null, null, 'B', false);
			Task kivPartition = new Task("\\***KIV***\\", null, null, null, null, 'B', false);
			int tickedPartitionIndex = current.indexOf(tickedPartition);
			int kivPartitionIndex = current.indexOf(kivPartition);

			if ((index - 1) < tickedPartitionIndex) {
				oldTask = current.get(index - 1);
			}
			else if ((index - 1) >= tickedPartitionIndex && (index - 1) <= (kivPartitionIndex - 2)) {
				oldTask = current.get(index);
			}
			else {
				oldTask = current.get(index + 1);
			}

			if (storedTasksByTime.contains(oldTask) || storedTasksByPriority.contains(oldTask)) {
				storedTasksByTime.remove(oldTask);
				storedTasksByTime.remove(oldTask);
			}
			else if (storedTasksByTicked.contains(oldTask) || storedTasksByKIV.contains(oldTask)) {
				return "Cannot edit from ticked and KIV list.";
			}
		}

		else if (listTracker == KEY_FREESLOTS) {
			oldTask = current.remove(index - 1);
			storedTasksByTime.remove(oldTask);
			storedTasksByTime.remove(oldTask);
		}

		// in undone lists
		else {
			oldTask = current.remove(index-1);
			// Edit the other Vector<Task>
			if (listTracker == KEY_SORTED_TIME ) {
				storedTasksByPriority.remove(oldTask);
			}
			else if (listTracker == KEY_SORTED_PRIORITY) {
				storedTasksByTime.remove(oldTask);
			}
		}

		newTask = oldTask.copy();

		// Edit description of task
		if (description != null && !description.equals("")) {
			newTask.setDescription(description);
		}

		// Edit startDate only
		if (startDate != null && endDate == null) {
			// Edited task can update startDate without worrying of endDate being earlier than startDate
			if (newTask.getEndDate() == null) {
				if (newTask instanceof FloatingTask) {
					newTask = new TimedTask(newTask.getDescription(), startDate, new Time(0,0), null, null, newTask.getPriority(), newTask.getRepeat());
				}
				// Editing TimedTask or RepeatingTask
				else {
					newTask.setStartDate(startDate);
				}
			}
			else {
				// Error: Edited task will end up with earlier endDate than startDate
				if (newTask.getEndDate().compareTo(startDate) == -1) {
					addTaskIntoUndone(oldTask);
					return "Invalid edit on starting date";
				}
				// Edit DeadlineTask
				newTask = new TimedTask(newTask.getDescription(), startDate, new Time(0, 0), newTask.getEndDate(), newTask.getEndTime(), newTask.getPriority(), newTask.getRepeat());
			}
		}

		// Edit endDate only
		if (endDate != null && startDate == null) {
			// Edited task can update endDate without worrying of endDate being earlier than startDate
			if (newTask.getStartDate() == null) {
				if (newTask instanceof FloatingTask) {
					newTask = new DeadlineTask(newTask.getDescription(), endDate, new Time(23, 59), newTask.getPriority(), newTask.getRepeat());
				}
				// Editing DeadlineTask
				else {
					newTask.setEndDate(endDate);
				}
			}
			else {
				// Error: Edited task will end up with earlier endDate than startDate
				if (newTask.getStartDate().compareTo(endDate) == 1) {
					addTaskIntoUndone(oldTask);
					return "Invalid edit on ending date";
				}
				// Editing TimedTask and RepeatingTask
				newTask.setEndDate(endDate);
				if (newTask.getEndTime() == null) {
					newTask.setEndTime(new Time(23, 59));
				}
			}
		}
		// Edit startDate and endDate
		if (startDate != null && endDate != null) {
			// Error: endDate is earlier than startDate
			if (startDate.compareTo(endDate) == 1) {
				addTaskIntoUndone(oldTask);
				return "Invalid edit on dates";
			}
			else {
				if (newTask instanceof FloatingTask) {
					newTask = new TimedTask(newTask.getDescription(), startDate, new Time(0,0), endDate, new Time(23, 59), newTask.getPriority(), newTask.getRepeat());
				}
				else if (newTask instanceof TimedTask || newTask instanceof RepeatingTask) {
					newTask.setStartDate(startDate);
					newTask.setEndDate(endDate);
				}
				else if (newTask instanceof DeadlineTask) {
					newTask = new TimedTask(newTask.getDescription(), startDate, new Time(0,0), endDate, newTask.getEndTime(), newTask.getPriority(), newTask.getRepeat());
				}
			}
		}

		// Edit startTime only
		if (startTime != null && endTime == null) {
			// Edited task can update startTime without worrying of endTime being earlier than startTime
			if (newTask.getEndTime() == null) {
				if (newTask instanceof FloatingTask) {
					newTask = new TimedTask(newTask.getDescription(), Date.getCurrentDate(), startTime, null, null, newTask.getPriority(), newTask.getRepeat());
				}
				else if (newTask instanceof TimedTask || newTask instanceof RepeatingTask) {
					newTask.setStartTime(startTime);
				}
				else if (newTask instanceof DeadlineTask) {
					newTask = new TimedTask(newTask.getDescription(), Date.getCurrentDate(), startTime, newTask.getEndDate(), newTask.getEndTime(), newTask.getPriority(), newTask.getRepeat());
				}
			}
			else {
				// Error: Edited task will end up with earlier endTime than startTime
				if (newTask.getEndTime().compareTo(startTime) == -1) {
					addTaskIntoUndone(oldTask);
					return "Invalid edit on starting time";
				}
				newTask.setStartTime(startTime);
			}
		}
		// Edit endTime only
		if (endTime != null && startTime == null) {
			// Edited task can update endTime without worrying of endTime being earlier than startTime
			if (newTask.getStartTime() == null) {
				if (newTask instanceof FloatingTask) {
					newTask = new DeadlineTask(newTask.getDescription(), Date.getCurrentDate(), endTime, newTask.getPriority(), newTask.getRepeat());
				}
				// Edit DeadlineTask
				else {
					newTask.setEndTime(endTime);
				}
			}
			else {
				// Error: Edited task will end up with earlier endTime than startTime

				if (newTask.getStartTime().compareTo(endTime) == 1) {
					addTaskIntoUndone(oldTask);
					return "Invalid edit on ending time";
				}
				// Edit TimedTask or RepeatingTask
				newTask.setEndTime(endTime);
				if (newTask.getEndDate() == null) {
					newTask.setEndDate(newTask.getStartDate());
				}
			}
		}

		// Edit startTime and endTime
		if (startTime != null && endTime != null) {
			// Error: endTime is earlier than startTime
			if (startTime.compareTo(endTime) == 1) {
				addTaskIntoUndone(oldTask);
				return "Invalid edit on both timings";
			}
			else {
				if (newTask instanceof FloatingTask) {
					newTask = new TimedTask(newTask.getDescription(), Date.getCurrentDate(), startTime, Date.getCurrentDate(), endTime, newTask.getPriority(), newTask.getRepeat());
				}
				else if (newTask instanceof TimedTask || newTask instanceof RepeatingTask ) {
					newTask.setStartTime(startTime);
					newTask.setEndTime(endTime);
					if (newTask.getEndDate() == null) {
						newTask.setEndDate(newTask.getStartDate());
					}
				}
				else if (newTask instanceof DeadlineTask) {
					newTask = new TimedTask(newTask.getDescription(), newTask.getEndDate(), startTime, newTask.getEndDate(), endTime, newTask.getPriority(), newTask.getRepeat());
				}
			}
		}

		// Edit priority
		if (priority != '\u0000') {
			newTask.setPriority(priority);
		}

		// Edit repeating
		if (isRepeating) {
			// Add repeat to the task
			if (newTask.getRepeat() == false) {
				if (newTask.getStartDate() != null) {
					newTask = new RepeatingTask(newTask.getDescription(), newTask.getStartDate(), newTask.getStartTime(), newTask.getEndTime(), newTask.getPriority(), true);
				}
				else if (newTask.getEndDate() != null) {
					newTask = new RepeatingTask(newTask.getDescription(), newTask.getEndDate(), newTask.getStartTime(), newTask.getEndTime(), newTask.getPriority(), true);
				}
				else {
					addTaskIntoUndone(oldTask);
					return "Invalid edit to repeated task. Missing date";
				}
			}
			// Remove repeat from the task
			else if (newTask.getRepeat() == true) {
				newTask = remakeTask(newTask);
			}
		}	

		// Add newTask into undone list
		if (listTracker == KEY_SEARCH || listTracker == KEY_FREESLOTS) {
			addTaskIntoUndone(newTask);
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
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
	private void addTaskIntoUndone(Task oldTask) {
		storedTasksByTime.add(oldTask);
		storedTasksByPriority.add(oldTask);
	}
}
