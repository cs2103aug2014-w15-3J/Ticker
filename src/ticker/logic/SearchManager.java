package ticker.logic;

import java.util.Collections;
import java.util.Vector;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotoh;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotohWindowedAffine;
import ticker.common.Date;
import ticker.common.DateTime;
import ticker.common.Task;
import ticker.common.Time;
import ticker.common.TimedTask;
import ticker.common.sortByTime;
import ticker.parser.TimePeriod;

//@author A0114535M
public class SearchManager {

	// CONSTANTS
	// Char constants
	private static final char NULL_CHAR = '\u0000';
	private static final char PRIORITY_IMPORTANT = 'A';
	private static final char PRIORITY_NORMAL = 'B';
	private static final char PRIORITY_TRIVIAL = 'C';
	// Integer constants
	private static final int EQUAL = 0;
	private static final double PASSING_SIMILARITY_SCORE = 65.0;
	private static final int OFFSET_INDEX = 1;
	// String constants
	private static final String EMPTY_STRING = "";
	// String constants for command types
	private static final String COMMAND_TAKE = "take";
	// String constants for stamps
	private static final String STAMP_FREESLOT = "\\***FREE***\\";
	private static final String STAMP_KIV = "\\***KIV***\\";
	private static final String STAMP_TICKED = "\\***TICKED***\\";
	// String constants for feedback
	private static final String FEEDBACK_APPEND_IS_TAKEN = " has been added.";

	// ATTRIBUTES
	// Temporary sorted storages
	private Vector<Task> storedTasksByTime;
	private Vector<Task> storedTasksByPriority;
	private Vector<Task> storedTasksByTicked; // not sorted
	private Vector<Task> storedTasksByKIV; // not sorted
	// Temporary storage for searchResults
	private static Vector<Task> searchResultsTime;
	private static Vector<Task> searchResultsTicked;
	private static Vector<Task> searchResultsKIV;
	private static Vector<Task> searchResults;
	private static Vector<Task> freeslotList;
	private static Vector<StringMatch> matchList;

	// Instances of other components
	private UndoManager undoMng;

	public SearchManager(Vector<Task> storedTasksByTime,
			Vector<Task> storedTasksByPriority,
			Vector<Task> storedTasksByTicked, Vector<Task> storedTasksByKIV) {
		this.storedTasksByTime = storedTasksByTime;
		this.storedTasksByPriority = storedTasksByPriority;
		this.storedTasksByTicked = storedTasksByTicked;
		this.storedTasksByKIV = storedTasksByKIV;

		undoMng = UndoManager.getInstance(storedTasksByPriority,
				storedTasksByTime, storedTasksByTicked, storedTasksByKIV);

		freeslotList = new Vector<Task>();

		searchResults = new Vector<Task>();
	}

	/**
	 * This method searches for all tasks with the specified task property.
	 * Searching through description is based on the level of similarity between
	 * specified key and task description.
	 *
	 * @param key            Search description key.
	 * @param isRepeating    Query for repeating tasks.
	 * @param startDate      Starting date query.
	 * @param startTime      Starting time query.
	 * @param endDate        Ending date query.
	 * @param endTime        Ending time query.
	 * @param priority       Query for tasks with a specified level of priority
	 * @return Vector of tasks that fits the query
	 */
	public Vector<Task> search(String key, boolean isRepeating, Date startDate,
			Date endDate, Time startTime, Time endTime, char priority) {
		matchList = new Vector<StringMatch>();

		initialiseSubSearchResults();

		// Search by Key only
		if (key != null && !key.equals(EMPTY_STRING)) {
			searchByKeyInSubSearchResults(key);
		}

		if (isRepeating) {
			searchByRepeatingInSubSearchResults();
		}

		// Search by priority
		if (priority != NULL_CHAR
				&& (priority == PRIORITY_IMPORTANT
						|| priority == PRIORITY_NORMAL || priority == PRIORITY_TRIVIAL)) {
			searchByPriorityInSubSearchResults(priority);
		}

		// Search for date and time assumes that there will be a date that is
		// passed with the time
		// Search by start date and start time
		if (startDate != null) {
			if (startTime != null) {
				searchByStartDateAndTimeInSubSearchResults(startDate, startTime);
			} else if (startTime == null) {
				searchByStartDateInSubSearchResults(startDate);
			}
		}

		// Search by end date and end time
		if (endDate != null) {
			if (endTime != null) {
				searchByEndDateAndTimeInSubSearchResults(endDate, endTime);
			} else if (endTime == null) {
				searchByEndDateInSubSearchResults(endDate);
			}
		}

		collateSubSearchResults();

		return searchResults;
	}

