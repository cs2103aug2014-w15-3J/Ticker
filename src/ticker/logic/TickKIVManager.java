//@author A0114535M

/* Team ID: W15-3J
 * Name: Li Jia'En, Nicholette
 * Matric Number: A0114535M
 * Project Title: CE1 TextBuddy
 * Purpose: This class receives text commands from the user and edits a textfile. 
 * The commands are for add, display, delete, clear and exit.
 * Assumptions: 
 * This program assumes that:
 * -the user knows the format for each command
 * -the user input lines in the textfile is not numbered.
 * -(option c) the file is saved to disk when the user exit the program
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
	private static final int KEY_SORTED_TIME = 1;
	private static final int KEY_SORTED_PRIORITY = 2;
	private static final int KEY_TICKED = 3;
	private static final int KEY_KIV = 4;
	private static final int KEY_SEARCH = 5;
	private static final int KEY_FREESLOTS = 6;
	// String constants for type of lists used by UndoManager
	private static final String LIST_TIME = "time";
	private static final String LIST_TICKED = "ticked";
	private static final String LIST_KIV = "kiv";
	private static final String LIST_SEARCH = "search";

	// Instances of other components
	private UndoManager undoMng;
	private Vector<Task> storedTasksByPriority, storedTasksByTime, storedTasksByTicked, storedTasksByKIV;

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
	TickKIVManager(Vector<Task> storedTasksByPriority, Vector<Task> storedTasksByTime, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByKIV) {
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
	String tick(int index, int listTracker, Vector<Task> current) throws ArrayIndexOutOfBoundsException {
		Task ticked;

		if (listTracker == KEY_KIV || listTracker == KEY_TICKED || listTracker == KEY_FREESLOTS) {
			return "Can only tick from undone list and search list.";
		}

		else if (listTracker == KEY_SEARCH) {
			Task tickedPartition = new Task("\\***TICKED***\\", null, null, null, null, 'B', false);
			Task kivPartition = new Task("\\***KIV***\\", null, null, null, null, 'B', false);
			int tickedPartitionIndex = current.indexOf(tickedPartition);
			int kivPartitionIndex = current.indexOf(kivPartition);

			if ((index - 1) < tickedPartitionIndex) {
				ticked = current.remove(index - 1);
			}
			else if ((index - 1) >= tickedPartitionIndex && (index - 1) <= (kivPartitionIndex - 2)) {
				ticked = current.remove(index);
			}
			else {
				ticked = current.remove(index + 1);
			}

			if (storedTasksByTime.contains(ticked) || storedTasksByPriority.contains(ticked)) {
				storedTasksByTime.remove(ticked);
				storedTasksByPriority.remove(ticked);
			}
			else if (storedTasksByTicked.contains(ticked)) {
				return "Task is already ticked.";
			}
			else if (storedTasksByKIV.contains(ticked)) {
				return "Cannot tick a task from KIV. Please unKIV task first.";
			}

		}

		else {
			ticked = current.remove(index-1);
		}
		storedTasksByTime.remove(ticked);
		storedTasksByPriority.remove(ticked);
		storedTasksByTicked.add(0, ticked);

		Event event = new Event(COMMAND_TICK, ticked, LIST_TIME, LIST_TICKED);
		undoMng.add(event);

		return ticked.toString() + " is done!";
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
	String untick(int index, int listTracker, Vector<Task> current) throws ArrayIndexOutOfBoundsException{
		Task unticked;

		if (listTracker != KEY_TICKED || listTracker != KEY_SEARCH) {
			return "Can only untick from ticked list and search list.";
		}
		if (listTracker == KEY_SEARCH) {
			Task tickedPartition = new Task("\\***TICKED***\\", null, null, null, null, 'B', false);
			Task kivPartition = new Task("\\***KIV***\\", null, null, null, null, 'B', false);
			int tickedPartitionIndex = current.indexOf(tickedPartition);
			int kivPartitionIndex = current.indexOf(kivPartition);

			if ((index - 1) < tickedPartitionIndex) {
				unticked = current.remove(index - 1);
			}
			else if ((index - 1) >= tickedPartitionIndex && (index - 1) <= (kivPartitionIndex - 2)) {
				unticked = current.remove(index);
			}
			else {
				unticked = current.remove(index + 1);
			}

			if (storedTasksByTime.contains(unticked) || storedTasksByPriority.contains(unticked)) {
				return "Cannot untick a task from undone list.";
			}
			else if (storedTasksByTicked.contains(unticked)) {
				storedTasksByTicked.remove(unticked);
			}
			else if (storedTasksByKIV.contains(unticked)) {
				return "Cannot untick a task from KIV.";
			}

		}
		else {
			unticked = current.remove(index-1);
		}

		storedTasksByTime.add(unticked);
		storedTasksByPriority.add(unticked);

		Event event = new Event(COMMAND_UNTICK, unticked, LIST_TIME, LIST_TICKED);
		undoMng.add(event);

		return unticked.toString() + " is back to undone.";
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
	String kiv(int index, int listTracker, Vector<Task> current, String currentListName) 
			throws ArrayIndexOutOfBoundsException,IllegalArgumentException {

		Task kiv;

		if (listTracker == KEY_TICKED || listTracker == KEY_KIV || listTracker == KEY_FREESLOTS) {
			return "Can only kiv in undone list and search list.";
		}

		else if (listTracker == KEY_SEARCH) {
			Task tickedPartition = new Task("\\***TICKED***\\", null, null, null, null, 'B', false);
			Task kivPartition = new Task("\\***KIV***\\", null, null, null, null, 'B', false);
			int tickedPartitionIndex = current.indexOf(tickedPartition);
			int kivPartitionIndex = current.indexOf(kivPartition);

			if ((index - 1) < tickedPartitionIndex) {
				kiv = current.remove(index - 1);
			}
			else if ((index - 1) >= tickedPartitionIndex && (index - 1) <= (kivPartitionIndex - 2)) {
				kiv = current.remove(index);
			}
			else {
				kiv = current.remove(index + 1);
			}

			if (storedTasksByTime.contains(kiv) || storedTasksByPriority.contains(kiv)) {
				storedTasksByTime.remove(kiv);
				storedTasksByPriority.remove(kiv);
			}
			else if (storedTasksByKIV.contains(kiv)) {
				return "Task is already KIVed.";
			}
			else if (storedTasksByTicked.contains(kiv)) {
				return "Cannot KIV a task from ticked. Please untick task first.";
			}

		}

		else {
			kiv = current.remove(index-1);
		}

		// Add to the front so the latest additions are on top
		storedTasksByKIV.add(0, kiv);
		storedTasksByTime.remove(kiv);
		storedTasksByPriority.remove(kiv);

		Event event = new Event(COMMAND_KIV, kiv, LIST_TIME, LIST_KIV);
		undoMng.add(event);

		return kiv.toString() + " will be kept in view.";

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
	String unkiv(int index, int listTracker, Vector<Task> current) throws ArrayIndexOutOfBoundsException {
		Task unkiv;
		
		if (listTracker != KEY_KIV || listTracker != KEY_SEARCH) {
			return "Can only kiv in kiv list and search list.";
		}

		if (listTracker == KEY_SEARCH) {
			Task tickedPartition = new Task("\\***TICKED***\\", null, null, null, null, 'B', false);
			Task kivPartition = new Task("\\***KIV***\\", null, null, null, null, 'B', false);
			int tickedPartitionIndex = current.indexOf(tickedPartition);
			int kivPartitionIndex = current.indexOf(kivPartition);

			if ((index - 1) < tickedPartitionIndex) {
				unkiv = current.remove(index - 1);
			}
			else if ((index - 1) >= tickedPartitionIndex && (index - 1) <= (kivPartitionIndex - 2)) {
				unkiv = current.remove(index);
			}
			else {
				unkiv = current.remove(index + 1);
			}

			if (storedTasksByTime.contains(unkiv) || storedTasksByPriority.contains(unkiv)) {
				return "Cannot unkiv a task from undone list.";
			}
			else if (storedTasksByTicked.contains(unkiv)) {
				return "Cannot unkiv a task from ticked list.";
			}
			else if (storedTasksByKIV.contains(unkiv)) {
				storedTasksByKIV.remove(unkiv);
			}

		}
		else {
			unkiv = current.remove(index-1);
		}

		// Add to the front so the latest additions are on top
		storedTasksByTime.add(unkiv);
		storedTasksByPriority.add(unkiv);

		Event event = new Event(COMMAND_UNKIV, unkiv, LIST_TIME, LIST_KIV);
		undoMng.add(event);

		return unkiv.toString() + " is back to undone.";

	}
}
