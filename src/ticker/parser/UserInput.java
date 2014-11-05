package ticker.parser;
import ticker.common.Date;
import ticker.common.Time;
import ticker.common.Task.RepeatingInterval;
//@author  A0115369B
public class UserInput {
	
	private String command;
	private String description;
	private Time startTime;
	private Time endTime;
	private Date endDate;
	private Date startDate;
	private boolean isRepeating;
	private int index;
	private char priority;
	protected RepeatingInterval repeatingInterval;
	
	// TODO: instantiate UserInput()
	public UserInput(){
	}
	
	public UserInput(CMD cmd,String description){
		this.setCommand(cmd.toString());
		this.setDescription(description);
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Time getStartTime() {
		return startTime;
	}
	
	public Time getEndTime() {
		return endTime;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setStartTime(Time time){
		this.startTime=time;
	}
	
	public void setEndTime(Time time){
		this.endTime=time;
	}
	
	public void setStartDate(Date date){
		this.startDate=date;
	}
	
	public void setEndDate(Date date){
		this.endDate=date;
	}
	
	public boolean getRepeating() {
		return isRepeating();
	}
	
	public boolean getAppending() {
		return isRepeating();
	}
	
	public int getIndex() {
		return index;
	}
	
	public char getPriority() {
		return priority;
	}

	void validifyTime(){
		if (endTime!=null&&endDate==null){
			if (startTime==null&&startDate==null){
				endDate = Date.getCurrentDate();
			}
			else if (startTime!=null&&startDate==null){
				startDate = endDate = Date.getCurrentDate();
			}
			else if (startTime!=null&&startDate!=null){
				endDate = startDate;
			}
		}
		
		else if (endTime!=null){
			if (startTime!=null&&startDate==null){
				startDate = endDate;
			}
			else if (startTime==null&&startDate!=null){
				startTime = new Time(0,0);
			}
		}
		
		//endTime == null
		else {
			if (endDate == null&&startTime!=null&&startDate==null){
				startDate=Date.getCurrentDate();
			}
			else if (endDate != null&&startTime!=null&&startDate!=null){
				endTime = new Time(23,59);
			}
			else if (startTime==null&&startDate!=null&&endDate!=null){
				startTime = new Time(0,0);
				endTime = new Time(23,59);
			}
			else if (startTime==null&&startDate!=null&&endDate==null){
				startTime = new Time(0,0);
			}
			else if (startDate==null&&endDate!=null){
				if (startTime==null){
					endTime = new Time (23,59);
				}
				else {
					startDate=endDate;
				}
			}
		}
	}

	public void setPriority(char priority) {
		this.priority = priority;
	}

	public boolean isRepeating() {
		return isRepeating;
	}

	public void setRepeating(boolean isRepeating) {
		this.isRepeating = isRepeating;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setRepeatingInterval(RepeatingInterval ri) {
		this.repeatingInterval = ri;
	}
	
	public RepeatingInterval getRepeatingInterval() {
		return this.repeatingInterval;
	}

}