	/**
	 * This method searches for expired tasks with the specified task property.
	 * Searching through description is based on the level of similarity between
	 * specified key and task description.
	 *
	 * @param key			Search description key.
	 * @param isRepeating	Query for repeating tasks.
	 * @param startDate		Starting date query.
	 * @param startTime		Starting time query.
	 * @param endDate		Ending date query.
	 * @param endTime		Ending time query.
	 * @param priority		Query for tasks with a specified level of priority
	 * @return Vector of tasks that fits the query
	 */
	public Vector<Task> searchExpired(String key, boolean isRepeating,
			Date startDate, Date endDate, Time startTime, Time endTime,
			char priority) {
		matchList = new Vector<StringMatch>();

		initialiseExpiredSubSearchResults();

		// Search by Key only
		if (key != null && !key.equals(EMPTY_STRING)) {
			searchByKeyInSubSearchResults(key);
		}

		if (isRepeating) {
			searchByRepeatingInSubSearchResults();
		}

		// Search by priority
		if (priority != NULL_CHAR
				&& (priority == PRIORITY_IMPORTANT
						|| priority == PRIORITY_NORMAL || priority == PRIORITY_TRIVIAL)) {
			searchByPriorityInSubSearchResults(priority);
		}

		// Search for date and time assumes that there will be a date that is
		// passed with the time
		// Search by start date and start time
		if (startDate != null) {
			if (startTime != null) {
				searchByStartDateAndTimeInSubSearchResults(startDate, startTime);
			} else if (startTime == null) {
				searchByStartDateInSubSearchResults(startDate);
			}
		}

		// Search by end date and end time
		if (endDate != null) {
			if (endTime != null) {
				searchByEndDateAndTimeInSubSearchResults(endDate, endTime);
			} else if (endTime == null) {
				searchByEndDateInSubSearchResults(endDate);
			}
		}

		collateSubSearchResults();

		return searchResults;
	}

	/**
	 * This method searches for freeslots within a certain time period.
	 *
	 * @param startDate		Starting date query.
	 * @param startTime		Starting time query.
	 * @param endDate		Ending date query.
	 * @param endTime		Ending time query.
	 * @return Vector of tasks and freeslots within the period queried.
	 */
	public Vector<Task> searchForFreeSlots(Date startDate, Time startTime,
			Date endDate, Time endTime) {
		DateTime start = new DateTime(startDate, startTime);
		DateTime end = new DateTime(endDate, endTime);
		TimePeriod timePeriod = new TimePeriod(start, end);

		freeslotList = new Vector<Task>();

		Vector<TimePeriod> result = new Vector<TimePeriod>();
		result.add(timePeriod);

		for (Task task : storedTasksByTime) {
			if (task.getStartDate() != null && task.getStartTime() != null
					&& task.getEndDate() != null && task.getEndTime() != null
					&& task.getStartDate().compareTo(startDate) >= EQUAL
					&& task.getEndDate().compareTo(endDate) <= EQUAL
					&& task.getStartTime().compareTo(startTime) >= EQUAL
					&& task.getEndTime().compareTo(endTime) <= EQUAL) {
				freeslotList.add(task);
			}
		}

		// Sort the tasks beforehand so the earliest task in the period will be
		// considered first
		// This allows us to do comparison only once
		Collections.sort(freeslotList, new sortByTime());

		for (Task task : freeslotList) {
			TimePeriod taskPeriod = new TimePeriod(new DateTime(
					task.getStartDate(), task.getStartTime()), new DateTime(
					task.getEndDate(), task.getEndTime()));

			// Check from the back
			for (int i = result.size() - 1; i >= 0; i--) {
				TimePeriod resultPeriod = result.get(i);

				// If potential free slot is within or equals the timings of a
				// scheduled task
				if (resultPeriod.getStart().compareTo(taskPeriod.getStart()) >= EQUAL
						&& resultPeriod.getEnd().compareTo(taskPeriod.getEnd()) <= EQUAL) {
					result.remove(i);

					// If potential free slot has larger period then the timings
					// of a scheduled task on both tail ends
				} else if (resultPeriod.getStart().compareTo(
						taskPeriod.getStart()) < EQUAL
						&& resultPeriod.getEnd().compareTo(taskPeriod.getEnd()) > EQUAL) {
					updateTwoTailedFreeslot(result, taskPeriod, i, resultPeriod);

					// If potential free slot has no overlap with scheduled task
					// on the left tail end
				} else if (resultPeriod.getStart().compareTo(
						taskPeriod.getStart()) < EQUAL
						&& resultPeriod.getEnd().compareTo(
								taskPeriod.getStart()) >= EQUAL) {
					updateLeftTailedFreeslot(result, taskPeriod, i,
							resultPeriod);

					// If potential free slot has no overlap with scheduled task
					// on the right tail end
				} else if (resultPeriod.getStart().compareTo(
						resultPeriod.getStart()) >= EQUAL
						&& resultPeriod.getEnd().compareTo(taskPeriod.getEnd()) > EQUAL) {
					updateRightTailedFreeslot(result, taskPeriod, i,
							resultPeriod);

					// If free slots has no overlap with tasks
				} else {
					continue;
				}
			}
		}

		mergeFreeslotsAndExistingUndoneTasks(result);
		Collections.sort(freeslotList, new sortByTime());
		return freeslotList;
	}

