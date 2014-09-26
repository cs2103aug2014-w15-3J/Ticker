import java.util.Scanner;


public class Parser {

	Scanner sc;
	
	public Parser(){
		sc = new Scanner(System.in);
	}

	public void getCommand(){
		String command = sc.nextLine();
		while (command.isEmpty())
			command = sc.nextLine();
		String[] words = command.split(" ");
		for (int i=0;i<words.length;i++){
			words[i] = words[i].trim();
		}
	
		if (words[0].equals("add")){
			callAdd(words);
		}
		
		if (words[0].equals("delete")){
			callDelete(words);
		}
		
		if (words[0].equals("search")){
			callSearch(words);
		}
		
		if (words[0].equals("edit")){
			callEdit(words);
		}
	}
	
	private void callAdd(String[] words){
		
		
		
	}
	
	private void callDelete(String[] words){
		
		
		
	}
	
	private void callEdit(String[] words){
		
		
		
	}
	
	private void callSearch(String[] words){
		
		
	
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
	
	
	private Date constructDate(String str){
		return null;
	}

}
