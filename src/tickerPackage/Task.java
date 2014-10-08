package tickerPackage;

public class Task {
	protected String description;
	protected Date startDate;
	protected Time startTime;
	protected Date endDate;
	protected Time endTime;
	protected int priority;

	public Task(String description, Date startDate, Time startTime, Date endDate, Time endTime, int priority) {
		this.description = description;
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
		this.priority = priority;
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
	
	public int getPriority() {
		return priority;
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
	
	public void setPriority(int input) {
		priority = input;
	}

}