	/**
	 * This method merges the calculated freeslots with undone tasks.
	 * 
	 * @param result	Vector of freeslots.
	 */
	private void mergeFreeslotsAndExistingUndoneTasks(Vector<TimePeriod> result) {
		for (TimePeriod freePeriod : result) {
			TimedTask freeslot = new TimedTask(STAMP_FREESLOT,
					freePeriod.getStartDate(), freePeriod.getStartTime(),
					freePeriod.getEndDate(), freePeriod.getEndTime(),
					PRIORITY_NORMAL, false);
			freeslotList.add(freeslot);
		}
	}

	/**
	 * This method trims the freeslot as it is overlapped on the left tail.
	 * 
	 * @param result		Vector of potential freeslots.
	 * @param taskPeriod	Period of the task being compared to
	 * @param index			Index of freeslot.
	 * @param resultPeriod	Period of freeslot.
	 */
	private void updateRightTailedFreeslot(Vector<TimePeriod> result,
			TimePeriod taskPeriod, int index, TimePeriod resultPeriod) {
		result.remove(index);
		result.add(new TimePeriod(taskPeriod.getEnd(), resultPeriod.getEnd()));
	}

	/**
	 * This method trims the freeslot as it is overlapped on the right tail.
	 * 
	 * @param result		Vector of potential freeslots.
	 * @param taskPeriod	Period of the task being compared to
	 * @param index			Index of freeslot.
	 * @param resultPeriod	Period of freeslot.
	 */
	private void updateLeftTailedFreeslot(Vector<TimePeriod> result,
			TimePeriod taskPeriod, int index, TimePeriod resultPeriod) {
		result.remove(index);
		result.add(new TimePeriod(resultPeriod.getStart(), taskPeriod
				.getStart()));
	}

	/**
	 * This method trims the freeslot as it is overlapped in the middle.
	 * 
	 * @param result		Vector of potential freeslots.
	 * @param taskPeriod	Period of the task being compared to
	 * @param index			Index of freeslot.
	 * @param resultPeriod	Period of freeslot.
	 */
	private void updateTwoTailedFreeslot(Vector<TimePeriod> result,
			TimePeriod taskPeriod, int index, TimePeriod resultPeriod) {
		result.remove(index);
		result.add(new TimePeriod(resultPeriod.getStart(), taskPeriod
				.getStart()));
		result.add(new TimePeriod(taskPeriod.getEnd(), resultPeriod.getEnd()));
	}

	/**
	 * This method takes the freeslot.
	 *
	 * @param index			Index of freeslot to be taken.
	 * @param description	Name of task description that fills in the freeslot.
	 * @return Message from the action of taking a freeslot.
	 * @throws IllegalArgumentException		If event is created wrongly.
	 */
	public String take(int displayedIndex, String description)
			throws IllegalArgumentException {
		int actualIndex = getActualIndex(displayedIndex);
		TimedTask chosenSlot = (TimedTask) freeslotList.get(actualIndex);
		if (chosenSlot.getDescription() != STAMP_FREESLOT) {
			storedTasksByTime.remove(chosenSlot);
			storedTasksByPriority.remove(chosenSlot);
		}
		chosenSlot.setDescription(description);
		storedTasksByTime.add(chosenSlot);
		storedTasksByPriority.add(chosenSlot);

		// Throws IllegalArgumentException
		Event event = new Event(COMMAND_TAKE, chosenSlot);
		undoMng.add(event);

		return description + FEEDBACK_APPEND_IS_TAKEN;
	}

