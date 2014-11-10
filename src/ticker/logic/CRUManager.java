//@author A0114535M

/* Team ID: W15-3J
 * Project Title: Ticker
 * Class: CRUManager
 * Description: This class adds, delete and edits task.
 * Assumptions: 
 * This program assumes that:
 * -this class will be called by Logic class.
 * -the Logic class will always be used with classes CRUDManager, 
 * TickKIVManager, UndoRedoManager and SearchManager.
 */

package ticker.logic;

import java.util.Vector;
import java.util.logging.Level;

import ticker.common.Date;
import ticker.common.DeadlineTask;
import ticker.common.FloatingTask;
import ticker.common.RepeatingTask;
import ticker.common.Task;
import ticker.common.Time;
import ticker.common.TimedTask;

public class CRUManager {


	private static final String LOG_UNCATCHED_TASK_IN_EDIT_START_TIME = "Uncatched task in edit startTime";
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
	private static final String STAMP_TICKED = "\\***TICKED***\\";
	private static final String STAMP_KIV = "\\***KIV***\\";
	// String constant
	private static final String EMPTY_STRING = "";
	// Char constants
	private static final char NULL_CHAR = '\u0000';
	private static final char PRIORITY_NORMAL = 'B';
	// Integer constants
	private static final int INIT = 0;
	private static final int SMALLER = -1;
	private static final int BIGGER = 1;
	// Integer constants for offsets
	private static final int OFFSET_INDEX = 1;
	private static final int OFFSET_TICKED = 1;
	private static final int OFFSET_KIV = 2;
	// Integer constants for time
	private static final int START_HOUR = 0;
	private static final int START_MIN = 0;
	private static final int END_HOUR = 23;
	private static final int END_MIN = 59;
	// Feedback messages
	private static final String FEEDBACK_APPEND_IS_UPDATED = " has been updated.";
	private static final String FEEDBACK_APPEND_IS_DELETED = " has been removed.";
	private static final String FEEDBACK_APPEND_IS_ADDED = " has been added.";
	private static final String FEEDBACK_ERROR_DUPLICATE_TASK = "Task already exists.";
	private static final String FEEDBACK_ERROR_REPEATING_TASK_WITHOUT_DATE = "Cannot add repeating tasks without a date.";
	private static final String FEEDBACK_ERROR_INVALID_ENDING_DATE = "Invalid ending date.";
	private static final String FEEDBACK_ERROR_INVALID_DATE_TIME = "Invalid ending date or time.";
	private static final String FEEDBACK_ERROR_INVALID_EDIT_TO_REPEATED_TASK = "Invalid edit to repeated task. Missing date";
	private static final String FEEDBACK_ERROR_INVALID_EDIT_ON_BOTH_TIMINGS = "Invalid edit on both timings";
	private static final String FEEDBACK_ERROR_INVALID_EDIT_ON_ENDING_TIME = "Invalid edit on ending time";
	private static final String FEEDBACK_ERROR_INVALID_EDIT_ON_STARTING_TIME = "Invalid edit on starting time";
	private static final String FEEDBACK_ERROR_INVALID_EDIT_ON_DATES = "Invalid edit on dates";
	private static final String FEEDBACK_ERROR_INVALID_EDIT_ON_ENDING_DATE = "Invalid edit on ending date";
	private static final String FEEDBACK_ERROR_INVALID_EDIT_ON_STARTING_DATE = "Invalid edit on starting date";
	private static final String FEEDBACK_ERROR_CANNOT_EDIT_FROM_TICKED_AND_KIV_LIST = "Cannot edit from ticked and KIV list.";
	private static final String FEEDBACK_ERROR_CANNOT_EDIT_FROM_KIV_LIST = "Cannot edit from KIV list.";
	private static final String FEEDBACK_ERROR_CANNOT_EDIT_FROM_TICKED_LIST = "Cannot edit from ticked list.";
	private static final String FEEDBACK_ERROR_CANNOT_ADD_WITHOUT_DESCRIPTION = "Cannot add without description.";
	private static final String FEEDBACK_ERROR_DELETE_FREESLOT = "Cannot delete freeslot.";
	// Log messages
	private static final String LOG_UNCATCHED_TASK_IN_STARTDATE = "Uncatched task in edit startDate";

