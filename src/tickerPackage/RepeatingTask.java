package tickerPackage;

public class RepeatingTask extends Task {
	// TODO: how is date implemented in Task
	final int id = 3; //used in storage
	Date date;

	public RepeatingTask(String description, Date date, Time startTime, Time endTime, int priority, boolean isRepeating) {
		super(description, date, startTime, null, endTime, priority, isRepeating);
		this.date = date;
		System.out.println("created repeating task");
	}
	
	public Date getDate() {
		return date;
	}
	
	@Override
	// TODO: error in printing time for startTime + endTime, endTime only
	public String toString(){
		String timing = "";
		String temp = "";
		
		if(getStartTime()!=null&&getEndTime()!=null){
			temp = " from " + getStartTime() + " to " + getEndTime() + ", ";
		}
		
		else if (getStartTime()!=null){
			temp = " start at " + getStartTime() + ", ";
		}
		
		else if (getEndTime()!=null){
			temp = " end at " + getEndTime() + ", ";
		}
		
		timing += temp + " ";
		
		timing += getDate();		
		
		System.out.println("print repeating");
		return "<repeating> (" + timing + ") " + getDescription(); 
	}
}
