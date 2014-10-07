// TODO: add priority to task

package tickerPackage;

public class Parser {
	
	public Parser(){

	}

	public UserInput processInput(String command){
		System.out.println("processInput");
		String[] words = command.split(" ");
		for (int i=0;i<words.length;i++){
			words[i] = words[i].trim();
		}
	
		if (words[0].equals("add")){
			String description = null;
			int firstIndex = command.indexOf('"');
			if (firstIndex == -1) return null;
			int secondIndex = command.indexOf('"',firstIndex+1);
			if (secondIndex != command.lastIndexOf('"')) return null;
			
			description = command.substring(firstIndex+1,secondIndex);
			return callAdd(words,description);
		
		}
		
		if (words[0].equals("delete")){
			return callDelete(words);
		}
		
		if (words[0].equals("search")){
			return callSearch(command.substring(command.lastIndexOf("\"")).trim());
		//mistake in the line above
		}
		
		if (words[0].equals("edit")){
			return callEdit(words,command);
		}
		
		if (words[0].equals("list")){
			return callList(words);
		}
		
		if (words[0].equals("tick")){
			return callTick(words);
		}
		
		return null;
		
	}
	
	private UserInput callAdd(String[] words,String description){
		System.out.println("callAdd");
		UserInput input = new UserInput();
		
		input.command="add";
		input.description = description;
		
		
		for (int i=0;i<words.length;i++){
			
			if (words[i].equals("-st")){
				input.startTime = constructTime(words[i+1]);
			
				if (input.startTime==null){
					System.out.println("Error");
					return null;
				}
			}
			
			if (words[i].equals("-et")){
				input.endTime = constructTime(words[i+1]);
			
				if (input.endTime==null){
					System.out.println("Error");
					return null;
				}
			}
			
			if (words[i].equals("-sd")){
				input.startDate = constructDate(words[i+1]);
			
				if (input.startDate==null){
					System.out.println("Error");
					return null;
				}
			}
			
			if (words[i].equals("-ed")){
				input.endDate = constructDate(words[i+1]);
			
				if (input.endDate==null){
					System.out.println("Error");
					return null;
				}
			}
			
			if (words[i].equals("-r")){
				input.isAppendingRepeating = true;
			}
		}
		
		return input;
	}
	
	private UserInput callDelete(String[] words){
		
		UserInput input = new UserInput();
		input.command="delete";
		input.index=Integer.parseInt(words[1]);
		return input;
	}
	
	private UserInput callTick(String[] words){
		
		UserInput input = new UserInput();
		input.command="tick";
		input.index=Integer.parseInt(words[1]);
		return input;
	}
	
	private UserInput callEdit(String[] words,String command){
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
			
		UserInput input = new UserInput();
		input.command = "edit";
		input.index=index;
		input.description = description;
		input.isAppendingRepeating = isAppending;
		
		return input;
	}
	
	private UserInput callSearch(String str){
		
		UserInput input = new UserInput();
		
		input.command = "search";
		input.description = "str";
		
		return input;
	}
	
	private UserInput callList (String[] words){
		
		UserInput input = new UserInput();
		input.command = "list";
		return input;
		
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
		System.out.println("ConstructDate");
		
		int index = str.indexOf("/");
		System.out.println("index = " + index);
		if (index==-1) return null;
		
		int date = Integer.parseInt(str.substring(0,index));
		int month=0;
		int year = Date.getCurrentYear();
		
		String monthStr;
		
		if (str.lastIndexOf("/")==index){
			monthStr = str.substring(index+1);
			try {  
				month = Integer.parseInt(monthStr);  
			}  
				catch(NumberFormatException nfe) {    
			}  
		}
		
		else {
			monthStr = str.substring(index+1,str.lastIndexOf("/"));
			try {
				year = Integer.parseInt(str.substring(str.lastIndexOf("/")+1));  
			}	catch(NumberFormatException nfe) {    
			}  
		}

		try {  
			month = Integer.parseInt(str.substring(index+1,str.lastIndexOf("/")));  
		}	catch(NumberFormatException nfe) {    
		}	catch(IndexOutOfBoundsException ioobe){
		} 
		
		final int[] numOfDays = {0,31,28,31,30,31,30,31,31,30,31,30,31};
		final String[] months = {"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		if (month == 0){
			for (int i=0;i<months.length;i++){
				if (months[i].toLowerCase().equals(monthStr.toLowerCase())){
					month = i;
					break;
				}
			}
		}
		
		System.out.println(year + "   " + month + "   "+ date);
			
		if (month!=0&&date<=numOfDays[month])
			return new Date(year,month,date);
		
		return null;
	}
}
