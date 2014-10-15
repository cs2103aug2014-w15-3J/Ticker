package ticker.logic;

import java.util.Stack;
import java.util.Vector;

import ticker.common.Task;

public class UndoManager {
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_TICK = "tick";
	private static final String COMMAND_CMI = "cmi";
	
	private static final int TASKS_DEADLINE_INDEX = 1;
	private static final int TASKS_PRIORITY_INDEX = 2;
	private static final int TASKS_TICKED_INDEX = 3;
	private static final int TASKS_CMI_INDEX = 4;
	
	private static UndoManager theOne;
	private Stack<Event> undoStack, redoStack;
	private Vector<Task> storedTasksByPriority, storedTasksByDeadline, storedTasksByTicked, storedTasksByCMI;
	
	private UndoManager(Vector<Task> storedTasksByPriority, Vector<Task> storedTasksByDeadline, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByCMI) {
		undoStack = new Stack<Event>();
		redoStack = new Stack<Event>();
		storedTasksByPriority = this.storedTasksByPriority;
		storedTasksByDeadline = this.storedTasksByDeadline;
		storedTasksByTicked = this.storedTasksByTicked;
		storedTasksByCMI = this.storedTasksByCMI;
	}
	
	public void undo() {
		if(!undoStack.isEmpty()) {
			Event previousAction = undoStack.pop();
			redoStack.push(previousAction);
			
			switch(previousAction.getCommand()) {
				case COMMAND_EDIT:
					storedTasksByPriority.remove(previousAction.getTaskAfterEdit());
					storedTasksByPriority.add(previousAction.getTaskBeforeEdit());
					storedTasksByDeadline.remove(previousAction.getTaskAfterEdit());
					storedTasksByDeadline.add(previousAction.getTaskBeforeEdit());
					break;
				case COMMAND_ADD:
					storedTasksByPriority.remove(previousAction.getTaskBeforeEdit());
					storedTasksByDeadline.remove(previousAction.getTaskBeforeEdit());
					break;
				case COMMAND_DELETE:
					storedTasksByPriority.add(previousAction.getTaskBeforeEdit());
					storedTasksByDeadline.add(previousAction.getTaskBeforeEdit());
					break;
				case COMMAND_TICK:
					//TODO:refactor
					//the previous action moves task from normal to ticked, hence now moves task from ticked to normal
					if(previousAction.getListTypeBefore() == TASKS_DEADLINE_INDEX) {
						storedTasksByTicked.remove(previousAction.getTaskBeforeEdit());
						storedTasksByPriority.add(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.add(previousAction.getTaskBeforeEdit());
					} else if(previousAction.getListTypeBefore() == TASKS_TICKED_INDEX) {
						storedTasksByTicked.add(previousAction.getTaskBeforeEdit());
						storedTasksByPriority.remove(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.remove(previousAction.getTaskBeforeEdit());
					} else {
						throw new IllegalArgumentException("the index must be 1 or 3!");
					}
					break;
				case COMMAND_CMI:
					//TODO:refactor
					if(previousAction.getListTypeBefore() == TASKS_DEADLINE_INDEX) {
						storedTasksByCMI.remove(previousAction.getTaskBeforeEdit());
						storedTasksByPriority.add(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.add(previousAction.getTaskBeforeEdit());
					} else if(previousAction.getListTypeBefore() == TASKS_CMI_INDEX) {
						storedTasksByCMI.add(previousAction.getTaskBeforeEdit());
						storedTasksByPriority.remove(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.remove(previousAction.getTaskBeforeEdit());
					} else {
						throw new IllegalArgumentException("the index must be 1 or 4!");
					}
					break;
			}
		}
	}
	
	public void redo() {
		if(!redoStack.isEmpty()) {
			Event nextAction = redoStack.pop();
			undoStack.push(nextAction);
			
			switch(nextAction.getCommand()) {
			case COMMAND_EDIT:
				storedTasksByPriority.add(nextAction.getTaskAfterEdit());
				storedTasksByPriority.remove(nextAction.getTaskBeforeEdit());
				storedTasksByDeadline.add(nextAction.getTaskAfterEdit());
				storedTasksByDeadline.remove(nextAction.getTaskBeforeEdit());
				break;
			case COMMAND_ADD:
				storedTasksByPriority.add(nextAction.getTaskBeforeEdit());
				storedTasksByDeadline.add(nextAction.getTaskBeforeEdit());
				break;
			case COMMAND_DELETE:
				storedTasksByPriority.remove(nextAction.getTaskBeforeEdit());
				storedTasksByDeadline.remove(nextAction.getTaskBeforeEdit());
				break;
			case COMMAND_TICK:
				//TODO:refactor
				if(nextAction.getListTypeBefore() == TASKS_DEADLINE_INDEX) {
					storedTasksByTicked.add(nextAction.getTaskBeforeEdit());
					storedTasksByPriority.remove(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.remove(nextAction.getTaskBeforeEdit());
				} else if(nextAction.getListTypeBefore() == TASKS_TICKED_INDEX) {
					storedTasksByTicked.remove(nextAction.getTaskBeforeEdit());
					storedTasksByPriority.add(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.add(nextAction.getTaskBeforeEdit());
				} else {
					throw new IllegalArgumentException("the index must be 1 or 3!");
				}
				break;
			case COMMAND_CMI:
				//TODO:refactor
				if(nextAction.getListTypeBefore() == TASKS_DEADLINE_INDEX) {
					storedTasksByCMI.add(nextAction.getTaskBeforeEdit());
					storedTasksByPriority.remove(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.remove(nextAction.getTaskBeforeEdit());
				} else if(nextAction.getListTypeBefore() == TASKS_CMI_INDEX) {
					storedTasksByCMI.remove(nextAction.getTaskBeforeEdit());
					storedTasksByPriority.add(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.add(nextAction.getTaskBeforeEdit());
				} else {
					throw new IllegalArgumentException("the index must be 1 or 4!");
				}
				break;
			}
		}
	}
	
	public void add(Event eventAction) {
		undoStack.push(eventAction);
		redoStack.clear();
	}
	
	//Singleton
	public static UndoManager getInstance(Vector<Task> storedTasksByPriority, Vector<Task> storedTasksByDeadline, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByCMI) {
		if(theOne == null) {
			theOne = new UndoManager(storedTasksByPriority, storedTasksByDeadline, storedTasksByTicked, storedTasksByCMI);
		}
		return theOne;
	}
}
