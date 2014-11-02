package ticker.logic;

import java.util.Vector;

import ticker.common.Task;

public class TickCMIManager {
	// CONSTANTS
	// String constants for command types
	private static final String COMMAND_UNTICK = "untick";
	private static final String COMMAND_TICK = "tick";
	private static final String COMMAND_UNCMI = "uncmi";
	private static final String COMMAND_CMI = "cmi";
	// Integer key constants for lists used by listTracker
	private static final int KEY_TICKED = 3;
	private static final int KEY_CMI = 4;
	private static final int KEY_SEARCH = 5;
	// String constants for type of lists used by UndoManager
	private static final String TASKS_TIME = "TIME";
	private static final String TASKS_TICKED = "TICKED";
	private static final String TASKS_CMI = "CMI";
	private static final String TASKS_SEARCH = "SEARCH";

	// Instances of other components
	private UndoManager undoMng;
	private Vector<Task> storedTasksByPriority, storedTasksByTime, storedTasksByTicked, storedTasksByCMI;

	TickCMIManager(Vector<Task> storedTasksByPriority, Vector<Task> storedTasksByTime, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByCMI) {
		this.storedTasksByPriority = storedTasksByPriority;
		this.storedTasksByTime = storedTasksByTime;
		this.storedTasksByTicked = storedTasksByTicked;
		this.storedTasksByCMI = storedTasksByCMI;

		undoMng = UndoManager.getInstance(storedTasksByPriority, storedTasksByTime, storedTasksByTicked, storedTasksByCMI);
	}

	String tick(int index, int listTracker, Vector<Task> current) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		Task ticked;

		if (listTracker == KEY_CMI) {
			System.out.println("cannot tick in cmi list");
			throw new IllegalArgumentException();
		}

		else if (listTracker == KEY_SEARCH) {
			ticked = current.get(index-1);
			if (storedTasksByTime.contains(ticked) || storedTasksByPriority.contains(ticked)) {
				storedTasksByTime.remove(ticked);
				storedTasksByPriority.remove(ticked);
			}
			else if (storedTasksByTicked.contains(ticked)) {
				return "Task is already ticked.";
			}
			else if (storedTasksByCMI.contains(ticked)) {
				return "Cannot tick a task from CMI. Please unCMI task first.";
			}

		}

		else {
			ticked = current.remove(index-1);
		}
		storedTasksByTime.remove(ticked);
		storedTasksByPriority.remove(ticked);
		storedTasksByTicked.add(0, ticked);

		Event event = new Event(COMMAND_TICK, ticked, TASKS_TIME, TASKS_TICKED);
		undoMng.add(event);

		return ticked.toString() + " is done!";
	}

	String untick(int index, int listTracker, Vector<Task> current) throws ArrayIndexOutOfBoundsException, IllegalArgumentException{
		Task unticked;

		if (listTracker != KEY_TICKED && listTracker != KEY_SEARCH) {
			throw new IllegalArgumentException();
		}
		
		if (listTracker == KEY_SEARCH) {
			unticked = current.get(index-1);
			if (storedTasksByTime.contains(unticked) || storedTasksByPriority.contains(unticked)) {
				return "Cannot untick a task from undone list.";
			}
			else if (storedTasksByTicked.contains(unticked)) {
				storedTasksByTicked.remove(unticked);
			}
			else if (storedTasksByCMI.contains(unticked)) {
				return "Cannot untick a task from CMI.";
			}

		}
		else {
				unticked = current.remove(index-1);
		}
		
		storedTasksByTime.add(unticked);
		storedTasksByPriority.add(unticked);

		Event event = new Event(COMMAND_UNTICK, unticked, TASKS_TIME, TASKS_TICKED);
		undoMng.add(event);

		return unticked.toString() + " is back to undone.";
	}

	String cmi(int index, int listTracker, Vector<Task> current, String currentListName) 
			throws ArrayIndexOutOfBoundsException,IllegalArgumentException {
		if (listTracker == KEY_TICKED) {
			throw new IllegalArgumentException();
		}

		Task cmi;

		if (listTracker == KEY_TICKED) {
			System.out.println("Cannot cmi in ticked list");
			throw new IllegalArgumentException();
		}

		else if (listTracker == KEY_SEARCH) {
			cmi = current.get(index-1);
			if (storedTasksByTime.contains(cmi) || storedTasksByPriority.contains(cmi)) {
				storedTasksByTime.remove(cmi);
				storedTasksByPriority.remove(cmi);
			}
			else if (storedTasksByCMI.contains(cmi)) {
				return "Task is already CMIed.";
			}
			else if (storedTasksByTicked.contains(cmi)) {
				return "Cannot CMI a task from ticked. Please untick task first.";
			}

		}

		else {
			cmi = current.remove(index-1);
		}
		
		// Add to the front so the latest additions are on top
		storedTasksByCMI.add(0, cmi);
		storedTasksByTime.remove(cmi);
		storedTasksByPriority.remove(cmi);

		Event event = new Event(COMMAND_CMI, cmi, TASKS_TIME, TASKS_CMI);
		undoMng.add(event);

		return cmi.toString() + " will be kept in view.";

	}

	String uncmi(int index, int listTracker, Vector<Task> current) 
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		Task uncmi;
		
		if (listTracker != KEY_CMI && listTracker != KEY_SEARCH) {
			throw new IllegalArgumentException();
		}
		
		if (listTracker == KEY_SEARCH) {
			uncmi = current.get(index-1);
			if (storedTasksByTime.contains(uncmi) || storedTasksByPriority.contains(uncmi)) {
				return "Cannot uncmi a task from undone list.";
			}
			else if (storedTasksByTicked.contains(uncmi)) {
				return "Cannot uncmi a task from ticked list.";
			}
			else if (storedTasksByCMI.contains(uncmi)) {
				storedTasksByCMI.remove(uncmi);
			}

		}
		else {
				uncmi = current.remove(index-1);
		}

		// Add to the front so the latest additions are on top
		storedTasksByTime.add(uncmi);
		storedTasksByPriority.add(uncmi);

		Event event = new Event(COMMAND_UNCMI, uncmi, TASKS_TIME, TASKS_CMI);
		undoMng.add(event);

		return uncmi.toString() + " is back to undone.";

	}
}
