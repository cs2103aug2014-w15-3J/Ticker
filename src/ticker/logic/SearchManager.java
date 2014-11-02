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
	private Vector<Task> storedTasksByCMI; // not sorted
	private static Vector<Task> searchResultsTime;
	private static Vector<Task> searchResultsTicked;
	private static Vector<Task> searchResultsCMI;
	private static Vector<Task> searchResults;
	String key; 

	public SearchManager(Vector<Task> storedTasksByTime, Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByCMI) {
		this.storedTasksByTime = storedTasksByTime;
		this.storedTasksByTicked = storedTasksByTicked;
		this.storedTasksByCMI = storedTasksByCMI;
		
		searchResults = new Vector<Task>();
	}

	public Vector<Task> search(String key, boolean isRepeating, Date startDate, Date endDate, Time startTime, Time endTime,
			char priority) {
		matchList = new Vector<StringMatch>();
		this.key = key;
		
		searchResultsTime = storedTasksByTime;
		searchResultsTicked = storedTasksByTicked;
		searchResultsCMI = storedTasksByCMI;

		// Search by Key only
		if (key != null && key.length() != 0) {
			searchResultsTime = searchByKey(key, storedTasksByTime);
			searchResultsTicked = searchByKey(key, storedTasksByTicked);
			searchResultsCMI = searchByKey(key, storedTasksByCMI);
		}
		//TODO: implement isRepeat Search
		// Search by priority
		if (priority != '\u0000' && (priority == 'A' || priority == 'B' || priority == 'C')) {
			searchResultsTime = searchByPriority(priority, storedTasksByTime);
			searchResultsTicked = searchByPriority(priority, storedTasksByTicked);
			searchResultsCMI = searchByPriority(priority, storedTasksByCMI);
		}

		// Search by start date and start time
		if (startDate != null) {
			if (startTime != null) {
				searchResultsTime = searchByStartDateAndTime(startDate, startTime, searchResultsTime);
				searchResultsTicked = searchByStartDateAndTime(startDate, startTime, searchResultsTicked);
				searchResultsCMI = searchByStartDateAndTime(startDate, startTime, searchResultsCMI);
			}
			else if (startTime == null) {
				searchResultsTime = searchByStartDate(startDate, searchResultsTime);
				searchResultsTicked = searchByStartDate(startDate, searchResultsTicked);
				searchResultsCMI = searchByStartDate(startDate, searchResultsCMI);
			}
			
		}

		// Search by end date and end time
		if (endDate != null) {
			if (endTime != null) {
				searchResultsTime = searchByEndDateAndTime(endDate, endTime, searchResultsTime);
				searchResultsTicked = searchByEndDateAndTime(endDate, endTime, searchResultsTicked);
				searchResultsCMI = searchByEndDateAndTime(endDate, endTime, searchResultsCMI);
			}
			else if (endTime == null) {
				searchResultsTime = searchByEndDate(endDate, searchResultsTime);
				searchResultsTicked = searchByEndDate(endDate, searchResultsTicked);
				searchResultsCMI = searchByEndDate(endDate, searchResultsCMI);
			}
		}

		for (Task searchTime: searchResultsTime) {
			searchResults.add(searchTime);
		}

		searchResults.add(new Task("\\***TICKED***\\", null, null, null, null, 'B', false));

		for (Task searchTicked: searchResultsTicked) {
			searchResults.add(searchTicked);
		}

		searchResults.add(new Task("\\***CMI***\\", null, null, null, null, 'B', false));

		for (Task searchCMI: searchResultsCMI) {
			searchResults.add(searchCMI);
		}

		return searchResults;

	}

	/**
	 * @param key
	 * @param taskList
	 * @return
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

	private Vector<Task> searchByPriority(char priority, Vector<Task> taskList) {
		Vector<Task> temp = new Vector<Task>();

		for (Task task: taskList) {
			if (task.getPriority() == priority) {
				temp.add(task);
			}
		}
		return temp;
	}

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
