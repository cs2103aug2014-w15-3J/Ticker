package ticker.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Scanner;
import java.util.Vector;

import tickerPackage.Task;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;


/**
*  Storage is the storage component for Ticker. 
* 
*  @author Choo Jia Le
*  @version 0.1
*/
public class Storage {
	private Vector<Task> storedTasksByPriority = new Vector<Task>();
	private Vector<Task> storedTasksByDeadline = new Vector<Task>();
	private Vector<Task> storedTasksByTicked = new Vector<Task>();
	private Vector<Task> storedTasksByCMI = new Vector<Task>();
	private File fileSortedByPriority, fileSortedByDeadline, fileSortedByTicked, fileSortedByCMI;
	private Scanner fileReader;
	private BufferedWriter fileWriter;
	private boolean isCorrupt = false, isMissing = false;
	
	// These messages are used when IO errors occur
	private static final String MESSAGE_FILE_LOAD_ERROR = "Could not load from %1$s properly: %2$s";
	private static final String MESSAGE_FILE_CREATE_ERROR = "Could not create the file %1$s: %2$s";
	private static final String MESSAGE_FILE_WRITE_ERROR = "Could not write to %1$s: \"%2$s\"";
	private static final String MESSAGE_FILE_READ_ERROR = "Could not read from %1$s: \"%2$s\"";

	private static final int TASKS_DEADLINE_INDEX = 1;
	private static final int TASKS_PRIORITY_INDEX = 2;
	private static final int TASKS_TICKED_INDEX = 3;
	private static final int TASKS_CMI_INDEX = 4;
	private static final String TASKS_PRIORITY_FILENAME = "priority.json";
	private static final String TASKS_DEADLINE_FILENAME = "deadline.json"; 
	private static final String TASKS_TICKED_FILENAME = "ticked.json";
	private static final String TASKS_CMI_FILENAME = "cmi.json";

	public Storage() {
		 initFile();
	}
	
	/**
	 * Initialize the file that will be edited.
	 * If the file exists, read the content into the program.
	 * If the file doesn't exist, create the file.
	 */
	private void initFile() {
		fileSortedByDeadline = new File(TASKS_DEADLINE_FILENAME);
		fileSortedByPriority = new File(TASKS_PRIORITY_FILENAME);
		fileSortedByTicked = new File(TASKS_TICKED_FILENAME);
		fileSortedByCMI = new File(TASKS_CMI_FILENAME);
		
		checkFileExist(fileSortedByDeadline);
		checkFileExist(fileSortedByPriority);
		checkFileExist(fileSortedByTicked);
		checkFileExist(fileSortedByCMI);
		
		//if one or more of the file is being altered, reset all files
		if(isMissing) {
			clearFile();
		}
	}
	
	private void checkFileExist(File file) {
		if (file.exists()) {
			readFileContentIntoStorageArray(file);
		} else {
			createNewFile(file);
			isMissing = true;
		}
	}
	
	/**
	 * Creates a new file using the input file name as basis for the text file.
	 * Location will be local to the directory of the running Java class file.
	 * Program will exit if file creation fails.
	 */
	private void createNewFile(File jsonFile) {
		try {
			jsonFile.createNewFile();
		} catch (IOException ioe) {
			String fileCreationError = String.format(MESSAGE_FILE_CREATE_ERROR, jsonFile.getName(), ioe.getMessage());
			showToUser(fileCreationError);
			System.exit(-1);
		}
	}
	
	/**
	*  Writes the specific <code>Vector<Task></code> into JSON file as specified by the key 
	*	
	*  @param key 						the key to specify which file to write into. 
	*  @param tasks 					the list of tasks to be converted into JSON and written into the file
	*  @return 							<code>true</code> if the specific vector is added to the file successfully.
	*									<code>false</code> otherwise.
	*  @throws IllegalArgumentException If the input is not 1 or 2
	*/
	public boolean writeStorageArrayIntoFile(int key, Vector<Task> tasks) throws IllegalArgumentException {
		try {
			//TODO: refactor
			if(key == TASKS_PRIORITY_INDEX) {
				fileWriter = new BufferedWriter(new FileWriter(fileSortedByPriority));
				setStoredTaskByPriority(tasks);
				String result = convertToJSON(storedTasksByPriority);
				fileWriter.write(result);
			} else if (key == TASKS_DEADLINE_INDEX) {
				fileWriter = new BufferedWriter(new FileWriter(fileSortedByDeadline));
				setStoredTaskByDeadline(tasks);
				String result = convertToJSON(storedTasksByDeadline);
				fileWriter.write(result);
			} else if (key == TASKS_TICKED_INDEX) {
				fileWriter = new BufferedWriter(new FileWriter(fileSortedByTicked));
				setStoredTaskByDeadline(tasks);
				String result = convertToJSON(storedTasksByTicked);
				fileWriter.write(result);
			} else if (key == TASKS_CMI_INDEX) {
				fileWriter = new BufferedWriter(new FileWriter(fileSortedByCMI));
				setStoredTaskByDeadline(tasks);
				String result = convertToJSON(storedTasksByCMI);
				fileWriter.write(result);
			} else {
				throw new IllegalArgumentException("illegal key " + key);
			}

			fileWriter.flush();
			fileWriter.close();
			return true;
			
		} catch (IOException ioe) {
			//TODO: to be modified
			String fileWriteError = String.format(MESSAGE_FILE_WRITE_ERROR, fileSortedByDeadline.getName(), ioe.getMessage()); //TODO: to be changed
			showToUser(fileWriteError);
			return false;
		}
	}
	
