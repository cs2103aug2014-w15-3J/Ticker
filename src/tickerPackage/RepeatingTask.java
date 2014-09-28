package tickerPackage;

public class RepeatingTask extends Task {
	// TODO: how is date implemented in Task
	Date day;
	Time startTime;
	Time endTime;
	
	RepeatingTask(String description, Date day, Time startTime, Time endTime) {
		super(description);
		this.day = day;
		this.startTime = startTime;
		this.endTime = endTime;
	}
}
