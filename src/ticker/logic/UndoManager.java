package ticker.logic;

import java.util.Stack;
import java.util.Vector;

import ticker.common.Task;

//@author A0116673A

public class UndoManager {
	//These message will be shown during exception
	private static final String MESSAGE_EXCEPTION_DELETE = "The tasks must be TIME, TICKED, PRIORITY or KIV";
	private static final String MESSAGE_EXCEPTION_TICKUNTICK = "The tasks must be TIME, PRIORITY OR TICKED";
	private static final String MESSAGE_EXCEPTION_KIVUNKIV = "The tasks must be TIME, PRIORITY or KIV";
	
	//list of commands
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_TICK = "tick";
	private static final String COMMAND_KIV = "kiv";
	private static final String COMMAND_UNKIV = "unkiv";
	private static final String COMMAND_UNTICK = "untick";
	private static final String COMMAND_TAKE = "take";
	
	private static final String LIST_TIME = "time";
	private static final String LIST_PRIORITY = "priority";
	private static final String LIST_TICKED = "ticked";
	private static final String LIST_KIV = "kiv";
	
	//list of feedbacks
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
	
	/**
	 * Undo the previous command carried out by user. 
	 * 
	 * @return void
	 */
	public String undo() {
		if(!undoStack.isEmpty()) {
			Event previousAction = undoStack.pop();
			redoStack.push(previousAction);
			
			switch(previousAction.getCommand()) {
				case COMMAND_EDIT:
					assert previousAction.getTaskAfterEdit() != null && previousAction.getTaskBeforeEdit() != null;
					return undoEditCommand(previousAction);
				case COMMAND_ADD:
				case COMMAND_TAKE:
					assert previousAction.getTaskBeforeEdit() != null;
					return undoAddTakeCommand(previousAction);
				case COMMAND_DELETE:
					assert previousAction.getTaskBeforeEdit() != null && previousAction.getListTypeBefore() != null;
					return undoDeleteCommand(previousAction);
				case COMMAND_TICK:
				case COMMAND_UNTICK:
					assert previousAction.getListTypeBefore() != null && previousAction.getTaskBeforeEdit() != null;
					return undoTickUntickCommand(previousAction);
				case COMMAND_KIV:
				case COMMAND_UNKIV:
					assert previousAction.getListTypeBefore() != null && previousAction.getTaskBeforeEdit() != null;
					return undoKIVUnkivCommand(previousAction);
			}
		}
		return FEEDBACK_UNSUCCESSFUL_UNDO; 
		
	}
	
	/**
	 * Redo the command carried out by the user.
	 * @return void
	 */
	public String redo() {
		if(!redoStack.isEmpty()) {
			Event nextAction = redoStack.pop();
			undoStack.push(nextAction);
			
			switch(nextAction.getCommand()) {
			case COMMAND_EDIT:
				assert nextAction.getTaskAfterEdit() != null && nextAction.getTaskBeforeEdit() != null;
				return redoEditCommand(nextAction);
			case COMMAND_ADD:
			case COMMAND_TAKE:
				assert nextAction.getTaskBeforeEdit() != null;
				return redoAddTakeCommand(nextAction);
			case COMMAND_DELETE:
				assert nextAction.getTaskBeforeEdit() != null;
				return redoDeleteCommand(nextAction);
			case COMMAND_TICK:
			case COMMAND_UNTICK:
				assert nextAction.getTaskBeforeEdit() != null && nextAction.getListTypeBefore() != null;
				return redoTickUntickCommand(nextAction);
			case COMMAND_KIV:
			case COMMAND_UNKIV:
				assert nextAction.getTaskBeforeEdit() != null && nextAction.getListTypeBefore() != null;
				return redoKIVUnkivCommand(nextAction);
			}
		} 
		
		return FEEDBACK_UNSUCCESSFUL_REDO;
	}
	
	/**
	 * Add user's input event into a stack for undo/redo purposes.
	 * 
	 * @param eventAction user's input event
	 */
	public void add(Event eventAction) {
		undoStack.push(eventAction);
		redoStack.clear();
	}

