package ticker.main;

public class RepeatingTask extends Task {
	// TODO: how is date implemented in Task
	Date date;
	Time startTime;
	Time endTime;
	
	public RepeatingTask(String description, Date date, Time startTime, Time endTime) {
		super(description);
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public Date getDate() {
		return date;
	}
	
	public Time getStartTime() {
		return startTime;
	}
	
	public Time getEndTime() {
		return endTime;
	}
}
