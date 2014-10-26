package ticker.common;

public class DeadlineTask extends Task {
	final int id = 4; //used in storage
	
	public DeadlineTask(String description, Date endDate, Time endTime, char priority, boolean isRepeating) {
		super(description, null, null, endDate, endTime, priority, isRepeating);
	}
	
	public DeadlineTask copy() {
		return new DeadlineTask(this.getDescription(), this.getEndDate(),	this.getEndTime(), this.getPriority(), 
				this.getRepeat());
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
