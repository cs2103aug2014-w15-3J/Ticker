package ticker.common;

public class DeadlineTask extends Task {
	final int id = 4; //used in storage
	
	public DeadlineTask(String description, Date endDate, Time endTime, char priority, boolean isRepeating) {
		super(description, null, null, endDate, endTime, priority, isRepeating);
	}
	
	@Override
	public String toString(){
		
		String timing = " deadline ";
		
		if (getEndTime()!=null) { 
			timing += getEndTime() + ", ";
		}
		
		timing += getEndDate();
		
		return getDescription() + timing;
	}
}
