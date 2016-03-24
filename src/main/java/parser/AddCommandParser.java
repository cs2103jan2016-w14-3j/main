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
	private static final String RECURRING_FLAG_AND = "and";
	private static final String DURATION_OR_RECURRING_FLAG_FROM = "from";
	private static final String DEADLINE_TASK = "deadline";
	private static final String EVENT_TASK = "one-time event";
	private static final String RECURRING_TASK = "recurring";
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
	private static final int FIELD_NOT_EXIST = -1;


	protected PrettyTimeParser timeParser;

	public AddCommandParser() {
		super();
		timeParser = new PrettyTimeParser();
	}


	public static void main(String[] args)
	{
		PrettyTimeParser pars = new PrettyTimeParser();
		//System.out.println(pars.parse("from next monday to next wed"));
		AddCommandParser parser = new AddCommandParser();
		//System.out.println(parser.isRecurringTask("from mon to wed do this and that"));
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
		parameters[TIME] = determineTime(commandContent);
		parameters[PRIORITY] = determinePriority(commandContent);
		parameters[TASK_TYPE] = determineTaskType(commandContent);
		parameters[STATUS] = determineStatus(commandContent);

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

	protected String determineTime(String content) {


		String timeSegment = determineTimeSegment(content);

		List<Date> dates = timeParser.parse(timeSegment);

		if (dates.size() == 0) {
			return EMPTY_STRING;
		}
		else {
			modifyDateToTomorrowIfExpired(dates);

			String result = setDefaultTimeIfNotSpecified(content, dates);

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
			if (!isValidPriority(priority)) {
				throw new InvalidInputFormatException("Please enter a valid priority level");
			}
			return content.substring(content.indexOf(PRIORITY_FLAG) + 1).trim();
		}
		else {
			return PRIORITY_LEVEL.LOW.getType();
		}
	}

	private boolean isValidPriority(String priority) {
		if (priority.equals(PRIORITY_LEVEL.HIGH.getType()) || priority.equalsIgnoreCase(PRIORITY_LEVEL.MEDIUM.getType()) ||
				priority.equalsIgnoreCase(PRIORITY_LEVEL.LOW.getType())) {
			return true;
		}
		return false;
	}

	protected String determineTaskType(String content) {
		int timeIndex = getStartingIndexOfIdentifier(content);
		if (timeIndex == FIELD_NOT_EXIST) {
			return EVENT_TASK;
		}
		else if (isRecurringTask(content)) {
			return RECURRING_TASK;
		}
		else if (isDurationTask(content)) {
			return DURATION_TASK;
		}
		else {
			String identifier = content.substring(timeIndex, timeIndex + 2);
			if (identifier.equalsIgnoreCase(DEADLINE_FLAG_BY) ||
					identifier.equalsIgnoreCase(DEADLINE_FLAG_BEFORE)) {
				return DEADLINE_TASK;
			}

			return EVENT_TASK;
		}
	}


	private boolean isRecurringTask(String content) {

		int timeIndex = getStartingIndexOfIdentifier(content);
		int priorityIndex = getStartingIndexOfPriority(content);
		if (timeIndex == FIELD_NOT_EXIST) {
			return false;
		}
		if (priorityIndex == FIELD_NOT_EXIST) {
			content = content.substring(timeIndex);
		}
		else {
			content = content.substring(timeIndex, priorityIndex - 1);
		}

		List<Date> dates = timeParser.parse(content);
		if (dates.size() == 1) {
			if (containsWholeWord(content, RECURRING_FLAG_EVERY)) {
				return true;
			}
		}
		else if (dates.size() > 1) {
			if (containsWholeWord(content, RECURRING_FLAG_AND)) {
				return true;
			}
			else if (containsWholeWord(content, DURATION_OR_RECURRING_FLAG_FROM)) {
				if (!dates.get(0).toString().substring(0, 10).equals
						(dates.get(1).toString().substring(0, 10))) {
					return true;
				}
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
	private boolean isDurationTask(String content) {
		if (timeParser.parse(content).size() > 1) {
			return true;
		}
		return false;
	}
	
	protected String determineStatus(String content) {
		List<Date> dates = timeParser.parse(content);
		int size = dates.size();
		if (size == 0) {
			return UPCOMING_TASK;
		}
		else if (isOverdue(dates.get(size - 1))) {
			return OVERDUE_TASK;
		}
		else {
			return UPCOMING_TASK;
		}
	}


	protected String formatToStandardCommandContent(String content) {
		content = content.replaceAll(EXTRA_WHITE_SPACES, WHITE_SPACE).trim();
		content = StringUtils.replace(content, TOMORROW_IN_SHORT, TOMORROW_IN_FULL);
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
		//content.spit
		if (content.equalsIgnoreCase(DEADLINE_FLAG_BY)) {
			return true;
		}
		else if (content.equalsIgnoreCase(DEADLINE_FLAG_BEFORE)) {
			return true;
		}

		else if (content.equalsIgnoreCase(EVENT_FLAG_AT)) {
			return true;
		}
		else if (content.equalsIgnoreCase(EVENT_FLAG_ON)) {
			return true;
		}
		else if (content.equalsIgnoreCase(RECURRING_FLAG_EVERY)) {
			return true;
		}
		else if (content.equalsIgnoreCase(DURATION_OR_RECURRING_FLAG_FROM)) {
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
