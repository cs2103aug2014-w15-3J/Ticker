package tickerPackage;

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
}
