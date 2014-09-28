package tickerPackage;

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
			String description = null;
			int firstIndex = command.indexOf('"');
			if (firstIndex == -1) return;
			int secondIndex = command.indexOf('"',firstIndex+1);
			if (secondIndex != command.lastIndexOf('"')) return;
			
			description = command.substring(firstIndex+1,secondIndex);
			callAdd(words,description);
		
		}
		
		if (words[0].equals("delete")){
			callDelete(words);
		}
		
		if (words[0].equals("search")){
			callSearch(command.substring(command.lastIndexOf("\"")).trim());
		}
		
		if (words[0].equals("edit")){
			callEdit(words,command);
		}
		
		if (words[0].equals("list")){
			callList(words);
		}
		
	}
	
	private void callAdd(String[] words,String description){
		Time st = null;
		Time et = null;
		Date sd = null;
		Date ed = null;
		Boolean isRepeating = false;
		
		for (int i=0;i<words.length;i++){
			
			if (words[i].equals("-st")){
				st = constructTime(words[i+1]);
			
				if (st==null){
					System.out.println("Error");
					return;
				}
			}
			
			if (words[i].equals("-et")){
				et = constructTime(words[i+1]);
			
				if (et==null){
					System.out.println("Error");
					return;
				}
			}
			
			if (words[i].equals("-sd")){
				sd = constructDate(words[i+1]);
			
				if (sd==null){
					System.out.println("Error");
					return;
				}
			}
			
			if (words[i].equals("-ed")){
				ed = constructDate(words[i+1]);
			
				if (ed==null){
					System.out.println("Error");
					return;
				}
			}
			
			if (words[i].equals("-r")){
				isRepeating = true;
			}
		}
		
		Ticker.getTicker().getLogic().add(description,isRepeating,sd,ed,st,et);
	}
	
	private void callDelete(String[] words){
		
		Ticker.getTicker().getLogic().delete(Integer.parseInt(words[1]));
		
	}
	
	private void callEdit(String[] words,String command){
		String description = "";
		
		if (command.indexOf('"')!=-1&&command.lastIndexOf('"')>command.indexOf('"'))
			description = command.substring(command.indexOf('"')+1,command.lastIndexOf('"'));
			
		int index = Integer.parseInt(words[1]); 
		boolean isAppending = false;
		
		for (int i=0;i<words.length;i++){
			if (words[i].equals("-a")){
				isAppending = true;
				break;
			}
		}
			
		Ticker.getTicker().getLogic().edit(index,isAppending,description);
	}
	
	private void callSearch(String str){
		
		Ticker.getTicker().getLogic().search(str);
	
	}
	
	private void callList (String[] words){
		
		Ticker.getTicker().getLogic().list();
		
	}
	
	
	private static String removeBlank(String str){
		str=str.trim();
		for (int i=0;i<str.length();i++){
			while (str.charAt(i)==' ')
				str = str.substring(0,i)+str.substring(i+1);
			if (i==str.length()) return str;
		}
		return str;
	}
	
	
	private Time constructTime(String str){
		str=removeBlank(str);
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
		
		int index = str.indexOf(",");
		
		String month = str.substring(index+1);
		int date = Integer.parseInt(str.substring(0,index));
		
		final int[] numOfDays = {0,31,28,31,30,31,30,31,31,30,31,30,31};
		final String[] months = {"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		int monthNum = 0;
		for (int i=0;i<months.length;i++){
			if (months[i].equals(month)){
				monthNum = i;
				break;
			}
		
		if (monthNum!=0&&date<=numOfDays[monthNum])
			return new Date(2014,monthNum,date);
		}  // hard coded to 2014, will solve this issue later
		
		return null;
	}
}
