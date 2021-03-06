package ticker.main;

public class TimedTask extends Task {
	Date startDate;
	Time startTime;
	Date endDate;
	Time endTime;
	
	public TimedTask(String description, Date startDate, Time startTime, Date endDate, Time endTime) {
		super(description);
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
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
}
