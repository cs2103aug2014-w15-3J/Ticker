package ticker.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ticker.common.Task;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

//@author A0116673A
/**
 * Storage is the storage component for Ticker. It stores and retrieve data from
 * datastore.
 * 
 */
public class Storage {

	// These messages are used when IO errors occur
	private static final String MESSAGE_FILE_CREATE_ERROR = "Could not create the file %1$s: %2$s";
	private static final String MESSAGE_FILE_WRITE_ERROR = "Could not write to %1$s: \"%2$s\"";
	private static final String MESSAGE_FILE_READ_ERROR = "Could not read from %1$s: \"%2$s\"";

	private static final String MESSAGE_FILE_MODIFICATION = "Detected modification of datafile. Reinitialising now.";
	private static final String MESSAGE_FILE_INVALID = "Invalid filename";
	private static final String MESSAGE_PARAMETER_ILLEGAL = "illegal key ";

	private static final int TASKS_DEADLINE_INDEX = 1;
	private static final int TASKS_PRIORITY_INDEX = 2;
	private static final int TASKS_TICKED_INDEX = 3;
	private static final int TASKS_KIV_INDEX = 4;
	private static final String TASKS_PRIORITY_FILENAME = "priority.json";
	private static final String TASKS_DEADLINE_FILENAME = "deadline.json";
	private static final String TASKS_TICKED_FILENAME = "ticked.json";
	private static final String TASKS_KIV_FILENAME = "kiv.json";

	private static final String JSON_EMPTY = "[]";

	// These messages are used by Logger
	private static final String LOGGER_MESSAGE_MISSING = "Missing JSON file";
	private static final String LOGGER_MESSAGE_ALTER = "Altered JSON file";
	private static final String LOGGER_MESSAGE_STORAGE = "Storage";

	private static Logger logger;
	private Vector<Task> storedTasksByPriority;
	private Vector<Task> storedTasksByDeadline;
	private Vector<Task> storedTasksByTicked;
	private Vector<Task> storedTasksByKiv;
	private File fileSortedByPriority, fileSortedByDeadline,
			fileSortedByTicked, fileSortedByKiv;
	private Scanner fileReader;
	private BufferedWriter fileWriter;
	private boolean isMissing;

	public Storage() {
		storedTasksByPriority = new Vector<Task>();
		storedTasksByDeadline = new Vector<Task>();
		storedTasksByTicked = new Vector<Task>();
		storedTasksByKiv = new Vector<Task>();
		isMissing = false;
		logger = Logger.getLogger(LOGGER_MESSAGE_STORAGE);
	}

	/**
	 * Initialize the file that will be edited. If the file exists, read the
	 * content into the program. If the file doesn't exist, create the file.
	 * 
	 * @throws IllegalStateException
	 *             If storage file had been tampered with
	 */
	public void initFile() throws IllegalStateException {
		fileSortedByDeadline = new File(TASKS_DEADLINE_FILENAME);
		fileSortedByPriority = new File(TASKS_PRIORITY_FILENAME);
		fileSortedByTicked = new File(TASKS_TICKED_FILENAME);
		fileSortedByKiv = new File(TASKS_KIV_FILENAME);

		try {
			checkFileExist(fileSortedByDeadline);
			checkFileExist(fileSortedByPriority);
			checkFileExist(fileSortedByTicked);
			checkFileExist(fileSortedByKiv);
		} catch (JsonSyntaxException jse) {
			logger.log(Level.WARNING, LOGGER_MESSAGE_ALTER);
			clearFile();
			restoreDataFromFile(TASKS_PRIORITY_INDEX);
			restoreDataFromFile(TASKS_DEADLINE_INDEX);
			restoreDataFromFile(TASKS_TICKED_INDEX);
			restoreDataFromFile(TASKS_KIV_INDEX);
			throw new IllegalStateException();
		}
		// if one or more of the file is being altered, reset all files
		if (isMissing) {
			logger.log(Level.WARNING, LOGGER_MESSAGE_MISSING);
			clearFile();
			restoreDataFromFile(TASKS_PRIORITY_INDEX);
			restoreDataFromFile(TASKS_DEADLINE_INDEX);
			restoreDataFromFile(TASKS_TICKED_INDEX);
			restoreDataFromFile(TASKS_KIV_INDEX);
			throw new IllegalStateException();
		}
	}

