import java.util.Scanner;


public class Parser {

	Scanner sc;
	
	public Parser(){
		sc = new Scanner(System.in);
	}

	
	private Time constructTime(String str){
		int firstIndex = str.indexOf(':');
		int lastIndex = str.lastIndexOf(':');
		
		if (firstIndex == lastIndex){
			for (int i=0;i<str.length();i++){
				if (i!=firstIndex&&(str.charAt(i)<'0'||str.charAt(i)>'9'))
					return null;
			}
			
			if (firstIndex!=0&&firstIndex!=str.length()-1){
				int hour = Integer.parseInt(str.substring(0,firstIndex));
				int minute = Integer.parseInt(str.substring(firstIndex+1));
				if (hour<24&&minute<60)
					return new Time(hour,minute); 
			}
		}
		
		return null;
	}
	
}