	// Instances of other components
	private UndoManager undoMng;
	private Vector<Task> storedTasksByPriority, storedTasksByTime,
	storedTasksByTicked, storedTasksByKiv;

	CRUManager(Vector<Task> storedTasksByTime,
			Vector<Task> storedTasksByPriority,
			Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByKiv) {
		this.storedTasksByPriority = storedTasksByPriority;
		this.storedTasksByTime = storedTasksByTime;
		this.storedTasksByTicked = storedTasksByTicked;
		this.storedTasksByKiv = storedTasksByKiv;

		undoMng = UndoManager.getInstance(storedTasksByPriority,
				storedTasksByTime, storedTasksByTicked, storedTasksByKiv);
	}

	/**
	 * This method adds a task.
	 *
	 * @param description	Task description to be set.
	 * @param isRepeating	Set a task as repeating.
	 * @param startDate		Start date to be set.
	 * @param endDate		End date to be set.
	 * @param startTime		Start time to be set.
	 * @param endTime		 End time to be set.
	 * @param priority		Level of priority to be set.
	 * @return Feedback after adding.
	 * @throws IllegalArgumentException		If event is created wrongly.
	 */
	String add(String description, boolean isRepeating, Date startDate,
			Date endDate, Time startTime, Time endTime, char priority)
					throws IllegalArgumentException {

		if (description == null || description == EMPTY_STRING) {
			return FEEDBACK_ERROR_CANNOT_ADD_WITHOUT_DESCRIPTION;
		}

		Task newTask;

		if (isRepeating) {
			if (startDate != null && endDate != null && startTime != null
					&& endTime != null && (endDate.compareTo(startDate) == SMALLER 
					|| endTime.compareTo(startTime) == SMALLER)) {
				return FEEDBACK_ERROR_INVALID_DATE_TIME;
			}
			if (startDate != null && endDate != null && startTime == null
					&& endTime == null && (endDate.compareTo(startDate) == SMALLER)) {
				return FEEDBACK_ERROR_INVALID_ENDING_DATE;
			}

			if (startDate != null) {
				newTask = new RepeatingTask(description, startDate, startTime,
						endTime, priority, isRepeating);
			} else if (endDate != null) {
				newTask = new RepeatingTask(description, endDate, startTime,
						endTime, priority, isRepeating);
			} else {
				return FEEDBACK_ERROR_REPEATING_TASK_WITHOUT_DATE;
			}
		} else if (startDate == null && startTime == null) {
			if (endDate == null && endTime == null) {
				newTask = new FloatingTask(description, priority, false);
			} else {
				newTask = new DeadlineTask(description, endDate, endTime,
						priority, false);
			}
		} else {
			if (startDate != null && endDate != null && startTime != null
					&& endTime != null && (endDate.compareTo(startDate) == SMALLER 
					|| endTime.compareTo(startTime) == SMALLER)) {
				return FEEDBACK_ERROR_INVALID_DATE_TIME;
			}
			if (startDate != null && endDate != null && startTime == null
					&& endTime == null && (endDate.compareTo(startDate) == SMALLER)) {
				return FEEDBACK_ERROR_INVALID_ENDING_DATE;
			}
			newTask = new TimedTask(description, startDate, startTime, endDate,
					endTime, priority, false);
		}

		if (storedTasksByPriority.contains(newTask)
				|| storedTasksByTicked.contains(newTask)
				|| storedTasksByKiv.contains(newTask)) {
			return FEEDBACK_ERROR_DUPLICATE_TASK;
		}

		addTaskIntoUndone(newTask);

		Event event = new Event(COMMAND_ADD, newTask);
		undoMng.add(event);

		return description + FEEDBACK_APPEND_IS_ADDED;
	}

