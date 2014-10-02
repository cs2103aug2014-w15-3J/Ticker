package ticker.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.*;

import tickerPackage.Date;
import tickerPackage.Task;
import tickerPackage.Time;

class Storage {
	private final ArrayList<String> storedTexts = new ArrayList<String>(); // to be changed to Task
	private File textFile;
	private Scanner fileReader;
	private BufferedWriter fileWriter;
	
	// These messages are used when IO errors occur
	private static final String MESSAGE_FILE_LOAD_ERROR = "Could not load from %1$s properly: %2$s";
	private static final String MESSAGE_FILE_CREATE_ERROR = "Could not create the file %1$s: %2$s";
	private static final String MESSAGE_FILE_WRITE_ERROR = "Could not write to %1$s: \"%2$s\"";
	private static final String MESSAGE_FILE_READ_ERROR = "Could not read from %1$s: \"%2$s\"";

	
//	protected boolean writeStorageArrayIntoFile(){
//
//	}
	
	
	
	protected String convertItemIntoJSONObject(String str){
		JSONObject obj = new JSONObject(str);
		return obj.toString();
	}
	
	protected boolean readFileIntoStorageArray(){
		createNewFileReader();
		while(!storedTexts.isEmpty()){
			String str = storedTexts.remove(0);
			convertItemIntoJSONObject(str);
			//to be completed
		}
		fileReader.close();
		return true;
	}
	 
	/**
	 * Creates a new reader for the current text file.
	 * Program will exit if there is a file read error.
	 */
	private void createNewFileReader() {
		try {
			fileReader = new Scanner(textFile);
		} catch (FileNotFoundException fnfe) {
			String fileReadError = String.format(MESSAGE_FILE_READ_ERROR, textFile.getName(), fnfe.getMessage());
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
			fileWriter = new BufferedWriter(new FileWriter(textFile));
			fileWriter.write(new String());
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException ioe) {
			String fileWriteError = String.format(MESSAGE_FILE_WRITE_ERROR, textFile.getName(), ioe.getMessage());
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
	
	protected ArrayList<String> getStoredTexts() {
		return storedTexts;
	}
	
	protected String parseFloatingTaskIntoJSON(Task data) {
		try{
			//convert Java Object into JSON
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("description", data.getDescription());
			return jsonObj.toString();
		
		} catch(JSONException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	protected JSONObject parseTimeIntoJSON(Time data) {
		try{
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("hour", data.getHour());
			jsonObj.put("min", data.getMinute());
			return jsonObj;
			
		} catch(JSONException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	protected JSONObject parseDateIntoJSON(Date data) {
		try{
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("year", data.getYear());
			jsonObj.put("month", data.getMonth());
			jsonObj.put("date", data.getDate());
			return jsonObj;
			
		} catch(JSONException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
}
