// TODO: add priority to task
package tickerPackage;

public class Parser {
	
	public static final String INVALID_ST_AND_ED = "Cannot add a task with only start time and end date";
	public static final String INVALID_ET_AND_SD = "Cannot add a task with only end time and start date";
	public static final String INVALID_ARGUMENT = "Invalid Argument";
	public static final String INVALID_SEARCH = "Invalid search, task description must be within double quote";
	
	public Parser(){
	}

	public UserInput processInput(String command){
		//System.out.println("processInput");
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
		
		if (words[0].toLowerCase().equals("delete")){
			return callDelete(words);
		}
		
		if (words[0].toLowerCase().equals("search")){
			
			int firstIndex = command.indexOf('"');
			int secondIndex = command.indexOf('"',firstIndex+1);
			if ((secondIndex != command.lastIndexOf('"'))||firstIndex==-1) 
				return new UserInput("error",INVALID_SEARCH);

			return callSearch(command.substring(firstIndex+1,secondIndex).trim());
		}
		
		if (words[0].toLowerCase().equals("edit")){
			return callEdit(words,command);
		}
		
		if (words[0].toLowerCase().equals("list")){
			return callList(words);
		}
		
		if (words[0].toLowerCase().equals("tick")){
			return callTick(words);
		}
		
		return null;
		
	}
	
	private UserInput callAdd(String[] words,String description){
		//System.out.println("callAdd");
		UserInput input = new UserInput();
		
		input.command="add";
		input.description = description;
		
		for (int i=0;i<words.length;i++){
			
			if (words[i].equals("-st")){
				if (words.length==i+1){
					return new UserInput("error",INVALID_ARGUMENT);
				}
				input.startTime = constructTime(words[i+1]);
			
				if (input.startTime==null){
					System.out.println("Error");
					return null;
				}
			}
			
			if (words[i].equals("-et")){
				if (words.length==i+1){
					return new UserInput("error",INVALID_ARGUMENT);
				}
				
				input.endTime = constructTime(words[i+1]);
				
				if (input.endTime==null){
					System.out.println("Error");
					return null;
				}
			}
			
			if (words[i].equals("-sd")){
				if (words.length==i+1){
					return new UserInput("error",INVALID_ARGUMENT);
				}
				
				input.startDate = constructDate(words[i+1]);
			
				if (input.startDate==null){
					System.out.println("Error");
					return null;
				}
			}
			
			if (words[i].equals("-ed")){
				if (words.length==i+1){
					return new UserInput("error",INVALID_ARGUMENT);
				}
				
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
		
		input.validifyTime();
		
		if (input.startDate==null&&input.endDate!=null&&input.startTime!=null&&input.endTime==null){
			return new UserInput("error",INVALID_ST_AND_ED);
		}
		else if (input.startDate!=null&&input.endDate==null&&input.startTime==null&&input.endTime!=null){
			return new UserInput("error",INVALID_ET_AND_SD);
		}
		
		Date tempStartDate=null, tempEndDate=null;
		Time tempStartTime=null, tempEndTime=null;
		
		checkDashTimeDate(tempStartDate,tempStartTime,tempEndDate,tempEndTime,description.substring(description.lastIndexOf("\"")+1));
		
		return input;
	}
	
	private static void checkDashTimeDate(Date sd,Time st,Date ed,Time et, String description){
		String[] strings = description.split(" +"); 
		for (String s:strings){
			if (s.indexOf("-")!=-1&&s.indexOf("-")==s.lastIndexOf("-")){
				int index = s.indexOf("-");
				if (constructTime(s.substring(index)+1)!=null){
					st=constructTime(s.substring(index+1));
				}
				if (constructTime(s.substring(index+1))!=null){
					et=constructTime(s.substring(index+1));
				}
				if (constructDate(s.substring(index)+1)!=null){
					sd=constructDate(s.substring(index+1));
				}
				if (constructDate(s.substring(index+1))!=null){
					ed=constructDate(s.substring(index+1));
				}
			}
		}
	}
	
	private UserInput callDelete(String[] words){
		UserInput input = new UserInput();
		input.command="delete";
		if (words.length==1){
			return new UserInput("error",INVALID_ARGUMENT);
		}
		input.index=Integer.parseInt(words[1]);
		return input;
	}
	
	private UserInput callTick(String[] words){
		UserInput input = new UserInput();
		input.command="tick";
		if (words.length==1){
			return new UserInput("error",INVALID_ARGUMENT);
		}
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
		if (words.length==2){
			if (words[1].equals("p"))
				input.description="priority";	
			if (words[1].equals("t"))
				input.description="time";
		}
		if (input.description==null)
			return new UserInput("error","invalid input");
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
	
	
	private static Time constructTime(String str){
		str=removeBlank(str);
		int firstIndex = str.indexOf(':');
		int lastIndex = str.lastIndexOf(':');
		
		int hour = -1; 
		int minute = -1;
		
		if (firstIndex==-1){
			for (int i=0;i<str.length();i++){
				if (str.charAt(i)<'0'||str.charAt(i)>'9')
					return null;
			}
		
			int time = Integer.parseInt(str);
			
			if (time < 100) {
				hour = time;
				minute = 0;
			}
			
			else if (time < 10000){
				hour = time/100;
				minute = time%100;
			}
		}
		
		else if (firstIndex == lastIndex){
			
			for (int i=0;i<str.length();i++){
				if (i!=firstIndex&&(str.charAt(i)<'0'||str.charAt(i)>'9'))
					return null;
			}
			
			if (firstIndex!=0&&firstIndex!=str.length()-1){
				hour = Integer.parseInt(str.substring(0,firstIndex));
				minute = Integer.parseInt(str.substring(firstIndex+1)); 
			}
			
			if (hour>=0&&hour<24&&minute<60&&minute>=0)
				return new Time(hour,minute);
		}
		
		return null;
	}
	
	
	private static Date constructDate(String str){
		//System.out.println("ConstructDate");
		
		int index = str.indexOf("/");
		//System.out.println("index = " + index);
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
		
		int[] numOfDays = {0,31,28,31,30,31,30,31,31,30,31,30,31};
		if (Date.isLeapYear(year)){
			numOfDays[2]=29;
		}
		final String[] months = {"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		if (month == 0){
			for (int i=0;i<months.length;i++){
				if (months[i].toLowerCase().equals(monthStr.toLowerCase())){
					month = i;
					break;
				}
			}
		}
		
		//System.out.println(year + "   " + month + "   "+ date);
		
		if (month!=0&&date<=numOfDays[month])
			return new Date(year,month,date);
		
		return null;
	}
}
