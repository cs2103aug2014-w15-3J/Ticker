package ticker.common;

public class TimedTask extends Task {
	final int id = 2; //used in storage
	
	public TimedTask(String description, Date startDate, Time startTime, Date endDate, Time endTime, char priority, boolean isRepeating) {
		super(description, startDate, startTime, endDate, endTime, priority, isRepeating);
	}
	
	public TimedTask copy() {
		return new TimedTask(this.getDescription(), this.getStartDate(), this.getStartTime(), this.getEndDate(),
				this.getEndTime(), this.getPriority(), this.getRepeat());
	}
	
	public void isExpired() {
		if (this.endDate.compareTo(Date.getCurrentDate()) == 1 && this.endTime.compareTo(Time.getCurrentTime()) == 1) {
			this.isExpired = true;
			return;
		}
		this.isExpired = false;
		return;
	}
	
	@Override 
	public String toString(){
		String timing = "";
		
		System.out.println("got into floating");
		
		/*
		if(getStartTime()!=null&&getEndTime()!=null&&getStartDate()!=null&&getEndTime()!=null){
			temp = " from " + getStartDate() + ", " + getStartTime() + " to " + getEndDate() + ", " + getEndTime();
		}
		
		else if (getStartTime()!=null&&getStartDate()!=null&&getEndTime()!=null){
			temp = " start at " + getStartDate() + ", " + getStartTime() + "to " + getEndTime();
		}
		
		else if (getStartTime()!=null&&getStartDate()!=null&&getEndDate()!=null){
			temp = " start at " + getStartDate() + ", " + getStartTime() + "to " + getEndDate();
		}
		
		else if (getStartDate()!=null&&getStartTime()!=null){
			temp = " start at " + getStartDate() + ", " + getStartTime();
		}
		
		else if (getStartDate()!=null) {
			temp = " start at " + getStartDate();
		}
		*/
		
		timing += (!(getStartTime()==null&&getStartDate()==null))?" from":"";
		timing += (getStartDate()==null)?"":" "+getStartDate();
		timing += (getStartTime()==null)?"":", "+getStartTime();
		timing += (!(getEndTime()==null&&getEndDate()==null))?" to":"";
		timing += (getEndDate()==null)?"":" "+getEndDate();
		timing += (getEndTime()==null)?"":", "+getEndTime();
		
		return getDescription() + timing; 
	}
}
