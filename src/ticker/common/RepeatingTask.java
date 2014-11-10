package ticker.common;

//@author A0114535M
/**
 * Description: This class is used for repeating tasks.
 */
public class RepeatingTask extends Task {
	// CONSTANTS
	// Integer constants for days
	private static final int SUNDAY = 0;
	private static final int MONDAY = 1;
	private static final int TUESDAY = 2;
	private static final int WEDNESDAY = 3;
	private static final int THURSDAY = 4;
	private static final int FRIDAY = 5;
	private static final int SATURDAY = 6;
	// Other integer constants
	private static final int EQUAL = 0;
	private static final int SMALLER = -1;
	// String constants
	private static final String STRING_END = "(end at ";
	private static final String STRING_START = "(start at ";
	private static final String STRING_CLOSE = ") ";
	private static final String STRING_TO = " to ";
	private static final String STRING_FROM = "(from ";
	private static final String STRING_SATURDAY = "<Saturday> ";
	private static final String STRING_FRIDAY = "<Friday> ";
	private static final String STRING_THURSDAY = "<Thursday> ";
	private static final String STRING_WEDNESDAY = "<Wednesday> ";
	private static final String STRING_TUESDAY = "<Tuesday> ";
	private static final String STRING_MONDAY = "<Monday> ";
	private static final String STRING_SUNDAY = "<Sunday> ";
	private static final String EMPTY_STRING = "";
	// String feedback
	private static final String FEEDBACK_ERROR_GET_DAY = "Error in getting Day for RepeatingTask";

	// ATTRIBUTES
	public final int id = 3; // used in storage
	private int day;

	public RepeatingTask(String description, Date date, Time startTime,
			Time endTime, char priority, boolean isRepeating) {
		super(description, date, startTime, null, endTime, priority,
				isRepeating);
		this.day = Date.dayOfWeek(date);
	}
	
	/**
	 * This method deep copies a RepeatingTask.
	 */
	public RepeatingTask copy() {
		return new RepeatingTask(this.getDescription(), this.getStartDate(),
				this.getStartTime(), this.getEndTime(), this.getPriority(),
				this.getRepeat());
	}

	public int getDay() { // 0: Sunday, 1: Monday, ... 6: Saturday
		return day;
	}

	/**
	 * This method checks for task expiry.
	 */
	@Override
	public void isExpired() {
		if (this.endDate != null
				&& this.endTime != null
				&& (this.endDate.compareTo(Date.getCurrentDate()) == SMALLER || (this.endDate
						.compareTo(Date.getCurrentDate()) == EQUAL && this.endTime
						.compareTo(Time.getCurrentTime()) == SMALLER))) {
			this.isExpired = true;
			return;
		}
		this.isExpired = false;
		return;
	}
	
	/**
	 * This method sets the start date of the repeating task.
	 */
	@Override
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
		this.day = Date.dayOfWeek(startDate);
	}
	
	/**
	 * This method prints the RepeatingTask.
	 */
	@Override
	public String toString() {
		String timing = EMPTY_STRING;

		if (getStartTime() != null && getEndTime() != null) {
			timing = STRING_FROM + getStartTime() + STRING_TO + getEndTime()
					+ STRING_CLOSE;
		} else if (getStartTime() != null) {
			timing = STRING_START + getStartTime() + STRING_CLOSE;
		} else if (getEndTime() != null) {
			timing = STRING_END + getEndTime() + STRING_CLOSE;
		}

		switch (this.day) {
		case SUNDAY:
			return STRING_SUNDAY + timing + getDescription();
		case MONDAY:
			return STRING_MONDAY + timing + getDescription();
		case TUESDAY:
			return STRING_TUESDAY + timing + getDescription();
		case WEDNESDAY:
			return STRING_WEDNESDAY + timing + getDescription();
		case THURSDAY:
			return STRING_THURSDAY + timing + getDescription();
		case FRIDAY:
			return STRING_FRIDAY + timing + getDescription();
		case SATURDAY:
			return STRING_SATURDAY + timing + getDescription();
		default:
			return FEEDBACK_ERROR_GET_DAY;
		}
	}
}
