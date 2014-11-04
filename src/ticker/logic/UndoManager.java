package ticker.logic;

import java.util.Stack;
import java.util.Vector;

import ticker.common.Task;

public class UndoManager {
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_TICK = "tick";
	private static final String COMMAND_KIV = "kiv";
	private static final String COMMAND_UNKIV = "unkiv";
	private static final String COMMAND_UNTICK = "untick";
	
	private static final String LIST_TIME = "time";
	private static final String LIST_PRIORITY = "priority";
	private static final String LIST_TICKED = "ticked";
	private static final String LIST_KIV = "kiv";
	
	private static final String FEEDBACK_SUCCESSFUL_UNDO = "Undoing action";
	private static final String FEEDBACK_SUCCESSFUL_REDO = "Redoing action";
	private static final String FEEDBACK_UNSUCCESSFUL_UNDO = "You have reached the last undo";
	private static final String FEEDBACK_UNSUCCESSFUL_REDO = "You have reached the last redo";
	
	private static UndoManager theOne;
	private Stack<Event> undoStack, redoStack;
	private Vector<Task> storedTasksByPriority, storedTasksByDeadline, storedTasksByTicked, storedTasksByKIV;
	
	private UndoManager(Vector<Task> storedTasksByPriority, Vector<Task> storedTasksByDeadline, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByKIV) {
		undoStack = new Stack<Event>();
		redoStack = new Stack<Event>();
		this.storedTasksByPriority = storedTasksByPriority;
		this.storedTasksByDeadline = storedTasksByDeadline;
		this.storedTasksByTicked = storedTasksByTicked;
		this.storedTasksByKIV = storedTasksByKIV;
	}
	
