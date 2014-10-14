package tickerPackage;

import java.util.Calendar;

/*
 * 
 *  This class represents a time
 * 
 *  Coded by Liu Kexin, 26 Sep 2014
 */ 
 
public class Time {
	private int hour;
	private int minute;
	public Time (int hour,int minute){
		this.hour = hour;
		this.minute =  minute;
	}
/*
	public static void main(String[] args){
		
		Time tm1 = new Time(9,5);
		Time tm2 = new Time(14,30);
		
		System.out.println(tm1);
		System.out.println(tm2);
		
		System.out.println(tm1.compareTo(tm2));
	
	}
*/
	public String toString(){
		String foobar = (minute<10) ? "0" : "" ;
		return hour + ":" +  foobar  + minute;
	}
	
	public int getHour(){
		return hour;
	}

	public int getMinute(){
		return minute;
	}
	
	public void setHour(int hour){
		this.hour=hour;
	}
	
	public void setMinute(int minute){
		this.minute = minute;
	}
	
	public int compareTo(Time other){
		if (this.getHour()<other.getHour()||(this.getHour()==other.getHour()&&this.getMinute()<other.getMinute()))
			return -1;
		if (this.getHour()==other.getHour()&&this.getMinute()==other.getMinute())
			return 0;
		return 1;
	}

	public static Time getCurrentTime(){
		Calendar cal = Calendar.getInstance();
		return new Time(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
	}
}
