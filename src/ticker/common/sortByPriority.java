package ticker.common;

import java.util.Comparator;

//@author A0114535M
/**
 * Description: This class implements the Comparator interface and is 
 * used to sort tasks according to these tiers-
 * 1: Priority (sorted with highest priority first)
 * 2: Timing (sorted with earliest deadline first)
 * 3: Task type (TimedTask, DeadlineTask, FloatingTask, RepeatingTask)
 * 4: Lexicographical order
 */
public class sortByPriority implements Comparator<Task> {

	// CONSTANTS
	private static final int EQUAL = 0;
	private static final int BIGGER = 1;
	private static final int SMALLER = -1;

	/**
	 * This method compares the tasks by their priority, date, time, task type
	 * and lexicographical string description.
	 */
	public int compare(Task task1, Task task2) {
		// Comparing between Priority
		if (task1.priority != task2.priority) {
			return task1.priority - task2.priority;
		}

		// Comparing between non-repeating tasks and RepeatingTasks
		if (task1.isRepeating == false && task2.isRepeating == true) {
			return SMALLER;
		}
		if (task1.isRepeating == true && task2.isRepeating == false) {
			return BIGGER;
		}

		// Comparing between RepeatingTasks
		if (task1.isRepeating == true && task2.isRepeating == true) {
			RepeatingTask rt1 = (RepeatingTask) task1;
			RepeatingTask rt2 = (RepeatingTask) task2;
			return rt1.getDay() - rt2.getDay();
		}

		if (task1.startDate != null && task2.startDate != null) {

			// Primary comparison between TimedTasks using startDate
			int startDateComparator = task1.startDate
					.compareTo(task2.startDate);

			if (startDateComparator != EQUAL) {
				return startDateComparator;
			}

			// Secondary comparison between TimedTasks using startTime
			if (task1.startTime != null && task2.startTime != null) {
				int startTimeComparator = task1.startTime
						.compareTo(task2.startTime);

				if (startTimeComparator != EQUAL) {
					return startTimeComparator;
				}
			}
			// TimedTasks with startTime will be above TimedTasks without
			// startTime
			if (task1.startTime != null && task2.startTime == null) {
				return SMALLER;
			}
			if (task1.startTime == null && task2.startTime != null) {
				return BIGGER;
			}

			// Tertiary comparison between TimedTasks using description
			return task1.description.compareToIgnoreCase(task2.description);
		}

		// Comparing between TimedTask and DeadlineTask
		if (task1.startDate != null
				&& (task2.startDate == null && task2.endDate != null)) {

			// Primary comparison between TimedTasks and DeadlineTasks using
			// startDate and endDate respectively
			int mixedDateComparator = task1.startDate.compareTo(task2.endDate);

			if (mixedDateComparator != EQUAL) {
				return mixedDateComparator;
			}

			// Secondary comparison between TimedTasks and DeadlineTasks using
			// startTime and endTime respectively
			if (task1.startTime != null && task2.endTime != null) {
				int mixedTimeComparator = task1.startTime
						.compareTo(task2.endTime);

				if (mixedTimeComparator != EQUAL) {
					return mixedTimeComparator;
				}
			}
			// Task with time will be before the other
			if (task1.startTime != null && task2.endTime == null) {
				return SMALLER;
			}
			if (task1.startTime == null && task2.endTime != null) {
				return BIGGER;
			}

			// Tertiary comparison using description
			return task1.description.compareToIgnoreCase(task2.description);
		}

		if ((task1.startDate == null && task1.endDate != null)
				&& task2.startDate != null) {

			// Primary comparison between TimedTasks and DeadlineTasks using
			// startDate and endDate respectively
			int mixedDateComparator = task1.endDate.compareTo(task2.startDate);

			if (mixedDateComparator != EQUAL) {
				return mixedDateComparator;
			}

			// Secondary comparison between TimedTasks and DeadlineTasks using
			// startTime and endTime respectively
			if (task1.endTime != null && task2.startTime != null) {
				int mixedTimeComparator = task1.endTime
						.compareTo(task2.startTime);

				if (mixedTimeComparator != EQUAL) {
					return mixedTimeComparator;
				}
			}
			// Task with time will be before the other
			if (task1.endTime != null && task2.startTime == null) {
				return SMALLER;
			}
			if (task1.endTime == null && task2.startTime != null) {
				return BIGGER;
			}

			// Tertiary comparison using description
			return task1.description.compareToIgnoreCase(task2.description);
		}

		// Comparing TimedTasks with FloatingTasks
		// TimedTasks placed before FloatingTasks
		if (task1.startDate != null
				&& (task2.startDate == null && task2.endDate == null)) {
			return SMALLER;
		}
		if ((task1.startDate == null && task1.endDate == null)
				&& task2.startDate != null) {
			return BIGGER;
		}

		// Comparing between DeadlineTasks
		if ((task1.startDate == null && task1.endDate != null)
				&& (task2.startDate == null && task2.endDate != null)) {
			// Primary comparison between TimedTasks and DeadlineTasks using
			// startDate and endDate respectively
			int endDateComparator = task1.endDate.compareTo(task2.endDate);

			if (endDateComparator != EQUAL) {
				return endDateComparator;
			}

			// Secondary comparison between TimedTasks and DeadlineTasks using
			// startTime and endTime respectively
			if (task1.endTime != null && task2.endTime != null) {
				int endTimeComparator = task1.endTime.compareTo(task2.endTime);

				if (endTimeComparator != EQUAL) {
					return endTimeComparator;
				}
			}
			// Task with time will be before the other
			if (task1.endTime != null && task2.endTime == null) {
				return SMALLER;
			}
			if (task1.endTime == null && task2.endTime != null) {
				return BIGGER;
			}

			// Tertiary comparison between DeadlineTasks using description
			return task1.description.compareToIgnoreCase(task2.description);
		}

		// Comparing DeadlineTasks with FloatingTasks
		// DeadlineTasks placed before FloatingTasks
		if ((task1.startDate == null && task1.endDate != null)
				&& (task2.startDate == null && task2.endDate == null)) {
			return SMALLER;
		}
		if ((task1.startDate == null && task1.endDate == null)
				&& (task2.startDate == null && task2.endDate != null)) {
			return BIGGER;
		}

		// Comparing between FloatingTasks
		return task1.description.compareToIgnoreCase(task2.description);
	}
}
