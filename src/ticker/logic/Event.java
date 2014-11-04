package ticker.logic;

import ticker.common.Task;

public class Event {
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_TICK = "tick";
	private static final String COMMAND_KIV = "kiv";
	private static final String COMMAND_UNKIV = "unkiv";
	private static final String COMMAND_UNTICK = "untick";
	
	private String command;
	private Task taskBeforeEdit, taskAfterEdit;
	private String listTypeBefore, listTypeAfter;
	private int indexBefore;
	
	//Called when the command is add
	public Event(String command, Task task) {
		taskBeforeEdit = task;
		this.command = command;
		
		if(!(command.equals(COMMAND_ADD))) {
			throw new IllegalArgumentException("This constructor is for add or delete command");
		}
	}
	
	//Called when the command is delete
	public Event(String command, Task task, String listTypeBefore, int indexBefore) {
		this.command = command;
		
		if(!(command.equals(COMMAND_DELETE))) {
			throw new IllegalArgumentException("This constructor is for delete command");
		}
		
		taskBeforeEdit = task;
		this.listTypeBefore = listTypeBefore;
		this.indexBefore = indexBefore;
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
	
	//Called when the command is ticked or kiv or untick or unkiv
	public Event(String command, Task task, String listTypeBefore, String listTypeAfter) {
		this.command = command;
		
		if(!(command.equals(COMMAND_TICK)) && !(command.equals(COMMAND_KIV)) && !(command.equals(COMMAND_UNTICK)) && !(command.equals(COMMAND_UNKIV))) {
			throw new IllegalArgumentException("This constructor is for kiv or tick or unkiv or untick command");
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
