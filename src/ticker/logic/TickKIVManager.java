//@author A0114535M

/* Team ID: W15-3J
 * Project Title: Ticker
 * Class: TickKIVManager
 * Description: This class performs tick, untick, kiv and unkiv commands.
 * Assumptions: 
 * This program assumes that:
 * -this class will be called by Logic class.
 * -the Logic class will always be used with classes CRUDManager, TickKIVManager, UndoRedoManager and SearchManager.
 */

package ticker.logic;

import java.util.Vector;

import ticker.common.Task;

public class TickKIVManager {

	// CONSTANTS
	// String constants for command types
	private static final String COMMAND_UNTICK = "untick";
	private static final String COMMAND_TICK = "tick";
	private static final String COMMAND_UNKIV = "unkiv";
	private static final String COMMAND_KIV = "kiv";
	// Integer key constants for lists used by listTracker
	private static final int KEY_TICKED = 3;
	private static final int KEY_KIV = 4;
	private static final int KEY_SEARCH = 5;
	private static final int KEY_FREESLOTS = 6;
	// Integer constants for offsets
	private static final int TOP_OF_THE_LIST = 0;
	private static final int OFFSET_INDEX = 1;
	private static final int OFFSET_TICKED = 1;
	private static final int OFFSET_KIV = 2;
	// String constants for type of lists used by UndoManager
	private static final String LIST_TIME = "time";
	private static final String LIST_TICKED = "ticked";
	private static final String LIST_KIV = "kiv";
	// String constant for stamps
	private static final String FREESLOT_STAMP = "\\***FREE***\\";
	private static final String KIV_LIST_STAMP = "\\***KIV***\\";
	private static final String TICKED_LIST_STAMP = "\\***TICKED***\\";
	// Character constants for priority level
	private static final char PRIORITY_NORMAL = 'B';
	// Feedback messages
	private static final String FEEDBACK_APPEND_IS_KIV = " will be kept in view.";
	private static final String FEEDBACK_APPEND_IS_UNDONE = " is back to undone.";
	private static final String FEEDBACK_APPEND_IS_DONE = " is done!";
	private static final String FEEDBACK_ERROR_CANNOT_UNKIV_FROM_TICKED_LIST = "Cannot unkiv a task from ticked list.";
	private static final String FEEDBACK_ERROR_CANNOT_UNKIV_FROM_UNDONE_LIST = "Cannot unkiv a task from undone list.";
	private static final String FEEDBACK_ERROR_MISUSED_UNKIV = "Can only unkiv in kiv list and search list.";
	private static final String FEEDBACK_ERROR_CANNOT_KIV_FREESLOT = "Cannot kiv freeslot.";
	private static final String FEEDBACK_ERROR_CANNOT_KIV_TICKED_TASK = "Cannot KIV a task from ticked. Please untick task first.";
	private static final String FEEDBACK_ERROR_ALREADY_KIVED = "Task is already KIVed.";
	private static final String FEEDBACK_ERROR_MISUSED_KIV = "Can only kiv in undone list, search list and search for freeslots list.";
	private static final String FEEDBACK_ERROR_CANNOT_UNTICK_FROM_KIV = "Cannot untick a task from KIV.";
	private static final String FEEDBACK_ERROR_CANNOT_UNTICK_FROM_UNDONE = "Cannot untick a task from undone list.";
	private static final String FEEDBACK_ERROR_MISUSED_UNTICK = "Can only untick from ticked list and search list.";
	private static final String FEEDBACK_ERROR_CANNOT_TICK_FREESLOT = "Cannot tick freeslot.";
	private static final String FEEDBACK_ERROR_CANNOT_TICK_KIV_TASK = "Cannot tick a task from KIV. Please unKIV task first.";
	private static final String FEEDBACK_ERROR_ALREADY_TICKED = "Task is already ticked.";
	private static final String FEEDBACK_ERROR_MISUSED_TICK = "Can only tick from undone list, search list and search freeslots list.";

	// ATTRIBUTES
	// Instances of other components
	private UndoManager undoMng;
	private Vector<Task> storedTasksByPriority, storedTasksByTime, storedTasksByTicked, storedTasksByKIV;

