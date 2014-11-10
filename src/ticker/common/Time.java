package ticker.common;

import java.util.Calendar;

//@author A0115369B
 
/*
 * This class represents a time
 */

public class Time {
	private int hour;
	private int minute;
	public Time (int hour,int minute){
		this.hour = hour;
		this.minute =  minute;
	}

	public String toString(){
		String minutePrefix = (minute<10) ? "0" : "" ;
		return hour + ":" +  minutePrefix  + minute;
	}
	
	public int getHour(){
		return hour;
	}

	public int getMinute(){
		return minute;
	}
	
	public void setHour(int hour){
		this.hour = hour;
	}
	
	public void setMinute(int minute){
		this.minute = minute;
	}
	
	public int compareTo(Time other){
		if (this.getHour() < other.getHour() || (this.getHour() == other.getHour() && this.getMinute() < other.getMinute()))
			return -1;
		if (this.getHour() == other.getHour() && this.getMinute() == other.getMinute())
			return 0;
		return 1;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Time) {
			Time myTime = (Time) obj;
			return this.getHour() == myTime.getHour() && this.getMinute() == myTime.getMinute();
		} else {
			return false;
		}
	}
	
	public static Time getCurrentTime(){
		Calendar cal = Calendar.getInstance();
		return new Time(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
	}
	public boolean smallerThan(Time other) {
		return this.compareTo(other) < 0;
	}
}
