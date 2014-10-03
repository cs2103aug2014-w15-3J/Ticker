package tickerPackage;

public class DeadlineTask extends Task {
	Date endDate;
	Time endTime;
	
	public DeadlineTask(String description, Date endDate, Time endTime) {
		super(description);
		this.endDate = endDate;
		this.endTime = endTime;
	}
	
	public Time getEndTime() {
		return endTime;
	}
	
	public Date getEndDate() {
		return endDate;
	}
}
