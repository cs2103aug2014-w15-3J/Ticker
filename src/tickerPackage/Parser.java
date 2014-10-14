// TODO: add priority to task
package tickerPackage;

public class Parser {
	
	public static final String INVALID_ST_AND_ED = "Cannot add a task with only start time and end date";
	public static final String INVALID_ET_AND_SD = "Cannot add a task with only end time and start date";
	public static final String INVALID_ARGUMENT = "Invalid Argument";
	public static final String INVALID_SEARCH = "Invalid search, task description must be within double quote";
	public static final String INVALID_PRIORITY = "Priority can only be A B or C";
	
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
			return callAdd(words,description,command);
		
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
		
		if (words[0].toLowerCase().equals("cmi")){
			return callCMI(words);
		}
		
		if (words[0].toLowerCase().equals("help")){
			return callHelp(words);
		}
		
		if (words[0].toLowerCase().equals("clear")){
			return callClear(words);
		}
		
		if (words[0].toLowerCase().equals("exit")){
			System.exit(0);
		}
		
		return null;
		
	}
	
	private UserInput callAdd(String[] words,String description,String command){
		System.out.println("callAdd");
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
			
			if (words[i].equals("-p")){
				if (words.length==i+1){
					return new UserInput("error",INVALID_ARGUMENT);
				}
				
				String priority = words[i+1].toUpperCase();
				
				if (priority=="A"||priority=="C"||priority=="C")
					input.priority=priority.charAt(0);
				else {
					return new UserInput("error",INVALID_PRIORITY);
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
		
		StartEndTimeDate result = checkDashTimeDate(command.substring(command.lastIndexOf("\"")+1)); System.out.println("checkdash");
		
		if (result.getStartDate()!=null){
			input.startDate=result.getStartDate();
		}
		
		if (result.getEndDate()!=null){
			input.endDate=result.getEndDate();
		}
		
		if (result.getStartTime()!=null){
			input.startTime=result.getStartTime();
		}
		
		if (result.getEndTime()!=null){
			input.endTime=result.getEndTime();
		}
		
		input.validifyTime();
		System.out.println(input.startDate +""+ input.startTime + input.endDate+input.endTime);
		System.out.println("Current Date " + Date.getCurrentDate());
		
		if (input.startDate==null&&input.endDate!=null&&input.startTime!=null&&input.endTime==null){
			return new UserInput("error",INVALID_ST_AND_ED);
		}
		else if (input.startDate!=null&&input.endDate==null&&input.startTime==null&&input.endTime!=null){
			return new UserInput("error",INVALID_ET_AND_SD);
		}
		
		return input;
	}
	
	private static StartEndTimeDate checkDashTimeDate(String description){
		String[] strings = description.split(" +");  System.out.println("description = " + description);
		StartEndTimeDate result = new StartEndTimeDate();
		for (String s:strings){
			System.out.println(s);
			if (s.indexOf("-")!=-1&&s.indexOf("-")==s.lastIndexOf("-")){
				int index = s.indexOf("-");System.out.println("index = "+index);
				if (constructTime(s.substring(0,index))!=null){
					result.setStartTime(constructTime(s.substring(0,index)));
				}
				if (constructTime(s.substring(index+1))!=null){
					result.setEndTime(constructTime(s.substring(index+1)));
				}
				if (constructDate(s.substring(0,index))!=null){
					result.setStartDate(constructDate(s.substring(0,index)));
				}
				if (constructDate(s.substring(index+1))!=null){
					result.setEndDate(constructDate(s.substring(index+1)));
				}
			}
		}
		return result;
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
	
	private UserInput callHelp(String[] words){
		UserInput input = new UserInput();
		input.command="help";
		return input;
	}
	
	private UserInput callClear(String[] words){
		UserInput input = new UserInput();
		input.command="clear";
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

	private UserInput callCMI(String[] words){
		UserInput input = new UserInput();
		input.command="cmi";
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
			if (words[1].equals("priority")||words[1].equals("p"))
				input.description="priority";
			if (words[1].equals("time")||words[1].equals("ti"))
				input.description="time";
			if (words[1].equals("c")||words[1].equals("cmi"))
				input.description="cmi";
			if (words[1].equals("ticked")||words[1].equals("tick"))
				input.description="ticked";
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
		if (str.equals("")) return null;
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
		
			int time = Integer.parseInt(str); System.out.println("time = " + time);
			
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
		}
		
		
		if (hour>=0&&hour<24&&minute<60&&minute>=0)
			return new Time(hour,minute);
		
		return null;
	}
	
	
	private static Date constructDate(String str){
		//System.out.println("ConstructDate");
		if (str.equals("")) return null;
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

class StartEndTimeDate{
	private Date sd;
	private Date ed;
	private Time st;
	private Time et;
	
	public StartEndTimeDate(){
		
	}
	public Date getStartDate(){return sd;}
	public Time getStartTime(){return st;}
	public Date getEndDate(){return ed;}
	public Time getEndTime(){return et;}
	public void setStartDate(Date d){this.sd=d;}
	public void setStartTime(Time t){this.st=t;}
	public void setEndDate(Date d){this.ed=d;}
	public void setEndTime(Time t){this.et=t;}
}
