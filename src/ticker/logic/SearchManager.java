//@author A0114535M

/* Team ID: W15-3J
 * Name: Li Jia'En, Nicholette
 * Matric Number: A0114535M
 * Project Title: CE1 TextBuddy
 * Purpose: This class receives text commands from the user and edits a textfile. 
 * The commands are for add, display, delete, clear and exit.
 * Assumptions: 
 * This program assumes that:
 * -the user knows the format for each command
 * -the user input lines in the textfile is not numbered.
 * -(option c) the file is saved to disk when the user exit the program
 */

package ticker.logic;

// Package Common
import ticker.common.Date;
import ticker.common.Task;
import ticker.common.Time;
import ticker.common.sortByTime;

// Package Java util
import java.util.Collections;
import java.util.Vector;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.ChapmanLengthDeviation;
import uk.ac.shef.wit.simmetrics.similaritymetrics.ChapmanMatchingSoundex;
import uk.ac.shef.wit.simmetrics.similaritymetrics.ChapmanMeanLength;
import uk.ac.shef.wit.simmetrics.similaritymetrics.EuclideanDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import uk.ac.shef.wit.simmetrics.similaritymetrics.NeedlemanWunch;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotoh;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotohWindowedAffine;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Soundex;

// TODO: check description by end of project
/*
 * Class: SearchManager
 * Description: Calculates the level of similarity between the search key and the existing tasks in
 * the task list. Returns only the tasks with appointed level of similarity.
 */

public class SearchManager {
	Vector<StringMatch> matchList;
	private Vector<Task> storedTasksByTime;
	private Vector<Task> storedTasksByTicked; // not sorted
	private Vector<Task> storedTasksByKIV; // not sorted
	private static Vector<Task> searchResultsTime;
	private static Vector<Task> searchResultsTicked;
	private static Vector<Task> searchResultsKIV;
	private static Vector<Task> searchResults;
	String key; 
	
