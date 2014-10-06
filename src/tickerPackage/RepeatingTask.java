package tickerPackage;

public class RepeatingTask extends Task {
	// TODO: how is date implemented in Task
	final int id = 3; //used in storage
	Date date;
	Time startTime;
	Time endTime;
	
	public RepeatingTask(String description, Date date, Time startTime, Time endTime) {
		super(description);
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public Date getDate() {
		return date;
	}
	
	public Time getStartTime() {
		return startTime;
	}
	
	public Time getEndTime() {
		return endTime;
	}
	
	public String toString(){
		String timing = "";
		String temp = "";
		
		if(getStartTime()!=null&&getEndTime()!=null){
			temp = " from " + getStartTime() + " to " + getEndTime();
		}
		
		else if (getStartTime()!=null){
			temp = " start at " + getStartTime();
		}
		
		else if (getEndTime()!=null){
			temp = " end at " + getEndTime();
		}
		
		timing += temp;
		
		timing += getDate();		
				
		return "<repeating> " + description + timing; 
	}
}
