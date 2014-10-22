// TODO: add priority to task
package ticker.parser;
import java.util.logging.Level;
import java.util.logging.Logger;

import ticker.common.*;

public class Parser {
	
	public static final String INVALID_ST_AND_ED = "Cannot add a task with only start time and end date";
	public static final String INVALID_ET_AND_SD = "Cannot add a task with only end time and start date";
	public static final String INVALID_ARGUMENT = "Invalid Argument";
	public static final String INVALID_SEARCH = "Invalid search, task description must be within double quote";
	public static final String INVALID_PRIORITY = "Priority can only be A B or C";
	
	private static Logger logger = Logger.getLogger("parser");
	
	public Parser(){
	}

	public UserInput processInput(String command){
		logger.log(Level.INFO,"processInput");
		String[] words = command.split(" +");
		if (words.length==0) return null;
		
		String key = words[0].toLowerCase();
		
		if (key.equals("add")){
			String description = null;
			int firstIndex = command.indexOf('"');
			if (firstIndex == -1) {
				logger.log(Level.INFO,"no double quotes in the user's input");
				return null;
			}
			int secondIndex = command.indexOf('"',firstIndex+1);
			if (secondIndex != command.lastIndexOf('"')) {
				logger.log(Level.INFO,"too many double quotes in the user's input");
				return null;
			}
			
			description = command.substring(firstIndex+1,secondIndex);
			return callAdd(words,description,command);
		
		}
		
		if (key.equals("delete")||key.equals("del")||key.equals("remove")){
			return callDelete(words);
		}
		
		if (key.equals("search")){
			
			int firstIndex = command.indexOf('"');
			int secondIndex = command.indexOf('"',firstIndex+1);
			if ((secondIndex != command.lastIndexOf('"'))||firstIndex==-1){ 
				logger.log(Level.INFO,"invalid input for search");
				return new UserInput(CMD.ERROR,INVALID_SEARCH);
			}

			return callSearch(command.substring(firstIndex+1,secondIndex).trim());
		}
		
		if (key.equals("edit")){
			return callEdit(words,command);
		}
		
		if (key.equals("list")){
			return callList(words);
		}
		
		if (key.equals("tick")){
			return callTick(words);
		}
		
		if (key.equals("cmi")){
			return callCMI(words);
		}
		
		if (key.equals("untick")){
			return callUntick(words);
		}
		
		if (key.equals("uncmi")){
			return callUnCMI(words);
		}
		
		if (key.equals("help")){
			return callHelp(words);
		}
		
		if (key.equals("clear")){
			return callClear(words);
		}
		
		if (key.equals("undo")){
			return new UserInput(CMD.UNDO,null);
		}
		
		if (key.equals("redo")){
			return new UserInput(CMD.REDO,null);
		}
		
		if (key.equals("exit")){
			System.exit(0);
		}
		
		return null;
		
	}
	
	private boolean processSTET(String[] words,int index,UserInput input){
		if (words.length==index+1){
			return false;
		}
		Time time = constructTime(words[index+1]);
		if (time==null){
			return false;
		}
		
		if (words[index].equals("-st")){
			input.startTime=time;
		}
		
		else {
			input.endTime=time;
		}
		
		return true;
	}
	
	private boolean processSDED(String[] words,int index,UserInput input){
		if (words.length==index+1){
			return false;
		}
		Date date = constructDate(words[index+1]);
		if (date==null){
			return false;
		}
		
		if (words[index].equals("-sd")){
			input.startDate=date;
		}
		
		else {
			input.endDate=date;
		}
		
		return true;
	}
	
