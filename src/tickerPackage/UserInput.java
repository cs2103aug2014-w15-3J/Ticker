package tickerPackage;

public class UserInput {
	
	public String command;
	public String description;
	public Time startTime;
	public Time endTime;
	public Date endDate;
	public Date startDate;
	public boolean isAppendingRepeating;
	public int index;
	public int priority;
	
	// TODO: instantiate UserInput()
	public UserInput(){
		
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Time getStartTime() {
		return startTime;
	}
	
	public Time getEndTime() {
		return endTime;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public boolean getRepeating() {
		return isAppendingRepeating;
	}
	
	public boolean getAppending() {
		return isAppendingRepeating;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getPriority() {
		return priority;
	}

}
