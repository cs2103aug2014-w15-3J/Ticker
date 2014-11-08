package ticker.common;

public class RepeatingTask extends Task {
	final int id = 3; //used in storage
	private static final int SUNDAY = 0;
	private static final int MONDAY = 1;
	private static final int TUESDAY = 2;
	private static final int WEDNESDAY = 3;
	private static final int THURSDAY = 4;
	private static final int FRIDAY = 5;
	private static final int SATURDAY = 6;
	
	private int day;

	public RepeatingTask(String description, Date date, Time startTime, Time endTime, char priority, boolean isRepeating) {
		super(description, date, startTime, null, endTime, priority, isRepeating);
		this.day = Date.dayOfWeek(date);
		System.out.println("created repeating task");
	}
	
	public RepeatingTask copy() {
		return new RepeatingTask(this.getDescription(), this.getStartDate(), this.getStartTime(), this.getEndTime(), this.getPriority(), this.getRepeat());
	}
	
	public int getDay() { // 0: Sunday, 1: Monday, ... 6: Saturday
		return day;
	}
	
	@Override
	public void isExpired() {
		if (this.endDate != null && this.endTime != null && (this.endDate.compareTo(Date.getCurrentDate()) == -1 || 
				(this.endDate.compareTo(Date.getCurrentDate()) == 0 && this.endTime.compareTo(Time.getCurrentTime()) == -1))) {
			this.isExpired = true;
			return;
		}
		this.isExpired = false;
		return;
	}
	
	@Override
	// TODO: error in printing time for startTime + endTime, endTime only
	public String toString(){
		String timing = "";
		
		if(getStartTime()!=null&&getEndTime()!=null){
			timing = "(from " + getStartTime() + " to " + getEndTime() + ") ";
		}
		
		else if (getStartTime()!=null){
			timing = "(start at " + getStartTime() + ") ";
		}
		
		else if (getEndTime()!=null){
			timing = "(end at " + getEndTime() + ") ";
		}		
		
		System.out.println("print repeating");
		
		switch (this.day) {
		case SUNDAY:
			return "<Sunday> " + timing + getDescription();
		case MONDAY:
			return "<Monday> " + timing + getDescription();
		case TUESDAY:
			return "<Tueday> " + timing + getDescription();
		case WEDNESDAY:
			return "<Wednesday> " + timing + getDescription();
		case THURSDAY:
			return "<Thursday> " + timing + getDescription();
		case FRIDAY:
			return "<Friday> " + timing + getDescription();
		case SATURDAY:
			return "<Saturday> " + timing + getDescription();
		default:
			return "Error in getting Day for RepeatingTask";
		}
	}
}
