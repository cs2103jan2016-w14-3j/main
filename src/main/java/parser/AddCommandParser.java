/* @@author A0127481E */
package main.java.parser;

/* import statements */
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import main.java.enumeration.PriorityLevel;
import main.java.enumeration.TaskType;
import main.java.exception.InvalidInputFormatException;

/**
 * This class formats and parses the add command.
 * @author Ouyang Danwen
 *
 */
public class AddCommandParser {

	/* error messages used in this class */
	private static final String ERROR_MESSAGE_EMPTY_TASK = "Cannot add an empty task!";
	private static final String ERROR_MESSAGE_MISSING_TASK_NAME = "Task name is missing!";
	private static final String ERROR_MESSAGE_AMBIGUOUS_TIME = "Ambiguous time entered!";
	private static final String ERROR_MESSAGE_UNSUPPORTED_DATE_FORMAT = ""
			+ "Date format not supported!";
	private static final String ERROR_MESSAGE_INVALID_PRIORITY = ""
			+ "Please enter a valid priority level";

	/* string constants used in this class */
	private static final String DEADLINE_FLAG_BY = "by";
	private static final String DEADLINE_FLAG_BEFORE = "before";
	private static final String EVENT_FLAG_ON = "on";
	private static final String EVENT_FLAG_AT = "at";
	private static final String DURATION_FLAG_FROM = "from";
	private static final String DURATION_FLAG_TO = "to";
	private static final String PRIORITY_FLAG = "#";	
	private static final String TIME_EMPTY = "[]";
	private static final String TIME_SEPARATOR = ":";
	private static final String EXTRA_WHITE_SPACES = "\\s+";
	private static final String STRING_WHITE_SPACE = " ";
	private static final String QUOTATION = "\"";
	private static final String TOMORROW_IN_FULL = "tomorrow";
	private static final String TOMORROW_IN_SHORT = "tmr";
	private static final String STRING_EMPTY = "";
	private static final String STRING_NOW = "now";
	private static final String STRING_TODAY = "today";
	private static final String DEFAULT_TIME = "8am";
	private static final String TASK_OVERDUE = "overdue";
	private static final String TASK_UPCOMING = "upcoming";
	private static final String TASK_FLOATING = "floating";
	private static final String ALIAS_PRIORITY_HIGH = "h";
	private static final String ALIAS_1_PRIORITY_MEDIUM = "med";
	private static final String ALIAS_2_PRIORITY_MEDIUM = "m";
	private static final String ALIAS_PRIORITY_LOW = "l";

	/* numeric constants used in this class */
	private static final int FIELD_NOT_EXIST = -1;
	private static final int TASK = 0;
	private static final int TIME = 1;
	private static final int PRIORITY = 2;
	private static final int TASK_TYPE = 3;
	private static final int STATUS = 4;

	/* attributes of the class */
	protected PrettyTimeParser timeParser;

	public AddCommandParser() {
		timeParser = new PrettyTimeParser();
	}

	/**
	 * @param commandContent
	 * @return parameters of the command as a string array
	 * @throws InvalidInputFormatException
	 */
	public String[] determineParameters(String commandContent) 
			throws InvalidInputFormatException {
		assert commandContent != null;

		//task description cannot be empty
		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException(ERROR_MESSAGE_EMPTY_TASK);
		}

		//determine parameters of the command
		String formattedCommandContent = formatToStandardCommandContent(commandContent);
		String timeSegment = determineTimeSegment(formattedCommandContent.toLowerCase());
		String[] commandParameters;
		commandParameters = setParameters(formattedCommandContent, timeSegment);

