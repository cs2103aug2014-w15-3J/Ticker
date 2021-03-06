package ticker.main;

/*
 *  
 *  this class represents a Date
 * 
 *  Coded by Liu Kexin, 26 Sep 2014
 */

public class Date {
	private int year;
	private int month;
	private int date;
	private static final String[] months = {"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	
	/*
	public static void main(String[] args){
		Date dt1 = new Date(2014,3,16);
		Date dt2 = new Date(2014,4,29);
		Date dt3 = new Date(2015,1,1);
		
		System.out.println(dt1);
		System.out.println(dt2);
		System.out.println(dt3);

		System.out.println(dt1.compareTo(dt2));
		System.out.println(dt3.compareTo(dt1));
		System.out.println(dt1.compareTo(dt1));
	}
	*/
	
	public Date (int year,int month, int date){
		this.date= date;
		this.month=month;
		this.year=year;
	}

	public String toString(){
		return date+", "+months[month]+", "+ year;
	}
	
	public String getMonthStr(){
		return months[getMonth()];
	}
	
	public int getMonth(){
		return month;
	}
	
	public int getYear(){
		return year;
	}
	
	public int getDate(){
		return date;
	}
	
	public void setYear(int year){
		this.year=year;		
	}
	
	public void setMonth(int month){
		this.month=month;
	}
	
	public void setDate(int date){
		this.date=date;	
	}
	
	public int compareTo(Date other){
		if (this.getYear() < other.getYear()){
			return -1;
		}
	
		if (this.getYear() > other.getYear()){
			return 1;
		}
		
		else {
			if (this.getMonth() < other.getMonth())
				return -1;
			if (this.getMonth() > other.getMonth())
				return 1;
			else {
				if (this.getDate()==other.getDate())
					return 0;
				return this.getDate()>other.getDate()?1:-1;
			}
		}
	}

}