	public String undo() {
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
					return FEEDBACK_SUCCESSFUL_UNDO;
				case COMMAND_ADD:
					assert previousAction.getTaskBeforeEdit() != null;
					
					storedTasksByPriority.remove(previousAction.getTaskBeforeEdit());
					storedTasksByDeadline.remove(previousAction.getTaskBeforeEdit());
					return FEEDBACK_SUCCESSFUL_UNDO;
				case COMMAND_DELETE:
					assert previousAction.getTaskBeforeEdit() != null && previousAction.getListTypeBefore() != null;
					
					if(previousAction.getListTypeBefore().equals(LIST_TIME) || previousAction.getListTypeBefore().equals(LIST_PRIORITY)) {
						storedTasksByPriority.add(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.add(previousAction.getTaskBeforeEdit());
					} else if (previousAction.getListTypeBefore().equals(LIST_TICKED)) {
						storedTasksByTicked.add(previousAction.getIndexBefore(), previousAction.getTaskBeforeEdit());
					} else if (previousAction.getListTypeBefore().equals(LIST_KIV)) {
						storedTasksByKIV.add(previousAction.getIndexBefore(), previousAction.getTaskBeforeEdit());
					} else {
						throw new IllegalArgumentException("The tasks must be TIME, TICKED, PRIORITY or KIV");
					}
					return FEEDBACK_SUCCESSFUL_UNDO;
				case COMMAND_TICK:
				case COMMAND_UNTICK:
					//TODO:refactor
					//the previous action moves task from normal to ticked, hence now moves task from ticked to normal
					assert previousAction.getListTypeBefore() != null && previousAction.getTaskBeforeEdit() != null;
					
					if(previousAction.getListTypeBefore().equals(LIST_TIME) || previousAction.getListTypeBefore().equals(LIST_PRIORITY)) {
						storedTasksByTicked.remove(previousAction.getTaskBeforeEdit());
						storedTasksByPriority.add(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.add(previousAction.getTaskBeforeEdit());
					} else if(previousAction.getListTypeBefore().equals(LIST_TICKED)) {
						storedTasksByTicked.add(previousAction.getTaskBeforeEdit());
						storedTasksByPriority.remove(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.remove(previousAction.getTaskBeforeEdit());
					} else {
						throw new IllegalArgumentException("The tasks must be TIME, PRIORITY OR TICKED");
					}
					return FEEDBACK_SUCCESSFUL_UNDO;
				case COMMAND_KIV:
				case COMMAND_UNKIV:
					//TODO:refactor
					assert previousAction.getListTypeBefore() != null && previousAction.getTaskBeforeEdit() != null;
					
					if(previousAction.getListTypeBefore().equals(LIST_TIME) || previousAction.getListTypeBefore().equals(LIST_PRIORITY)) {
						storedTasksByKIV.remove(previousAction.getTaskBeforeEdit());
						storedTasksByPriority.add(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.add(previousAction.getTaskBeforeEdit());
					} else if(previousAction.getListTypeBefore().equals(LIST_KIV)) {
						storedTasksByKIV.add(previousAction.getTaskBeforeEdit());
						storedTasksByPriority.remove(previousAction.getTaskBeforeEdit());
						storedTasksByDeadline.remove(previousAction.getTaskBeforeEdit());
					} else {
						throw new IllegalArgumentException("The tasks must be TIME, PRIORITY or KIV");
					}
					return FEEDBACK_SUCCESSFUL_UNDO;
			}
		}
		return FEEDBACK_UNSUCCESSFUL_UNDO; 
		
	}
	
	public String redo() {
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
				return FEEDBACK_SUCCESSFUL_REDO;
			case COMMAND_ADD:
				assert nextAction.getTaskBeforeEdit() != null;
				
				storedTasksByPriority.add(nextAction.getTaskBeforeEdit());
				storedTasksByDeadline.add(nextAction.getTaskBeforeEdit());
				return FEEDBACK_SUCCESSFUL_REDO;
			case COMMAND_DELETE:
				assert nextAction.getTaskBeforeEdit() != null;
				
				if(nextAction.getListTypeBefore().equals(LIST_TIME) || nextAction.getListTypeBefore().equals(LIST_PRIORITY)) {
					storedTasksByPriority.remove(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.remove(nextAction.getTaskBeforeEdit());
				} else if (nextAction.getListTypeBefore().equals(LIST_TICKED)) {
					storedTasksByTicked.remove(nextAction.getTaskBeforeEdit());
				} else if (nextAction.getListTypeBefore().equals(LIST_KIV)) {
					storedTasksByKIV.remove(nextAction.getTaskBeforeEdit());
				} else {
					throw new IllegalArgumentException("The tasks must be TIME, PRIORITY, TICKED OR KIV");
				}
				return FEEDBACK_SUCCESSFUL_REDO;
			case COMMAND_TICK:
			case COMMAND_UNTICK:
				//TODO:refactor
				assert nextAction.getTaskBeforeEdit() != null && nextAction.getListTypeBefore() != null;
				
				if(nextAction.getListTypeBefore().equals(LIST_TIME) || nextAction.getListTypeBefore().equals(LIST_PRIORITY)) {
					storedTasksByTicked.add(nextAction.getTaskBeforeEdit());
					storedTasksByPriority.remove(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.remove(nextAction.getTaskBeforeEdit());
				} else if(nextAction.getListTypeBefore().equals(LIST_TICKED)) {
					storedTasksByTicked.remove(nextAction.getTaskBeforeEdit());
					storedTasksByPriority.add(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.add(nextAction.getTaskBeforeEdit());
				} else {
					throw new IllegalArgumentException("The tasks must be TIME, PRIORITY or TICKED");
				}
				return FEEDBACK_SUCCESSFUL_REDO;
			case COMMAND_KIV:
			case COMMAND_UNKIV:
				//TODO:refactor
				assert nextAction.getTaskBeforeEdit() != null && nextAction.getListTypeBefore() != null;
				
				if(nextAction.getListTypeBefore().equals(LIST_TIME) || nextAction.getListTypeBefore().equals(LIST_PRIORITY)) {
					storedTasksByKIV.add(nextAction.getTaskBeforeEdit());
					storedTasksByPriority.remove(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.remove(nextAction.getTaskBeforeEdit());
				} else if(nextAction.getListTypeBefore().equals(LIST_KIV)) {
					storedTasksByKIV.remove(nextAction.getTaskBeforeEdit());
					storedTasksByPriority.add(nextAction.getTaskBeforeEdit());
					storedTasksByDeadline.add(nextAction.getTaskBeforeEdit());
				} else {
					throw new IllegalArgumentException("The tasks must be TIME, PRIORITY or KIV");
				}
				return FEEDBACK_SUCCESSFUL_REDO;
			}
		} 
		
		return FEEDBACK_UNSUCCESSFUL_REDO;
	}
	
	public void add(Event eventAction) {
		undoStack.push(eventAction);
		redoStack.clear();
	}
	
	//Singleton
	public static UndoManager getInstance(Vector<Task> storedTasksByPriority, Vector<Task> storedTasksByDeadline, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByKIV) {
		if(theOne == null) {
			theOne = new UndoManager(storedTasksByPriority, storedTasksByDeadline, storedTasksByTicked, storedTasksByKIV);
		}
		return theOne;
	}
	
	public void clearStateForTesting() {
		theOne = null;
	}
}
