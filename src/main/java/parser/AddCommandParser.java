package main.java.parser;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class AddCommandParser extends Parser {

	private static final String WHITE_SPACE = " ";
	private static final String DEADLINE_FLAG = "by";
	private static final String EVENT_FLAG_ON = "on";
	private static final String EVENT_FLAG_AT = "at";
	private static final String DEADLINE_TASK = "deadline";
	private static final String EVENT_TASK = "event";
	private static final String PRIORITY_FLAG = "#";	
	private static final String OVERDUE = "overdue";
	private static final String UNDONE = "undone";
	private static final String EMPTY_TIME = "[]";
	private static final String EXTRA_WHITE_SPACES = "\\s+";
	private static final String DEFAULT_TIME = "8am";
	private static final int FIELD_NOT_EXIST = -1;

	protected PrettyTimeParser timeParser;

	public AddCommandParser() {
		super();
		timeParser = new PrettyTimeParser();
	}


	public String[] determineParameters(String commandType, String commandContent) 
			throws InvalidInputFormatException {
		assert commandType != null;
		//assert 1==2;
		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException("Cannot add an empty task!");
		}
		String[] parameters = new String[5];
		commandContent = formatToStandardCommandContent(commandContent);
		parameters[TASK] = determineTask(commandContent);
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

		int timeIndex = getStartingIndexOfIdentifier(content);
		int priorityIndex = getStartingIndexOfPriority(content);
		if (timeIndex == FIELD_NOT_EXIST) {
			return EMPTY_STRING;
		}
		if (priorityIndex == FIELD_NOT_EXIST) {
			content = content.substring(timeIndex);
		}
		else {
			content = content.substring(timeIndex, priorityIndex - 1);
		}

		List<Date> dates = timeParser.parse(content);

		if (dates.size() == 0) {
			return EMPTY_STRING;
		}

		if (isOverdue(dates.get(0))) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dates.get(0));
			calendar.add(Calendar.DATE, 1);  // number of days to add
			dates.set(0,calendar.getTime()); // dt is now the new date
		}


		String parsedTime = dates.toString();
		String currentTime = new Date().toString();
		parsedTime = parsedTime.substring(parsedTime.indexOf(TIME_SEPARATOR) - 2, 
				parsedTime.indexOf(TIME_SEPARATOR, parsedTime.indexOf
						(TIME_SEPARATOR) + 1) + 2);
		currentTime = currentTime.substring(currentTime.indexOf
				(TIME_SEPARATOR) - 2, 
				currentTime.indexOf(TIME_SEPARATOR, currentTime.indexOf
						(TIME_SEPARATOR) + 1) + 2);

		if (parsedTime.equals(currentTime)) {
			String result = timeParser.parse(content + 
					WHITE_SPACE + DEFAULT_TIME).toString();
			return result.substring(1, result.length() - 1);
		}

		return dates.toString().substring(1, dates.toString().length() - 1);
	}

	protected String determinePriority(String content) {

		if (content.contains(PRIORITY_FLAG)) {
			return content.substring(content.indexOf(PRIORITY_FLAG) + 1).trim();
		}
		else {
			return EMPTY_STRING;
		}
	}

	protected String determineTaskType(String content) {
		int timeIndex = getStartingIndexOfIdentifier(content);
		if (timeIndex == FIELD_NOT_EXIST) {
			return EVENT_TASK;
		}
		else {
			String identifier = content.substring(timeIndex, timeIndex + 2);
			if (identifier.equalsIgnoreCase(DEADLINE_FLAG)) {
				return DEADLINE_TASK;
			}
			return EVENT_TASK;
		}
	}

	protected String determineStatus(String content) {
		List<Date> dates = timeParser.parse(content);
		if (dates.size() != 0) {
			if (isOverdue(dates.get(0))) {
				return OVERDUE;
			}
		}
		return UNDONE;
	}

	protected String formatToStandardCommandContent(String content) {
		content = content.replaceAll(EXTRA_WHITE_SPACES, WHITE_SPACE).trim();
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
			String oldTime = getRoughTime(timeParser.parse(content).toString());
			for (int i = 2; i <= count; i++) {
				int index = StringUtils.ordinalIndexOf(content, WHITE_SPACE, i);
				String newTime = content.substring(0, index);
				newTime = timeParser.parse(newTime).toString();
				if (!newTime.equals(EMPTY_TIME)) {
					newTime = getRoughTime(newTime);
				}
				if (newTime.equals(oldTime)) {
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
		if (content.equalsIgnoreCase(DEADLINE_FLAG)) {
			return true;
		}

		else if (content.equalsIgnoreCase(EVENT_FLAG_AT)) {
			return true;
		}
		else if (content.equalsIgnoreCase(EVENT_FLAG_ON)) {
			return true;
		}
		return false;
	}
	
	private boolean isOverdue(Date time) {
		return time.before(new Date());
	}
	
	private String getRoughTime(String time) {
		String[] segments = time.split(TIME_SEPARATOR);
		time = segments[0] + segments[1] + segments[2].substring(2);
		return time;
	}
}
