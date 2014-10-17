package ticker.logic;

import ticker.common.Task;

public class Event {
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_TICK = "tick";
	private static final String COMMAND_CMI = "cmi";
	
	private String command;
	private Task taskBeforeEdit, taskAfterEdit;
	private int listTypeBefore, listTypeAfter;
	
	public Event(String command, Task task) {
		taskBeforeEdit = task;
		this.command = command;
		
		if(!(command.equals(COMMAND_ADD)) && !(command.equals(COMMAND_DELETE))) {
			throw new IllegalArgumentException("This constructor is for add or delete command");
		}
	}
	
	//Called when the command is edit
	public Event(String command, Task taskBeforeEdit, Task taskAfterEdit) {
		this.command = command;
		
		if(!command.equals(COMMAND_EDIT)) {
			throw new IllegalArgumentException("This constructor is for edit command");
		}
		
		this.taskBeforeEdit = taskBeforeEdit;
		this.taskAfterEdit = taskAfterEdit;
	}
	
	//Called when the command is ticked or cmi
	public Event(String command, Task task, int listTypeInit, int listTypeAfter) {
		this.command = command;
		
		if(!(command.equals(COMMAND_TICK)) && !(command.equals(COMMAND_CMI))) {
			throw new IllegalArgumentException("This constructor is for cmi or tick command");
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
	
	public int getListTypeBefore() {
		return listTypeBefore;
	}
	
	public int getListTypeAfter() {
		return listTypeAfter;
	}
	
}
