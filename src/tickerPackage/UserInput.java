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
	
	public UserInput(String command,String description){
		this.command=command;
		this.description=description;
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

	void validifyTime(){
		if (endTime!=null&&endDate==null){
			if (startTime==null&&startDate==null){
				endDate = Date.getCurrentDate();
			}
			else if (startTime!=null&&startDate==null){
				startDate = endDate = Date.getCurrentDate();
			}
			else if (startTime!=null&&startDate!=null){
				endDate = startDate;
			}
		}
		
		else if (endTime!=null){
			if (startTime!=null&&startDate==null){
				startDate = endDate;
			}
			else if (startTime==null&&startDate!=null){
				startTime = new Time(0,0);
			}
		}
		
		else {
			if (endDate == null&&startTime!=null&&startDate==null){
				startDate=Date.getCurrentDate();
			}
			else if (endDate != null&&startTime!=null&&startDate!=null){
				endTime = new Time(23,59);
			}
		}
		
	}

}
