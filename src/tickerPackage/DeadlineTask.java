package tickerPackage;

public class DeadlineTask extends Task {
	final int id = 4; //used in storage
	
	public DeadlineTask(String description, Date endDate, Time endTime, int priority) {
		super(description, null, null, endDate, endTime, priority);
	}
	
	public String toString(){
		
		String timing = " deadline ";
		
		if (getEndTime()!=null)
			timing += getEndTime();
		
		timing += getEndDate();
		
		return getDescription() + timing;
	}
}
