package ticker.common;
//@author A0115369B
public class DateTime{
	private Date date;
	private Time time;
	
	public DateTime(){}
	public DateTime(Date date,Time time){
		this.date = date;
		this.time = time;
	} 
	
	public Time getTime(){return this.time;}
	public Date getDate(){return this.date;}
	public void setTime(Time time){this.time = time;}
	public void setDate(Date date){this.date = date;}
	
	public boolean equalsTo(DateTime other){
		return this.getTime().equals(other.getTime())&&this.getDate().equals(other.getDate());
	}

	public boolean smallerThan(DateTime other){
		return this.getDate().smallerThan(other.getDate())||(this.getDate().equals(other.getDate())&&this.getTime().smallerThan(other.getTime()));
	}
}