	/**
	 * This method collates and packages the substituent search results for the
	 * actual search result.
	 */
	private void collateSubSearchResults() {
		for (Task searchTime : searchResultsTime) {
			searchResults.add(searchTime);
		}

		searchResults.add(new Task(STAMP_TICKED, null, null, null, null,
				PRIORITY_NORMAL, false));

		for (Task searchTicked : searchResultsTicked) {
			searchResults.add(searchTicked);
		}

		searchResults.add(new Task(STAMP_KIV, null, null, null, null,
				PRIORITY_NORMAL, false));

		for (Task searchKIV : searchResultsKIV) {
			searchResults.add(searchKIV);
		}
	}

	/**
	 * This method searches tasks by their end dates.
	 * 
	 * @param endDate	Queried end date.
	 */
	private void searchByEndDateInSubSearchResults(Date endDate) {
		searchResultsTime = searchByEndDate(endDate, searchResultsTime);
		searchResultsTicked = searchByEndDate(endDate, searchResultsTicked);
		searchResultsKIV = searchByEndDate(endDate, searchResultsKIV);
	}

	/**
	 * This method searches tasks by their end dates and end times.
	 * 
	 * @param endDate	Queried end date.
	 * @param endTime	Queried end time.
	 */
	private void searchByEndDateAndTimeInSubSearchResults(Date endDate,
			Time endTime) {
		searchResultsTime = searchByEndDateAndTime(endDate, endTime,
				searchResultsTime);
		searchResultsTicked = searchByEndDateAndTime(endDate, endTime,
				searchResultsTicked);
		searchResultsKIV = searchByEndDateAndTime(endDate, endTime,
				searchResultsKIV);
	}

	/**
	 * This method searches tasks by their start dates.
	 * 
	 * @param startDate		Queried start date.
	 */
	private void searchByStartDateInSubSearchResults(Date startDate) {
		searchResultsTime = searchByStartDate(startDate, searchResultsTime);
		searchResultsTicked = searchByStartDate(startDate, searchResultsTicked);
		searchResultsKIV = searchByStartDate(startDate, searchResultsKIV);
	}

	/**
	 * This method searches tasks by their start dates and start times.
	 * 
	 * @param startDate		Queried start date.
	 * @param startTime		Queried start time.
	 */
	private void searchByStartDateAndTimeInSubSearchResults(Date startDate,
			Time startTime) {
		searchResultsTime = searchByStartDateAndTime(startDate, startTime,
				searchResultsTime);
		searchResultsTicked = searchByStartDateAndTime(startDate, startTime,
				searchResultsTicked);
		searchResultsKIV = searchByStartDateAndTime(startDate, startTime,
				searchResultsKIV);
	}

	/**
	 * This method searches tasks by their priority.
	 * 
	 * @param priority		Queried level of priority.
	 */
	private void searchByPriorityInSubSearchResults(char priority) {
		searchResultsTime = searchByPriority(priority, searchResultsTime);
		searchResultsTicked = searchByPriority(priority, searchResultsTicked);
		searchResultsKIV = searchByPriority(priority, searchResultsKIV);
	}

	/**
	 * This method searches for repeating tasks.
	 */
	private void searchByRepeatingInSubSearchResults() {
		searchResultsTime = searchRepeating(searchResultsTime);
		searchResultsTicked = searchRepeating(searchResultsTicked);
		searchResultsKIV = searchRepeating(searchResultsKIV);
	}

	/**
	 * This method searches tasks by their description and their level of
	 * similarity with the key.
	 * 
	 * @param key	Queried key.
	 */
	private void searchByKeyInSubSearchResults(String key) {
		searchResultsTime = searchByKey(key, searchResultsTime);
		searchResultsTicked = searchByKey(key, searchResultsTicked);
		searchResultsKIV = searchByKey(key, searchResultsKIV);
	}

	/**
	 * This method initialises the substituent search results.
	 */
	private void initialiseSubSearchResults() {
		searchResultsTime = storedTasksByTime;
		searchResultsTicked = storedTasksByTicked;
		searchResultsKIV = storedTasksByKIV;
	}

	/**
	 * This method initialises substituent search results with expired tasks.
	 */
	private void initialiseExpiredSubSearchResults() {
		searchResultsTime = searchExpired(storedTasksByTime);
		searchResultsTicked = searchExpired(storedTasksByTicked);
		searchResultsKIV = searchExpired(storedTasksByKIV);
	}