	/**
	 * This method deletes a task from a tasklist.
	 *
	 * @param index				Index of the specified task displayed in UI.
	 * @param listTracker		List key of the current task list being displayed.
	 * @param current			Current task list being displayed.
	 * @param currentListName	Name of current list being displayed.
	 * @return Feedback after a task is deleted.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds the boundaries of task list.
	 * @throws IllegalArgumentException			If event is created wrongly.
	 */
	String delete(int displayedIndex, int listTracker, Vector<Task> current,
			String currentListName) throws ArrayIndexOutOfBoundsException,
			IllegalArgumentException {

		Task deleted;
		int actualIndex = getActualIndex(displayedIndex);

		if (listTracker == KEY_SORTED_TIME) {
			deleted = deleteFromTimedList(current, currentListName, actualIndex);
		} else if (listTracker == KEY_SORTED_PRIORITY) {
			deleted = deleteFromPriorityList(current, currentListName,
					actualIndex);
		} else if (listTracker == KEY_SEARCH) {
			deleted = deleteFromSearchList(current, actualIndex);
		} else if (listTracker == KEY_FREESLOTS) {
			deleted = current.get(actualIndex);
			if (deleted.getDescription() == FREESLOT_STAMP) {
				return FEEDBACK_ERROR_DELETE_FREESLOT;
			} else {
				deleteFromFreeslots(current, deleted, actualIndex);
			}
		} else {
			deleted = deleteFromTickedOrKiv(current, currentListName,
					actualIndex);
		}

		return deleted.getDescription() + FEEDBACK_APPEND_IS_DELETED;
	}

	/**
	 * This method deletes a task from ticked or kiv list.
	 * 
	 * @param current			Current tasklist.
	 * @param currentListName	Name of current tasklist.
	 * @param actualIndex		Index of task to be deleted.
	 * @return Deleted task.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds the boundaries of task list.
	 * @throws IllegalArgumentException			If event is created wrongly.
	 */
	private Task deleteFromTickedOrKiv(Vector<Task> current,
			String currentListName, int actualIndex)
					throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		Task deleted = current.remove(actualIndex);