	/**
	 * Writes the specific <code>Vector<Task></code> into JSON file as specified
	 * by the key
	 *
	 * @param key
	 *            the key to specify which file to write into.
	 * @param tasks
	 *            the list of tasks to be converted into JSON and written into
	 *            the file
	 * @return <code>true</code> if the specific vector is added to the file
	 *         successfully. <code>false</code> otherwise.
	 * @throws IllegalArgumentException
	 *             If the input is not 1 or 2
	 */
	public boolean writeStorageArrayIntoFile(int key, Vector<Task> tasks)
			throws IllegalArgumentException {
		try {
			if (key == TASKS_PRIORITY_INDEX) {
				fileWriter = new BufferedWriter(new FileWriter(
						fileSortedByPriority));
				setStoredTaskByPriority(tasks);
				String result = convertToJSON(storedTasksByPriority);
				fileWriter.write(result);
			} else if (key == TASKS_DEADLINE_INDEX) {
				fileWriter = new BufferedWriter(new FileWriter(
						fileSortedByDeadline));
				setStoredTaskByDeadline(tasks);
				String result = convertToJSON(storedTasksByDeadline);
				fileWriter.write(result);
			} else if (key == TASKS_TICKED_INDEX) {
				fileWriter = new BufferedWriter(new FileWriter(
						fileSortedByTicked));
				setStoredTaskByDeadline(tasks);
				String result = convertToJSON(storedTasksByTicked);
				fileWriter.write(result);
			} else if (key == TASKS_KIV_INDEX) {
				fileWriter = new BufferedWriter(new FileWriter(fileSortedByKiv));
				setStoredTaskByDeadline(tasks);
				String result = convertToJSON(storedTasksByKiv);
				fileWriter.write(result);
			} else {
				throw new IllegalArgumentException(MESSAGE_PARAMETER_ILLEGAL
						+ key);
			}

			fileWriter.flush();
			fileWriter.close();
			return true;

		} catch (IOException ioe) {
			String fileWriteError = String.format(MESSAGE_FILE_WRITE_ERROR,
					fileSortedByDeadline.getName(), ioe.getMessage());
			showToUser(fileWriteError);
			return false;
		}
	}

	/**
	 * Restore the data from the file that stores the data in JSON format
	 *
	 * @param key
	 *            the key to specify which file to retrieve from.
	 * @return a list of tasks in <code>Vector<Task></code> form
	 * @throws IllegalArgumentException
	 *             If the input is not 1 or 2
	 */
	public Vector<Task> restoreDataFromFile(int key) throws JsonSyntaxException {
		if (key == TASKS_PRIORITY_INDEX) {
			readFileContentIntoStorageArray(fileSortedByPriority);
			return storedTasksByPriority;
		} else if (key == TASKS_DEADLINE_INDEX) {
			readFileContentIntoStorageArray(fileSortedByDeadline);
			return storedTasksByDeadline;
		} else if (key == TASKS_TICKED_INDEX) {
			readFileContentIntoStorageArray(fileSortedByTicked);
			return storedTasksByTicked;
		} else if (key == TASKS_KIV_INDEX) {
			readFileContentIntoStorageArray(fileSortedByKiv);
			return storedTasksByKiv;
		} else {
			throw new IllegalArgumentException(MESSAGE_PARAMETER_ILLEGAL + key);
		}
	}

