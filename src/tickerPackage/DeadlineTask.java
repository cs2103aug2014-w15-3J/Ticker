package tickerPackage;

public class DeadlineTask extends Task {
	final int id = 4; //used in storage
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

	public String toString(){
		
		String timing = " deadline ";
		
		if (getEndTime()!=null)
			timing += getEndTime();
		
		timing += getEndDate();
		
		return description + timing;
	}
}