		// Throws IllegalArgumentException
		Event event = new Event(COMMAND_DELETE, deleted, currentListName,
				actualIndex);
		undoMng.add(event);
		return deleted;
	}

	/**
	 * This method deletes a task from freeslots list.
	 * 
	 * @param current			Current tasklist.
	 * @param currentListName	Name of current tasklist.
	 * @param actualIndex		Index of task to be deleted.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds the boundaries of task list.
	 * @throws IllegalArgumentException			If event is created wrongly.
	 */
	private void deleteFromFreeslots(Vector<Task> current, Task deleted,
			int actualIndex) throws ArrayIndexOutOfBoundsException,
			IllegalArgumentException {
		current.remove(actualIndex);
		int index = storedTasksByTime.indexOf(deleted);
		removeTaskFromUndoneLists(deleted);
		// Throws IllegalArgumentException
		Event event = new Event(COMMAND_DELETE, deleted, TASKS_TIME, index);
		undoMng.add(event);
	}

	/**
	 * This method deletes a task from the search list.
	 * 
	 * @param current		Current tasklist.
	 * @param actualIndex	Index of task to be deleted.
	 * @return Deleted task.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds the boundaries of task list.
	 * @throws IllegalArgumentException			If event is created wrongly.
	 */
	private Task deleteFromSearchList(Vector<Task> current, int actualIndex)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		Task deleted;
		Task tickedPartition = new Task(STAMP_TICKED, null, null, null, null,
				PRIORITY_NORMAL, false);
		Task kivPartition = new Task(STAMP_KIV, null, null, null, null,
				PRIORITY_NORMAL, false);
		int tickedPartitionIndex = current.indexOf(tickedPartition);
		int kivPartitionIndex = current.indexOf(kivPartition);

		int displayedKivPartitionIndex = getDisplayedKivPartitionIndex(kivPartitionIndex);

		if (actualIndex < tickedPartitionIndex) {
			deleted = current.remove(actualIndex);
		} else if (actualIndex >= tickedPartitionIndex
				&& actualIndex <= displayedKivPartitionIndex) {
			deleted = current.remove(actualIndex + OFFSET_TICKED);
		} else {
			deleted = current.remove(actualIndex + OFFSET_KIV);
		}

		if (storedTasksByTime.contains(deleted)
				|| storedTasksByPriority.contains(deleted)) {
			deleteUndoneFromSearchList(deleted);
		} else if (storedTasksByTicked.contains(deleted)) {
			deleteTickedFromSearchList(deleted);
		} else if (storedTasksByKiv.contains(deleted)) {
			deleteKivFromSearchList(deleted);
		}
		return deleted;
	}

	/**
	 * This method deletes a kiv task in search list.
	 * 
	 * @param deleted	Kiv task to be deleted.
	 * @throws IllegalArgumentException		If event is created wrongly.
	 */
	private void deleteKivFromSearchList(Task deleted)
			throws IllegalArgumentException {
		int indexCounter = getIndexInOriginalList(deleted, storedTasksByKiv);
		storedTasksByKiv.remove(deleted);
		// Throws IllegalArgumentException
		Event event = new Event(COMMAND_DELETE, deleted, TASKS_KIV,
				indexCounter);
		undoMng.add(event);
	}

	/**
	 * This method deletes a ticked task in search list.
	 * 
	 * @param deleted		Ticked task to be deleted.
	 * @throws IllegalArgumentException		If event is created wrongly.
	 */
	private void deleteTickedFromSearchList(Task deleted)
			throws IllegalArgumentException {
		int indexCounter = getIndexInOriginalList(deleted, storedTasksByTicked);
		storedTasksByTicked.remove(deleted);
		// Throws IllegalArgumentException
		Event event = new Event(COMMAND_DELETE, deleted, TASKS_TICKED,
				indexCounter);
		undoMng.add(event);
	}

	/**
	 * This method deletes an undone task in search list.
	 * 
	 * @param deleted	Undone task to be deleted.
	 * @throws IllegalArgumentException		If event is created wrongly.
	 */
	private void deleteUndoneFromSearchList(Task deleted)
			throws IllegalArgumentException {
		int indexCounter = getIndexInOriginalList(deleted, storedTasksByTime);
		removeTaskFromUndoneLists(deleted);

		// Throws IllegalArgumentException
		Event event = new Event(COMMAND_DELETE, deleted, TASKS_TIME,
				indexCounter);
		undoMng.add(event);
	}

	/**
	 * This method gets the index of the original tasklist.
	 * 
	 * @param deleted	Task to be deleted.
	 * @return Index of task to be deleted in the original tasklist.
	 */
	private int getIndexInOriginalList(Task deleted, Vector<Task> originalList) {
		int indexCounter;
		indexCounter = INIT;
		for (Task task : originalList) {
			if (task.equals(deleted)) {
				break;
			}
			indexCounter++;
		}
		return indexCounter;
	}

	/**
	 * This method deletes a task from priority list.
	 * 
	 * @param current			Current tasklist.
	 * @param currentListName	Name of current tasklist.
	 * @param actualIndex		Index of task to be deleted.
	 * @return Deleted task.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds the boundaries of task list.
	 * @throws IllegalArgumentException			If event is created wrongly.
	 */
	private Task deleteFromPriorityList(Vector<Task> current,
			String currentListName, int actualIndex)
					throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		;
		Task deleted = current.remove(actualIndex);
		storedTasksByTime.remove(deleted);
		// Throws IllegalArgumentException
		Event event = new Event(COMMAND_DELETE, deleted, currentListName,
				actualIndex);
		undoMng.add(event);
		return deleted;
	}

	/**
	 * This method deletes a task timed list.
	 * 
	 * @param current			Current tasklist.
	 * @param currentListName	Name of current tasklist.
	 * @param actualIndex		Index of task to be deleted.
	 * @return Deleted task.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds the boundaries of task list.
	 * @throws IllegalArgumentException			If event is created wrongly.
	 */
	private Task deleteFromTimedList(Vector<Task> current,
			String currentListName, int actualIndex)
					throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		Task deleted = current.remove(actualIndex);
		storedTasksByPriority.remove(deleted);
		// Throws IllegalArgumentException
		Event event = new Event(COMMAND_DELETE, deleted, currentListName,
				actualIndex);
		undoMng.add(event);
		return deleted;
	}

	/**
	 * This method deletes a task from undone list.
	 * 
	 * @param current			Current tasklist.
	 * @param currentListName	Name of current tasklist.
	 * @param actualIndex		Index of task to be deleted.
	 * @return Deleted task.
	 */
	private void removeTaskFromUndoneLists(Task task) {
		storedTasksByTime.remove(task);
		storedTasksByPriority.remove(task);
	}

	/**
	 * This method edits a task.
	 *
	 * @param description	Task description to be set.
	 * @param isRepeating	Set a task as repeating.
	 * @param startDate		Start date to be set.
	 * @param endDate		End date to be set.
	 * @param startTime		Start time to be set.
	 * @param endTime		End time to be set.
	 * @param priority		Level of priority to be set.
	 * @param listTracker	List key of the current task list being displayed.
	 * @param current		Current task list being displayed.
	 * @return Feedback after editing.
	 * @throws ArrayIndexOutOfBoundsException	If index exceeds boundaries of current list.
	 */
	String edit(int displayedIndex, String description, boolean isRepeating,
			Date startDate, Date endDate, Time startTime, Time endTime,
			char priority, int listTracker, Vector<Task> current)
					throws ArrayIndexOutOfBoundsException {
		Task oldTask;
		Task newTask;
		int actualIndex = getActualIndex(displayedIndex);

		if (listTracker == KEY_TICKED) {
			return FEEDBACK_ERROR_CANNOT_EDIT_FROM_TICKED_LIST;
		} else if (listTracker == KEY_KIV) {
			return FEEDBACK_ERROR_CANNOT_EDIT_FROM_KIV_LIST;
		}

		else if (listTracker == KEY_SEARCH) {
			Task tickedPartition = new Task(STAMP_TICKED, null, null, null,
					null, PRIORITY_NORMAL, false);
			Task kivPartition = new Task(STAMP_KIV, null, null, null, null,
					PRIORITY_NORMAL, false);
			int tickedPartitionIndex = current.indexOf(tickedPartition);
			int kivPartitionIndex = current.indexOf(kivPartition);

			int displayedKivPartitionIndex = getDisplayedKivPartitionIndex(kivPartitionIndex);

			if (actualIndex < tickedPartitionIndex) {
				oldTask = current.get(actualIndex);
			} else if (actualIndex >= tickedPartitionIndex
					&& actualIndex <= displayedKivPartitionIndex) {
				oldTask = current.get(actualIndex + OFFSET_TICKED);
			} else {
				oldTask = current.get(actualIndex + OFFSET_KIV);
			}

			if (storedTasksByTime.contains(oldTask)
					|| storedTasksByPriority.contains(oldTask)) {
				removeTaskFromUndoneLists(oldTask);
			} else if (storedTasksByTicked.contains(oldTask)
					|| storedTasksByKiv.contains(oldTask)) {
				return FEEDBACK_ERROR_CANNOT_EDIT_FROM_TICKED_AND_KIV_LIST;
			}
		} else if (listTracker == KEY_FREESLOTS) {
			oldTask = current.remove(actualIndex);
			removeTaskFromUndoneLists(oldTask);
		} else {
			oldTask = current.remove(actualIndex);
			removeTaskFromUndoneLists(oldTask);
		}

		newTask = oldTask.copy();

		editDescription(description, newTask);

		editPriority(priority, newTask);

		// Edit startDate only
		if (startDate != null && endDate == null) {
			// Edited task can update startDate without worrying of endDate
			// being earlier than startDate
			if (newTask.getEndDate() == null) {
				if (newTask instanceof FloatingTask) {
					newTask = new TimedTask(newTask.getDescription(),
							startDate, new Time(START_HOUR, START_MIN), null,
							null, newTask.getPriority(), newTask.getRepeat());
					// Editing TimedTask or RepeatingTask
				} else if (newTask instanceof TimedTask) {
					newTask.setStartDate(startDate);
				} else if (newTask instanceof RepeatingTask) {
					newTask = (RepeatingTask) newTask;
					newTask.setStartDate(startDate);
				} else {
					Logic.logger.log(Level.WARNING, LOG_UNCATCHED_TASK_IN_STARTDATE);
				}
				// Error: Edited task will end up with earlier endDate than
				// startDate
			} else {
				if (newTask.getEndDate().compareTo(startDate) == SMALLER) {
					addTaskIntoUndone(oldTask);
					return FEEDBACK_ERROR_INVALID_EDIT_ON_STARTING_DATE;
					// Editing TimedTask or RepeatingTask
				} else if (newTask.getStartDate() != null) {
					newTask.setStartDate(startDate);
				} else if (newTask instanceof DeadlineTask){				
					newTask = new TimedTask(newTask.getDescription(), startDate,
							new Time(START_HOUR, START_MIN), newTask.getEndDate(),
							newTask.getEndTime(), newTask.getPriority(),
							newTask.getRepeat());
				}
			}
		}

		// Edit endDate only
		if (endDate != null && startDate == null) {
			// Edited task can update endDate without worrying of endDate being
			// earlier than startDate
			if (newTask.getStartDate() == null) {
				if (newTask instanceof FloatingTask) {
					newTask = new DeadlineTask(newTask.getDescription(),
							endDate, new Time(END_HOUR, END_MIN),
							newTask.getPriority(), newTask.getRepeat());
					// Editing DeadlineTask
				} else {
					newTask.setEndDate(endDate);
				}

			} else {
				// Error: Edited task will end up with earlier endDate than startDate
				if (newTask.getStartDate().compareTo(endDate) == BIGGER) {
					addTaskIntoUndone(oldTask);
					return FEEDBACK_ERROR_INVALID_EDIT_ON_ENDING_DATE;
				}
				// Editing TimedTask and RepeatingTask
				newTask.setEndDate(endDate);
				if (newTask.getEndTime() == null) {
					newTask.setEndTime(new Time(END_HOUR, END_MIN));
				}
			}
		}

		// Edit startDate and endDate
		if (startDate != null && endDate != null) {
			// Error: endDate is earlier than startDate
			if (startDate.compareTo(endDate) == BIGGER) {
				addTaskIntoUndone(oldTask);
				return FEEDBACK_ERROR_INVALID_EDIT_ON_DATES;
			} else {
				if (newTask instanceof FloatingTask) {
					newTask = new TimedTask(newTask.getDescription(),
							startDate, new Time(START_HOUR, START_MIN),
							endDate, new Time(END_HOUR, END_MIN),
							newTask.getPriority(), newTask.getRepeat());
				} else if (newTask instanceof TimedTask) {
					setStartDateAndEndDate(startDate, endDate, newTask);
				} else if (newTask instanceof RepeatingTask) {
					newTask = (RepeatingTask) newTask;
					setStartDateAndEndDate(startDate, endDate, newTask);
				} else if (newTask instanceof DeadlineTask) {
					newTask = new TimedTask(newTask.getDescription(),
							startDate, new Time(START_HOUR, START_MIN),
							endDate, newTask.getEndTime(),
							newTask.getPriority(), newTask.getRepeat());
				}
			}
		}

		// Edit startTime only
		if (startTime != null && endTime == null) {
			// Edited task can update startTime without worrying of endTime
			// being earlier than startTime
			if (newTask.getEndTime() == null) {
				if (newTask instanceof FloatingTask) {
					newTask = new TimedTask(newTask.getDescription(),
							Date.getCurrentDate(), startTime, null, null,
							newTask.getPriority(), newTask.getRepeat());
				} else if (newTask instanceof TimedTask
						|| newTask instanceof RepeatingTask) {
					newTask.setStartTime(startTime);
				} else {
					Logic.logger.log(Level.WARNING, LOG_UNCATCHED_TASK_IN_EDIT_START_TIME);
				}

			} else {
				// Error: Edited task will end up with earlier endTime than
				// startTime
				if (newTask.getEndTime().compareTo(startTime) == SMALLER) {
					addTaskIntoUndone(oldTask);
					return FEEDBACK_ERROR_INVALID_EDIT_ON_STARTING_TIME;
				} else if (newTask.getStartTime() != null) {
					newTask.setStartTime(startTime);
				} else if (newTask instanceof DeadlineTask) {
					newTask = new TimedTask(newTask.getDescription(),
							Date.getCurrentDate(), startTime,
							newTask.getEndDate(), newTask.getEndTime(),
							newTask.getPriority(), newTask.getRepeat());
				}
			}
		}

		// Edit endTime only
		if (endTime != null && startTime == null) {
			// Edited task can update endTime without worrying of endTime being
			// earlier than startTime
			if (newTask.getStartTime() == null) {
				if (newTask instanceof FloatingTask) {
					newTask = new DeadlineTask(newTask.getDescription(),
							Date.getCurrentDate(), endTime,
							newTask.getPriority(), newTask.getRepeat());
					// Edit DeadlineTask
				} else {
					newTask.setEndTime(endTime);
				}
			} else {
				// Error: Edited task will end up with earlier endTime than startTime
				if (newTask.getStartTime().compareTo(endTime) == 1) {
					addTaskIntoUndone(oldTask);
					return FEEDBACK_ERROR_INVALID_EDIT_ON_ENDING_TIME;
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
			if (startTime.compareTo(endTime) == BIGGER) {
				addTaskIntoUndone(oldTask);
				return FEEDBACK_ERROR_INVALID_EDIT_ON_BOTH_TIMINGS;
			} else {
				if (newTask instanceof FloatingTask) {
					newTask = new TimedTask(newTask.getDescription(),
							Date.getCurrentDate(), startTime,
							Date.getCurrentDate(), endTime,
							newTask.getPriority(), newTask.getRepeat());
				} else if (newTask instanceof TimedTask
						|| newTask instanceof RepeatingTask) {
					newTask.setStartTime(startTime);
					newTask.setEndTime(endTime);
					if (newTask.getEndDate() == null) {
						newTask.setEndDate(newTask.getStartDate());
					}
				} else if (newTask instanceof DeadlineTask) {
					newTask = new TimedTask(newTask.getDescription(),
							newTask.getEndDate(), startTime,
							newTask.getEndDate(), endTime,
							newTask.getPriority(), newTask.getRepeat());
				}
			}
		}

		// Edit repeating
		if (isRepeating) {
			// Add repeat to the task
			if (newTask.getRepeat() == false) {
				if (newTask.getStartDate() != null) {
					newTask = new RepeatingTask(newTask.getDescription(),
							newTask.getStartDate(), newTask.getStartTime(),
							newTask.getEndTime(), newTask.getPriority(), true);
				} else if (newTask.getEndDate() != null) {
					newTask = new RepeatingTask(newTask.getDescription(),
							newTask.getEndDate(), newTask.getStartTime(),
							newTask.getEndTime(), newTask.getPriority(), true);
				} else {
					addTaskIntoUndone(oldTask);
					return FEEDBACK_ERROR_INVALID_EDIT_TO_REPEATED_TASK;
				}
				// Remove repeat from the task
			} else if (newTask.getRepeat() == true) {
				newTask = remakeTask(newTask);
			}
		}

		addEditedTask(listTracker, current, oldTask, newTask, actualIndex);

		return oldTask.getDescription() + FEEDBACK_APPEND_IS_UPDATED;
	}

	/**
	 * This method sets the startDate and endDate of a task.
	 * 
	 * @param startDate		Date to be set as start date for a task.
	 * @param endDate		Date to be set as end date for a task.
	 * @param newTask		Task to be set
	 */
	private void setStartDateAndEndDate(Date startDate, Date endDate, Task task) {
		task.setStartDate(startDate);
		task.setEndDate(endDate);
	}

	/**
	 * This method adds back the edited task.
	 * 
	 * @param listTracker	Key of current tasklist.
	 * @param current		Current tasklist.
	 * @param oldTask		Task before editing.
	 * @param newTask		Task after editing.
	 * @param actualIndex	Actual index in tasklist
	 * @throws IllegalArgumentException		If event is created wrongly.
	 */
	private void addEditedTask(int listTracker, Vector<Task> current,
			Task oldTask, Task newTask, int actualIndex)
					throws IllegalArgumentException {
		if (listTracker != KEY_TICKED || listTracker != KEY_KIV) {
			addTaskIntoUndone(newTask);
		} else {
			current.add(actualIndex, newTask);
		}

		// Throws IllegalArgumentException
		Event event = new Event(COMMAND_EDIT, oldTask, newTask);
		undoMng.add(event);
	}

	/**
	 * This method edits the priority of the task.
	 * 
	 * @param priority	New priority to be set.
	 * @param newTask	Task with the edits.
	 */
	private void editPriority(char priority, Task newTask) {
		if (priority != NULL_CHAR) {
			newTask.setPriority(priority);
		}
	}

	/**
	 * This method edits the description of the task.
	 * 
	 * @param description	New description to be set.
	 * @param newTask		Task with the edits.
	 */
	private void editDescription(String description, Task newTask) {
		if (description != null && !description.equals(EMPTY_STRING)) {
			newTask.setDescription(description);
		}
	}

	/**
	 * This method resets a task into a TimedTask.
	 *
	 * @param task		Task to be remade.
	 * @return TimedTask
	 * @throws IllegalArgumentException		If description is empty.
	 */
	private TimedTask remakeTask(Task task) throws IllegalArgumentException {
		String description = task.getDescription();
		Date startDate = task.getStartDate();
		Date endDate = task.getEndDate();
		Time startTime = task.getStartTime();
		Time endTime = task.getEndTime();
		char priority = task.getPriority();

		if (description == null || description.equals(EMPTY_STRING)) {
			throw new IllegalArgumentException();
		}

		TimedTask newTask;

		newTask = new TimedTask(description, startDate, startTime, endDate,
				endTime, priority, false);

		return newTask;
	}

	/**
	 * This method adds task into undone lists (time list and priority list).
	 *
	 * @param Task	Task to be added.
	 */
	private void addTaskIntoUndone(Task task) {
		storedTasksByTime.add(task);
		storedTasksByPriority.add(task);
	}

	/**
	 * This method calculates the actual index of the task displayed in UI.
	 *
	 * @param index		Index of the specified task displayed in UI.
	 * @return Actual index of tasklist.
	 */
	private int getActualIndex(int index) {
		return index - OFFSET_INDEX;
	}

	/**
	 * This method calculates the actual index of the task displayed in UI.
	 *
	 * @param index		Index of the specified task displayed in UI.
	 * @return Actual index of tasklist.
	 */
	private int getDisplayedKivPartitionIndex(int kivPartitionIndex) {
		return kivPartitionIndex - OFFSET_KIV;
	}
}