	TickKIVManager(Vector<Task> storedTasksByPriority, Vector<Task> storedTasksByTime, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByKIV) {
		this.storedTasksByPriority = storedTasksByPriority;
		this.storedTasksByTime = storedTasksByTime;
		this.storedTasksByTicked = storedTasksByTicked;
		this.storedTasksByKIV = storedTasksByKIV;

		undoMng = UndoManager.getInstance(storedTasksByPriority, storedTasksByTime, storedTasksByTicked, storedTasksByKIV);
	}

	/**
	 * This method marks the specified task as ticked.
	 *
	 * @param index			 Index of the specified task displayed in UI.
	 * @param listTracker    List key of the current task list being displayed.
	 * @param current		 Current task list being displayed.
	 * @return     Feedback after a task is ticked.
	 * @throws ArrayIndexOutOfBounds  If index exceeds the boundaries of task list.
	 */
	protected String tick(int displayedIndex, int listTracker, Vector<Task> current) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		Task ticked;
		int actualIndex = getActualIndex(displayedIndex);

		if (listTracker == KEY_KIV || listTracker == KEY_TICKED) {
			return FEEDBACK_ERROR_MISUSED_TICK;
		} else if (listTracker == KEY_SEARCH) {
			Task tickedPartition = new Task(TICKED_LIST_STAMP, null, null, null, null, PRIORITY_NORMAL, false);
			Task kivPartition = new Task(KIV_LIST_STAMP, null, null, null, null, PRIORITY_NORMAL, false);
			int tickedPartitionIndex = current.indexOf(tickedPartition);
			int kivPartitionIndex = current.indexOf(kivPartition);

			int displayedKivPartitionIndex = getDisplayedKivPartitionIndex(kivPartitionIndex);

			if (actualIndex < tickedPartitionIndex) {
				ticked = current.remove(actualIndex);
			} else if (actualIndex >= tickedPartitionIndex && actualIndex <= displayedKivPartitionIndex) {
				ticked = current.remove(actualIndex + OFFSET_TICKED);
			} else {
				// task occurs after kivPartitionIndex
				ticked = current.remove(actualIndex + OFFSET_KIV);
			}

			if (storedTasksByTime.contains(ticked) || storedTasksByPriority.contains(ticked)) {
				removeTaskFromUndoneLists(ticked);
			} else if (storedTasksByTicked.contains(ticked)) {
				return FEEDBACK_ERROR_ALREADY_TICKED;
			} else if (storedTasksByKIV.contains(ticked)) {
				return FEEDBACK_ERROR_CANNOT_TICK_KIV_TASK;
			}

		} else if (listTracker == KEY_FREESLOTS) {
			ticked = current.get(actualIndex);

			if (ticked.getDescription() == FREESLOT_STAMP) {
				return FEEDBACK_ERROR_CANNOT_TICK_FREESLOT;
			} else {
				current.remove(actualIndex);
			}

		} else {
			ticked = current.remove(actualIndex);
		}

		moveToTicked(ticked);
		
		// Throws IllegalArgumentException
		Event event = new Event(COMMAND_TICK, ticked, LIST_TIME, LIST_TICKED);
		undoMng.add(event);

