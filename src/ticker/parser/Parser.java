// TODO: add priority to task
package ticker.parser;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Calendar;
import java.util.List;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import ticker.common.*;

public class Parser {
	
	public static final String INVALID_ST_AND_ED = "Cannot add a task with only start time and end date";
	public static final String INVALID_ET_AND_SD = "Cannot add a task with only end time and start date";
	public static final String INVALID_ARGUMENT = "Invalid Argument";
	public static final String INVALID_SEARCH = "Invalid search";
	public static final String INVALID_EDIT = "Invalid edit";
	public static final String EMPTY_ADD = "Cannot add a task with empty description";
	
	private static Logger logger = Logger.getLogger("parser");
	private PrettyTimeParser ptp;
	
	public Parser(){
		ptp = new PrettyTimeParser();
	}

	public UserInput processInput(String command){
		logger.log(Level.INFO,"processInput");
		String[] words = command.toLowerCase().split(" +");
		if (words.length==0) return null;
		
		String key = words[0].toLowerCase();
		
		if (key.equals("add")){
			if (words.length==1){
				return new UserInput(CMD.ERROR,EMPTY_ADD);
			}
			return callAdd(words,command);
		}
		
		if (key.equals("delete")||key.equals("del")||key.equals("remove")){
			return callDelete(words);
		}
		
		if (key.equals("search")){
			if(words.length==1){
				return new UserInput(CMD.ERROR,INVALID_SEARCH);
			}
			return callSearch(words,command);
		}
		
		if (key.equals("edit")){
			if (words.length<=2){
				return new UserInput(CMD.ERROR,INVALID_EDIT);
			}
			return callEdit(words,command);
		}
		
		if (key.equals("list")||key.equals("show")){
			return callList(words);
		}
		
		if (key.equals("tick")||key.equals("done")){
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
	
	private UserInput callAdd(String[] words,String command){
		logger.log(Level.INFO,"callAdd");
		String description = extractDesc(command);
		UserInput input = new UserInput(CMD.ADD,description);
		
		input.setPriority('B');
		
		for (int i=0;i<words.length;i++){
			
			if (words[i].toLowerCase().equals("-impt")||words[i].toLowerCase().equals("-important")){
				input.setPriority('A');
			}
			
			if (words[i].toLowerCase().equals("-trivial")){
				input.setPriority('C');
			}

			if (words[i].equals("-r")){
				input.setRepeating(true);
			}
		}
		
		nlp(description,input);
		StartEndTimeDate result = checkDashTimeDate(command.substring(command.lastIndexOf("\"")+1));
		mergeTimeResult(result,input);
		
		input.validifyTime();

		if (input.getStartDate()==null&&input.getEndDate()!=null&&input.getStartTime()!=null&&input.getEndTime()==null){
			return new UserInput(CMD.ERROR,INVALID_ST_AND_ED);
		}
		else if (input.getStartDate()!=null&&input.getEndDate()==null&&input.getStartTime()==null&&input.getEndTime()!=null){
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
		input.setCommand("delete");
		if (words.length==1){
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		try{
			input.setIndex(Integer.parseInt(words[1]));
		} catch(NumberFormatException nfe) { 
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		return input;
	}
	
	private UserInput callHelp(String[] words){
		UserInput input = new UserInput();
		input.setCommand(CMD.HELP.toString());
		return input;
	}
	
	private UserInput callClear(String[] words){
		UserInput input = new UserInput();
		input.setCommand(CMD.CLEAR.toString());
		return input;
	}

	private UserInput callTick(String[] words){
		UserInput input = new UserInput();
		input.setCommand(CMD.TICK.toString());
		if (words.length==1){
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		input.setIndex(Integer.parseInt(words[1]));
		return input;
	}

	private UserInput callCMI(String[] words){
		UserInput input = new UserInput();
		input.setCommand(CMD.CMI.toString());
		if (words.length==1){
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		input.setIndex(Integer.parseInt(words[1]));
		return input;
	}

	private UserInput callUntick(String[] words){
		UserInput input = new UserInput();
		input.setCommand(CMD.UNTICK.toString());
		if (words.length==1){
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		input.setIndex(Integer.parseInt(words[1]));
		return input;
	}

	private UserInput callUnCMI(String[] words){
		UserInput input = new UserInput();
		input.setCommand(CMD.UNCMI.toString());
		if (words.length==1){
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		input.setIndex(Integer.parseInt(words[1]));
		return input;
	}
	
	private String extractDesc(String str){
		if ((str.length()>4&&str.substring(0,4).equalsIgnoreCase("add "))
				||(str.length()>6&&str.substring(0, 7).equalsIgnoreCase("search "))){
			str=str.substring(str.indexOf(" ")+1);
		}
		
		else if (str.length()>5&&str.substring(0,5).equalsIgnoreCase("edit ")){
			str=str.substring(str.indexOf(" ")+1);
			str=str.substring(str.indexOf(" ")+1);
		}
		
		String[] splitted = str.split(" +");
		String res = str;
		for (int i = 0;i<splitted.length;i++){
			if (splitted[i].indexOf('-')!=-1&&!splitted[i].equals("-t")){
				int startIndex = res.indexOf(splitted[i]);
				int endIndex = startIndex + splitted[i].length();
				if (startIndex-1>=0&&res.charAt(startIndex-1)==' '){
					startIndex--;
				}
				res=res.substring(0,startIndex)+res.substring(endIndex);
			}
		}
		
		return res.trim();
	}
	
	private UserInput callEdit(String[] words,String command){
		String description = extractDesc(command);
			
		int index = Integer.parseInt(words[1]); 
		
		UserInput input = new UserInput(CMD.EDIT,description);
		input.setIndex(index);
		
		for (int i=0;i<words.length;i++){
			
			if (words[i].toLowerCase().equals("-impt")||words[i].toLowerCase().equals("-important")){
				input.setPriority('A');
			}
			
			if (words[i].toLowerCase().equals("-trivial")){
				input.setPriority('C');
			}

			if (words[i].equals("-r")){
				input.setRepeating(true);
			}
		}
		
		nlp(description,input);
		StartEndTimeDate result = checkDashTimeDate(command.substring(command.lastIndexOf("\"")+1));
		mergeTimeResult(result,input);
		
		input.validifyTime();
		
		if (!((input.getStartTime()==null)&&(input.getEndTime()==null)&&(input.getStartDate()==null)&&(input.getEndDate()==null))){
			input.setCommand("editt");
		}

		if (input.getStartDate()==null&&input.getEndDate()!=null&&input.getStartTime()!=null&&input.getEndTime()==null){
			return new UserInput(CMD.ERROR,INVALID_ST_AND_ED);
		}
		else if (input.getStartDate()!=null&&input.getEndDate()==null&&input.getStartTime()==null&&input.getEndTime()!=null){
			return new UserInput(CMD.ERROR,INVALID_ET_AND_SD);
		}
		
		return input;
	}
	
	private UserInput callSearch(String[] words,String command){
		
		String description = extractDesc(command); 
		
		UserInput input = new UserInput(CMD.SEARCH,description);
		
		for (int i=0;i<words.length;i++){
			if (words[i].equals("-impt")||words[i].equals("-important")){
				input.setPriority('A');
			}
			if (words[i].equals("-trivial")){
				input.setPriority('C');
			}
			if (words[i].equals("-normal")){
				input.setPriority('B');
			}
		}
		
		nlp(description,input);
		StartEndTimeDate result = checkDashTimeDate(command.substring(command.lastIndexOf("\"")+1));
		mergeTimeResult(result,input);

		if (!((input.getStartTime()==null)&&(input.getEndTime()==null)&&(input.getStartDate()==null)&&(input.getEndDate()==null))){
			getSearchTimePeriod(input,command);
		}
				
		
		return input;
	}
	
	private UserInput callList (String[] words){
		
		UserInput input = new UserInput(CMD.LIST,"time");
			
		if (words.length>=2){
			if (words[1].equals("priority")||words[1].equals("p"))
				input.setDescription("priority");
			if (words[1].equals("c")||words[1].equals("cmi"))
				input.setDescription("cmi");
			if (words[1].equals("ticked")||words[1].equals("tick"))
				input.setDescription("ticked");
		}
		return input;
		
	}
	
	private void getSearchTimePeriod(UserInput input, String description){
	
		if(input.getStartTime()==null){
			input.setStartTime(new Time(0,0));
		}
		if(input.getEndTime()==null){
			input.setEndTime(new Time(23,59));
		}
		if(input.getStartDate()==null&&input.getEndDate()==null){
			input.setStartDate(Date.getCurrentDate());
			input.setEndDate(Date.getCurrentDate());
		}
		else if (input.getStartDate()==null){
			input.setStartDate(Date.getCurrentDate());
		}
		else if (input.getEndDate()==null){
			input.setEndDate(input.getStartDate());
		}
	}
	
	private void mergeTimeResult(StartEndTimeDate result,UserInput ui){
		if (result.getStartDate()!=null){
			ui.setStartDate(result.getStartDate());
		}
		
		if (result.getEndDate()!=null){
			ui.setEndDate(result.getEndDate());
		}
		
		if (result.getStartTime()!=null){
			ui.setStartTime(result.getStartTime());
		}
		
		if (result.getEndTime()!=null){
			ui.setEndTime(result.getEndTime());
		}
	}
	
	private void nlp(String description,UserInput input){
		
		if (description.indexOf("-t")==-1) return;
		
		input.setDescription(description.substring(0,description.indexOf("-t")).trim());		
		List<java.util.Date> dates = this.ptp.parse(description.substring(description.indexOf("-t")+2));
		if (dates.size()==2){
			input.setStartDate(convertDate(dates.get(0)));
			input.setStartTime(convertTime(dates.get(0)));
			input.setEndDate(convertDate(dates.get(1)));
			input.setEndTime(convertTime(dates.get(1)));
		}
		else if (dates.size()==1){
			if (isDeadLine(description)){
				input.setEndDate(convertDate(dates.get(0)));
				input.setEndTime(convertTime(dates.get(0)));
			}
			
			else {
				input.setStartDate(convertDate(dates.get(0)));
				input.setStartTime(convertTime(dates.get(0)));
			}
		}
	}
	
	private boolean isDeadLine(String description){
		description = description.toLowerCase();
		if (description.indexOf("deadline")!=-1){
			return true;
		}
		
		int index=description.indexOf("by");
		if (index!=-1&&this.ptp.parse(description.substring(index)).size()==1){
			return true;
		}

		index=description.indexOf("before");
		if (index!=-1&&this.ptp.parse(description.substring(index)).size()==1){
			return true;
		}
		
		index=description.indexOf("in");
		if (index!=-1&&this.ptp.parse(description.substring(index)).size()==1){
			if (description.indexOf("finish")!=-1||description.indexOf("do")!=-1||description.indexOf("complete")!=-1)
				return true;
		}
		
		return false;
	}
	
	private Time convertTime(java.util.Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return new Time(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
	}
	
	private Date convertDate(java.util.Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return new Date(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DATE));
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
	
	
	static Date constructDate(String str){

		if (str.equals("")) return null;
		int index = str.indexOf("/");

		if (index==-1) return null;
		
		int month = 0;
		int date=0;
		int year = Date.getCurrentYear();
		
		if (str.lastIndexOf("/")==index){
			
			try {  
				date = Integer.parseInt(str.substring(index+1)); 
				month = Integer.parseInt(str.substring(0,index));
			}  
				catch(NumberFormatException nfe) {    
			}  
		}
		
		else {

			try {
				year = Integer.parseInt(str.substring(0,index));  
				if (year<100){
					year += 2000;
				}
				month =  Integer.parseInt(str.substring(index+1,str.lastIndexOf("/")));  
				date =  Integer.parseInt(str.substring(str.indexOf("/")+1));  
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
				if (str.toLowerCase().indexOf(months[i].toLowerCase())!=-1){
					month = i;
					break;
				}
			}
		}
		
		if (month>0&&month<13&&date<=numOfDays[month]){
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
