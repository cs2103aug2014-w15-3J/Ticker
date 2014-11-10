package ticker.common;

//@author A0114535M
public class TimedTask extends Task {
	// CONSTANTS
	private static final int EQUAL = 0;
	private static final int SMALLER = -1;
	private static final String FROM = " from";
	private static final String SPACE = " ";
	private static final String COMMA = ", ";
	private static final String TO = " to";

	// ATTRIBUTES
	public final int id = 2; // used in storage

	public TimedTask(String description, Date startDate, Time startTime,
			Date endDate, Time endTime, char priority, boolean isRepeating) {
		super(description, startDate, startTime, endDate, endTime, priority,
				isRepeating);
	}
	
	/**
	 * This method deep copies a TimedTask.
	 */
	public TimedTask copy() {
		return new TimedTask(this.getDescription(), this.getStartDate(),
				this.getStartTime(), this.getEndDate(), this.getEndTime(),
				this.getPriority(), this.getRepeat());
	}
	
	/**
	 * This method checks for task expiry.
	 */
	public void isExpired() {
		if (this.endDate != null
				&& this.endTime != null
				&& (this.endDate.compareTo(Date.getCurrentDate()) == SMALLER || (this.endDate
						.compareTo(Date.getCurrentDate()) == EQUAL && this.endTime
						.compareTo(Time.getCurrentTime()) == SMALLER))) {
			this.isExpired = true;
			return;
		}
		this.isExpired = false;
		return;
	}

	// @author A0115369B
	/**
	 * This method prints the TimedTask.
	 */
	@Override
	public String toString(){
		String timing = new String();
		
		if (!(getStartTime() == null&&getStartDate() == null)){
			timing += FROM;
		}
		
		if (getStartDate() != null){
			timing += SPACE;
			timing += getStartDate();
		}
		
		if (getStartTime() != null){
			timing += COMMA;
			timing += getStartTime();
		}
		
		if(!(getEndTime() == null&&getEndDate() == null)){
			timing += TO;
		}
		
		if (getEndDate() != null){
			timing += SPACE;
			timing += getEndDate();
		}
		
		if (getEndTime() != null){
			timing += COMMA;
			timing += getEndTime();
		}
		
		return getDescription() + timing; 
	}
}
