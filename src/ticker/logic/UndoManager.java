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
	private static final String COMMAND_UNCMI = "uncmi";
	private static final String COMMAND_UNTICK = "untick";
	
	private static final String TASKS_TIME = "TIME";
	private static final String TASKS_TICKED = "TICKED";
	private static final String TASKS_CMI = "CMI";
	
	private static UndoManager theOne;
	private Stack<Event> undoStack, redoStack;
	private Vector<Task> storedTasksByPriority, storedTasksByDeadline, storedTasksByTicked, storedTasksByCMI;
	
	private UndoManager(Vector<Task> storedTasksByPriority, Vector<Task> storedTasksByDeadline, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByCMI) {
		undoStack = new Stack<Event>();
		redoStack = new Stack<Event>();
		this.storedTasksByPriority = storedTasksByPriority;
		this.storedTasksByDeadline = storedTasksByDeadline;
		this.storedTasksByTicked = storedTasksByTicked;
		this.storedTasksByCMI = storedTasksByCMI;
	}
	
	public void undo() {
		if(!undoStack.isEmpty()) {
			Event previousAction = undoStack.pop();
			redoStack.push(previousAction);
			
			switch(previousAction.getCommand()) {
				case COMMAND_EDIT:
					assert previousAction.getTaskAfterEdit() != null && previousAction.getTaskBeforeEdit() != null;
					
					storedTasksByPriority.remove(previousAction.getTaskAfterEdit());
					storedTasksByPriority.add(previousAction.getTaskBeforeEdit());
					storedTasksByDeadline.remove(previousAction.getTaskAfterEdit());
					storedTasksByDeadline.add(previousAction.getTaskBeforeEdit());
					break;
				case COMMAND_ADD:
					assert previousAction.getTaskBeforeEdit() != null;
					
					storedTasksByPriority.remove(previousAction.getTaskBeforeEdit());
					storedTasksByDeadline.remove(previousAction.getTaskBeforeEdit());
					break;
				case COMMAND_DELETE:
					assert previousAction.getTaskBeforeEdit() != null && previousAction.getListTypeBefore() != null;
					
					if(previousAction.getListTypeBefore().equals(TASKS_TIME)) {
						storedTasksByPriority.add(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.add(previousAction.getTaskBeforeEdit());
					} else if (previousAction.getListTypeBefore().equals(TASKS_TICKED)) {
						storedTasksByTicked.add(previousAction.getIndexBefore(), previousAction.getTaskBeforeEdit());
					} else if (previousAction.getListTypeBefore().equals(TASKS_CMI)) {
						storedTasksByCMI.add(previousAction.getIndexBefore(), previousAction.getTaskBeforeEdit());
					} else {
						throw new IllegalArgumentException("The tasks must be DEADLINE, TICKED OR CMI");
					}
					break;
				case COMMAND_TICK:
				case COMMAND_UNTICK:
					//TODO:refactor
					//the previous action moves task from normal to ticked, hence now moves task from ticked to normal
					assert previousAction.getListTypeBefore() != null && previousAction.getTaskBeforeEdit() != null;
					
					if(previousAction.getListTypeBefore().equals(TASKS_TIME)) {
						storedTasksByTicked.remove(previousAction.getTaskBeforeEdit());
						storedTasksByPriority.add(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.add(previousAction.getTaskBeforeEdit());
					} else if(previousAction.getListTypeBefore().equals(TASKS_TICKED)) {
						storedTasksByTicked.add(previousAction.getTaskBeforeEdit());
						storedTasksByPriority.remove(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.remove(previousAction.getTaskBeforeEdit());
					} else {
						throw new IllegalArgumentException("The tasks must be DEADLINE or TICKED");
					}
					break;
				case COMMAND_CMI:
				case COMMAND_UNCMI:
					//TODO:refactor
					assert previousAction.getListTypeBefore() != null && previousAction.getTaskBeforeEdit() != null;
					
					if(previousAction.getListTypeBefore().equals(TASKS_TIME)) {
						storedTasksByCMI.remove(previousAction.getTaskBeforeEdit());
						storedTasksByPriority.add(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.add(previousAction.getTaskBeforeEdit());
					} else if(previousAction.getListTypeBefore().equals(TASKS_CMI)) {
						storedTasksByCMI.add(previousAction.getTaskBeforeEdit());
						storedTasksByPriority.remove(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.remove(previousAction.getTaskBeforeEdit());
					} else {
						throw new IllegalArgumentException("The tasks must be DEADLINE or CMI");
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
				assert nextAction.getTaskAfterEdit() != null && nextAction.getTaskBeforeEdit() != null;
				
				storedTasksByPriority.add(nextAction.getTaskAfterEdit());
				storedTasksByPriority.remove(nextAction.getTaskBeforeEdit());
				storedTasksByDeadline.add(nextAction.getTaskAfterEdit());
				storedTasksByDeadline.remove(nextAction.getTaskBeforeEdit());
				break;
			case COMMAND_ADD:
				assert nextAction.getTaskBeforeEdit() != null;
				
				storedTasksByPriority.add(nextAction.getTaskBeforeEdit());
				storedTasksByDeadline.add(nextAction.getTaskBeforeEdit());
				break;
			case COMMAND_DELETE:
				assert nextAction.getTaskBeforeEdit() != null;
				
				if(nextAction.getListTypeBefore().equals(TASKS_TIME)) {
					storedTasksByPriority.remove(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.remove(nextAction.getTaskBeforeEdit());
				} else if (nextAction.getListTypeBefore().equals(TASKS_TICKED)) {
					storedTasksByTicked.remove(nextAction.getTaskBeforeEdit());
				} else if (nextAction.getListTypeBefore().equals(TASKS_CMI)) {
					storedTasksByCMI.remove(nextAction.getTaskBeforeEdit());
				} else {
					throw new IllegalArgumentException("The tasks must be DEADLINE, TICKED OR CMI");
				}
				break;
			case COMMAND_TICK:
			case COMMAND_UNTICK:
				//TODO:refactor
				assert nextAction.getTaskBeforeEdit() != null && nextAction.getListTypeBefore() != null;
				
				if(nextAction.getListTypeBefore().equals(TASKS_TIME)) {
					storedTasksByTicked.add(nextAction.getTaskBeforeEdit());
					storedTasksByPriority.remove(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.remove(nextAction.getTaskBeforeEdit());
				} else if(nextAction.getListTypeBefore().equals(TASKS_TICKED)) {
					storedTasksByTicked.remove(nextAction.getTaskBeforeEdit());
					storedTasksByPriority.add(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.add(nextAction.getTaskBeforeEdit());
				} else {
					throw new IllegalArgumentException("The tasks must be DEADLINE or TICKED");
				}
				break;
			case COMMAND_CMI:
			case COMMAND_UNCMI:
				//TODO:refactor
				assert nextAction.getTaskBeforeEdit() != null && nextAction.getListTypeBefore() != null;
				
				if(nextAction.getListTypeBefore().equals(TASKS_TIME)) {
					storedTasksByCMI.add(nextAction.getTaskBeforeEdit());
					storedTasksByPriority.remove(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.remove(nextAction.getTaskBeforeEdit());
				} else if(nextAction.getListTypeBefore().equals(TASKS_CMI)) {
					storedTasksByCMI.remove(nextAction.getTaskBeforeEdit());
					storedTasksByPriority.add(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.add(nextAction.getTaskBeforeEdit());
				} else {
					throw new IllegalArgumentException("The tasks must be DEADLINE or CMI");
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
	
	public void clearStateForTesting() {
		theOne = null;
	}
}