	/**
	 * Singleton constructor for the UndoManager.
	 * @param storedTasksByPriority	list of tasks sorted by priority
	 * @param storedTasksByDeadline list of tasks sorted by deadline
	 * @param storedTasksByTicked 	list of tasks sorted by tick
	 * @param storedTasksByKIV		list of tasks sorted by KIV
	 * @return	UndoManager			the UndoManager object
	 */
	public static UndoManager getInstance(Vector<Task> storedTasksByPriority, Vector<Task> storedTasksByDeadline, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByKIV) {
		if(theOne == null) {
			theOne = new UndoManager(storedTasksByPriority, storedTasksByDeadline, storedTasksByTicked, storedTasksByKIV);
		}
		return theOne;
	}
	
	/**
	 * Undo KIV or UNKIV command
	 * 
	 * @param previousAction user's input event
	 * @return				 successful feedback message
	 */
	private String undoKIVUnkivCommand(Event previousAction) {
		if(previousAction.getListTypeBefore().equals(LIST_TIME) || previousAction.getListTypeBefore().equals(LIST_PRIORITY)) {
			storedTasksByKIV.remove(previousAction.getTaskBeforeEdit());
			redoAddTakeCommand(previousAction);
		} else if(previousAction.getListTypeBefore().equals(LIST_KIV)) {
			storedTasksByKIV.add(previousAction.getTaskBeforeEdit());
			undoAddTakeCommand(previousAction);
		} else {
			throw new IllegalArgumentException(MESSAGE_EXCEPTION_KIVUNKIV);
		}
		return FEEDBACK_SUCCESSFUL_UNDO;
	}
	
	/**
	 * Undo Tick or Untick command
	 * @param previousAction	user's input event
	 * @return				 	successful feedback message
	 */
	private String undoTickUntickCommand(Event previousAction) {
		if(previousAction.getListTypeBefore().equals(LIST_TIME) || previousAction.getListTypeBefore().equals(LIST_PRIORITY)) {
			storedTasksByTicked.remove(previousAction.getTaskBeforeEdit());
			redoAddTakeCommand(previousAction);
		} else if(previousAction.getListTypeBefore().equals(LIST_TICKED)) {
			storedTasksByTicked.add(previousAction.getTaskBeforeEdit());
			undoAddTakeCommand(previousAction);
		} else {
			throw new IllegalArgumentException(MESSAGE_EXCEPTION_TICKUNTICK);
		}
		return FEEDBACK_SUCCESSFUL_UNDO;
	}

	/**
	 * Undo Delete command
	 * @param previousAction	user's input event
	 * @return				 	successful feedback message
	 */
	private String undoDeleteCommand(Event previousAction) {
		if(previousAction.getListTypeBefore().equals(LIST_TIME) || previousAction.getListTypeBefore().equals(LIST_PRIORITY)) {
			redoAddTakeCommand(previousAction);
		} else if (previousAction.getListTypeBefore().equals(LIST_TICKED)) {
			storedTasksByTicked.add(previousAction.getIndexBefore(), previousAction.getTaskBeforeEdit());
		} else if (previousAction.getListTypeBefore().equals(LIST_KIV)) {
			storedTasksByKIV.add(previousAction.getIndexBefore(), previousAction.getTaskBeforeEdit());
		} else {
			throw new IllegalArgumentException(MESSAGE_EXCEPTION_DELETE);
		}
		return FEEDBACK_SUCCESSFUL_UNDO;
	}
	
	/**
	 * Undo Add or Take command
	 * @param previousAction	user's input event
	 * @return				 	successful feedback message
	 */
	private String undoAddTakeCommand(Event previousAction) {
		storedTasksByPriority.remove(previousAction.getTaskBeforeEdit());
		storedTasksByDeadline.remove(previousAction.getTaskBeforeEdit());
		return FEEDBACK_SUCCESSFUL_UNDO;
	}
	