	private void readFileContentIntoStorageArray(File jsonFile){
		createNewFileReader(jsonFile);
		String json = "";
		while(fileReader.hasNextLine()) {
			json += fileReader.nextLine();

		}
		try {
			Vector<Task> tasks = JSONToTasksVector(json);
			if(jsonFile.getName().equals(TASKS_DEADLINE_FILENAME)) {
				setStoredTaskByDeadline(tasks);			
			} else {
				setStoredTaskByPriority(tasks);
			}
		} catch (IllegalStateException ise) {
			System.out.println(); //TODO: decision to be made. if system is being tampered with, delete the whole database? To be completed
		}
		fileReader.close();
	}
	
	/**
	*  Restore the data from the file that stores the data in JSON format
	*	
	*  @param key 						the key to specify which file to retrieve from. 
	*  @return 							a list of tasks in <code>Vector<Task></code> form								
	*  @throws IllegalArgumentException If the input is not 1 or 2
	*/
	public Vector<Task> restoreDataFromFile(int key){
		if(key == TASKS_PRIORITY_INDEX) {
			readFileContentIntoStorageArray(fileSortedByPriority);
			return storedTasksByPriority;
		} else if (key == TASKS_DEADLINE_INDEX) {
			readFileContentIntoStorageArray(fileSortedByDeadline);
			return storedTasksByDeadline;
		} else if (key == TASKS_TICKED_INDEX) {
			readFileContentIntoStorageArray(fileSortedByTicked);
			return storedTasksByTicked;
		} else if (key == TASKS_CMI_INDEX) {
			readFileContentIntoStorageArray(fileSortedByCMI);
			return storedTasksByCMI;
		} else {
			throw new IllegalArgumentException("illegal key " + key);
		}
	}

	/**
	 * Creates a new reader for the current text file.
	 * Program will exit if there is a file read error.
	 */
	private void createNewFileReader(File jsonFile) {
		try {
			if(jsonFile.getName() == "deadline.json") {
				fileReader = new Scanner(fileSortedByDeadline);
			} else {
				fileReader = new Scanner(fileSortedByPriority);
			}
		} catch (FileNotFoundException fnfe) {
			String fileReadError = null;
			if(jsonFile.getName().equals(TASKS_DEADLINE_FILENAME)) {
				fileReadError = String.format(MESSAGE_FILE_READ_ERROR, fileSortedByDeadline.getName(), fnfe.getMessage());
			} else {
				fileReadError = String.format(MESSAGE_FILE_READ_ERROR, fileSortedByPriority.getName(), fnfe.getMessage());
			}
			showToUser(fileReadError);
			System.exit(-1);
		}
	}
	
	/**
	 * Writes a new empty String into the file to clear out all text.
	 * Program will exit if file write fails.
	 */
	private void clearFile() {
		try {
			//TODO: refactor
			fileWriter = new BufferedWriter(new FileWriter(fileSortedByDeadline));
			fileWriter.write(new String("[]"));
			fileWriter.flush();

			fileWriter = new BufferedWriter(new FileWriter(fileSortedByPriority));
			fileWriter.write(new String("[]"));
			fileWriter.flush();
			
			fileWriter = new BufferedWriter(new FileWriter(fileSortedByTicked));
			fileWriter.write(new String("[]"));
			fileWriter.flush();
			
			fileWriter = new BufferedWriter(new FileWriter(fileSortedByCMI));
			fileWriter.write(new String("[]"));
			fileWriter.flush();

			fileWriter.close();
		} catch (IOException ioe) {
			String fileWriteError = String.format(MESSAGE_FILE_WRITE_ERROR, fileSortedByDeadline.getName(), ioe.getMessage()); //TODO: to be modified
			showToUser(fileWriteError);
			System.exit(-1);
		}
	}
	
	/**
	 * Prints the text to console and terminates the line.
	 * 
	 * @param text	The text to be printed.
	 */
	private void showToUser(String text) {
		System.out.println(text);
	}
	
