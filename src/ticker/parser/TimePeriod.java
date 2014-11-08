package ticker.parser;
//@author A0115369B
import ticker.common.Date;
import ticker.common.DateTime;
import ticker.common.Time;

public class TimePeriod{
	private DateTime start;
	private DateTime end;
	
	public TimePeriod(){
		start = new DateTime(null,null);
		end = new DateTime(null,null);
	}
	public TimePeriod(DateTime start,DateTime end){
		this.start=start;
		this.end=end;
	}
	
	public DateTime getStart(){return start;}
	public DateTime getEnd(){return end;}
	public Date getStartDate(){return start.getDate();}
	public Time getStartTime(){return start.getTime();}
	public Date getEndDate(){return end.getDate();}
	public Time getEndTime(){return end.getTime();}
	public void setStartDate(Date d){this.getStart().setDate(d);}
	public void setStartTime(Time t){this.getStart().setTime(t);}
	public void setEndDate(Date d){this.getEnd().setDate(d);}
	public void setEndTime(Time t){this.getEnd().setTime(t);}
}