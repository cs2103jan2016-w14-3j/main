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
	private static final String RECURRING_FLAG_EVERY = "every";
	private static final String DURATION_FLAG_FROM = "from";
	private static final String DURATION_FLAG_TO = "to";
	private static final String DEADLINE_TASK = "deadline";
	private static final String EVENT_TASK = "one-time event";
	private static final String RECURRING_TASK_EVERY = "recurring";
	private static final String RECURRING_TASK_ALTERNATE = "alternate";
	private static final String DURATION_TASK = "duration";
	private static final String PRIORITY_FLAG = "#";	
	private static final String EMPTY_TIME = "[]";
	private static final String EXTRA_WHITE_SPACES = "\\s+";
	private static final String DEFAULT_TIME = "8am";
	private static final String TOMORROW_IN_FULL = "tomorrow";
	private static final String TOMORROW_IN_SHORT = "tmr";
	private static final String OVERDUE_TASK = "overdue";
	private static final String UPCOMING_TASK = "upcoming";
	private static final String COMPLETED_TASK = "completed";
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


	public static void main(String[] args)
	{
		PrettyTimeParser pars = new PrettyTimeParser();
		System.out.println(pars.parse("by mon 11:19pm"));
		AddCommandParser parser = new AddCommandParser();
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

	protected String determineTime(String timeSegment) {


		//String timeSegment = determineTimeSegment(content);

		List<Date> dates = timeParser.parse(timeSegment);
		
		if (dates.size() == 0) {
			return "[]";
		}
		else {
			modifyDateToTomorrowIfExpired(dates);
			
			String result = setDefaultTimeIfNotSpecified(timeSegment, dates);
			

			return result;
		}
	}

	private void modifyDateToTomorrowIfExpired(List<Date> dates) {
		for (int i = 0; i < dates.size(); i++) {
			if (isOverdue(dates.get(i))) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dates.get(i));
				calendar.add(Calendar.DATE, 1);  // number of days to add
				dates.set(i,calendar.getTime()); // dt is now the new date
			}
		}

	}

	private String setDefaultTimeIfNotSpecified(String content, List<Date> dates) {
		String parsedTime = dates.toString();
		String currentSystemTime = new Date().toString();
		parsedTime = getRoughTime(parsedTime);
		currentSystemTime = getRoughTime(currentSystemTime);

		if (parsedTime.equals(currentSystemTime)) {
			String result = timeParser.parse(
					content + WHITE_SPACE + DEFAULT_TIME).toString();
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
		if (timeIndex == FIELD_NOT_EXIST) {
			return EMPTY_STRING;
		}

		else if (priorityIndex == FIELD_NOT_EXIST) {
			return content.substring(timeIndex);
		}
		else {
			return content.substring(timeIndex, priorityIndex - 1);
		}
	}

	protected String determinePriority(String content) throws InvalidInputFormatException {

		if (content.contains(PRIORITY_FLAG)) {
			String priority = content.substring(content.indexOf(PRIORITY_FLAG) + 1).trim();
			priority = priority.toLowerCase();
			if (!isValidPriority(priority)) {
				throw new InvalidInputFormatException("Please enter a valid priority level");
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
		else if (isRecurringTask(timeSegment, timeIndex)) {
			if (timeSegment.contains(RECURRING_TASK_ALTERNATE)) {
				return RECURRING_TASK_ALTERNATE;
			}
			else {
				return RECURRING_TASK_EVERY;
			}
		}
		else if (isDurationTask(timeSegment)) {
			return DURATION_TASK;
		}
		else {
			if (content.substring(timeIndex, timeIndex + 2).equalsIgnoreCase
					(DEADLINE_FLAG_BY)|| content.substring(timeIndex, 
							timeIndex + 6).equalsIgnoreCase(DEADLINE_FLAG_BEFORE)) {
				return DEADLINE_TASK;
			}

			return EVENT_TASK;
		}
	}


	private boolean isRecurringTask(String timeSegment, int timeIndex) {


		List<Date> dates = timeParser.parse(timeSegment);
		if (dates.size() == 1) {
			if (containsWholeWord(timeSegment, RECURRING_FLAG_EVERY)) {
				return true;
			}

		}

		return false;
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
		if (timeParser.parse(timeSegment).size() == 2 &&
				(containsWholeWord(timeSegment, DURATION_FLAG_FROM)
						|| containsWholeWord(timeSegment, DURATION_FLAG_TO))) {
			//System.out.println("HERE");
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
	private String addPrepositionIfApplicable(String content) {
		//contains keyword "tomorrow"
		if (content.contains(TOMORROW_IN_FULL)) {
			//"tomorrow" is the first word
			if (content.indexOf(TOMORROW_IN_FULL) == 0) {
				int startIndex = content.indexOf(WHITE_SPACE) + 1;
				//there is a valid preposition following "tomorrow"
				if ((startIndex < content.length() && 
						(isValidTimeIdentifier(content.substring(
								startIndex, startIndex + 2)) || isValidTimeIdentifier
								(content.substring(startIndex, startIndex + 4)) || 
								isValidTimeIdentifier(content.substring
										(startIndex, startIndex + 6))))) {
					content = content.substring(startIndex);
					int index = content.indexOf(WHITE_SPACE) + 1;
					content = content.substring(0, index) + TOMORROW_IN_FULL + WHITE_SPACE 
							+ content.substring(index);
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
					if (segments[i].equals(TOMORROW_IN_FULL)) {
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
					if (index + 1 <= len && 
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
						newContent += segments[index + 1] + WHITE_SPACE + segments[index];
						for (int i = index + 2; i < len; i++) {
							newContent += WHITE_SPACE + segments[i];
						}

						return newContent;
					}
				}
			}
		}
		return content;
	}

	protected String formatToStandardCommandContent(String content) {
		content = content.replaceAll(EXTRA_WHITE_SPACES, WHITE_SPACE).trim();
		content = StringUtils.replace(content, TOMORROW_IN_SHORT, TOMORROW_IN_FULL);
		if (isNecessaryToAddPrepostion(content)) {
		content = addPrepositionIfApplicable(content);
		}
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


	private boolean isNecessaryToAddPrepostion(String content) {
		int index = content.indexOf(TOMORROW_IN_FULL);
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
		else if (word.equals(RECURRING_FLAG_EVERY)) {
			return true;
		}
		else if (word.equals(DURATION_FLAG_FROM)) {
			return true;
		}
		return false;
	}

	private boolean isOverdue(Date time) {
		//System.out.println(new Date());
		return time.before(new Date());
	}

	private String getRoughDate(String time) {
		String[] segments = time.split(TIME_SEPARATOR);
		time = segments[0] + segments[1] + segments[2].substring(2);
		return time;
	}
}