	/**
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
	public SearchManager(Vector<Task> storedTasksByTime, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByKIV) {
		this.storedTasksByTime = storedTasksByTime;
		this.storedTasksByTicked = storedTasksByTicked;
		this.storedTasksByKIV = storedTasksByKIV;
		
		searchResults = new Vector<Task>();
	}
	
	/**
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
	public Vector<Task> search(String key, boolean isRepeating, Date startDate, Date endDate, Time startTime, Time endTime,
			char priority) {
		matchList = new Vector<StringMatch>();
		this.key = key;
		
		searchResultsTime = storedTasksByTime;
		searchResultsTicked = storedTasksByTicked;
		searchResultsKIV = storedTasksByKIV;

		// Search by Key only
		if (key != null && key.length() != 0) {
			searchResultsTime = searchByKey(key, storedTasksByTime);
			searchResultsTicked = searchByKey(key, storedTasksByTicked);
			searchResultsKIV = searchByKey(key, storedTasksByKIV);
		}
		//TODO: implement isRepeat Search
		// Search by priority
		if (priority != '\u0000' && (priority == 'A' || priority == 'B' || priority == 'C')) {
			searchResultsTime = searchByPriority(priority, storedTasksByTime);
			searchResultsTicked = searchByPriority(priority, storedTasksByTicked);
			searchResultsKIV = searchByPriority(priority, storedTasksByKIV);
		}
		
		// Search for date and time assumes that there will be a date that is passed with the time
		// Search by start date and start time
		if (startDate != null) {
			if (startTime != null) {
				searchResultsTime = searchByStartDateAndTime(startDate, startTime, searchResultsTime);
				searchResultsTicked = searchByStartDateAndTime(startDate, startTime, searchResultsTicked);
				searchResultsKIV = searchByStartDateAndTime(startDate, startTime, searchResultsKIV);
			}
			else if (startTime == null) {
				searchResultsTime = searchByStartDate(startDate, searchResultsTime);
				searchResultsTicked = searchByStartDate(startDate, searchResultsTicked);
				searchResultsKIV = searchByStartDate(startDate, searchResultsKIV);
			}
			
		}

		// Search by end date and end time
		if (endDate != null) {
			if (endTime != null) {
				searchResultsTime = searchByEndDateAndTime(endDate, endTime, searchResultsTime);
				searchResultsTicked = searchByEndDateAndTime(endDate, endTime, searchResultsTicked);
				searchResultsKIV = searchByEndDateAndTime(endDate, endTime, searchResultsKIV);
			}
			else if (endTime == null) {
				searchResultsTime = searchByEndDate(endDate, searchResultsTime);
				searchResultsTicked = searchByEndDate(endDate, searchResultsTicked);
				searchResultsKIV = searchByEndDate(endDate, searchResultsKIV);
			}
		}

		for (Task searchTime: searchResultsTime) {
			searchResults.add(searchTime);
		}

		searchResults.add(new Task("\\***TICKED***\\", null, null, null, null, 'B', false));

		for (Task searchTicked: searchResultsTicked) {
			searchResults.add(searchTicked);
		}

		searchResults.add(new Task("\\***KIV***\\", null, null, null, null, 'B', false));

		for (Task searchKIV: searchResultsKIV) {
			searchResults.add(searchKIV);
		}

		return searchResults;

	}

	/**
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
	private Vector<Task> searchByKey(String key, Vector<Task> taskList) {
		Vector<Task> temp = new Vector<Task>();
		matchList.removeAllElements();
		
		int i = 0;
		for (Task task: taskList) {
			float score = getMatchLikelyhood(key.toLowerCase(), task.toString().toLowerCase());
			System.out.println(score);
			matchList.add(new StringMatch(i, score));
			i++;
		}

		Collections.sort(matchList, new StringMatchComparator());

		for (StringMatch sm : matchList) {
			if (sm.getSimilarityScore() < 65.0) {
				break;
			}
			temp.add(taskList.get(sm.getIndex()));
		}
		return temp;
	}
	
	/**
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
	private Vector<Task> searchByPriority(char priority, Vector<Task> taskList) {
		Vector<Task> temp = new Vector<Task>();

		for (Task task: taskList) {
			if (task.getPriority() == priority) {
				temp.add(task);
			}
		}
		return temp;
	}
	
	/**
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
	private Vector<Task> searchByStartDateAndTime(Date startDate, Time startTime, Vector<Task> taskList) {
		Vector<Task> temp = new Vector<Task>();

		for (Task task: taskList) {
			if (task.getStartDate() != null && task.getStartTime() != null 
					&& task.getStartDate().compareTo(startDate) >= 0 && task.getStartTime().compareTo(startTime) >= 0) {
				temp.add(task);
				continue;
			}
			if (task.getStartDate() != null && task.getStartTime() == null && task.getStartDate().compareTo(startDate) >= 0) {
				temp.add(task);
				continue;
			}
			if (task.getEndDate() != null && task.getEndTime() != null  && task.getEndDate().compareTo(startDate) >= 0 
					&& task.getEndTime().compareTo(startTime) >= 0) {
				temp.add(task);
				continue;
			}
			if (task.getEndDate() != null && task.getEndTime() == null && task.getEndDate().compareTo(startDate) >= 0) {
				temp.add(task);
			}
		}
		return temp;
	}
		
	/**
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
	private Vector<Task> searchByStartDate(Date startDate, Vector<Task> taskList) {
		Vector<Task> temp = new Vector<Task>();

		for (Task task: taskList) {
			if (task.getStartDate() != null && task.getStartDate().compareTo(startDate) >= 0) {
				temp.add(task);
				continue;
			}
			if (task.getEndDate() != null && task.getEndDate().compareTo(startDate) >= 0) {
				temp.add(task);
			}
		}
		return temp;
	}
	
	/**
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
	private Vector<Task> searchByEndDateAndTime(Date endDate, Time endTime, Vector<Task> taskList) {
		Vector<Task> temp = new Vector<Task>();

		for (Task task: taskList) {
			if (task.getEndDate() != null && task.getEndTime() != null && task.getEndDate().compareTo(endDate) <= 0 
					&& task.getEndTime().compareTo(endTime) <= 0) {
				temp.add(task);
				continue;
			}
			if (task.getEndDate() != null && task.getEndTime() == null && task.getEndDate().compareTo(endDate) <= 0) {
				temp.add(task);
				continue;
			}
			if (task.getStartDate() != null && task.getStartTime() != null && task.getStartDate().compareTo(endDate) <= 0
					&& task.getStartTime().compareTo(endTime) <= 0) {
				temp.add(task);
				continue;
			}
			if (task.getStartDate() != null && task.getStartDate().compareTo(endDate) < 0) {
				temp.add(task);
				continue;
			}
			if (task.getStartDate() != null && task.getStartTime() == null && task.getStartDate().compareTo(endDate) == 0) {
				temp.add(task);
			}
		}
		return temp;
	}
	
	/**
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
	private Vector<Task> searchByEndDate(Date endDate, Vector<Task> taskList) {
		Vector<Task> temp = new Vector<Task>();

		for (Task task: taskList) {
			if (task.getEndDate() != null && task.getEndDate().compareTo(endDate) <= 0) {
				temp.add(task);
				continue;
			}
			if (task.getStartDate() != null && task.getStartDate().compareTo(endDate) <= 0) {
				temp.add(task);
			}
		}
		return temp;
	}
	
	/**
	 * This method determines the action for each user command.
	 *
	 * @param userCommand Command from the user.
	 * @param fileName    Name of textfile.
	 * @param commandType Type of command from the user.
	 * @param input       Name of temporary data structure containing the contents.
	 * @return     Message from the action of the userCommand.
	 * @throws Error  If commandType is unidentified.
	 */
	private static float getMatchLikelyhood(final String str1, final String str2) {
		AbstractStringMetric metric;
		float avg = 0F, result = 0F;
		metric = new SmithWaterman();
		result = metric.getSimilarity(str1, str2);
		avg += result;
		metric = new SmithWatermanGotoh();
		result = metric.getSimilarity(str1, str2);
		avg += result;
		metric = new SmithWatermanGotohWindowedAffine();
		result = metric.getSimilarity(str1, str2);
		avg += result;
		metric = new MongeElkan();
		result = metric.getSimilarity(str1, str2);
		avg += result;
		return (avg / 4.0F) * 100.0F;
	}
}