		return ticked.toString() + FEEDBACK_APPEND_IS_DONE;
	}

	/**
	 * This method marks the specified task as unticked.
	 *
	 * @param index			 Index of the specified task displayed in UI.
	 * @param listTracker    List key of the current task list being displayed.
	 * @param current		 Current task list being displayed.
	 * @return     Feedback after a task is unticked.
	 * @throws ArrayIndexOutOfBounds  If index exceeds the boundaries of task list.
	 */
	protected String untick(int displayedIndex, int listTracker, Vector<Task> current) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		Task unticked;
		int actualIndex = getActualIndex(displayedIndex);

		if (listTracker != KEY_TICKED && listTracker != KEY_SEARCH) {
			return FEEDBACK_ERROR_MISUSED_UNTICK;
		}
		if (listTracker == KEY_SEARCH) {
			Task tickedPartition = new Task(TICKED_LIST_STAMP, null, null, null, null, PRIORITY_NORMAL, false);
			Task kivPartition = new Task(KIV_LIST_STAMP, null, null, null, null, PRIORITY_NORMAL, false);
			int tickedPartitionIndex = current.indexOf(tickedPartition);
			int kivPartitionIndex = current.indexOf(kivPartition);

			int displayedKivPartitionIndex = getDisplayedKivPartitionIndex(kivPartitionIndex);

			if (actualIndex < tickedPartitionIndex) {
				unticked = current.remove(actualIndex);
			} else if (actualIndex >= tickedPartitionIndex && actualIndex <= displayedKivPartitionIndex) {
				unticked = current.remove(actualIndex + OFFSET_TICKED);
			} else {
				// task occurs after kivPartitionIndex
				unticked = current.remove(actualIndex + OFFSET_KIV);
			}

			if (storedTasksByTime.contains(unticked) || storedTasksByPriority.contains(unticked)) {
				return FEEDBACK_ERROR_CANNOT_UNTICK_FROM_UNDONE;
			} else if (storedTasksByTicked.contains(unticked)) {
				storedTasksByTicked.remove(unticked);
			} else if (storedTasksByKIV.contains(unticked)) {
				return FEEDBACK_ERROR_CANNOT_UNTICK_FROM_KIV;
			}

		} else {
			unticked = current.remove(actualIndex);
		}

		addedToUndoneLists(unticked);
		
		// Throws IllegalArgumentException
		Event event = new Event(COMMAND_UNTICK, unticked, LIST_TIME, LIST_TICKED);
		undoMng.add(event);

		return unticked.toString() + FEEDBACK_APPEND_IS_UNDONE;
	}

	/**
	 * This method marks the specified task as kiv-ed.
	 *
	 * @param index			 Index of the specified task displayed in UI.
	 * @param listTracker    List key of the current task list being displayed.
	 * @param current		 Current task list being displayed.
	 * @return     Feedback after a task is kiv-ed.
	 * @throws ArrayIndexOutOfBounds  If index exceeds the boundaries of task list.
	 */
	protected String kiv(int displayedIndex, int listTracker, Vector<Task> current, String currentListName) 
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException {

		Task kiv;
		int actualIndex = getActualIndex(displayedIndex);

		if (listTracker == KEY_TICKED || listTracker == KEY_KIV) {
			return FEEDBACK_ERROR_MISUSED_KIV;
		} else if (listTracker == KEY_SEARCH) {
			Task tickedPartition = new Task(TICKED_LIST_STAMP, null, null, null, null, PRIORITY_NORMAL, false);
			Task kivPartition = new Task(KIV_LIST_STAMP, null, null, null, null, PRIORITY_NORMAL, false);
			int tickedPartitionIndex = current.indexOf(tickedPartition);
			int kivPartitionIndex = current.indexOf(kivPartition);

			int displayedKivPartitionIndex = getDisplayedKivPartitionIndex(kivPartitionIndex);

			if (actualIndex < tickedPartitionIndex) {
				kiv = current.remove(actualIndex);
			} else if (actualIndex >= tickedPartitionIndex && actualIndex <= displayedKivPartitionIndex) {
				kiv = current.remove(actualIndex + OFFSET_TICKED);
			} else {
				kiv = current.remove(actualIndex + OFFSET_KIV);
			}

			if (storedTasksByTime.contains(kiv) || storedTasksByPriority.contains(kiv)) {
				removeTaskFromUndoneLists(kiv);
			} else if (storedTasksByKIV.contains(kiv)) {
				return FEEDBACK_ERROR_ALREADY_KIVED;
			} else if (storedTasksByTicked.contains(kiv)) {
				return FEEDBACK_ERROR_CANNOT_KIV_TICKED_TASK;
			}

		} else if (listTracker == KEY_FREESLOTS) {
			kiv = current.get(actualIndex);
			if (kiv.getDescription() == FREESLOT_STAMP) {
				return FEEDBACK_ERROR_CANNOT_KIV_FREESLOT;
			} else {
				current.remove(actualIndex);
			}
		} else {
			kiv = current.remove(actualIndex);
		}

		moveToKiv(kiv);
		
		// Throws IllegalArgumentException
		Event event = new Event(COMMAND_KIV, kiv, LIST_TIME, LIST_KIV);
		undoMng.add(event);

		return kiv.toString() + FEEDBACK_APPEND_IS_KIV;

	}

	/**
	 * This method marks the specified task as unkiv-ed.
	 *
	 * @param index			 Index of the specified task displayed in UI.
	 * @param listTracker    List key of the current task list being displayed.
	 * @param current		 Current task list being displayed.
	 * @return     Feedback after a task is unkiv-ed.
	 * @throws ArrayIndexOutOfBounds  If index exceeds the boundaries of task list.
	 */
	protected String unkiv(int displayedIndex, int listTracker, Vector<Task> current) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		Task unkiv;
		int actualIndex = getActualIndex(displayedIndex);

		if (listTracker != KEY_KIV && listTracker != KEY_SEARCH) {
			return FEEDBACK_ERROR_MISUSED_UNKIV;
		}

		if (listTracker == KEY_SEARCH) {
			Task tickedPartition = new Task(TICKED_LIST_STAMP, null, null, null, null, PRIORITY_NORMAL, false);
			Task kivPartition = new Task(KIV_LIST_STAMP, null, null, null, null, PRIORITY_NORMAL, false);
			int tickedPartitionIndex = current.indexOf(tickedPartition);
			int kivPartitionIndex = current.indexOf(kivPartition);

			int displayedKivPartitionIndex = getDisplayedKivPartitionIndex(kivPartitionIndex);

			if (actualIndex < tickedPartitionIndex) {
				unkiv = current.remove(actualIndex);
			} else if (actualIndex >= tickedPartitionIndex && actualIndex <= displayedKivPartitionIndex) {
				unkiv = current.remove(actualIndex + OFFSET_TICKED);
			} else {
				unkiv = current.remove(actualIndex + OFFSET_KIV);
			}

			if (storedTasksByTime.contains(unkiv) || storedTasksByPriority.contains(unkiv)) {
				return FEEDBACK_ERROR_CANNOT_UNKIV_FROM_UNDONE_LIST;
			}
			else if (storedTasksByTicked.contains(unkiv)) {
				return FEEDBACK_ERROR_CANNOT_UNKIV_FROM_TICKED_LIST;
			}
			else if (storedTasksByKIV.contains(unkiv)) {
				storedTasksByKIV.remove(unkiv);
			}

		} else {
			unkiv = current.remove(actualIndex);
		}

		addedToUndoneLists(unkiv);
		
		// Throws IllegalArgumentException
		Event event = new Event(COMMAND_UNKIV, unkiv, LIST_TIME, LIST_KIV);
		undoMng.add(event);

		return unkiv.toString() + FEEDBACK_APPEND_IS_UNDONE;

	}

	/**
	 * This method calculates the actual index of the task displayed in UI.
	 *
	 * @param index			 Index of the specified task displayed in UI.
	 * @return     Actual index of tasklist.
	 */
	private int getActualIndex(int index) {
		return index - OFFSET_INDEX;
	}

	/**
	 * This method calculates the actual index of the task displayed in UI.
	 *
	 * @param index			 Index of the specified task displayed in UI.
	 * @return     Actual index of tasklist.
	 */
	private int getDisplayedKivPartitionIndex(int kivPartitionIndex) {
		return kivPartitionIndex - OFFSET_KIV;
	}

	/**
	 * This method adds a task into the undone lists.
	 * 
	 * @param task	Task to be added into undone lists.
	 */
	private void addedToUndoneLists(Task task) {
		storedTasksByTime.add(task);
		storedTasksByPriority.add(task);
	}

	/**
	 * This method removes a task from the undone lists.
	 * 
	 * @param task	Task to be removed.
	 */
	private void removeTaskFromUndoneLists(Task task) {
		storedTasksByTime.remove(task);
		storedTasksByPriority.remove(task);
	}

	/**
	 * This method shifts the task to the ticked list.
	 * 
	 * @param ticked	Task to be moved to ticked list.
	 */
	private void moveToTicked(Task ticked) {
		removeTaskFromUndoneLists(ticked);
		storedTasksByTicked.add(TOP_OF_THE_LIST, ticked);
	}

	/**
	 * This method shifts the task to the kiv list.
	 * 
	 * @param kiv	Task to be moved to kiv list.
	 */
	private void moveToKiv(Task kiv) {
		removeTaskFromUndoneLists(kiv);
		storedTasksByKIV.add(TOP_OF_THE_LIST, kiv);
	}
}