		return commandParameters;
	}

	/**
	 * @param formattedCommandContent
	 * @param timeSegment
	 * @return the command parameters after setting them
	 * @throws InvalidInputFormatException
	 */
	private String[] setParameters(String formattedCommandContent, String timeSegment) 
			throws InvalidInputFormatException {
		assert formattedCommandContent != null;
		assert timeSegment != null;

		String[] commandParameters = new String[5];

		//determine all the parameters
		commandParameters[TASK] = determineTask(formattedCommandContent);

		if (commandParameters[TASK].isEmpty()) {       //task name cannot be empty
			throw new InvalidInputFormatException(ERROR_MESSAGE_MISSING_TASK_NAME);
		}
		commandParameters[TIME] = determineTime(timeSegment);
		commandParameters[PRIORITY] = determinePriority(formattedCommandContent);
		commandParameters[TASK_TYPE] = determineTaskType(formattedCommandContent);
		commandParameters[STATUS] = determineStatus(timeSegment);

		return commandParameters;
	}

	/**
	 * Determine the name of the task.
	 * @param formattedCommandContent
	 * @return task name
	 * @throws InvalidInputFormatException
	 */
	protected String determineTask(String formattedCommandContent) 
			throws InvalidInputFormatException {
		assert formattedCommandContent != null;

		int timeIndex = getStartingIndexOfIdentifier(formattedCommandContent);

		//no time specified
		if (timeIndex == FIELD_NOT_EXIST) {
			return determineTaskWithNoTimeSpecified(formattedCommandContent);
		}

		//time is specified
		else {
			return determineTaskWithTimeSpecified(formattedCommandContent, timeIndex);
		}
	}

	/**
	 * @param formattedCommandContent
	 * @param timeIndex
	 * @return task name
	 */
	private String determineTaskWithTimeSpecified(String formattedCommandContent, 
			int timeIndex) {
		assert formattedCommandContent != null;

		//time is the first segment -> task is missing
		if (timeIndex == 0) {
			return STRING_EMPTY;
		}

		//task is present
		else {
			return formattedCommandContent.substring(0, timeIndex - 1);
		}
	}

	/**
	 * @param formattedCommandContent
	 * @return task name
	 */
	private String determineTaskWithNoTimeSpecified(String formattedCommandContent) {
		assert formattedCommandContent != null;

		int priorityIndex = getStartingIndexOfPriority(formattedCommandContent);

		//no priority specified
		if (priorityIndex == FIELD_NOT_EXIST) {
			return formattedCommandContent;      //commandContent is the task
		}

		//priority is specified
		else {

			//priority is the first segment -> task is missing
			if (priorityIndex == 0) {
				return STRING_EMPTY;
			}

			//task is present
			else {
				return formattedCommandContent.substring(0, priorityIndex - 1);
			}
		}
	}

	/**
	 * @param formattedCommandContent
	 * @return time of the task
	 * @throws InvalidInputFormatException
	 */
	protected String determineTime(String formattedCommandContent) 
			throws InvalidInputFormatException {
		assert formattedCommandContent != null;

		String timeSegment = determineTimeSegment(formattedCommandContent);
		List<Date> dates = timeParser.parse(timeSegment);

		//no time present
		if (dates.isEmpty()) {
			return TIME_EMPTY;
		}

		//time is present
		else {
			String time = formatTimeSegment(dates, timeSegment);
			return time;
		}
	}

	/**
	 * Format the time segment for esay manipulation.
	 * @param dates
	 * @param timeSegment
	 * @return the formatted time segment
	 * @throws InvalidInputFormatException
	 */
	private String formatTimeSegment(List<Date> dates, String timeSegment) 
			throws InvalidInputFormatException {
		assert timeSegment != null;

		//check the validity of input time
		isTimeAmbiguous(dates, timeSegment);

		//convert to string for easy manipulation
		String time = dates.toString();

		//do not set to default time if "now" is specified
		if (!timeSegment.contains(STRING_NOW)) {
			time = setDefaultTimeIfNotSpecified(timeSegment, dates);
		}
		return time;
	}

	/**
	 * @param dates
	 * @param timeSegment
	 * @throws InvalidInputFormatException
	 */
	private void isTimeAmbiguous(List<Date> dates, String timeSegment) 
			throws InvalidInputFormatException {
		assert dates != null;
		assert timeSegment != null;

		int size = dates.size();

		//not handling input with more than two different time
		if (size > 2) {
			throw new InvalidInputFormatException(ERROR_MESSAGE_AMBIGUOUS_TIME);
		}

		//only handles "from time_A to time_B" for input with two different time specified
		else if (size == 2) {
			if (!containsWholeWord(timeSegment, DURATION_FLAG_TO)) {
				throw new InvalidInputFormatException(ERROR_MESSAGE_UNSUPPORTED_DATE_FORMAT);
			}
		}
	}

	/**
	 * Set the default time to 8am to avoid using system time for the task.
	 * @param timeSegment
	 * @param dates
	 * @return the modified time if applicable
	 */
	private String setDefaultTimeIfNotSpecified(String timeSegment, List<Date> dates) {
		assert timeSegment != null;
		assert dates != null;

		String parsedTime = getRoughTime(dates.toString());
		String currentSystemTime = getRoughTime(new Date().toString());

		//user has not specified a time in day hours
		if (parsedTime.equals(currentSystemTime)) {
			String timeResult = timeParser.parse(
					timeSegment + STRING_WHITE_SPACE + DEFAULT_TIME).toString();
			return timeResult;
		}

		//user has specified a time in day hours
		else {
			return dates.toString();
		}
	}

	/**
	 * Determine the approximate time for comparsion.
	 * @param fullDate
	 * @return the approximate time
	 */
	private String getRoughTime(String fullDate) {
		assert fullDate != null;
		return fullDate.substring(fullDate.indexOf(TIME_SEPARATOR) - 2, 
				fullDate.indexOf(TIME_SEPARATOR, fullDate.indexOf(TIME_SEPARATOR) + 1) + 2);
	}


	/**
	 * @param formattedCommandContent
	 * @return the time segment from the formatted command content
	 */
	private String determineTimeSegment(String formattedCommandContent) {
		assert formattedCommandContent != null;

		int timeIndex = getStartingIndexOfIdentifier(formattedCommandContent);
		int priorityIndex = getStartingIndexOfPriority(formattedCommandContent);
		String timeSegment;

		//no time is specified
		if (timeIndex == FIELD_NOT_EXIST) {
			timeSegment = STRING_EMPTY;
		}

		//time is specified but priority is not specified
		else if (priorityIndex == FIELD_NOT_EXIST) {
			timeSegment = formattedCommandContent.substring(timeIndex);
		}

		//both time and priority are specified
		else {
			timeSegment = formattedCommandContent.substring(timeIndex, priorityIndex - 1);
		}
		timeSegment = formatTimeSegment(timeSegment);
		return timeSegment;
	}

	/**
	 * Fix the case where "on" is followed by "from ... to ..."
	 * which is not handled properly by the PrettyTimeParser library.
	 * @param timeSegment
	 * @return
	 */
	private String formatTimeSegment(String timeSegment) {
		assert timeSegment != null;

		if (containsWholeWord(timeSegment, EVENT_FLAG_ON) 
				&& containsWholeWord(timeSegment, DURATION_FLAG_FROM )){

			timeSegment = timeSegment.replaceAll(DURATION_FLAG_FROM, 
					STRING_EMPTY);
			timeSegment = removeTrailingSpaces(timeSegment);
		}

		return timeSegment;
	}

	/**
	 * @param formattedCommandContent
	 * @return priority of the task
	 * @throws InvalidInputFormatException
	 */
	protected String determinePriority(String formattedCommandContent) 
			throws InvalidInputFormatException {
		assert formattedCommandContent != null;

		//priority is specified
		if (formattedCommandContent.contains(PRIORITY_FLAG)) {
			String priority = formattedCommandContent.substring(
					formattedCommandContent.indexOf(PRIORITY_FLAG) + 1).trim();
			priority = priority.toLowerCase();

			//check validity of input priority
			if (!isValidPriority(priority)) {
				throw new InvalidInputFormatException(
						ERROR_MESSAGE_INVALID_PRIORITY);
			}

			//convert aliases to standard priority
			return getPriorityInFull(priority);
		}

		//priority is not specified
		else {
			return PriorityLevel.NOT_SPECIFIED.getType();
		}
	}

	/**
	 * Check whether the specified priority is valid.
	 * Return true for valid priority or false otherwise.
	 * @param priority
	 * @return true or false
	 */
	private boolean isValidPriority(String priority) {
		assert priority != null;

		if (priority.equals(PriorityLevel.HIGH.getType()) 
				|| priority.equals(PriorityLevel.MEDIUM.getType()) 
				|| priority.equals(PriorityLevel.LOW.getType()) 
				|| priority.equals(ALIAS_PRIORITY_HIGH) 
				|| priority.equals(ALIAS_1_PRIORITY_MEDIUM) 
				|| priority.equals(ALIAS_2_PRIORITY_MEDIUM) 
				|| priority.equals(ALIAS_PRIORITY_LOW)) {
			return true;
		}

		else {
			return false;
		}
	}

	/**
	 * Convert the aliases for valid priority level to the standard forms.
	 * @param priority
	 * @return the priority in full
	 */
	private String getPriorityInFull(String priority) {
		assert priority != null;

		if (priority.equals(PriorityLevel.HIGH.getType()) 
				|| priority.equals(ALIAS_PRIORITY_HIGH)) {
			priority = PriorityLevel.HIGH.getType();
		}

		else if (priority.equals(PriorityLevel.MEDIUM.getType()) 
				|| priority.equals(ALIAS_1_PRIORITY_MEDIUM) 
				|| priority.equals(ALIAS_2_PRIORITY_MEDIUM)) {
			priority = PriorityLevel.MEDIUM.getType();
		}

		else if (priority.equals(PriorityLevel.LOW.getType()) ||
				priority.equals(ALIAS_PRIORITY_LOW)) {
			priority = PriorityLevel.LOW.getType();
		}

		return priority;
	}

	/**
	 * @param formattdCommandContent
	 * @return type of the task
	 */
	protected String determineTaskType(String formattedCommandContent) {
		assert formattedCommandContent != null;

		int timeIndex = getStartingIndexOfIdentifier(formattedCommandContent);
		String timeSegment = determineTimeSegment(formattedCommandContent).toLowerCase();

		//time is not specified
		if (timeIndex == FIELD_NOT_EXIST) {
			return TaskType.EVENT.getType();
		}

		//time is in the format "from... to..."
		else if (isDurationTask(timeSegment)) {
			return TaskType.DURATION.getType();
		}

		//time is specified by a single date with no duration
		else {
			//time is specified by "by" and "before"
			if (formattedCommandContent.substring(timeIndex, timeIndex + 2)
					.equalsIgnoreCase(DEADLINE_FLAG_BY)|| formattedCommandContent.substring(
							timeIndex, timeIndex + 6).equalsIgnoreCase(DEADLINE_FLAG_BEFORE)) {
				return TaskType.DEADLINE.getType();
			}

			//time is specified by any other allowed prepositions or phrases
			else {
				return TaskType.EVENT.getType();
			}
		}
	}

	/**
	 * Check whether the whole keyword is contained in content.
	 * Return true if it is contained or false otherwise.
	 * @param content
	 * @param keyword
	 * @return true or false
	 */
	private boolean containsWholeWord(String content, String keyword) {
		assert content != null;
		assert keyword != null;

		String[] segments = content.split(STRING_WHITE_SPACE);  
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].equalsIgnoreCase(keyword)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check whether the task is a task which spans over a period of time.
	 * Return true if it is or false otherwise.
	 * @param timeSegment
	 * @return true or false
	 */
	private boolean isDurationTask(String timeSegment) {
		assert timeSegment != null;

		if (timeParser.parse(timeSegment).size() == 2 
				&& (containsWholeWord(timeSegment, DURATION_FLAG_FROM)
						|| containsWholeWord(timeSegment, DURATION_FLAG_TO))) {
			return true;
		}

		else {
			return false;
		}
	}

	/**
	 * @param timeSegment
	 * @return status of the task
	 */
	protected String determineStatus(String timeSegment) {
		assert timeSegment != null;

		//parse the time
		List<Date> dates = timeParser.parse(timeSegment);
		int size = dates.size();

		//no time is specified for the task
		if (size == 0) {
			return TASK_FLOATING;
		}

		//the task is overdue on adding
		else if (isOverdue(dates.get(size - 1)) && (!isToday(dates))) {
			return TASK_OVERDUE;
		}

		//time is specified for the task
		else {
			return TASK_UPCOMING;
		}
	}


	/**
	 * Check whether the time is today.
	 * Return true if it is or false otherwise.
	 * @param dates
	 * @return true or false
	 */
	private boolean isToday(List<Date> dates) {
		assert dates != null;
		return dates.toString().substring(1,11).equals
				(new Date().toString().substring(0, 10));
	}

	/**
	 * Determine the starting index of a whole word appeared in the content.
	 * @param content
	 * @param expression
	 * @return the starting index of the word
	 */
	private int getIndexOfWholeWord(String content, String expression) {
		assert content != null;
		assert expression != null;

		content = content.toLowerCase();
		String[] segments = content.split(STRING_WHITE_SPACE);
		int size = segments.length;
		int index = FIELD_NOT_EXIST;
		int finalIndex = 0;

		//determine the index of expression in segments
		for (int i = 0; i < size; i++) {
			if (segments[i].equals(expression)) {
				index = i;
			}
		}

		//recalculate the finalIndex in content based on the index in segments
		if (index != FIELD_NOT_EXIST) {
			for (int i = 0; i < index; i++) {
				finalIndex += segments[i].length() + 1;
			}
		}

		return finalIndex;
	}

	/**
	 * Add a preposition to the time segment for easy parsing if needed.
	 * @param commandContent
	 * @param timePhrase
	 * @return the modified command content
	 */
	private String addPrepositionIfApplicable(String commandContent, String timePhrase) {
		assert commandContent != null;
		assert timePhrase != null;

		//contains the particular time phrase
		if (containsWholeWord(commandContent, timePhrase)) {

			//time phrase is the first word
			if (getIndexOfWholeWord(commandContent, timePhrase) == 0) {
				commandContent = formatWithTimePhraseAsFirstWord(commandContent, timePhrase);
			}

			//time phrase is not the first word
			else {
				commandContent = formatWithTimePhraseNotAsFirstWord
						(commandContent, timePhrase);

			}
		}

		return commandContent;
	}

	/**
	 * @param commandContent
	 * @param timePhrase
	 * @return the formatted command content
	 */
	private String formatWithTimePhraseNotAsFirstWord(String commandContent, 
			String timePhrase) {
		assert commandContent != null;
		assert timePhrase != null;

		//determine the position of timePhrase
		String[] segments = commandContent.split(STRING_WHITE_SPACE);
		int index = determineIndexOfTimePhrase(segments, timePhrase);
		int len = segments.length;

		//no valid preposition before time phrase
		if (!isValidTimeIdentifier(segments[index - 1])) {

			//time phrase is the last word
			if (index + 1 == len) {
				commandContent = formatWithTimePhraseAsLastWord(segments, index);
			}

			//time phrase is not the last word, and no valid preposition after time phrase
			else if (index + 1 < len && 
					!isValidTimeIdentifier(segments[index + 1])) {
				commandContent = formatWithTimePhraseNotAsLastWordWithNoValidFollowingPrepositon(
						segments, index);
			}

			//time phrase is not the last word, and valid preposition after time phrase
			else if (index + 1 < len 
					&& isValidTimeIdentifier(segments[index + 1])) {
				commandContent = formatWithTimePhraseNotAsLastWordWithValidFollowingPreposition(
						segments, index);
			}
		}

		return commandContent;
	}

	/**
	 * @param segments
	 * @param index
	 * @return formatted command content
	 */
	private String formatWithTimePhraseNotAsLastWordWithValidFollowingPreposition(
			String[] segments, int index) {
		assert segments != null;

		//retrieve the content before the time phrase
		String newContent = STRING_EMPTY;
		for (int i = 0; i < index; i++) {
			newContent += segments[i] + STRING_WHITE_SPACE;
		}

		//retrieve the rest of the command content
		String rest = STRING_EMPTY;
		for (int i = index + 2; i < segments.length; i++) {
			rest += STRING_WHITE_SPACE + segments[i];
		}

		//no time phrase in rest of the command content
		if (timeParser.parse(rest).size() == 0) {
			newContent += EVENT_FLAG_ON + STRING_WHITE_SPACE + segments[index];
		}

		//time phrase present in rest of the command content
		else {
			newContent += segments[index + 1] + 
					STRING_WHITE_SPACE + segments[index];
		}

		return newContent + rest;
	}

	/**
	 * @param segments
	 * @param index
	 * @return formatted command content
	 */
	private String formatWithTimePhraseNotAsLastWordWithNoValidFollowingPrepositon(
			String[] segments, int index) {
		assert segments != null;

		//retrieve the content before the time phrase
		String newContent = STRING_EMPTY;
		for (int i = 0; i < index; i++) {
			newContent += segments[i] + STRING_WHITE_SPACE;
		}

		//append the preposition to newContent
		newContent += EVENT_FLAG_ON + STRING_WHITE_SPACE + segments[index];

		//append rest of the command content to newContent
		for (int i = index + 1; i < segments.length; i++) {
			newContent += STRING_WHITE_SPACE + segments[i];
		}
		return newContent;
	}

	/**
	 * @param segments
	 * @param index
	 * @return formatted command content
	 */
	private String formatWithTimePhraseAsLastWord(String[] segments, int index) {
		assert segments != null;

		String newContent = STRING_EMPTY;
		for (int i = 0; i < index; i++) {
			newContent += segments[i] + STRING_WHITE_SPACE;
		}

		//append the preposition to newContent
		newContent += EVENT_FLAG_ON + STRING_WHITE_SPACE + segments[index];

		return newContent;
	}

	/**
	 * @param segments
	 * @param timePhrase
	 * @return the starting index of the time phrase
	 */
	private int determineIndexOfTimePhrase(String[] segments, String timePhrase) {
		assert segments != null;
		assert timePhrase != null;

		int index = 0;
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].equals(timePhrase)) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * @param commandContent
	 * @param timePhrase
	 * @return the formatted command content
	 */
	private String formatWithTimePhraseAsFirstWord(String commandContent, String timePhrase) {
		assert commandContent != null;
		assert timePhrase != null;

		int startIndex = commandContent.indexOf(STRING_WHITE_SPACE) + 1;
		String nextWord = commandContent.substring(startIndex);

		//check if there is still content after time phrase
		if (nextWord.indexOf(STRING_WHITE_SPACE) != -1) {
			nextWord = nextWord.substring(
					0, nextWord.indexOf(STRING_WHITE_SPACE));
		}

		//there is a valid preposition following time phrase
		if (startIndex < commandContent.length() 
				&& isValidTimeIdentifier(nextWord)) {

			commandContent = commandContent.substring(startIndex);

			commandContent = formatWithValidFollowingPreposition(
					commandContent, timePhrase);
		}

		//no preposition after time phrase
		else {

			commandContent = EVENT_FLAG_ON + STRING_WHITE_SPACE + commandContent;
		}

		return commandContent;
	}

	/**
	 * @param commandContent
	 * @param timePhrase
	 * @return the formatted command content
	 */
	private String formatWithValidFollowingPreposition(
			String commandContent, String timePhrase) {
		assert commandContent != null;
		assert timePhrase != null;

		//no time phrase in rest of the command content
		if (timeParser.parse(commandContent).size() == 0) {
			commandContent = EVENT_FLAG_ON + STRING_WHITE_SPACE + timePhrase 
					+ STRING_WHITE_SPACE + commandContent;
		}

		//other time phrases present in rest of the command content
		else {
			int index = commandContent.indexOf(STRING_WHITE_SPACE) + 1;
			commandContent = commandContent.substring(0, index) + timePhrase 
					+ STRING_WHITE_SPACE + commandContent.substring(index);
		}

		return commandContent;
	}

	/**
	 * Format the command content to the standardized format:
	 * task-time-priority to facilitate parsing later.
	 * @param content
	 * @return the formatted command content
	 */
	protected String formatToStandardCommandContent(String content) {
		assert content != null;

		String formattedContent = preFormat(content);
		int timeIndex = getStartingIndexOfIdentifier(formattedContent);
		int priorityIndex = getStartingIndexOfPriority(formattedContent);
		int taskIndex = getStartingIndexOfTask(formattedContent, timeIndex, priorityIndex);

		//task only
		if (timeIndex == FIELD_NOT_EXIST && priorityIndex == FIELD_NOT_EXIST) {
			return formattedContent;
		}

		//no time,has priority
		else if (timeIndex == FIELD_NOT_EXIST) {
			return formatWithNoTimeSegment(formattedContent, taskIndex, priorityIndex);
		}

		//no priority,has time
		else if (priorityIndex == FIELD_NOT_EXIST) {
			return formatWithNoPrioritySegment(formattedContent, taskIndex, timeIndex);	
		}

		//time,priority and maybe task
		else {

			//no task
			if (taskIndex == FIELD_NOT_EXIST) {
				return formatWithNoTaskPresent(formattedContent, 
						timeIndex, priorityIndex);
			}

			//task is present
			else {
				return formatWithTaskPresent(formattedContent, 
						taskIndex, timeIndex, priorityIndex);
			}
		}
	}


	/**
	 * @param formattedContent
	 * @param timeIndex
	 * @param priorityIndex
	 * @return the formatted content
	 */
	private String formatWithNoTaskPresent(String formattedContent, 
			int timeIndex, int priorityIndex) {
		assert formattedContent != null;

		//priority followed by time
		if (timeIndex > priorityIndex) {
			return formattedContent.substring(timeIndex)+ STRING_WHITE_SPACE + 
					formattedContent.substring(0, timeIndex - 1);
		}

		//time followed by priority
		else {
			return formattedContent;
		}
	}

	/**
	 * @param formattedContent
	 * @param taskIndex
	 * @param timeIndex
	 * @param priorityIndex
	 * @return the formatted content
	 */
	private String formatWithTaskPresent(String formattedContent, int taskIndex, 
			int timeIndex, int priorityIndex) {
		assert formattedContent != null;

		//task is the first segment
		if (taskIndex < timeIndex && taskIndex < priorityIndex) {
			return formatWithTaskAsFirstSegment(formattedContent, taskIndex, 
					timeIndex, priorityIndex);
		}

		//priority-task-time
		else if (taskIndex < timeIndex) {
			return formattedContent.substring(taskIndex) + STRING_WHITE_SPACE +
					formattedContent.substring(priorityIndex,taskIndex - 1);
		}

		//time-task-priority
		else if (taskIndex < priorityIndex) {
			return formattedContent.substring(taskIndex, priorityIndex - 1) + 
					STRING_WHITE_SPACE + formattedContent.substring(
							timeIndex, taskIndex - 1) + STRING_WHITE_SPACE 
					+ formattedContent.substring(priorityIndex);
		}

		//task is the last segment
		else {
			return formatWithTaskAsLastSegment(formattedContent, taskIndex, 
					timeIndex, priorityIndex);
		}
	}

	/**
	 * @param formattedContent
	 * @param taskIndex
	 * @param timeIndex
	 * @param priorityIndex
	 * @return the formatted content
	 */
	private String formatWithTaskAsLastSegment(String formattedContent, 
			int taskIndex, int timeIndex, int priorityIndex) {
		assert formattedContent != null;

		//time-priority-task
		if (timeIndex < priorityIndex) {
			return formattedContent.substring(taskIndex) + STRING_WHITE_SPACE 
					+ formattedContent.substring(timeIndex, priorityIndex - 1) 
					+ STRING_WHITE_SPACE + formattedContent.substring(
							priorityIndex, taskIndex - 1);
		}

		//priority-time-task
		else {
			return formattedContent.substring(taskIndex) + STRING_WHITE_SPACE 
					+ formattedContent.substring(timeIndex, taskIndex - 1) + 
					STRING_WHITE_SPACE + formattedContent.substring(
							priorityIndex, timeIndex - 1);
		}
	}

	/**
	 * @param formattedContent
	 * @param taskIndex
	 * @param timeIndex
	 * @param priorityIndex
	 * @return the formatted content
	 */
	private String formatWithTaskAsFirstSegment(String formattedContent, 
			int taskIndex, int timeIndex, int priorityIndex) {
		assert formattedContent != null;

		//task-time-priority
		if (timeIndex < priorityIndex) {
			return formattedContent;
		}

		//task-priority-time
		else {
			return formattedContent.substring(taskIndex,priorityIndex) + 
					formattedContent.substring(timeIndex) + STRING_WHITE_SPACE + 
					formattedContent.substring(priorityIndex,timeIndex - 1);
		}
	}

	/**
	 * @param formattedContent
	 * @param taskIndex
	 * @param timeIndex
	 * @return formatted content
	 */
	private String formatWithNoPrioritySegment(String formattedContent, 
			int taskIndex, int timeIndex) {
		assert formattedContent != null;

		//only time
		if (taskIndex == FIELD_NOT_EXIST) {
			return formattedContent;
		}

		//task and time
		else {

			//time followed by task
			if (taskIndex > timeIndex) {
				return formattedContent.substring(taskIndex)+ STRING_WHITE_SPACE + 
						formattedContent.substring(0, taskIndex - 1);
			}

			//task followed by time
			else {
				return formattedContent;
			}
		}
	}

	/**
	 * @param formattedContent
	 * @param taskIndex
	 * @param priorityIndex
	 * @return formatted content
	 */
	private String formatWithNoTimeSegment(String formattedContent, int taskIndex, 
			int priorityIndex) {
		assert formattedContent != null;

		//only priority
		if (taskIndex == FIELD_NOT_EXIST) {
			return formattedContent;
		}

		//priority and task
		else {

			//priority followed by task
			if (taskIndex > priorityIndex) {
				return formattedContent.substring(taskIndex)+ STRING_WHITE_SPACE + 
						formattedContent.substring(0, taskIndex - 1);
			}

			//task followed by priority
			else {
				return formattedContent;
			}
		}
	}

	/**
	 * Format the content for easy parsing.
	 * @param content
	 * @return formatted content
	 */
	private String preFormat(String content) {
		assert content != null;

		content = removeTrailingSpaces(content);
		content = handleTimeWithoutPreposition(content);
		return content;
	}

	/**
	 * @param commandContent
	 * @return formatted command content
	 */
	private String handleTimeWithoutPreposition(String commandContent) {
		assert commandContent != null;

		//format "tmr" to "tomorrow"
		commandContent = commandContent.replaceAll(TOMORROW_IN_SHORT, TOMORROW_IN_FULL);

		//handle the "now" keyword
		if (isNecessaryToAddPrepostion(commandContent, STRING_NOW)) {
			commandContent = addPrepositionIfApplicable(commandContent, STRING_NOW);
		}

		//handle the "today" keyword
		else if (isNecessaryToAddPrepostion(commandContent, STRING_TODAY)) {
			commandContent = addPrepositionIfApplicable(commandContent, STRING_TODAY);
		}

		//handle the "tomorrow" keyword
		else if (isNecessaryToAddPrepostion(commandContent, TOMORROW_IN_FULL)) {
			commandContent = addPrepositionIfApplicable(commandContent, TOMORROW_IN_FULL);
		}

		return commandContent;
	}

	/**
	 * @param content
	 * @return trimmed content
	 */
	private String removeTrailingSpaces(String content) {
		assert content != null;
		content = content.replaceAll(EXTRA_WHITE_SPACES, STRING_WHITE_SPACE).trim();
		return content;
	}

	/**
	 * Check whether it is needed to add a preposition the command content.
	 * Return true if it is needed or false otherwise.
	 * @param content
	 * @param timePhrase
	 * @return true or false
	 */
	private boolean isNecessaryToAddPrepostion(String content, String timePhrase) {
		assert content != null;
		assert timePhrase != null;

		int index = content.indexOf(timePhrase);

		//the time phrase is in quotes
		if (containsWholeWord(content, QUOTATION + timePhrase + QUOTATION)) {
			return false;
		}

		//there is no such time phrase
		else if (index == FIELD_NOT_EXIST) {
			return false;
		}

		//there is such time phrase
		else {
			String front = content.substring(0,index).trim();

			//valid time phrase
			if (timeParser.parse(front).toString().equals(TIME_EMPTY)) {
				return true;
			}

			//invalid time phrase
			else {
				return false;
			}
		}

	}


	/**
	 * @param content
	 * @return the starting index of the priority segment
	 */
	private int getStartingIndexOfPriority(String content) {
		return content.indexOf(PRIORITY_FLAG);
	}


	/**
	 * @param content
	 * @param timeIndex
	 * @param priorityIndex
	 * @return the starting index of the task segment
	 */
	private int getStartingIndexOfTask(String content, int timeIndex, int priorityIndex) {
		assert content != null;

		//no time or priority -> only task
		if (timeIndex == FIELD_NOT_EXIST && priorityIndex == FIELD_NOT_EXIST) {
			return 0;
		}

		//no time, has priority -> priority-task/task-priority/priority
		else if (timeIndex == FIELD_NOT_EXIST) {
			return getStartingIndexOfTaskWithNoTimeSegment(content);
		}

		//no priority, has time -> time-task/task-time/time
		else if (priorityIndex == FIELD_NOT_EXIST) {
			return getStartingIndexOfTaskWithNoPrioirtySegment(content, timeIndex);
		}

		//time, priority and maybe task are present
		else {
			return getStartingIndexOfTaskWithBothTimeAndPriorityPresent(
					content, timeIndex, priorityIndex);
		}
	}

	/**
	 * @param content
	 * @param timeIndex
	 * @param priorityIndex
	 * @return the starting index of the task segment
	 */
	private int getStartingIndexOfTaskWithBothTimeAndPriorityPresent(
			String content, int timeIndex, int priorityIndex) {
		assert content != null;

		//time is the first segment
		if (timeIndex == 0) {
			return getStartingIndexOfTaskWithTimeAsFirstSegment(content, priorityIndex);

		}

		//priority is the first segment
		else if (priorityIndex == 0) {
			return getStartingIndexOfTaskWithPriorityAsFirstSegment(content, timeIndex);

		}

		//task is the first segment
		else {
			return 0;
		}
	}

	/**
	 * @param content
	 * @param timeIndex
	 * @return the starting index of the task segment
	 */
	private int getStartingIndexOfTaskWithPriorityAsFirstSegment(
			String content, int timeIndex) {
		assert content != null;

		//priority-time or priority-time-task
		if (timeIndex == content.indexOf(STRING_WHITE_SPACE) + 1) {
			int baseIndex = content.indexOf(STRING_WHITE_SPACE) + 1;
			String segment = content.substring(baseIndex);
			int index = locateTaskIndexInSegment(segment);

			//priority-time
			if (index == FIELD_NOT_EXIST) {
				return FIELD_NOT_EXIST;
			}

			//priority-time-task
			else {
				return baseIndex + index;
			}
		}

		//priority-task-time
		else {
			return content.indexOf(STRING_WHITE_SPACE) + 1;
		}
	}

	/**
	 * @param content
	 * @param priorityIndex
	 * @return the starting index of task segment
	 */
	private int getStartingIndexOfTaskWithTimeAsFirstSegment(
			String content, int priorityIndex) {
		assert content != null;

		//time-priority-task
		if (priorityIndex < content.lastIndexOf(STRING_WHITE_SPACE)) {
			return content.indexOf(STRING_WHITE_SPACE,content.indexOf
					(PRIORITY_FLAG)) + 1;
		}

		//time-task-priority or time-priority
		else {
			String segment = content.substring(0,content.indexOf
					(PRIORITY_FLAG) - 1);
			return locateTaskIndexInSegment(segment);
		}	
	}

	/**
	 * @param content
	 * @param timeIndex
	 * @return the starting index of the task segment
	 */
	private int getStartingIndexOfTaskWithNoPrioirtySegment(
			String content, int timeIndex) {
		assert content != null;

		//<time>/<time-task>
		if (timeIndex == 0) {
			return locateTaskIndexInSegment(content);	
		}

		//task-time
		else {
			return 0;
		}	
	}

	/**
	 * @param content
	 * @return the starting index of the task segment
	 */
	private int getStartingIndexOfTaskWithNoTimeSegment(String content) {
		assert content != null;

		//priority
		if (!content.contains(STRING_WHITE_SPACE)) {
			return FIELD_NOT_EXIST;
		}

		else {

			//priority-task
			if (content.substring(0,1).equals(PRIORITY_FLAG)) {
				return content.indexOf(STRING_WHITE_SPACE) + 1;
			}

			//task-priority
			else {
				return 0;
			}
		}
	}

	/**
	 * @param content
	 * @return the starting index of task segment in the found segment
	 */
	private int locateTaskIndexInSegment(String content) {
		assert content != null;

		//input: time/time-task
		int count = StringUtils.countMatches(content, STRING_WHITE_SPACE);

		//no task present
		if (count == 1) {
			return FIELD_NOT_EXIST;
		}

		//task present
		else {
			int taskIndex = getTaskIndex(content, count);
			return taskIndex;
		}
	}


	/**
	 * @param content
	 * @param count
	 * @return the index of the task based on the count of appearances
	 */
	private int getTaskIndex(String content, int count) {
		assert content != null;

		//initialize to not-exist at first
		int taskIndex = FIELD_NOT_EXIST;

		//determine the task index if task is present
		String oldDate = getRoughDate(timeParser.parse(content).toString());
		for (int i = 2; i <= count; i++) {

			//get the newDate by taking in one more word
			int index = StringUtils.ordinalIndexOf(content, STRING_WHITE_SPACE, i);
			String newDate = content.substring(0, index);
			newDate = timeParser.parse(newDate).toString();

			//time is not present
			if (!newDate.equals(TIME_EMPTY)) {
				newDate = getRoughDate(newDate);
			}

			//stop when newDate and oldDate are the same
			if (newDate.equals(oldDate)) {
				taskIndex = index + 1;
				break;
			}
		}

		return taskIndex;

	}

	/**
	 * Determine the starting index of time identifer to locate the time phrase.
	 * @param content
	 * @return the starting index of the time identifier
	 */
	private int getStartingIndexOfIdentifier(String content) {
		assert content != null;

		//a queue to store starting indices of the possible identifiers
		PriorityQueue<Integer> indexQueue;
		indexQueue = buildIndexQueue(content);

		//a list to store the indices in reversed order
		ArrayList<Integer> list;
		list = buildIndexListInReversedOrder(indexQueue);

		//no identifier -> no time phrase present
		if (list.size() == 0) {
			return FIELD_NOT_EXIST;
		}

		//only one match
		else if (list.size() == 1) {
			return getStartingIndexOfIdentifierWithOneMatch(content, list);
		}

		//there are possible matches
		else if (list.size() == 2) {
			return getStartingIndexOfIdentiferWithTwoMatches(content, list);
		}

		//list size is at least 3 -> check substrings to determine
		else {
			return getStartingIndexOfIdentifierWithMoreThanTwoMatches(content, list);
		}

	}

	/**
	 * @param content
	 * @param list
	 * @return the starting index of the time identifier
	 */
	private int getStartingIndexOfIdentifierWithMoreThanTwoMatches(String content, 
			ArrayList<Integer> list) {
		assert content != null;
		assert list != null;

		for (int i = 0; i < list.size(); i++) {

			//i is not the last index stored
			if (i < list.size() - 1) {
				String substring = content.substring(list.get(i),list.get(i + 1));

				//i is the starting index of the valid identifier
				if (!timeParser.parse(substring).toString().equals(TIME_EMPTY)) {
					return list.get(i);
				}
			}

			//i is the last index stored
			else {
				String substring = content.substring(list.get(i));

				//i is the starting index of the valid identifier
				if (!timeParser.parse(substring).toString().equals(TIME_EMPTY)) {
					return list.get(i);
				}
			}
		}

		//not valid identifier is found
		return FIELD_NOT_EXIST;
	}

	/**
	 * @param content
	 * @param list
	 * @return starting index of the time identifier
	 */
	private int getStartingIndexOfIdentiferWithTwoMatches(String content, 
			ArrayList<Integer> list) {
		assert content != null;
		assert list != null;

		//first one is valid
		if (!timeParser.parse(content.substring(list.get(0), list.get(1)))
				.toString().equals(TIME_EMPTY)) {
			return list.get(0);
		}

		//second one is valid
		else if (!timeParser.parse(content.substring(list.get(1)))
				.toString().equals(TIME_EMPTY)) {
			return list.get(1);
		}

		//both are invalid
		else {
			return FIELD_NOT_EXIST;
		}
	}

	/**
	 * @param content
	 * @param list
	 * @return starting index of the time identifier
	 */
	private int getStartingIndexOfIdentifierWithOneMatch(String content, 
			ArrayList<Integer> list) {
		assert content != null;
		assert list != null;

		//it is valid
		if (!timeParser.parse(content).toString().equals(TIME_EMPTY)) {
			return list.get(0);
		}

		//it is invalid
		else {
			return FIELD_NOT_EXIST;
		}
	}

	/**
	 * @param indexQueue
	 * @return the indices of time identifiers in the list in reversed order
	 */
	private ArrayList<Integer> buildIndexListInReversedOrder(
			PriorityQueue<Integer> indexQueue) {
		assert indexQueue != null;

		ArrayList<Integer> list = new ArrayList<Integer>();
		int size = indexQueue.size();
		for (int i = 0; i < size; i++) {
			list.add(indexQueue.poll());
		}

		return list;
	}

	/**
	 * @param content
	 * @return the indices of time identifiers in the list
	 */
	private PriorityQueue<Integer> buildIndexQueue(String content) {
		assert content != null;

		//get the number of words in content
		String[] segments = content.split(STRING_WHITE_SPACE);
		int numberOfSpaces = segments.length - 1;

		PriorityQueue<Integer> indexQueue = new PriorityQueue<Integer>();

		//a pointer to move along all words
		int pointer = 0;
		for (int i = 1; i <= numberOfSpaces; i++) {
			int index = StringUtils.ordinalIndexOf(content, STRING_WHITE_SPACE, i);

			//put the index in if the word is a possible identifier
			if (isValidTimeIdentifier(content.substring(pointer, index))) {
				indexQueue.offer(pointer);
			}

			//move to the next word
			pointer = index + 1;
		}

		return indexQueue;
	}

	/**
	 * Check whether the time identifier is valid.
	 * Return true if it is valid or false otherwise.
	 * @param content
	 * @return true or false
	 */
	private boolean isValidTimeIdentifier(String content) {
		assert content != null;
		String word = content.toLowerCase();

		if (word.equals(DEADLINE_FLAG_BY)) {
			return true;
		}
		else if (word.equals(DEADLINE_FLAG_BEFORE)) {
			return true;
		}

		else if (word.equals(EVENT_FLAG_AT)) {
			return true;
		}

		else if (word.equals(EVENT_FLAG_ON)) {
			return true;
		}

		else if (word.equals(DURATION_FLAG_FROM)) {
			return true;
		}

		else{
			return false;
		}
	}

	/**
	 * @param time
	 * @return true or false
	 */
	private boolean isOverdue(Date date) {
		assert date != null;
		return date.before(new Date());
	}

	/**
	 * @param time
	 * @return the approximate date
	 */
	private String getRoughDate(String time) {
		assert time != null;

		String[] segments = time.split(TIME_SEPARATOR);
		time = segments[0] + segments[1] + segments[2].substring(2);
		return time;
	}
}
/* @@author A0127481E */
