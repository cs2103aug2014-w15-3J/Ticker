package tickerPackage;

public class DeadlineTask extends Task {
	Date endDate;
	Time endTime;
	
	DeadlineTask(String description, Date endDate, Time endTime) {
		super(description);
		this.endDate = endDate;
		this.endTime = endTime;
	}
}
