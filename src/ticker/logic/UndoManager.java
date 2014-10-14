package ticker.logic;

import java.util.Stack;
import java.util.Vector;

import tickerPackage.Task;

public class UndoManager {
	final private String COMMAND_ADD = "add";
	final private String COMMAND_DELETE = "delete";
	final private String COMMAND_EDIT = "edit";
	
	private static UndoManager theOne;
	private Stack<Event> undoStack, redoStack;
	private Vector<Task> tasks;
	
	private UndoManager(Vector<Task> tasks) {
		undoStack = new Stack<Event>();
		redoStack = new Stack<Event>();
		tasks = this.tasks;
	}
	
	public void undo() {
		if(!undoStack.isEmpty()) {
			Event previousAction = undoStack.pop();
			redoStack.push(previousAction);
			if(previousAction.getCommandActual().equals(COMMAND_EDIT)) {
				tasks.remove(previousAction.getTaskAfterEdit());
				tasks.add(previousAction.getTaskBeforeEdit());
			} else if(previousAction.getCommandActual().equals(COMMAND_ADD)) {
				tasks.remove(previousAction.getTaskBeforeEdit());
			} else if(previousAction.getCommandActual().equals(COMMAND_DELETE)) {
				tasks.add(previousAction.getTaskBeforeEdit());
			} else {
				//TODO:
				throw new IllegalArgumentException();
			}	
		} else {
			//TODO: exception
			return;
		}
	}
	
	public void redo() {
		if(!undoStack.isEmpty()) {
			Event nextAction = redoStack.pop();
			undoStack.push(nextAction);
			if(nextAction.getCommandActual().equals(COMMAND_EDIT)) {
				tasks.remove(nextAction.getTaskAfterEdit());
				tasks.add(nextAction.getTaskBeforeEdit());
			} else if(nextAction.getCommandActual().equals(COMMAND_ADD)) {
				tasks.remove(nextAction.getTaskBeforeEdit());
			} else if(nextAction.getCommandActual().equals(COMMAND_DELETE)) {
				tasks.add(nextAction.getTaskBeforeEdit());
			} else {
				//TODO:
				throw new IllegalArgumentException();
			}	
		} else {
			//TODO: exception
			return;
		}
	}
	
	public void add(Event eventAction) {
		undoStack.push(eventAction);
		redoStack.clear();
	}
	
	//Singleton
	public static UndoManager getInstance(Vector<Task> tasks) {
		if(theOne == null) {
			theOne = new UndoManager(tasks);
		}
		return theOne;
	}
}
