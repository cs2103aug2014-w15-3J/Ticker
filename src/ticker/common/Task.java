package ticker.common;

public class Task {
	protected String description;
	protected Date startDate;
	protected Time startTime;
	protected Date endDate;
	protected Time endTime;
	protected char priority;
	protected boolean isRepeating;

	public Task(String description, Date startDate, Time startTime, Date endDate, Time endTime, char priority, boolean isRepeating) {
		this.description = description;
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
		this.priority = priority;
		this.isRepeating = isRepeating;
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

	public boolean equals(Object obj) {
		if (obj instanceof Task) {
			Task myTask = (Task) obj;
			if (this.getStartDate() == null && this.getEndDate() == null && this.getStartTime() == null && this.getStartDate() == null) {
				return this.getDescription().equals(myTask.getDescription()) && this.getPriority() == myTask.getPriority() 
						&& this.getRepeat() == myTask.getRepeat();
			} else if (this.getStartDate() == null && this.getEndDate() == null && this.getStartTime() == null) {
				return this.getDescription().equals(myTask.getDescription()) && this.getPriority() == myTask.getPriority() 
						&& this.getRepeat() == myTask.getRepeat() && this.getStartDate().equals(myTask.getStartDate());
			} else if (this.getStartDate() == null && this.getEndDate() == null) {
				return this.getDescription().equals(myTask.getDescription()) && this.getPriority() == myTask.getPriority() 
						&& this.getRepeat() == myTask.getRepeat() && this.getStartDate().equals(myTask.getStartDate()) && this.getStartTime().equals(myTask.getStartTime());
			} else if (this.getStartDate() == null) {
				return this.getDescription().equals(myTask.getDescription()) && this.getPriority() == myTask.getPriority() 
						&& this.getRepeat() == myTask.getRepeat() && this.getStartDate().equals(myTask.getStartDate()) && this.getStartTime().equals(myTask.getStartTime()) 
						&& this.getEndDate().equals(myTask.getEndDate());
			} else {
				return this.getDescription().equals(myTask.getDescription()) && this.getPriority() == myTask.getPriority() 
						&& this.getRepeat() == myTask.getRepeat() && this.getStartDate().equals(myTask.getStartDate()) && this.getStartTime().equals(myTask.getStartTime()) 
						&& this.getEndDate().equals(myTask.getEndDate()) && this.getEndTime().equals(myTask.getEndTime());
			}
			

		} else {
			return false;
		}
	}
}