package ticker.logic;

import ticker.common.Task;

//@author A0116673A

/**
 * This class models a user's input command event: record down the command
 * carried out and the task created. This is to be used together with
 * UndoManager.
 *
 */
public class Event {
	// A list of available commands
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_TICK = "tick";
	private static final String COMMAND_KIV = "kiv";
	private static final String COMMAND_UNKIV = "unkiv";
	private static final String COMMAND_UNTICK = "untick";
	private static final String COMMAND_TAKE = "take";

	// These messages are shown during exceptions
	private static final String EXCEPTION_TICKKIV = "This constructor is for kiv or tick or unkiv or untick command";
	private static final String EXCEPTION_EDIT = "This constructor is for edit command";
	private static final String EXCEPTION_DELETE = "This constructor is for delete command";
	private static final String EXCEPTION_ADDTAKE = "This constructor is for add or take command";

	private String command;
	private Task taskBeforeEdit, taskAfterEdit;
	private String listTypeBefore, listTypeAfter;
	private int indexBefore;

	// Called when the command is add and take
	public Event(String command, Task task) {
		taskBeforeEdit = task;
		this.command = command;

		if (!(command.equals(COMMAND_ADD)) && !(command.equals(COMMAND_TAKE))) {
			throw new IllegalArgumentException(EXCEPTION_ADDTAKE);
		}
	}

	// Called when the command is delete
	public Event(String command, Task task, String listTypeBefore,
			int indexBefore) {
		this.command = command;

		if (!(command.equals(COMMAND_DELETE))) {
			throw new IllegalArgumentException(EXCEPTION_DELETE);
		}

		taskBeforeEdit = task;
		this.listTypeBefore = listTypeBefore;
		this.indexBefore = indexBefore;
	}

	// Called when the command is edit
	public Event(String command, Task taskBeforeEdit, Task taskAfterEdit) {
		this.command = command;

		if (!command.equals(COMMAND_EDIT)) {
			throw new IllegalArgumentException(EXCEPTION_EDIT);
		}

		this.taskBeforeEdit = taskBeforeEdit;
		this.taskAfterEdit = taskAfterEdit;
	}

	// Called when the command is ticked or kiv or untick or unkiv
	public Event(String command, Task task, String listTypeBefore,
			String listTypeAfter) {
		this.command = command;

		if (!(command.equals(COMMAND_TICK)) && !(command.equals(COMMAND_KIV))
				&& !(command.equals(COMMAND_UNTICK))
				&& !(command.equals(COMMAND_UNKIV))) {
			throw new IllegalArgumentException(EXCEPTION_TICKKIV);
		}

		taskBeforeEdit = task;
		this.listTypeBefore = listTypeBefore;
		this.listTypeAfter = listTypeAfter;
	}

	public String getCommand() {
		return command;
	}

	public Task getTaskBeforeEdit() {
		return taskBeforeEdit;
	}

	public Task getTaskAfterEdit() {
		return taskAfterEdit;
	}

	public String getListTypeBefore() {
		return listTypeBefore;
	}

	public String getListTypeAfter() {
		return listTypeAfter;
	}

	public int getIndexBefore() {
		return indexBefore;
	}

}