	/**
	 * Undo Edit command
	 * @param previousAction	user's input event
	 * @return				 	successful feedback message
	 */
	private String undoEditCommand(Event previousAction) {
		storedTasksByPriority.remove(previousAction.getTaskAfterEdit());
		storedTasksByPriority.add(previousAction.getTaskBeforeEdit());
		storedTasksByDeadline.remove(previousAction.getTaskAfterEdit());
		storedTasksByDeadline.add(previousAction.getTaskBeforeEdit());
		return FEEDBACK_SUCCESSFUL_UNDO;
	}

	/**
	 * Redo KIV or UNKIV command
	 * @param previousAction	user's input event
	 * @return				 	successful feedback message
	 */
	private String redoKIVUnkivCommand(Event nextAction) {
		if(nextAction.getListTypeBefore().equals(LIST_TIME) || nextAction.getListTypeBefore().equals(LIST_PRIORITY)) {
			storedTasksByKIV.add(nextAction.getTaskBeforeEdit());
			undoAddTakeCommand(nextAction);
		} else if(nextAction.getListTypeBefore().equals(LIST_KIV)) {
			storedTasksByKIV.remove(nextAction.getTaskBeforeEdit());
			redoAddTakeCommand(nextAction);
		} else {
			throw new IllegalArgumentException(MESSAGE_EXCEPTION_KIVUNKIV);
		}
		return FEEDBACK_SUCCESSFUL_REDO;
	}

	/**
	 * Redo Tick or Untick command
	 * @param previousAction	user's input event
	 * @return				 	successful feedback message
	 */
	private String redoTickUntickCommand(Event nextAction) {
		if(nextAction.getListTypeBefore().equals(LIST_TIME) || nextAction.getListTypeBefore().equals(LIST_PRIORITY)) {
			storedTasksByTicked.add(nextAction.getTaskBeforeEdit());
			undoAddTakeCommand(nextAction);
		} else if(nextAction.getListTypeBefore().equals(LIST_TICKED)) {
			storedTasksByTicked.remove(nextAction.getTaskBeforeEdit());
			redoAddTakeCommand(nextAction);
		} else {
			throw new IllegalArgumentException(MESSAGE_EXCEPTION_TICKUNTICK);
		}
		return FEEDBACK_SUCCESSFUL_REDO;
	}

	/**
	 * Redo Delete command
	 * @param previousAction	user's input event
	 * @return				 	successful feedback message
	 */
	private String redoDeleteCommand(Event nextAction) {
		if(nextAction.getListTypeBefore().equals(LIST_TIME) || nextAction.getListTypeBefore().equals(LIST_PRIORITY)) {
			undoAddTakeCommand(nextAction);
		} else if (nextAction.getListTypeBefore().equals(LIST_TICKED)) {
			storedTasksByTicked.remove(nextAction.getTaskBeforeEdit());
		} else if (nextAction.getListTypeBefore().equals(LIST_KIV)) {
			storedTasksByKIV.remove(nextAction.getTaskBeforeEdit());
		} else {
			throw new IllegalArgumentException(MESSAGE_EXCEPTION_DELETE);
		}
		return FEEDBACK_SUCCESSFUL_REDO;
	}

	/**
	 * Redo Add or Take command
	 * @param previousAction	user's input event
	 * @return				 	successful feedback message
	 */
	private String redoAddTakeCommand(Event nextAction) {
		storedTasksByPriority.add(nextAction.getTaskBeforeEdit());
		storedTasksByDeadline.add(nextAction.getTaskBeforeEdit());
		return FEEDBACK_SUCCESSFUL_REDO;
	}

	/**
	 * Redo Edit command
	 * @param previousAction	user's input event
	 * @return				 	successful feedback message
	 */
	private String redoEditCommand(Event nextAction) {
		storedTasksByPriority.add(nextAction.getTaskAfterEdit());
		storedTasksByPriority.remove(nextAction.getTaskBeforeEdit());
		storedTasksByDeadline.add(nextAction.getTaskAfterEdit());
		storedTasksByDeadline.remove(nextAction.getTaskBeforeEdit());
		return FEEDBACK_SUCCESSFUL_REDO;
	}
	
	/**
	 * Remove the Singleton instance for unit testing purposes
	 */
	protected void clearStateForTesting() {
		theOne = null;
	}
}