	private UserInput callAdd(String[] words,String description,String command){
		logger.log(Level.INFO,"callAdd");
		UserInput input = new UserInput();
		
		input.command="add";
		input.description = description;
		
		for (int i=0;i<words.length;i++){
			
			if (words[i].equals("-st")||words[i].equals("-et")){	
				if (!(processSTET(words,i,input))){
					return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
				}
			}
			
			if (words[i].equals("-sd")||words[i].equals("-ed")){
				if (!(processSDED(words,i,input))){
					return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
				}
			}
			
			if (words[i].equals("-p")){
				if (words.length==i+1){
					return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
				}
				
				String priority = words[i+1].toUpperCase();
				
				if (priority.equals("A")||priority.equals("C")||priority.equals("C"))
					input.priority=priority.charAt(0);
				else {
					return new UserInput(CMD.ERROR,INVALID_PRIORITY);
				}
				
			}

			if (words[i].equals("-r")){
				input.isRepeating = true;
			}
		}
		
		StartEndTimeDate result = checkDashTimeDate(command.substring(command.lastIndexOf("\"")+1));
		
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

		if (input.startDate==null&&input.endDate!=null&&input.startTime!=null&&input.endTime==null){
			return new UserInput(CMD.ERROR,INVALID_ST_AND_ED);
		}
		else if (input.startDate!=null&&input.endDate==null&&input.startTime==null&&input.endTime!=null){
			return new UserInput(CMD.ERROR,INVALID_ET_AND_SD);
		}
		
		return input;
	}
	
	private static StartEndTimeDate checkDashTimeDate(String description){
		String[] strings = description.split(" +"); 
		StartEndTimeDate result = new StartEndTimeDate();
		for (String s:strings){
			if (s.indexOf("-")!=-1&&s.indexOf("-")==s.lastIndexOf("-")){
				int index = s.indexOf("-");
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
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		input.index=Integer.parseInt(words[1]);
		return input;
	}
	
	private UserInput callHelp(String[] words){
		UserInput input = new UserInput();
		input.command=CMD.HELP.toString();
		return input;
	}
	
	private UserInput callClear(String[] words){
		UserInput input = new UserInput();
		input.command=CMD.CLEAR.toString();
		return input;
	}

	private UserInput callTick(String[] words){
		UserInput input = new UserInput();
		input.command=CMD.TICK.toString();
		if (words.length==1){
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		input.index=Integer.parseInt(words[1]);
		return input;
	}

	private UserInput callCMI(String[] words){
		UserInput input = new UserInput();
		input.command=CMD.CMI.toString();
		if (words.length==1){
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		input.index=Integer.parseInt(words[1]);
		return input;
	}

	private UserInput callUntick(String[] words){
		UserInput input = new UserInput();
		input.command=CMD.UNTICK.toString();
		if (words.length==1){
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		input.index=Integer.parseInt(words[1]);
		return input;
	}

	private UserInput callUnCMI(String[] words){
		UserInput input = new UserInput();
		input.command=CMD.UNCMI.toString();
		if (words.length==1){
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		input.index=Integer.parseInt(words[1]);
		return input;
	}
	
	private UserInput callEdit(String[] words,String command){
		String description = "";
		if (command.indexOf('"')!=-1&&command.lastIndexOf('"')>command.indexOf('"'))
			description = command.substring(command.indexOf('"')+1,command.lastIndexOf('"'));
			
		int index = Integer.parseInt(words[1]); 
		
		UserInput input = new UserInput(CMD.EDIT,description);
		input.index=index;
		
		for (int i=0;i<words.length;i++){
			if (words[i].equals("-t")){
				input.command = "editt";
				break;
			}
		}
		
		StartEndTimeDate result = checkDashTimeDate(command.substring(command.lastIndexOf("\"")+1));
		
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
		
		return input;
	}
	
	private UserInput callSearch(String str){
		
		UserInput input = new UserInput(CMD.SEARCH,str);
		return input;
	}
	
	private UserInput callList (String[] words){
		
		UserInput input = new UserInput();
		input.command = CMD.LIST.toString();
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
			return new UserInput(CMD.ERROR,"invalid input");
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
		}
		
		
		if (hour>=0&&hour<24&&minute<60&&minute>=0){
			logger.log(Level.INFO,"valid time constructed, hour = " + hour +" minute = " + minute);
			return new Time(hour,minute);
		}
		
		return null;
	}
	
	
	private static Date constructDate(String str){

		if (str.equals("")) return null;
		int index = str.indexOf("/");

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
				if (year<100){
					year += 2000;
				}
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
		
		if (month!=0&&date<=numOfDays[month]){
			logger.log(Level.INFO,"valid date constructed, year = " + year +" month = " + month + " date = " + date);
			return new Date(year,month,date);
		}
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