	/**
	 * This method searches for expired task within a tasklist.
	 *
	 * @param taskList		List of tasks to be searched from.
	 * @return Vector of expired tasks.
	 */
	private Vector<Task> searchExpired(Vector<Task> taskList) {
		Vector<Task> searchResult = new Vector<Task>();

		for (Task task : taskList) {
			if (task.getIsExpired()) {
				searchResult.add(task);
			}
		}

		return searchResult;
	}

	/**
	 * This method searches for repeating task within a tasklist.
	 *
	 * @param taskList		List of tasks to be searched from.
	 * @return Vector of repeating tasks.
	 */
	private Vector<Task> searchRepeating(Vector<Task> taskList) {
		Vector<Task> searchResult = new Vector<Task>();

		for (Task task : taskList) {
			if (task.getRepeat()) {
				searchResult.add(task);
			}
		}

		return searchResult;
	}

	/**
	 * This method searches for tasks through a word in the task description
	 * (exact or with errors) within a tasklist.
	 *
	 * @param taskList		List of tasks to be searched from.
	 * @return Vector of tasks that passes the specified SimMetrics similarity
	 *         points.
	 */
	private Vector<Task> searchByKey(String key, Vector<Task> taskList) {
		Vector<Task> searchResult = new Vector<Task>();
		matchList.removeAllElements();

		calculateSimilarityScore(key, taskList);

		Collections.sort(matchList, new StringMatchComparator());

		for (StringMatch sm : matchList) {
			if (sm.getSimilarityScore() < PASSING_SIMILARITY_SCORE) {
				break;
			}
			searchResult.add(taskList.get(sm.getIndex()));
		}
		return searchResult;
	}

	/**
	 * This method calcultes the individual tasks similarity score and stores
	 * them in matchlist.
	 * 
	 * @param key		Queried key.
	 * @param taskList	List of tasks to be searched from.
	 */
	private void calculateSimilarityScore(String key, Vector<Task> taskList) {
		for (int i = 0; i < taskList.size(); i++) {
			float score = getMatchLikelyhood(key.toLowerCase(), taskList.get(i)
					.toString().toLowerCase());
			matchList.add(new StringMatch(i, score));
		}
	}

	/**
	 * This method searches for tasks of a certain level of priority within a
	 * tasklist.
	 *
	 * @param taskList		List of tasks to be searched from.
	 * @return Vector of tasks with the specified level of priority.
	 */
	private Vector<Task> searchByPriority(char priority, Vector<Task> taskList) {
		Vector<Task> searchResult = new Vector<Task>();

		for (Task task : taskList) {
			if (task.getPriority() == priority) {
				searchResult.add(task);
			}
		}
		return searchResult;
	}

	/**
	 * This method searches for all tasks starting from from the start date and
	 * start time.
	 *
	 * @param taskList		List of tasks to be searched from.
	 * @param startDate		Starting date query.
	 * @param startTime		Starting time query.
	 * @return Vector of tasks that fits the query
	 */
	private Vector<Task> searchByStartDateAndTime(Date startDate,
			Time startTime, Vector<Task> taskList) {
		Vector<Task> searchResult = new Vector<Task>();

		for (Task task : taskList) {
			if (task.getStartDate() != null && task.getStartTime() != null
					&& task.getStartDate().compareTo(startDate) >= EQUAL
					&& task.getStartTime().compareTo(startTime) >= EQUAL) {
				searchResult.add(task);
				continue;
			}
			if (task.getStartDate() != null && task.getStartTime() == null
					&& task.getStartDate().compareTo(startDate) >= EQUAL) {
				searchResult.add(task);
				continue;
			}
			if (task.getEndDate() != null && task.getEndTime() != null
					&& task.getEndDate().compareTo(startDate) >= EQUAL
					&& task.getEndTime().compareTo(startTime) >= EQUAL) {
				searchResult.add(task);
				continue;
			}
			if (task.getEndDate() != null && task.getEndTime() == null
					&& task.getEndDate().compareTo(startDate) >= EQUAL) {
				searchResult.add(task);
			}
		}
		return searchResult;
	}