	/**
	*  Convert the <code>Vector<Task></code> input into JSON string
	*
	*  @param tasks  the list of tasks to be converted into JSON string
	*  @return JSON string
	*/
	public String convertToJSON(Vector<Task> tasks){
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Task.class, new CustomDeserializer());
		String json = gson.create().toJson(tasks);
		
		return json;
	}
	
	/**
	*  Convert the JSON string input into <code>Vector<Task></code>
	*
	*  @param json  					the JSON string to be converted into <code>Vector<Task></code>
	*  @return							a list of tasks in <code>Vector<Task></code>
	*  @throws IllegalStateException	if the file is not in JSON format	
	*/
	public Vector<Task> JSONToTasksVector(String json) throws IllegalStateException{
		Vector<Task> tasks = new Vector<Task>();
		GsonBuilder gson = new GsonBuilder();
		JsonParser parse = new JsonParser();
		JsonArray jsonArray = parse.parse(json).getAsJsonArray();
		gson.registerTypeAdapter(Task.class, new CustomDeserializer());
		for(int i = 0; i < jsonArray.size(); i++){
			Task output = gson.create().fromJson(jsonArray.get(i), Task.class);
			tasks.add(output);
		}
		return tasks;
	}
	
	public void setStoredTaskByPriority(Vector<Task> tasks) {
		storedTasksByPriority = tasks;
	}
	
	public void setStoredTaskByDeadline(Vector<Task> tasks) {
		storedTasksByDeadline = tasks;
	}
}

//	public Vector<Task> readJsonStream(InputStream in) throws IOException {
//	     JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
//	     try {
//	       return readTasksArray(reader);
//	     } finally {
//	       reader.close();
//	     }
//	   }
//
//	   public Vector<Task> readTasksArray(JsonReader reader) throws IOException {
//		   Vector<Task> tasks = new Vector<Task>();
//
//	     reader.beginArray();
//	     while (reader.hasNext()) {
//	       tasks.add(readTask(reader));
//	     }
//	     reader.endArray();
//	     return tasks;
//	   }
//
//	   public Task readTask(JsonReader reader) throws IOException {
//	     Date startDate = null, endDate = null;
//	     Time startTime = null, endTime = null;
//	     String description = null;
//
//	     reader.beginObject();
//	     while (reader.hasNext()) {
//	    	 String name = reader.nextName();
//	    	 if (name.equals("description")) {
//	    		 description = reader.nextString();
//	    	 } else if (name.equals("startDate") || (name.equals("date")) && reader.peek() != JsonToken.NULL) {
//	    		 startDate = readDate(reader);
//	    	 } else if  (name.equals("endDate") && reader.peek() != JsonToken.NULL){
//	    		 endDate = readDate(reader);
//	    	 } else if (name.equals("startTime") && reader.peek() != JsonToken.NULL) {
//	    		 startTime = readTime(reader);
//	    	 } else if (name.equals("endTime") && reader.peek() != JsonToken.NULL) {
//	    		 endTime = readTime(reader);
//	    	 } else {
//	    		 reader.skipValue();
//	    	 }
//	     }
//	     reader.endObject();
//	    
//	     if (startDate == null && endDate == null && startTime == null && endTime == null) {
//	    	 return new FloatingTask(description);
//	     } else if (endDate == null) {
//	    	 return new RepeatingTask(description, startDate, startTime, endTime);
//	     } else if (startDate == null && startTime == null){
//	    	 return new DeadlineTask(description, endDate, endTime);
//	     } else {
//	    	 return new TimedTask(description, startDate, startTime, endDate, endTime);
//	     }
//	   }
//
//	   private Time readTime(JsonReader reader) throws IOException {
//		int hour = -1, min = -1; //TODO: magic number
//		
//		reader.beginObject();
//		   while (reader.hasNext()) {
//			   String name = reader.nextName();
//		       if (name.equals("hour")) {
//		    	   hour = reader.nextInt();
//		       } else if (name.equals("min")) {
//		    	   min = reader.nextInt();
//		       } else {
//		    	   reader.skipValue();
//		       }
//		   }
//		   reader.endObject();
//		   return new Time(hour, min);
//	}
//
//	private Date readDate(JsonReader reader) throws IOException {
//		   int year = -1, month = -1, date = -1; //TODO: magic number
//		   
//		   reader.beginObject();
//		   while (reader.hasNext()) {
//			   String name = reader.nextName();
//		       if (name.equals("year")) {
//		    	   year = reader.nextInt();
//		       } else if (name.equals("month")) {
//		    	   month = reader.nextInt();
//		       } else if (name.equals("date")) {
//		    	   date = reader.nextInt();
//		       } else {
//		    	   reader.skipValue();
//		       }
//		   }
//		   reader.endObject();
//		   return new Date(year, month, date);
//	}
//