	/**
	 * Check if the file exist in the directory. If exist, read content into
	 * array. Else, create new file
	 * 
	 * @param file
	 *            file to be read
	 */
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
			String fileCreationError = String.format(MESSAGE_FILE_CREATE_ERROR,
					jsonFile.getName(), ioe.getMessage());
			showToUser(fileCreationError);
			System.exit(-1);
		}
	}

	/**
	 * Retrieve data from the JSON datastore and put it into specified array in
	 * Storage
	 * 
	 * @param jsonFile
	 *            the file to read
	 * @throws JsonParseException
	 *             if the data had been tampered with
	 */
	private void readFileContentIntoStorageArray(File jsonFile)
			throws JsonParseException {
		createNewFileReader(jsonFile);
		String json = "";
		while (fileReader.hasNextLine()) {
			json += fileReader.nextLine();

		}
		try {
			Vector<Task> tasks = JSONToTasksVector(json);
			if (jsonFile.getName().equals(TASKS_DEADLINE_FILENAME)) {
				setStoredTaskByDeadline(tasks);
			} else if (jsonFile.getName().equals(TASKS_PRIORITY_FILENAME)) {
				setStoredTaskByPriority(tasks);
			} else if (jsonFile.getName().equals(TASKS_KIV_FILENAME)) {
				setStoredTaskByKiv(tasks);
			} else if (jsonFile.getName().equals(TASKS_TICKED_FILENAME)) {
				setStoredTaskByTicked(tasks);
			} else {
				throw new IllegalArgumentException(MESSAGE_FILE_INVALID);
			}
		} catch (IllegalStateException ise) {
			initFile();
			throw new IllegalStateException(MESSAGE_FILE_MODIFICATION);
		}
		fileReader.close();
	}

	/**
	 * Creates a new reader for the current text file. Program will exit if
	 * there is a file read error.
	 */
	private void createNewFileReader(File jsonFile) {
		try {
			if (jsonFile.getName() == TASKS_DEADLINE_FILENAME) {
				fileReader = new Scanner(fileSortedByDeadline);
			} else if (jsonFile.getName() == TASKS_PRIORITY_FILENAME) {
				fileReader = new Scanner(fileSortedByPriority);
			} else if (jsonFile.getName() == TASKS_KIV_FILENAME) {
				fileReader = new Scanner(fileSortedByKiv);
			} else if (jsonFile.getName() == TASKS_TICKED_FILENAME) {
				fileReader = new Scanner(fileSortedByTicked);
			} else {
				throw new FileNotFoundException();
			}
		} catch (FileNotFoundException fnfe) {
			String fileReadError = null;
			if (jsonFile.getName().equals(TASKS_DEADLINE_FILENAME)) {
				fileReadError = String.format(MESSAGE_FILE_READ_ERROR,
						fileSortedByDeadline.getName(), fnfe.getMessage());
			} else {
				fileReadError = String.format(MESSAGE_FILE_READ_ERROR,
						fileSortedByPriority.getName(), fnfe.getMessage());
			}
			showToUser(fileReadError);
			System.exit(-1);
		}
	}

	/**
	 * Writes a new empty String into the file to clear out all text. Program
	 * will exit if file write fails.
	 */
	private void clearFile() {
		try {
			fileWriter = new BufferedWriter(
					new FileWriter(fileSortedByDeadline));
			writeEmptyJsonArray();

			fileWriter = new BufferedWriter(
					new FileWriter(fileSortedByPriority));
			writeEmptyJsonArray();

			fileWriter = new BufferedWriter(new FileWriter(fileSortedByTicked));
			writeEmptyJsonArray();

			fileWriter = new BufferedWriter(new FileWriter(fileSortedByKiv));
			writeEmptyJsonArray();

			fileWriter.close();
		} catch (IOException ioe) {
			String fileWriteError = String.format(MESSAGE_FILE_WRITE_ERROR,
					fileSortedByDeadline.getName(), ioe.getMessage());
			showToUser(fileWriteError);
			System.exit(-1);
		}
	}

	/**
	 * Write the JSON empty array into the specified file
	 * 
	 * @throws IOException
	 *             if file is not found.
	 */
	private void writeEmptyJsonArray() throws IOException {
		fileWriter.write(new String(JSON_EMPTY));
		fileWriter.flush();
	}

	/**
	 * Prints the text to console and terminates the line.
	 * 
	 * @param text
	 *            The text to be printed.
	 */
	private void showToUser(String text) {
		System.out.println(text);
	}

	/**
	 * Convert the <code>Vector<Task></code> input into JSON string
	 *
	 * @param tasks
	 *            the list of tasks to be converted into JSON string
	 * @return JSON string
	 */
	protected String convertToJSON(Vector<Task> tasks) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Task.class, new CustomDeserializer());
		String json = gson.create().toJson(tasks);

		return json;
	}

	/**
	 * Convert the JSON string input into <code>Vector<Task></code>
	 *
	 * @param json
	 *            the JSON string to be converted into <code>Vector<Task></code>
	 * @return a list of tasks in <code>Vector<Task></code>
	 * @throws IllegalStateException
	 *             if the file is not in JSON format
	 */
	protected Vector<Task> JSONToTasksVector(String json)
			throws IllegalStateException, JsonParseException {
		Vector<Task> tasks = new Vector<Task>();
		GsonBuilder gson = new GsonBuilder();
		JsonParser parse = new JsonParser();

		JsonArray jsonArray = parse.parse(json).getAsJsonArray();
		gson.registerTypeAdapter(Task.class, new CustomDeserializer());

		for (int i = 0; i < jsonArray.size(); i++) {
			Task output = gson.create().fromJson(jsonArray.get(i), Task.class);
			tasks.add(output);
		}
		return tasks;
	}

	/**
	 * Set the storedTasksByPriority according to the tasks parameter
	 * 
	 * @param tasks
	 *            Vector<Tasks> to set
	 */
	public void setStoredTaskByPriority(Vector<Task> tasks) {
		storedTasksByPriority = tasks;
	}

	/**
	 * Set the storedTasksByDeadline according to the tasks parameter
	 * 
	 * @param tasks
	 *            Vector<Tasks> to set
	 */
	public void setStoredTaskByDeadline(Vector<Task> tasks) {
		storedTasksByDeadline = tasks;
	}

	/**
	 * Set the storedTasksByKIV according to the tasks parameter
	 * 
	 * @param tasks
	 *            Vector<Tasks> to set
	 */
	public void setStoredTaskByKiv(Vector<Task> tasks) {
		storedTasksByKiv = tasks;
	}

	/**
	 * Set the storedTasksByTicked according to the tasks parameter
	 * 
	 * @param tasks
	 *            Vector<Tasks> to set
	 */
	public void setStoredTaskByTicked(Vector<Task> tasks) {
		storedTasksByTicked = tasks;
	}
}