	/**
	 * This method searches for all tasks starting from from the start date
	 * without start time.
	 *
	 * @param taskList		List of tasks to be searched from.
	 * @param startDate		Starting date query.
	 * @return Vector of tasks that fits the query
	 */
	private Vector<Task> searchByStartDate(Date startDate, Vector<Task> taskList) {
		Vector<Task> searchResult = new Vector<Task>();

		for (Task task : taskList) {
			// Add tasks that have starting date before queried startDate
			if (task.getStartDate() != null
					&& task.getStartDate().compareTo(startDate) >= EQUAL) {
				searchResult.add(task);
				continue;
			}
			// Add tasks that have deadline after startDate
			if (task.getEndDate() != null
					&& task.getEndDate().compareTo(startDate) >= EQUAL) {
				searchResult.add(task);
				continue;
			}
			// Add tasks that started before startDate and is still spanning
			// across queried time period
			if (task.getStartDate() != null && task.getEndDate() == null
					&& task.getStartDate().compareTo(startDate) < EQUAL) {
				searchResult.add(task);
			}
		}
		return searchResult;
	}

	/**
	 * This method searches for all tasks till the end date and end time.
	 *
	 * @param taskList		List of tasks to be searched from.
	 * @param endDate		Ending date query.
	 * @param endTime		Ending time query.
	 * @return Vector of tasks that fits the query
	 */
	private Vector<Task> searchByEndDateAndTime(Date endDate, Time endTime,
			Vector<Task> taskList) {
		Vector<Task> searchResult = new Vector<Task>();

		for (Task task : taskList) {
			if (task.getEndDate() != null && task.getEndTime() != null
					&& task.getEndDate().compareTo(endDate) <= EQUAL
					&& task.getEndTime().compareTo(endTime) <= EQUAL) {
				searchResult.add(task);
				continue;
			}
			if (task.getEndDate() != null && task.getEndTime() == null
					&& task.getEndDate().compareTo(endDate) <= EQUAL) {
				searchResult.add(task);
				continue;
			}
			if (task.getStartDate() != null && task.getStartTime() != null
					&& task.getStartDate().compareTo(endDate) <= EQUAL
					&& task.getStartTime().compareTo(endTime) <= EQUAL) {
				searchResult.add(task);
				continue;
			}
			if (task.getStartDate() != null
					&& task.getStartDate().compareTo(endDate) < EQUAL) {
				searchResult.add(task);
				continue;
			}
			if (task.getStartDate() != null && task.getStartTime() == null
					&& task.getStartDate().compareTo(endDate) == EQUAL) {
				searchResult.add(task);
			}
		}
		return searchResult;
	}

	/**
	 * This method searches for all tasks till the end date without end time.
	 *
	 * @param taskList		List of tasks to be searched from.
	 * @param endDate		Ending date query.
	 * @return Vector of tasks that fits the query
	 */
	private Vector<Task> searchByEndDate(Date endDate, Vector<Task> taskList) {
		Vector<Task> searchResult = new Vector<Task>();

		for (Task task : taskList) {
			// Deadline tasks and scheduled tasks that end before endDate are
			// added
			if (task.getEndDate() != null
					&& task.getEndDate().compareTo(endDate) <= EQUAL) {
				searchResult.add(task);
				continue;
			}
			// Scheduled tasks that start before endDate are added
			if (task.getStartDate() != null
					&& task.getStartDate().compareTo(endDate) <= EQUAL) {
				searchResult.add(task);
				continue;
			}
		}
		return searchResult;
	}

	/**
	 * This method calculates the actual index of the task displayed in UI.
	 *
	 * @param index		Index of the specified task displayed in UI.
	 * @return Actual index of tasklist.
	 */
	private int getActualIndex(int index) {
		return index - OFFSET_INDEX;
	}

	// @author A0114535M-reused
	/**
	 * This method determines the level of similarity between the task
	 * description and the given key (used library from SimMetrics)
	 *
	 * @param key				Key to be searched for.
	 * @param taskDescription	Description of the task
	 * @return Similarity points between the key and task description.
	 */
	private static float getMatchLikelyhood(String key,
			final String taskDescription) {
		AbstractStringMetric metric;
		float avg = 0F, result = 0F;
		metric = new SmithWaterman();
		result = metric.getSimilarity(key, taskDescription);
		avg += result;
		metric = new SmithWatermanGotoh();
		result = metric.getSimilarity(key, taskDescription);
		avg += result;
		metric = new SmithWatermanGotohWindowedAffine();
		result = metric.getSimilarity(key, taskDescription);
		avg += result;
		metric = new MongeElkan();
		result = metric.getSimilarity(key, taskDescription);
		avg += result;
		return (avg / 4.0F) * 100.0F;
	}
}
