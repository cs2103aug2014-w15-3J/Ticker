package ticker.parser;

//@author  A0115369B
/*
 * This class analyzes the String entered by the user and determines
 * what the user wants to do
 * 
 * usage: when Logic component calls the processInput method, the corresponding
 * UserInput object is returned to Logic.
 */

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
	private static final String[] months = {"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	private static final Time START_OF_DAY = new Time(0,0);
	private static final Time END_OF_DAY = new Time(23,59);
	private static Logger logger = Logger.getLogger("parser");
	private PrettyTimeParser ptp;
	
	public Parser(){
		ptp = new PrettyTimeParser();
	}

	//This method takes in a string from Logic and returns a UserInput object to Logic
	//indicating what the user want to do
	public UserInput processInput(String command){
		logger.log(Level.INFO,"processInput");
		String[] words = command.toLowerCase().split(" +");
		if (words.length == 0) return null;
		
		String key = words[0].toLowerCase();
		
		if (key.equals(ParserString.ADD)){
			if (words.length == 1){
				return new UserInput(CMD.ERROR,EMPTY_ADD);
			}
			return callAdd(words,command);
		}
		
		else if (key.equals(ParserString.DELETE) || key.equals(ParserString.DEL) || key.equals(ParserString.REMOVE)){
			return callDelete(words);
		}
		
		else if (key.equals(ParserString.SEARCH)){
			if(words.length == 1){
				return new UserInput(CMD.ERROR,INVALID_SEARCH);
			}
			return callSearch(words,command);
		}
		
		else if (key.equals(ParserString.EDIT)){
			if (words.length <= 2){
				return new UserInput(CMD.ERROR,INVALID_EDIT);
			}
			return callEdit(words,command);
		}
		
		else if (key.equals(ParserString.LIST) || key.equals(ParserString.SHOW)){
			return callList(words);
		}
		
		else if (key.equals(ParserString.TICK) || key.equals(ParserString.DONE)){
			return callTick(words);
		}
		
		else if (key.equals(ParserString.KIV)){
			return callKIV(words);
		}
		
		else if (key.equals(ParserString.UNTICK)){
			return callUntick(words);
		}
		
		else if (key.equals(ParserString.UNKIV)){
			return callUnKIV(words);
		}
		
		else if (key.equals(ParserString.HELP)){
			return callHelp(words);
		}
		
		else if (key.equals(ParserString.CLEAR)){
			return callClear(words);
		}
		
		else if (key.equals(ParserString.UNDO)){
			return new UserInput(CMD.UNDO,null);
		}
		
		else if (key.equals(ParserString.REDO)){
			return new UserInput(CMD.REDO,null);
		}
		
		else if (key.equals(ParserString.EXIT)){
			System.exit(0);
		}
		
		else if (key.equals(ParserString.TAKE)){
			return callTake(command, words);
		}
		
		else if (key.equals(ParserString.SEARCH_FREE_SHORT) || key.equals(ParserString.SEARCH_FREE)){
			return callSearchFree(command);
		}
		
		return null;
		
	}
	
	private UserInput callTake(String command,String[] words) {
		if (words.length < 3){
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		String description = command.substring(CMD.TAKE.toString().length() + 1);
		description = description.trim();
		description = description.substring(description.indexOf(ParserString.SPACE));
		description = description.trim();
		
		UserInput input = new UserInput(CMD.TAKE,description);
		
		try{
			input.setIndex(Integer.parseInt(words[1]));
		} catch(NumberFormatException nfe) { 
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		
		return input;
	}

	private UserInput callAdd(String[] words,String command){
		logger.log(Level.INFO,"callAdd");
		String description = extractDesc(command);
		UserInput input = new UserInput(CMD.ADD,description);
		input.setPriority(ParserString.NORMAL_PRIORITY_CHAR);
		
		for (int i = 0; i < words.length; i++){
			
			String lowerCase = words[i].toLowerCase();
			
			if (lowerCase.equals(ParserString.HIGH_PRIORITY_SHORT) || lowerCase.equals(ParserString.HIGH_PRIORITY)){
				input.setPriority(ParserString.HIGH_PRIORITY_CHAR);
			}
			
			else if (lowerCase.equals(ParserString.LOW_PRIORITY)){
				input.setPriority(ParserString.LOW_PRIORITY_CHAR);
			}

			else if (lowerCase.equals(ParserString.REPEATING) || lowerCase.equals("-rw")){
				input.setRepeating(true);
				//input.setRepeatingInterval(RepeatingInterval.WEEK);
			}
			/*
			else if (lowerCase.equals("-rd")){
				input.setRepeating(true);
				input.setRepeatingInterval(RepeatingInterval.DAY);
			}
			
			else if (lowerCase.equals("-rm")){
				input.setRepeating(true);
				input.setRepeatingInterval(RepeatingInterval.MONTH);
			}
			*/
		}
		
		nlp(description,input);
		TimePeriod result = checkDashTimeDate(command);
		mergeTimeResult(result,input);
		extractSingleDate(input);
		
		if (input.getStartDate() == null && input.getEndDate() != null
				&& input.getStartTime() != null && input.getEndTime() == null){
			return new UserInput(CMD.ERROR,INVALID_ST_AND_ED);
		}
		else if (input.getStartDate() != null && input.getEndDate() == null
				&& input.getStartTime() == null && input.getEndTime() != null){
			return new UserInput(CMD.ERROR,INVALID_ET_AND_SD);
		}
		
		input.validifyTime();
		
		return input;
	}
	
	private static TimePeriod checkDashTimeDate(String description){
		String[] strings = description.split(" +"); 
		TimePeriod result = new TimePeriod();
		for (String s:strings){
			if (s.indexOf(ParserString.DASH_STRING) != -1 && s.indexOf(ParserString.DASH_STRING) == s.lastIndexOf(ParserString.DASH_STRING)){
				int index = s.indexOf(ParserString.DASH_STRING);
				if (constructTime(s.substring(0,index)) != null){
					result.setStartTime(constructTime(s.substring(0,index)));
				}
				if (constructTime(s.substring(index+1)) != null){
					result.setEndTime(constructTime(s.substring(index+1)));
				}
				if (constructDate(s.substring(0,index)) != null){
					result.setStartDate(constructDate(s.substring(0,index)));
				}
				if (constructDate(s.substring(index+1)) != null){
					result.setEndDate(constructDate(s.substring(index+1)));
				}
			}
		}
		return result;
	}
	
	private UserInput callDelete(String[] words){
		UserInput input = new UserInput(CMD.DEL,null);
		return extractIndex(words,input);
	}
	
	private UserInput callHelp(String[] words){
		UserInput input = new UserInput();
		input.setCommand(CMD.HELP.toString());
		return input;
	}
	
	private UserInput callClear(String[] words){
		if (words.length > 1){
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		}
		UserInput input = new UserInput(CMD.CLEAR,null);
		return input;
	}
	
	//This method extracts the index from user's input, assign it to the UserInput object
	//and returns the modified UserInput object. It is used for delete tick untick kiv unkiv commands.
	private UserInput extractIndex(String[] words,UserInput input){
		if (words.length < 2)
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		
		try {
			input.setIndex(Integer.parseInt(words[1]));
		} catch (NumberFormatException nfe){
			return new UserInput(CMD.ERROR,INVALID_ARGUMENT);
		} 
		return input;
	}

	private UserInput callTick(String[] words){
		UserInput input = new UserInput(CMD.TICK,null);
		return extractIndex(words,input);
	}

	private UserInput callKIV(String[] words){
		UserInput input = new UserInput(CMD.KIV,null);
		return extractIndex(words,input);
	}

	private UserInput callUntick(String[] words){
		UserInput input = new UserInput(CMD.UNTICK,null);
		return extractIndex(words,input);
	}

	private UserInput callUnKIV(String[] words){
		UserInput input = new UserInput(CMD.UNKIV,null);
		return extractIndex(words,input);
	}
	
	//This method extracts the description part from the String entered by user
	private String extractDesc(String str){
		if ((str.length() > 4 && str.substring(0,4).equalsIgnoreCase(ParserString.ADD_WITH_SPACE))
				|| (str.length() > 6 && str.substring(0, 7).equalsIgnoreCase(ParserString.SEARCH_WITH_SPACE))){
			str = str.substring(str.indexOf(ParserString.SPACE) + 1);
		}
		
		else if (str.length() > 5 && str.substring(0,5).equalsIgnoreCase(ParserString.EDIT_WITH_SPACE)){
			str = str.substring(str.indexOf(ParserString.SPACE) + 1);
			str = str.substring(str.indexOf(ParserString.SPACE) + 1);
		}
		
		String[] splitted = str.split(" +");
		String res = str;
		for (int i = 0; i < splitted.length; i++){
			if (splitted[i].indexOf(ParserString.DASH_STRING) != -1 && !splitted[i].equals(ParserString.NLP_FLAG)){
				int startIndex = res.indexOf(splitted[i]);
				int endIndex = startIndex + splitted[i].length();
				if (startIndex-1 >= 0 && res.charAt(startIndex-1) == ParserString.SPACE){
					startIndex--;
				}
				res = res.substring(0,startIndex) + res.substring(endIndex);
			}
		}
		
		return res.trim();
	}
	
	//This method analyses the UserInput object. If input.description contains date 
	//in the format mm/dd or yy/mm/dd (without any dash), this part of description 
	//will be extracted and the date parsed will be assigned to input.startDate and input.endDate
	
	private void extractSingleDate(UserInput input){
		if (input.getStartDate() == null && input.getEndDate() == null){
			String res = input.getDescription();
			String[] splitted = res.split(" +");
			for (int i = 0; i < splitted.length; i++){
				if (constructDate(splitted[i]) != null){
					int startIndex = res.indexOf(splitted[i]);
					int endIndex = startIndex + splitted[i].length();
					if (startIndex-1 >= 0 && res.charAt(startIndex-1) == ParserString.SPACE){
						startIndex--;
					}
					res = res.substring(0,startIndex) + res.substring(endIndex);
					input.setStartDate(constructDate(splitted[i]));
					input.setEndDate(constructDate(splitted[i]));
				}
			}
			input.setDescription(res);
		}
	}
	
	private UserInput callEdit(String[] words,String command){
		String description = extractDesc(command);		
		int index = Integer.parseInt(words[1]); 
		
		UserInput input = new UserInput(CMD.EDIT,description);
		input.setIndex(index);
		
		for (int i=0; i < words.length; i++){
			
			if (words[i].toLowerCase().equals(ParserString.HIGH_PRIORITY_SHORT) || words[i].toLowerCase().equals(ParserString.HIGH_PRIORITY)){
				input.setPriority(ParserString.HIGH_PRIORITY_CHAR);
			}
			
			if (words[i].toLowerCase().equals(ParserString.NORMAL_PRIORITY)){
				input.setPriority(ParserString.NORMAL_PRIORITY_CHAR);
			}
			
			if (words[i].toLowerCase().equals(ParserString.LOW_PRIORITY)){
				input.setPriority(ParserString.LOW_PRIORITY_CHAR);
			}

			if (words[i].equals(ParserString.REPEATING)){
				input.setRepeating(true);
			}
		}
		
		nlp(description,input);
		TimePeriod result = checkDashTimeDate(command);
		mergeTimeResult(result,input);
		extractSingleDate(input);
		input.validifyTime();
		
		if (input.getStartDate() == null && input.getEndDate() != null 
				&& input.getStartTime() != null && input.getEndTime() == null){
			return new UserInput(CMD.ERROR,INVALID_ST_AND_ED);
		}
		else if (input.getStartDate() != null && input.getEndDate() == null
				&& input.getStartTime() == null && input.getEndTime() != null){
			return new UserInput(CMD.ERROR,INVALID_ET_AND_SD);
		}
		
		return input;
	}
	
	private UserInput callSearch(String[] words,String command){
		
		String description = extractDesc(command); 
		UserInput input = new UserInput(CMD.SEARCH,description);
		
		for (int i = 0; i < words.length; i++){
			if (words[i].equals(ParserString.HIGH_PRIORITY_SHORT) || words[i].equals(ParserString.HIGH_PRIORITY)){
				input.setPriority(ParserString.HIGH_PRIORITY_CHAR);
			}
			if (words[i].equals(ParserString.LOW_PRIORITY)){
				input.setPriority(ParserString.LOW_PRIORITY_CHAR);
			}
			if (words[i].equals(ParserString.NORMAL_PRIORITY)){
				input.setPriority(ParserString.NORMAL_PRIORITY_CHAR);
			}
			if (words[i].equals(ParserString.EXPIRED_SHORT) || words[i].equals(ParserString.EXPIRED)){
				input.setCommand(ParserString.SEARCH_EXPIRED);
			}
				
		}
		
		nlp(description,input);
		TimePeriod result = checkDashTimeDate(command);
		mergeTimeResult(result,input);
		extractSingleDate(input);
		
		if (!((input.getStartTime() == null) && (input.getEndTime() == null) && (input.getStartDate() == null) 
				&& (input.getEndDate() == null))){
			getSearchTimePeriod(input,command);
		}
		
		return input;
	}
	
	//search for free slots
	private UserInput callSearchFree(String command){

		UserInput input = new UserInput(CMD.SEARCHFREE,command);
		
		nlp(command,input);
		TimePeriod result = checkDashTimeDate(command);
		mergeTimeResult(result,input);
		extractSingleDate(input);
		
		if (!((input.getStartTime() == null) && (input.getEndTime() == null) && (input.getStartDate() == null) && (input.getEndDate() == null))){
			getSearchTimePeriod(input,command);
		}
		else {
			input.setStartDate(Date.getCurrentDate());
			input.setEndDate(Date.getCurrentDate());
			input.setStartTime(START_OF_DAY);
			input.setEndTime(END_OF_DAY);
		}
		
		input.setDescription(null);
		return input;
	}
	
	private UserInput callList (String[] words){
		
		UserInput input = new UserInput(CMD.LIST,ParserString.TIME);
			
		if (words.length >= 2){
			if (words[1].equals(ParserString.PRIORITY) || words[1].equals(ParserString.PRIORITY_SHORT))
				input.setDescription(ParserString.PRIORITY);
			if (words[1].equals(ParserString.KIV_SHORT) || words[1].equals(ParserString.KIV))
				input.setDescription(ParserString.KIV);
			if (words[1].equals(ParserString.TICKED) || words[1].equals(ParserString.TICK))
				input.setDescription(ParserString.TICKED);
		}
		return input;
		
	}
	
	private void getSearchTimePeriod(UserInput input, String description){
	
		if(input.getStartTime() == null){
			input.setStartTime(START_OF_DAY);
		}
		if(input.getEndTime() == null){
			input.setEndTime(END_OF_DAY);
		}
		if(input.getStartDate() == null&&input.getEndDate() == null){
			input.setStartDate(Date.getCurrentDate());
			input.setEndDate(Date.getCurrentDate());
		}
		else if (input.getStartDate() == null){
			input.setStartDate(Date.getCurrentDate());
		}
		else if (input.getEndDate() == null){
			input.setEndDate(input.getStartDate());
		}
	}
	
	//This method writes a time period into UserInput object
	private void mergeTimeResult(TimePeriod result,UserInput ui){
		if (result.getStartDate() != null){
			ui.setStartDate(result.getStartDate());
		}
		
		if (result.getEndDate() != null){
			ui.setEndDate(result.getEndDate());
		}
		
		if (result.getStartTime() != null){
			ui.setStartTime(result.getStartTime());
		}
		
		if (result.getEndTime() != null){
			ui.setEndTime(result.getEndTime());
		}
	}
	
	//This method calls the PrettyTime natural language processing library, processes the String
	//entered by the user and writes the result into UserInput object
	private void nlp(String description,UserInput input){
		
		if (description.indexOf(ParserString.NLP_FLAG) == -1) return;
		
		input.setDescription(description.substring(0,description.indexOf(ParserString.NLP_FLAG)).trim());		
		List<java.util.Date> dates = this.ptp.parse(description.substring(description.indexOf(ParserString.NLP_FLAG)+2));
		if (dates.size() == 2){
			input.setStartDate(convertDate(dates.get(0)));
			input.setStartTime(convertTime(dates.get(0)));
			input.setEndDate(convertDate(dates.get(1)));
			input.setEndTime(convertTime(dates.get(1)));
		}
		else if (dates.size() == 1){
			if (isDeadLine(description)){
				input.setEndDate(convertDate(dates.get(0)));
				
				if (convertTime(dates.get(0)).equals(Time.getCurrentTime())){
					input.setEndTime(END_OF_DAY);
				}
				else {
					input.setEndTime(convertTime(dates.get(0)));
				}
			
			}
			
			else {
				input.setStartDate(convertDate(dates.get(0)));
				input.setStartTime(convertTime(dates.get(0)));
			}
		}
	}
	
	//This method determines whether the Time/Date in nlp result is a deadline.
	private boolean isDeadLine(String description){
		description = description.toLowerCase();
		if (description.indexOf("deadline") != -1){
			return true;
		}
		
		int index=description.indexOf("by");
		if (index != -1 && this.ptp.parse(description.substring(index)).size() == 1){
			return true;
		}

		index=description.indexOf("before");
		if (index != -1 && this.ptp.parse(description.substring(index)).size() == 1){
			return true;
		}
		
		index=description.indexOf("in");
		if (index != -1 && this.ptp.parse(description.substring(index)).size() == 1){
			if (description.indexOf("finish") != -1 || description.indexOf("do") != -1 || description.indexOf("complete") != -1)
				return true;
		}
		
		return false;
	}
	
	//This method converts a java.util.Date object to a Ticker.common.Date object
	private Time convertTime(java.util.Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return new Time(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
	}
	
	//This method converts a java.util.Time object to a Ticker.common.Time object
	private Date convertDate(java.util.Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return new Date(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DATE));
	}
	
	private static String removeBlank(String str){
		str = str.trim();
		for (int i=0; i < str.length(); i++){
			while (str.charAt(i) == ParserString.SPACE)
				str = str.substring(0,i) + str.substring(i+1);
			if (i == str.length()) return str;
		}
		return str;
	}
	
	//This method tries to interpret a String entered by user and return a Time object 
	private static Time constructTime(String str){
		str = removeBlank(str);
		if (str.equals("")) return null;
		int firstIndex = str.indexOf(':');
		int lastIndex = str.lastIndexOf(':');
		
		int hour = -1; 
		int minute = -1;
		
		boolean isPM = false;
		if (str.length() > 2){
			String lastTwoChars = str.substring(str.length()-2);
			if (lastTwoChars.equalsIgnoreCase(ParserString.PM)){
				isPM = true;
				str = str.substring(0,str.length()-2);
			}
			else if(lastTwoChars.equalsIgnoreCase(ParserString.AM)){
				str = str.substring(0,str.length()-2);
			}
		}
		
		if (firstIndex == -1){
			
			for (int i = 0; i < str.length(); i++){
				if (str.charAt(i) < '0' || str.charAt(i) > '9')
					return null;
			}
			int time = 0;
			try{
				time = Integer.parseInt(str);
			}catch(NumberFormatException nfe){
				return null;
			}
			
			if (time < 100) {
				hour = time;
				minute = 0;
			}
			else if (time < 10000){
				hour = time / 100;
				minute = time % 100;
			}
		}
		
		else if (firstIndex == lastIndex){
			
			for (int i = 0; i < str.length(); i++){
				if (i != firstIndex&&(str.charAt(i) < '0' || str.charAt(i) > '9'))
					return null;
			}
			if (firstIndex != 0 && firstIndex != str.length() - 1){
				hour = Integer.parseInt(str.substring(0,firstIndex));
				minute = Integer.parseInt(str.substring(firstIndex+1)); 
			}
		}
		
		if (isPM){
			hour += 12;
		}
		if (hour >= 0 && hour < 24 && minute < 60 && minute >= 0){
			logger.log(Level.INFO,"valid time constructed, hour = " + hour + " minute = " + minute);
			return new Time(hour,minute);
		}
		return null;
	}
	
	//This method tries to interpret a String entered by user and return a Date object 
	static Date constructDate(String str){

		if (str.isEmpty()) return null;
		int index = str.indexOf(ParserString.SLASH);
		if (index == -1) return null;
		
		int month = 0;
		int date=0;
		int year = Date.getCurrentYear();
		
		if (str.lastIndexOf(ParserString.SLASH) == index){
			
			try {  
				date = Integer.parseInt(str.substring(index + 1)); 
				month = Integer.parseInt(str.substring(0,index));
			}  
				catch(NumberFormatException nfe) {    
			}  
		}
		
		else {

			try {
				year = Integer.parseInt(str.substring(0,index));  
				if (year < 100){
					year += 2000;
				}
				month =  Integer.parseInt(str.substring(index + 1,str.lastIndexOf(ParserString.SLASH)));  
				date =  Integer.parseInt(str.substring(str.lastIndexOf(ParserString.SLASH) + 1));  
			}	catch(NumberFormatException nfe) {
			}  
		}

		try {  
			month = Integer.parseInt(str.substring(index + 1,str.lastIndexOf(ParserString.SLASH)));  
		}	catch(NumberFormatException nfe) {    
		}	catch(IndexOutOfBoundsException ioobe){
		} 
		
		int[] numOfDays = {0,31,28,31,30,31,30,31,31,30,31,30,31};
		if (Date.isLeapYear(year)){
			numOfDays[2] = 29;
		}
		
		if (month == 0){
			for (int i = 0; i < months.length; i++){
				if (str.toLowerCase().indexOf(months[i].toLowerCase()) != -1){
					month = i;
					break;
				}
			}
		}
		
		if (month > 0 && month < 13 && date <= numOfDays[month]){
			logger.log(Level.INFO,"valid date constructed, year = " + year + " month = " + month + " date = " + date);
			return new Date(year,month,date);
		}
		return null;
	}
}
