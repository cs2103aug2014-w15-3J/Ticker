package ticker.common;

public class Task {
	protected String description;
	protected Date startDate;
	protected Time startTime;
	protected Date endDate;
	protected Time endTime;
	protected char priority;
	protected boolean isRepeating;
	protected boolean isExpired;

	public Task(String description, Date startDate, Time startTime, Date endDate, Time endTime, 
			char priority, boolean isRepeating) {
		this.description = description;
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
		this.priority = priority;
		this.isRepeating = isRepeating;
	}
	
	public Task copy() {
		return new Task(this.getDescription(), this.getStartDate(), this.getStartTime(), this.getEndDate(),
				this.getEndTime(), this.getPriority(), this.getRepeat());
	}

	public String toString() {
		return description;
	}

	// Getters
	public String getDescription() {
		return description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Time getStartTime() {
		return startTime;
	}

	public Time getEndTime() {
		return endTime;
	}
	
	public char getPriority() {
		return priority;
	}
	
	public boolean getRepeat() {
		return isRepeating;
	}

	// Setters
	public void setDescription(String input) {
		description = input;
	}

	public void setStartDate(Date input) {
		startDate = input;
	}

	public void setEndDate(Date input) {
		endDate = input;
	}

	public void setStartTime(Time input) {
		startTime = input;
	}

	public void setEndTime(Time input) {
		endTime = input;
	}
	
	public void setPriority(char input) {
		priority = input;
	}
	
	public void setRepeat(boolean input) {
		isRepeating = input;
	}
	
	/*public void isExpired() {
		isExpired = false;
	}*/

	public boolean equals(Object obj) {
		

		if (obj instanceof Task) {
			Task myTask = (Task) obj; 
			if (myTask.getRepeat()!=this.getRepeat()) {
				return false;
			}
			
			if (!myTask.getDescription().equals(this.getDescription())){ 
				return false;
			}
			
			if (this.getPriority()!=myTask.getPriority()) {
				return false;
			}
			
			if ((this.getStartTime()==null&&myTask.getStartTime()!=null)||
				(this.getStartTime()!=null&&myTask.getStartTime()==null)||
				(this.getStartTime()!=null)&&myTask.getStartTime()!=null&&this.getStartTime().equals(myTask.getStartTime())){
				return false;
			}
			
			if ((this.getEndTime()==null&&myTask.getEndTime()!=null)||
				(this.getEndTime()!=null&&myTask.getEndTime()==null)||
				(this.getEndTime()!=null)&&myTask.getEndTime()!=null&&this.getEndTime().equals(myTask.getEndTime())){
				return false;
			}
			
			if ((this.getStartDate()==null&&myTask.getStartDate()!=null)||
				(this.getStartDate()!=null&&myTask.getStartDate()==null)||
				(this.getStartDate()!=null)&&myTask.getStartDate()!=null&&this.getStartDate().equals(myTask.getStartDate())){
				return false;
			}
			
			if ((this.getEndDate()==null&&myTask.getEndDate()!=null)||
				(this.getEndDate()!=null&&myTask.getEndDate()==null)||
				(this.getEndDate()!=null)&&myTask.getEndDate()!=null&&this.getEndDate().equals(myTask.getEndDate())){
				return false;
			}
		}
		else return false;
		
		return true;
	}
}