package ticker.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Scanner;
import java.util.Vector;

import tickerPackage.DeadlineTask;
import tickerPackage.FloatingTask;
import tickerPackage.RepeatingTask;
import tickerPackage.Task;
import tickerPackage.TimedTask;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

class Storage {
	private Vector<Task> storedTasksByPriority = new Vector<Task>();
	private Vector<Task> storedTasksByDeadline = new Vector<Task>();
	private File fileSortedByPriority, fileSortedByDeadline;
	private Scanner fileReader;
	private BufferedWriter fileWriter;
	
	// These messages are used when IO errors occur
	private static final String MESSAGE_FILE_LOAD_ERROR = "Could not load from %1$s properly: %2$s";
	private static final String MESSAGE_FILE_CREATE_ERROR = "Could not create the file %1$s: %2$s";
	private static final String MESSAGE_FILE_WRITE_ERROR = "Could not write to %1$s: \"%2$s\"";
	private static final String MESSAGE_FILE_READ_ERROR = "Could not read from %1$s: \"%2$s\"";

	
	/**
	 * Initialize the file that will be edited.
	 * If the file exists, read the content into the program.
	 * If the file doesn't exist, create the file.
	 * 
	 */
	private void initFile() {
		fileSortedByDeadline = new File("deadline.json");
		fileSortedByPriority = new File("priority.json");
		if (fileSortedByDeadline.exists()) {
			readFileContentIntoStorageArray(fileSortedByDeadline);
		} else {
			createNewFile(fileSortedByDeadline);
		}
		
		if (fileSortedByPriority.exists()) {
			readFileContentIntoStorageArray(fileSortedByPriority);
		} else {
			createNewFile(fileSortedByPriority);
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
	
	protected boolean writeStorageArrayIntoFile(String keyword){
		try {
			if(keyword.equals("deadline")) {
				fileWriter = new BufferedWriter(new FileWriter(fileSortedByPriority));
				String result = convertToJSON(storedTasksByPriority);
				fileWriter.write(result);
			} else {
				fileWriter = new BufferedWriter(new FileWriter(fileSortedByDeadline));
				String result = convertToJSON(storedTasksByDeadline);
				fileWriter.write(result);
			}
			
			fileWriter.flush();
			fileWriter.close();
			return true;
			
		} catch (IOException ioe) {
			String fileWriteError = String.format(MESSAGE_FILE_WRITE_ERROR, fileSortedByDeadline.getName(), ioe.getMessage()); //TODO: to be changed
			showToUser(fileWriteError);
			return false;
		}
	}
	
	protected void readFileContentIntoStorageArray(File jsonFile){
		createNewFileReader(jsonFile);
		String json = fileReader.next();
		Vector<Task> tasks = JSONToTasksVector(json);
		setStoredTaskByDeadline(tasks);
		fileReader.close();
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
			if(jsonFile.getName() == "deadline.json") {
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
			fileWriter = new BufferedWriter(new FileWriter(fileSortedByDeadline));
			fileWriter.write(new String());
			fileWriter.flush();
			fileWriter = new BufferedWriter(new FileWriter(fileSortedByPriority));
			fileWriter.write(new String());
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
	 * @param text
	 * 			The text to be printed.
	 */
	public void showToUser(String text) {
		System.out.println(text);
	}
	
	public String convertToJSON(Vector<Task> tasks){
		GsonBuilder gson = new GsonBuilder().setPrettyPrinting();
		gson.registerTypeAdapter(Task.class, new CustomDeserializer());
		String json = gson.create().toJson(tasks);
		
		return json;
	}
	
	public Vector<Task> JSONToTasksVector(String json) {
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
	
	class CustomDeserializer implements JsonDeserializer<Task>{
		
		public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			if (json == null) {
				return null;
			} else {
				// null management can be improved
				int id = json.getAsJsonObject().get("id").getAsInt();
				switch(id){
				case 1:
					return context.deserialize(json, FloatingTask.class);
				case 2:
					return context.deserialize(json, TimedTask.class);
				case 3:
					return context.deserialize(json, RepeatingTask.class);
				case 4:
					return context.deserialize(json, DeadlineTask.class);
				default:
					return null;
				}
			}
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
}
