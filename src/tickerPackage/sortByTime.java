package tickerPackage;

import java.util.Comparator;

public class sortByTime implements Comparator<Task> {
	
	public int compare(Task task1, Task task2) {

		// Comparing between TimedTasks
		if (task1.startDate != null && task2.startDate != null) {

			// Primary comparison between TimedTasks using startDate
			int startDateComparator = task1.startDate.compareTo(task2.startDate);

			if (startDateComparator != 0) {
				return startDateComparator;
			}
			// Secondary comparison between TimedTasks using startTime
			if (task1.startTime != null && task2.startTime != null) {
				int startTimeComparator = task1.startTime.compareTo(task2.startTime);

				if (startTimeComparator != 0) {
					return startTimeComparator;
				}
			}
			// TimedTasks with startTime will be above TimedTasks without startTime
			if (task1.startTime != null && task2.startTime == null) {
				return -1;
			}
			if (task1.startTime == null && task2.startTime != null) {
				return 1;
			}

			return 0;

		}

		// Comparing between TimedTask and DeadlineTask
		if (task1.startDate != null && (task2.startDate == null && task2.endDate != null)) {

			// Primary comparison between TimedTasks and DeadlineTasks using startDate and endDate respectively
			int mixedDateComparator = task1.startDate.compareTo(task2.endDate);

			if (mixedDateComparator != 0) {
				return mixedDateComparator;
			}
			// Secondary comparison between TimedTasks and DeadlineTasks using startTime and endTime respectively
			if (task1.startTime != null && task2.endTime != null) {
				int mixedTimeComparator = task1.startTime.compareTo(task2.endTime);

				if (mixedTimeComparator != 0) {
					return mixedTimeComparator;
				}
			}
			// Task with time will be before the other
			if (task1.startTime != null && task2.endTime == null) {
				return -1;
			}
			if (task1.startTime == null && task2.endTime != null) {
				return 1;
			}

			return 0;					

		}
		
		if ((task1.startDate == null && task1.endDate != null) && task2.startDate != null) {

			// Primary comparison between TimedTasks and DeadlineTasks using startDate and endDate respectively
			int mixedDateComparator = task1.startDate.compareTo(task2.endDate);

			if (mixedDateComparator != 0) {
				return mixedDateComparator;
			}
			// Secondary comparison between TimedTasks and DeadlineTasks using startTime and endTime respectively
			if (task1.endTime != null && task2.startTime != null) {
				int mixedTimeComparator = task1.endTime.compareTo(task2.startTime);

				if (mixedTimeComparator != 0) {
					return mixedTimeComparator;
				}
			}
			// Task with time will be before the other
			if (task1.endTime != null && task2.startTime == null) {
				return -1;
			}
			if (task1.endTime == null && task2.startTime != null) {
				return 1;
			}

			return 0;					

		}
		
		// Comparing TimedTasks with FloatingTasks
		// TimedTasks placed before FloatingTasks
		if (task1.startDate != null && (task2.startDate == null && task2.endDate == null)) {
			return -1;
		}
		if ((task1.startDate == null && task1.endDate == null) && task2.startDate != null) {
			return -1;
		}

		// Comparing between DeadlineTasks
		if ((task1.startDate == null && task1.endDate != null) && (task2.startDate == null && task2.endDate != null)) {
			// Primary comparison between TimedTasks and DeadlineTasks using startDate and endDate respectively
			int endDateComparator = task1.endDate.compareTo(task2.endDate);

			if (endDateComparator != 0) {
				return endDateComparator;
			}
			// Secondary comparison between TimedTasks and DeadlineTasks using startTime and endTime respectively
			if (task1.endTime != null && task2.endTime != null) {
				int endTimeComparator = task1.endTime.compareTo(task2.endTime);

				if (endTimeComparator != 0) {
					return endTimeComparator;
				}
			}
			// Task with time will be before the other
			if (task1.endTime != null && task2.endTime == null) {
				return -1;
			}
			if (task1.endTime == null && task2.endTime != null) {
				return 1;
			}

			return 0;	
		}

		// Comparing DeadlineTasks with FloatingTasks
		// DeadlineTasks placed before FloatingTasks
		if ((task1.startDate == null && task1.endDate != null) && (task2.startDate == null && task2.endDate == null)) {
			return -1;
		}
		if ((task1.startDate == null && task1.endDate == null) && (task2.startDate != null && task2.endDate == null)) {
			return 1;
		}
		
		// Comparing between FloatingTasks
		return task1.description.compareTo(task2.description);
	}

}
