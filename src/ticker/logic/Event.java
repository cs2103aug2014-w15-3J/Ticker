package ticker.logic;

import tickerPackage.Task;

public class Event {
	final private String COMMAND_ADD = "add";
	final private String COMMAND_DELETE = "delete";
	final private String COMMAND_EDIT = "edit";
	
	private String commandActual, commandGenerated;
	private Task taskBeforeEdit, taskAfterEdit;
	
	public Event(String command, Task task) {
		taskBeforeEdit = task;
		commandActual = command;
		commandGenerated = generateCommand(command);
	}
	
	public Event(String command, Task taskBeforeEdit, Task taskAfterEdit) {
		taskBeforeEdit = this.taskBeforeEdit;
		taskAfterEdit = this.taskAfterEdit;
		commandActual = command;
		try{
			commandGenerated = generateCommand(command);
		} catch (IllegalArgumentException iae){
			//TODO: to be added
		}
	}

	private String generateCommand(String command) throws IllegalArgumentException{
		//TODO: to be changed to follow convention
		switch (command) {
		case COMMAND_ADD:
			return COMMAND_DELETE;
		case COMMAND_DELETE:
			return COMMAND_ADD;
		case COMMAND_EDIT:
			return COMMAND_EDIT;
		default:
			throw new IllegalArgumentException("invalid command");
		}
	}
	
	public String getCommandActual() {
		return commandActual;
	}
	
	public String getCommandGenerated() {
		return commandGenerated;
	}
	
	public Task getTaskBeforeEdit() {
		return taskBeforeEdit;
	}
	
	public Task getTaskAfterEdit() {
		return taskAfterEdit;
	}
	
}
