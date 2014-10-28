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
	// String constants for type of lists used by UndoManager
	private static final String TASKS_TIME = "TIME";
	private static final String TASKS_TICKED = "TICKED";
	private static final String TASKS_CMI = "CMI";

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

	String tick(int index, int listTracker, Vector<Task> current) {
		// Exception catching
		if (index <= 0 || index > current.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}

		if (listTracker == KEY_CMI) {
			System.out.println("Error in listTracker");
			throw new IllegalArgumentException();
		}

		Task ticked = current.remove(index-1);
		storedTasksByTime.remove(ticked);
		storedTasksByPriority.remove(ticked);
		storedTasksByTicked.add(0, ticked);

		Event event = new Event(COMMAND_TICK, ticked, TASKS_TIME, TASKS_TICKED);
		undoMng.add(event);

		return ticked.toString() + " is done!\n";
	}

	String untick(int index, int listTracker, Vector<Task> current) {
		// Exception catching
		if (index <= 0 || index > current.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}

		if (listTracker != KEY_TICKED) {
			throw new IllegalArgumentException();
		}

		Task unticked = current.remove(index-1);
		storedTasksByTime.add(unticked);
		storedTasksByPriority.add(unticked);

		Event event = new Event(COMMAND_UNTICK, unticked, TASKS_TIME, TASKS_TICKED);
		undoMng.add(event);

		return unticked.toString() + " is back to undone\n";
	}

	String cmi(int index, int listTracker, Vector<Task> current, String currentListName) throws ArrayIndexOutOfBoundsException {
		// Exception catching
		if (index <= 0 || index > current.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}

		if (listTracker == KEY_TICKED) {
			throw new IllegalArgumentException();
		}

		Task cmi = current.remove(index-1);
		// Add to the front so the latest additions are on top
		storedTasksByCMI.add(0, cmi);
		storedTasksByTime.remove(cmi);
		storedTasksByPriority.remove(cmi);

		Event event = new Event(COMMAND_CMI, cmi, TASKS_TIME, TASKS_CMI);
		undoMng.add(event);

		return cmi.toString() + " cannot be done!\n";

	}

	String uncmi(int index, int listTracker, Vector<Task> current) throws ArrayIndexOutOfBoundsException {
		// Exception catching
		if (index <= 0 || index > current.size()) {
			throw new ArrayIndexOutOfBoundsException();
		}

		if (listTracker != KEY_CMI) {
			throw new IllegalArgumentException();
		}

		Task uncmi = current.remove(index-1);
		// Add to the front so the latest additions are on top
		storedTasksByTime.add(uncmi);
		storedTasksByPriority.add(uncmi);

		Event event = new Event(COMMAND_UNCMI, uncmi, TASKS_TIME, TASKS_CMI);
		undoMng.add(event);

		return uncmi.toString() + "is back to undone!\n";

	}
}