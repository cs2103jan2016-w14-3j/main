/* @@author A0127481E */
package main.java.parser;
import main.java.data.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class AddCommandParser extends Parser {

	private static final String WHITE_SPACE = " ";
	private static final String DEADLINE_FLAG_BY = "by";
	private static final String DEADLINE_FLAG_BEFORE = "before";
	private static final String EVENT_FLAG_ON = "on";
	private static final String EVENT_FLAG_AT = "at";
	private static final String DURATION_FLAG_FROM = "from";
	private static final String DURATION_FLAG_TO = "to";
	private static final String DEADLINE_TASK = "deadline";
	private static final String EVENT_TASK = "one-time event";
	private static final String DURATION_TASK = "duration";
	private static final String PRIORITY_FLAG = "#";	
	private static final String EMPTY_TIME = "[]";
	private static final String EXTRA_WHITE_SPACES = "\\s+";
	private static final String DEFAULT_TIME = "8am";
	private static final String TOMORROW_IN_FULL = "tomorrow";
	private static final String TOMORROW_IN_SHORT = "tmr";
	private static final String NOW = "now";
	private static final String TODAY = "today";
	private static final String OVERDUE_TASK = "overdue";
	private static final String UPCOMING_TASK = "upcoming";
	private static final String FLOATING_TASK = "floating";
	private static final int FIELD_NOT_EXIST = -1;
	private static final String PRIORITY_HIGH_ALIAS = "h";
	private static final String PRIORITY_MEDIUM_ALIAS_1 = "med";
	private static final String PRIORITY_MEDIUM_ALIAS_2 = "m";
	private static final String PRIORITY_LOW_ALIAS = "l";



	protected PrettyTimeParser timeParser;

	public AddCommandParser() {
		super();
		timeParser = new PrettyTimeParser();

	}

	public String[] determineParameters(String commandContent) 
			throws InvalidInputFormatException {
		assert commandContent != null;


		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException("Cannot add an empty task!");
		}
		String[] parameters = new String[5];
		commandContent = formatToStandardCommandContent(commandContent);
		parameters[TASK] = determineTask(commandContent);
		if (parameters[TASK].isEmpty()) {
			throw new InvalidInputFormatException("Task name is missing!");
		}
		String timeSegment = determineTimeSegment(commandContent.toLowerCase());
		parameters[TIME] = determineTime(timeSegment);
		parameters[PRIORITY] = determinePriority(commandContent);
		parameters[TASK_TYPE] = determineTaskType(commandContent);
		parameters[STATUS] = determineStatus(timeSegment);

		return parameters;
	}

	protected String determineTask(String content) {
		int timeIndex = getStartingIndexOfIdentifier(content);
		if (timeIndex == FIELD_NOT_EXIST) {
			int priorityIndex = getStartingIndexOfPriority(content);
			if (priorityIndex == FIELD_NOT_EXIST) {
				return content;
			}
			else {
				if (priorityIndex == 0) {
					return EMPTY_STRING;
				}
				return content.substring(0, priorityIndex - 1);
			}
		}
		else {
			if (timeIndex == 0) {
				return EMPTY_STRING;
			}
			return content.substring(0, timeIndex - 1);
		}
	}

	protected String determineTime(String content) throws InvalidInputFormatException {


		String timeSegment = determineTimeSegment(content);
		//System.out.println(timeSegment);
		List<Date> dates = timeParser.parse(timeSegment);
		//System.out.println(dates);


		if (dates.size() == 0) {
			return "[]";
		}
		else {
			isTimeAmbiguous(dates, timeSegment);


			//modifyDateToTomorrowIfExpired(dates);
			String result = dates.toString();
			System.out.println(result);
			if (!timeSegment.contains("now")) {
				result = setDefaultTimeIfNotSpecified(timeSegment, dates);
			}
			return result;
		}
	}

	private void isTimeAmbiguous(List<Date> dates, String timeSegment) throws InvalidInputFormatException {
		int size = dates.size();
		if (size > 2) {
			throw new InvalidInputFormatException("Ambiguous time entered!");
		}
		if (size == 2) {
			if (!containsWholeWord(timeSegment, DURATION_FLAG_TO)) {
				throw new InvalidInputFormatException("Date format not supported!");
			}
		}
	}

	private String setDefaultTimeIfNotSpecified(String timeSegment, List<Date> dates) {
		String parsedTime = dates.toString();
		String currentSystemTime = new Date().toString();
		parsedTime = getRoughTime(parsedTime);
		currentSystemTime = getRoughTime(currentSystemTime);

		if (parsedTime.equals(currentSystemTime)) {
			String result = timeParser.parse(
					timeSegment + WHITE_SPACE + DEFAULT_TIME).toString();
			return result;
		}
		else {
			return dates.toString();
		}
	}

	private String getRoughTime(String fullDate) {

		return fullDate.substring(fullDate.indexOf(TIME_SEPARATOR) - 2, 
				fullDate.indexOf(TIME_SEPARATOR, fullDate.indexOf(TIME_SEPARATOR) + 1) + 2);
	}


	private String determineTimeSegment(String content) {
		int timeIndex = getStartingIndexOfIdentifier(content);
		int priorityIndex = getStartingIndexOfPriority(content);
		String timeSegment;
		if (timeIndex == -1) {
			timeSegment = EMPTY_STRING;
		}
		else if (priorityIndex == FIELD_NOT_EXIST) {
			timeSegment = content.substring(timeIndex);
		}
		else {
			timeSegment = content.substring(timeIndex, priorityIndex - 1);
		}
		timeSegment = formatTimeSegment(timeSegment);
		return timeSegment;
	}

	private String formatTimeSegment(String timeSegment) {
		if (containsWholeWord(timeSegment, EVENT_FLAG_ON) && 
				containsWholeWord(timeSegment, DURATION_FLAG_FROM )){
			timeSegment = timeSegment.replaceAll(DURATION_FLAG_FROM, 
					EMPTY_STRING);
			timeSegment = removeTrailingSpaces(timeSegment);
		}
		return timeSegment;
	}

	protected String determinePriority(String content) throws InvalidInputFormatException {

		if (content.contains(PRIORITY_FLAG)) {
			String priority = content.substring(content.indexOf
					(PRIORITY_FLAG) + 1).trim();
			priority = priority.toLowerCase();
			if (!isValidPriority(priority)) {
				throw new InvalidInputFormatException
				("Please enter a valid priority level");
			}

			return getPriorityInFull(priority);
		}
		else {
			return PRIORITY_LEVEL.NOT_SPECIFIED.getType();
		}
	}

	private boolean isValidPriority(String priority) {

		if (priority.equals(PRIORITY_LEVEL.HIGH.getType()) ||
				priority.equals(PRIORITY_LEVEL.MEDIUM.getType()) ||
				priority.equals(PRIORITY_LEVEL.LOW.getType()) || 
				priority.equals(PRIORITY_HIGH_ALIAS) ||
				priority.equals(PRIORITY_MEDIUM_ALIAS_1) || 
				priority.equals(PRIORITY_MEDIUM_ALIAS_2) || 
				priority.equals(PRIORITY_LOW_ALIAS)) {
			return true;
		}
		return false;
	}

	private String getPriorityInFull(String priority) {
		if (priority.equals(PRIORITY_LEVEL.HIGH.getType()) || 
				priority.equals(PRIORITY_HIGH_ALIAS)) {
			priority = PRIORITY_LEVEL.HIGH.getType();
		}
		else if (priority.equals(PRIORITY_LEVEL.MEDIUM.getType()) || 
				priority.equals(PRIORITY_MEDIUM_ALIAS_1) || 
				priority.equals(PRIORITY_MEDIUM_ALIAS_2)) {
			priority = PRIORITY_LEVEL.MEDIUM.getType();
		}
		else if (priority.equals(PRIORITY_LEVEL.LOW.getType()) ||
				priority.equals(PRIORITY_LOW_ALIAS)) {
			priority = PRIORITY_LEVEL.LOW.getType();
		}
		return priority;
	}

	protected String determineTaskType(String content) {
		int timeIndex = getStartingIndexOfIdentifier(content);
		String timeSegment = determineTimeSegment(content).toLowerCase();
		if (timeIndex == FIELD_NOT_EXIST) {
			return EVENT_TASK;
		}
		else if (isDurationTask(timeSegment)) {
			return DURATION_TASK;
		}
		else {
			if (content.substring(timeIndex, timeIndex + 2).equalsIgnoreCase
					(DEADLINE_FLAG_BY)|| content.substring(timeIndex, 
							timeIndex + 6).equalsIgnoreCase
					(DEADLINE_FLAG_BEFORE)) {
				return DEADLINE_TASK;
			}

			return EVENT_TASK;
		}
	}

	private boolean containsWholeWord(String content, String keyword) {
		String[] segments = content.split(WHITE_SPACE);  
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].equalsIgnoreCase(keyword)) {
				return true;
			}
		}
		return false;
	}
	private boolean isDurationTask(String timeSegment) {
		//System.out.println(timeSegment);
		if (timeParser.parse(timeSegment).size() == 2 &&
				(containsWholeWord(timeSegment, DURATION_FLAG_FROM)
						|| containsWholeWord(timeSegment, DURATION_FLAG_TO))) {
			return true;
		}
		return false;
	}

	protected String determineStatus(String timeSegment) {
		List<Date> dates = timeParser.parse(timeSegment);
		int size = dates.size();
		if (size == 0) {
			return FLOATING_TASK;
		}
		else if (isOverdue(dates.get(size - 1)) && (!dates.toString().
				substring(1,11).equals(new Date().toString().substring(0, 10)))) {
			return OVERDUE_TASK;
		}
		else {
			return UPCOMING_TASK;
		}
	}

	private String addPrepositionIfApplicable(String content, String timePhrase) {
		//contains keyword "tomorrow"
		if (content.contains(timePhrase)) {

			//"tomorrow" is the first word
			if (content.indexOf(timePhrase) == 0) {
				int startIndex = content.indexOf(WHITE_SPACE) + 1;
				String nextWord = content.substring(startIndex);
				if (nextWord.indexOf(WHITE_SPACE) != -1) {
					nextWord = nextWord.substring(0, 
							nextWord.indexOf(WHITE_SPACE));
				}
				//there is a valid preposition following "tomorrow"
				if (startIndex < content.length() && 
						isValidTimeIdentifier(nextWord)) {

					content = content.substring(startIndex);
					if (timeParser.parse(content).size() == 0) {

						return EVENT_FLAG_ON + WHITE_SPACE + 
								timePhrase + WHITE_SPACE + content;
					}
					int index = content.indexOf(WHITE_SPACE) + 1;
					content = content.substring(0, index) + timePhrase 
							+ WHITE_SPACE + content.substring(index);
					return content;
				}
				//no preposition after "tomorrow"
				else {

					content = EVENT_FLAG_ON + WHITE_SPACE + content;
				}
			}
			//"tomorrow" is not the first word
			else {
				//determine the position of "tomorrow"
				String[] segments = content.split(WHITE_SPACE);
				int len = segments.length;
				int index = 0;
				for (int i = 0; i < len; i++) {
					if (segments[i].equals(timePhrase)) {
						index = i;
						break;
					}
				}

				//there is a valid preposition before "tomorrow"
				if (isValidTimeIdentifier(segments[index - 1])) {
					return content;
				}

				//there is no valid preposition before "tomorrow"
				else {
					if (index + 1 == len) {
						String newContent = EMPTY_STRING;
						for (int i = 0; i < index; i++) {
							newContent += segments[i] + WHITE_SPACE;
						}
						newContent += EVENT_FLAG_ON + WHITE_SPACE + segments[index];
						return newContent;
					}
					if (index + 1 < len && 
							!isValidTimeIdentifier(segments[index + 1])) {
						String newContent = EMPTY_STRING;
						for (int i = 0; i < index; i++) {
							newContent += segments[i] + WHITE_SPACE;
						}
						newContent += EVENT_FLAG_ON + WHITE_SPACE + segments[index];
						for (int i = index + 1; i < len; i++) {
							newContent += WHITE_SPACE + segments[i];
						}
						return newContent;
					}

					if (index + 1 < len && 
							isValidTimeIdentifier(segments[index + 1])) {
						String newContent = EMPTY_STRING;
						for (int i = 0; i < index; i++) {
							newContent += segments[i] + WHITE_SPACE;
						}
						String rest = EMPTY_STRING;
						for (int i = index + 2; i < len; i++) {
							rest += WHITE_SPACE + segments[i];
						}
						if (timeParser.parse(rest).size() == 0) {
							newContent += EVENT_FLAG_ON + WHITE_SPACE + segments[index];
						}
						else {
							newContent += segments[index + 1] + 
									WHITE_SPACE + segments[index];
						}

						return newContent + rest;
					}
				}
			}
		}
		return content;
	}

	protected String formatToStandardCommandContent(String content) {
		content = preFormat(content);
		//System.out.println(content);
		int time = getStartingIndexOfIdentifier(content);
		int priority = getStartingIndexOfPriority(content);
		int task = getStartingIndexOfTask(content, time, priority);

		//task only
		if (time == FIELD_NOT_EXIST && priority == FIELD_NOT_EXIST) {
			return content;
		}
		//no time,has priority
		else if (time == FIELD_NOT_EXIST) {
			//only priority
			if (task == FIELD_NOT_EXIST) {
				return content;
			}

			else {
				if (task > priority) {
					return content.substring(task)+ WHITE_SPACE + 
							content.substring(0, task - 1);
				}
				else {
					return content;
				}
			}
		}
		//no priority,has time
		else if (priority == FIELD_NOT_EXIST) {
			//only time
			if (task == FIELD_NOT_EXIST) {
				return content;
			}

			else {
				if (task > time) {
					return content.substring(task)+ WHITE_SPACE + 
							content.substring(0, task - 1);
				}
				else {
					return content;
				}
			}	
		}

		//time,priority,task(maybe)
		else {
			if (task == FIELD_NOT_EXIST) {
				if (time > priority) {
					return content.substring(time)+ WHITE_SPACE + 
							content.substring(0, time - 1);
				}
				else {
					return content;
				}
			}

			else {
				if (task < time && task < priority) {
					if (time < priority) {
						return content;
					}
					//task-priority-time
					else {
						return content.substring(task,priority) + 
								content.substring(time) + WHITE_SPACE + 
								content.substring(priority,time - 1);
					}
				}
				//priority-task-time
				else if (task < time) {
					return content.substring(task) + WHITE_SPACE +
							content.substring(priority,task - 1);
				}
				//time-task-priority
				else if (task < priority) {
					return content.substring(task, priority - 1) + WHITE_SPACE 
							+ content.substring(time, task - 1) + WHITE_SPACE +
							content.substring(priority);
				}
				//task is the last
				else {
					//time-priority-task
					if (time < priority) {
						return content.substring(task) + WHITE_SPACE 
								+ content.substring(time, priority - 1) + WHITE_SPACE +
								content.substring(priority, task - 1);
					}
					//priority-time-task
					else {
						return content.substring(task) + WHITE_SPACE 
								+ content.substring(time, task - 1) + WHITE_SPACE +
								content.substring(priority, time - 1);
					}
				}
			}
		}
	}


	private String preFormat(String content) {
		content = removeTrailingSpaces(content);
		content = handleTimeWithoutPreposition(content);
		return content;
	}



	private String handleTimeWithoutPreposition(String content) {
		content = content.replaceAll(TOMORROW_IN_SHORT, TOMORROW_IN_FULL);
		if (isNecessaryToAddPrepostion(content, NOW)) {
			content = addPrepositionIfApplicable(content, NOW);
		}
		if (isNecessaryToAddPrepostion(content, TODAY)) {
			content = addPrepositionIfApplicable(content, TODAY);
		}
		if (isNecessaryToAddPrepostion(content, TOMORROW_IN_FULL)) {
			content = addPrepositionIfApplicable(content, TOMORROW_IN_FULL);
		}
		return content;
	}

	private String removeTrailingSpaces(String content) {
		content = content.replaceAll(EXTRA_WHITE_SPACES, WHITE_SPACE).trim();
		return content;
	}

	private boolean isNecessaryToAddPrepostion(String content, String timePhrase) {
		if (containsWholeWord(content, "\"" + timePhrase + "\"")) {
			return false;
		}
		int index = content.indexOf(timePhrase);
		if (index == -1) {
			return false;
		}

		String front = content.substring(0,index).trim();
		if (timeParser.parse(front).toString().equals(EMPTY_TIME)) {
			return true;
		}
		return false;
	}


	private int getStartingIndexOfPriority(String content) {
		return content.indexOf(PRIORITY_FLAG);
	}


	private int getStartingIndexOfTask(String content, int timeIndex, int priorityIndex) {
		//no time, no tag -> must have and only task
		if (timeIndex == FIELD_NOT_EXIST && priorityIndex == FIELD_NOT_EXIST) {
			return 0;
		}
		//no time, has tag -> tag-task/task-tag/tag
		else if (timeIndex == FIELD_NOT_EXIST) {
			//tag
			if (!content.contains(WHITE_SPACE)) {
				return FIELD_NOT_EXIST;
			}
			else {
				//tag-task
				if (content.substring(0,1).equals(PRIORITY_FLAG)) {
					return content.indexOf(WHITE_SPACE) + 1;
				}
				//task-tag
				else {
					return 0;
				}
			}
		}

		//no priority, has time -> time-task/task-time/time
		else if (priorityIndex == FIELD_NOT_EXIST) {

			//<time>/<time-task>
			if (timeIndex == 0) {
				//fill in
				return locateTaskIndexInSegment(content);	
			}
			//task-time
			else {
				return 0;
			}	
		}

		//has time, has priority -> 
		//1.task-priority-time
		//2.task-time-priority
		//3.time-task-priority
		//4.priority-task-time
		//5.time-priority-task
		//6.priority-time-task
		//7.priority-time
		//8.time-priority
		else {
			//3,5,8
			if (timeIndex == 0) {
				//5
				if (priorityIndex < content.lastIndexOf(WHITE_SPACE)) {
					return content.indexOf(WHITE_SPACE,content.indexOf
							(PRIORITY_FLAG)) + 1;
				}
				//3,8   <time-task>-priority/<time>-priority
				else {
					//fill in
					String segment = content.substring(0,content.indexOf
							(PRIORITY_FLAG) - 1);
					return locateTaskIndexInSegment(segment);


				}	
			}

			//4,6,7
			else if (priorityIndex == 0) {
				//6,7 priority-<time>/priority-<time-task>
				if (timeIndex == content.indexOf(WHITE_SPACE) + 1) {
					//fill in
					int baseIndex = content.indexOf(WHITE_SPACE) + 1;
					String segment = content.substring(baseIndex);
					int index = locateTaskIndexInSegment(segment);
					if (index == FIELD_NOT_EXIST) {
						return FIELD_NOT_EXIST;
					}
					return baseIndex + index;

				}
				//4
				else {
					return content.indexOf(WHITE_SPACE) + 1;
				}

			}

			//1,2 ->task must be present
			else {
				return 0;
			}

		}
	}

	private int locateTaskIndexInSegment(String content) {
		int taskIndex = FIELD_NOT_EXIST;
		//input: time/time-task
		int count = StringUtils.countMatches(content, WHITE_SPACE);


		if (count == 1) {
			return FIELD_NOT_EXIST;
		}

		else {
			String oldDate = getRoughDate(timeParser.parse(content).toString());
			for (int i = 2; i <= count; i++) {
				int index = StringUtils.ordinalIndexOf(content, WHITE_SPACE, i);
				String newDate = content.substring(0, index);
				newDate = timeParser.parse(newDate).toString();
				if (!newDate.equals(EMPTY_TIME)) {
					newDate = getRoughDate(newDate);
				}
				if (newDate.equals(oldDate)) {
					taskIndex = index + 1;
					break;
				}
			}
			return taskIndex;
		}

	}



	private int getStartingIndexOfIdentifier(String content) {
		String[] segments = content.split(WHITE_SPACE);
		int numSpace = segments.length - 1;

		//no time
		if (numSpace == 0) {
			return FIELD_NOT_EXIST;
		}

		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		int pointer = 0;


		for (int i = 1; i <= numSpace; i++) {
			int index = StringUtils.ordinalIndexOf(content, WHITE_SPACE, i);


			if (isValidTimeIdentifier(content.substring(pointer, index))) {
				pq.offer(pointer);
			}

			pointer = index + 1;
		}

		ArrayList<Integer> list = new ArrayList<Integer>();

		int size = pq.size();

		for (int i = 0; i < size; i++) {
			list.add(pq.poll());
		}



		//no time
		if (list.size() == 0) {
			return FIELD_NOT_EXIST;
		}

		//the only one match is the real identifier
		if (list.size() == 1) {
			if (!timeParser.parse(content).toString().equals(EMPTY_TIME)) {
				return list.get(0);
			}
			else {
				return FIELD_NOT_EXIST;
			}
		}

		if (list.size() == 2) {
			if (!timeParser.parse(content.substring(list.get(0), list.get(1)))
					.toString().equals(EMPTY_TIME)) {
				return list.get(0);
			}

			else if (!timeParser.parse(content.substring(list.get(1)))
					.toString().equals(EMPTY_TIME)) {
				return list.get(1);
			}

			else {
				return FIELD_NOT_EXIST;
			}
		}

		//list size is at least 3
		//check substrings to determine the starting index of the real identifier
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() - 1) {
				String substring = content.substring(list.get(i),list.get(i + 1));
				if (!timeParser.parse(substring).toString().equals(EMPTY_TIME)) {
					return list.get(i);
				}
			}

			else {
				String substring = content.substring(list.get(i));
				if (!timeParser.parse(substring).toString().equals(EMPTY_TIME)) {
					return list.get(i);
				}
			}
		}


		return FIELD_NOT_EXIST;
	}

	private boolean isValidTimeIdentifier(String content) {
		String word = content.toLowerCase();
		//content.spit
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
		return false;
	}

	private boolean isOverdue(Date time) {
		return time.before(new Date());
	}

	private String getRoughDate(String time) {
		String[] segments = time.split(TIME_SEPARATOR);
		time = segments[0] + segments[1] + segments[2].substring(2);
		return time;
	}
}
/* @@author A0127481E */