package main.java.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import main.java.data.*;
import org.apache.commons.lang3.StringUtils;


public class CommandParser {

	private static final String EMPTY_COMMAND = "empty";
	private static final String ADD_COMMAND = "add";
	private static final String DELETE_COMMAND = "delete";
	private static final String SEARCH_COMMAND = "search";
	private static final String CHANGE_DIRECTORY_COMMAND = "change";
	private static final String SORT_COMMAND = "sort";
	private static final String CLEAR_COMMAND = "clear";
	private static final String EDIT_COMMAND = "edit";
	private static final String UNDO_COMMAND = "undo";


	private static final String WHITE_SPACE = " ";
	private static final String DEADLINE_FLAG = "by";
	private static final String EVENT_FLAG_ON = "on";
	private static final String EVENT_FLAG_AT = "at";
	private static final String DEADLINE_TASK = "deadline";
	private static final String EVENT_TASK = "event";
	private static final String PRIORITY_FLAG = "#";
	private static final String EMPTY_STRING = "";
	private static final String EDIT_COMMAND_SEPARATOR = ",";
	private static final String TIME_SEPARATOR = ":";

	private static final int TASK = 0;
	private static final int TIME = 1;
	private static final int PRIORITY = 2;
	private static final int TASK_TYPE = 3;

	private static final int DAY_OF_WEEK = 0;
	private static final int MONTH = 1;
	private static final int DAY_OF_MONTH = 2;
	private static final int CLOCK_TIME = 3;

	private static PrettyTimeParser parser = new PrettyTimeParser();


	public CommandParser() {

	}

	public Command parseCommand(Command command) {
		assert command != null;
		String originalCommand = command.getOriginal();
		command.setType(determineCommandType(originalCommand));

		String commandContent = retrieveCommandContent(originalCommand);
		command.setContent(commandContent);
		command.setParameters(determineParameters(command.getType(),commandContent));
		return command;
	}

	private String determineCommandType(String originalCommand) {
		assert originalCommand != null;
		String keyword = getCommandKeyword(originalCommand);
		return keyword;
	}

	private String getCommandKeyword(String command) {
		assert command != null;
		String firstWord = getFirstKeyword(command);

		if (command.isEmpty()) {
			return EMPTY_COMMAND;
		}
		else {
			if (isCommand(ADD_COMMAND, firstWord)) {
				return ADD_COMMAND;
			}

			else if (isCommand(DELETE_COMMAND, firstWord)) {
				return DELETE_COMMAND;
			}

			else if (isCommand(SEARCH_COMMAND, firstWord)) {
				return SEARCH_COMMAND;
			}

			else if (isCommand(CHANGE_DIRECTORY_COMMAND, firstWord)) {
				return CHANGE_DIRECTORY_COMMAND;
			}

			else if (isCommand(SORT_COMMAND, firstWord)) {
				return SORT_COMMAND;
			}

			else if (isCommand(CLEAR_COMMAND, firstWord)) {
				return CLEAR_COMMAND;

			}

			else if (isCommand(EDIT_COMMAND, firstWord)) {
				return EDIT_COMMAND;

			}

			else if (isCommand(UNDO_COMMAND, firstWord)) {
				return UNDO_COMMAND;
			}

			else {
				return ADD_COMMAND;
			}
		}
	}

	private String getFirstKeyword(String command) {
		assert command != null;
		if (!command.contains(WHITE_SPACE)) {
			return command;
		}
		return command.substring(0,command.indexOf(WHITE_SPACE)).trim();
	}

	private boolean isCommand(String operation, String keyword) {
		assert keyword != null;
		return operation.equalsIgnoreCase(keyword);
	}

	private String retrieveCommandContent(String originalCommand) {
		assert originalCommand != null;
		if (originalCommand.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		if (getCommandKeyword(originalCommand).equalsIgnoreCase(ADD_COMMAND)) {
			if (!getFirstKeyword(originalCommand).equalsIgnoreCase(ADD_COMMAND)){
				return originalCommand;
			}
		}
		if (!originalCommand.contains(WHITE_SPACE)) {
			return EMPTY_STRING;
		}
		String content = originalCommand.substring(originalCommand.indexOf(WHITE_SPACE) + 1);
		return content.trim();
	}

	private String[] determineParameters(String commandType, String commandContent) {
		assert commandType != null;
		//assert 1==2;

		String[] parameters = new String[4];

		if (commandType.equalsIgnoreCase(EDIT_COMMAND) && 
				(commandContent.contains(EDIT_COMMAND_SEPARATOR))) {
			String[] segments = commandContent.split(EDIT_COMMAND_SEPARATOR);
			parameters[TASK] = determineTaskForEditCommand(segments);
			parameters[TIME] = determineTimeForEditCommand(segments);
			parameters[PRIORITY] = determinePriorityForEditCommand(segments);
			parameters[TASK_TYPE] = determineTaskTypeForEditCommand(segments);
		}
		else if (commandType.equalsIgnoreCase(ADD_COMMAND)){
			commandContent = formatToStandardCommandContent(commandContent);
			parameters[TASK] = determineTask(commandContent);
			parameters[TIME] = determineTime(commandContent);
			parameters[PRIORITY] = determinePriority(commandContent);
			parameters[TASK_TYPE] = determineTaskType(commandContent);
		}

		else if (commandType.equalsIgnoreCase(CHANGE_DIRECTORY_COMMAND)) {
			parameters[TASK] = commandContent.trim();
		}

		else if (commandType.equalsIgnoreCase(SORT_COMMAND)) {
			parameters[TASK] = commandContent.trim().toLowerCase();
		}

		else if (commandType.equalsIgnoreCase(SEARCH_COMMAND)) {
			//parameters[TASK] = commandContent.trim().toLowerCase();	
		}

		else if (commandType.equalsIgnoreCase(DELETE_COMMAND)) {

		}


		return parameters;


	}

	private String determineTask(String content) {
		int timeIndex = getStartingIndexOfIdentifier(content);
		if (timeIndex == -1) {
			int priorityIndex = getStartingIndexOfPriority(content);
			if (priorityIndex == -1) {
				return content;
			}
			else {
				return content.substring(0, priorityIndex - 1);
			}
		}
		else {
			return content.substring(0, timeIndex - 1);
		}
	}

	private String determineTime(String content) {

		int timeIndex = getStartingIndexOfIdentifier(content);
		int priorityIndex = getStartingIndexOfPriority(content);
		if (timeIndex == -1) {
			return EMPTY_STRING;
		}
		if (priorityIndex == -1) {
			content = content.substring(timeIndex);
		}
		else {
			content = content.substring(timeIndex, priorityIndex - 1);
		}

		System.out.println(content + "me");

		List<Date> dates = parser.parse(content);

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
			String result = parser.parse(content + " 8am").toString();
			return result.substring(1, result.length() - 1);
		}

		return dates.toString().substring(1, dates.toString().length() - 1);
	}

	private String determinePriority(String content) {

		if (content.contains(PRIORITY_FLAG)) {
			return content.substring(content.indexOf(PRIORITY_FLAG) + 1).trim();
		}
		else {
			return EMPTY_STRING;
		}
	}

	private String determineTaskType(String content) {
		int timeIndex = getStartingIndexOfIdentifier(content);
		if (timeIndex == -1) {
			return EVENT_TASK;
		}
		else {
			String identifier = content.substring(timeIndex, timeIndex + 1);
			if (identifier.equalsIgnoreCase(DEADLINE_FLAG)) {
				return DEADLINE_TASK;
			}
			return EVENT_TASK;
		}
	}

	private String determineTaskForEditCommand(String[] segments) {

		String task_A = determineTask(formatToStandardCommandContent(segments[0].trim())); 
		String task_B = determineTask(formatToStandardCommandContent(segments[1].trim()));

		if (task_B.equals(EMPTY_STRING)) {
			return task_A + " , " + task_A;
		}

		else {
			return task_A + " , " + task_B;
		}
	}

	private String determineTimeForEditCommand(String[] segments) {

		String time_A = determineTime(formatToStandardCommandContent(segments[0].trim())); 
		String time_B = determineTime(formatToStandardCommandContent(segments[1].trim()));

		if (!time_A.equals(EMPTY_STRING) && !time_B.equals(EMPTY_STRING)) {
			//change A to B

		}
		else if (!time_A.equals(EMPTY_STRING)) {
			//retain A
		}
		else if (!time_B.equals(EMPTY_STRING)) {
			//change A to B

		}
		else {
			//do nothing
		}
		return time_A + " , " + time_B;

	}

	private String determinePriorityForEditCommand(String[] segments) {
		//assert content != null;
		String priority_A = determinePriority(formatToStandardCommandContent(segments[0].trim())); 
		String priority_B = determinePriority(formatToStandardCommandContent(segments[1].trim()));

		if (!priority_A.equals(EMPTY_STRING) && !priority_B.equals(EMPTY_STRING)) {
			//change A to B
		}
		else if (!priority_A.equals(EMPTY_STRING)) {
			//retain A
		}
		else if (!priority_B.equals(EMPTY_STRING)) {
			//change A to B
		}
		else {
			//do nothing
		}
		return priority_A + " , " + priority_B;
	}


	private String determineTaskTypeForEditCommand(String[] segments) {
		String taskType;
		taskType = determineTaskType(formatToStandardCommandContent(segments[0].trim())) + " , " +
				determineTaskType(formatToStandardCommandContent(segments[1].trim()));
		return taskType;
	}

	private String formatToStandardCommandContent(String content) {
		content = content.replaceAll("\\s+", " ").trim();
		int time = getStartingIndexOfIdentifier(content);
		int priority = getStartingIndexOfPriority(content);
		int task = getStartingIndexOfTask(content, time, priority);

		//task only
		if (time == -1 && priority == -1) {
			//System.out.println(content + "me");
			return content;
		}
		//no time,has priority
		else if (time == -1) {
			//only priority
			if (task == -1) {
				return content;
			}

			else {
				if (task > priority) {
					return content.substring(task)+ " " +content.substring(0, task - 1);
				}
				else {
					return content;
				}
			}
		}
		//no priority,has time
		else if (priority == -1) {
			//only time
			if (task == -1) {
				return content;
			}

			else {
				if (task > time) {
					return content.substring(task)+ " " +content.substring(0, task - 1);
				}
				else {
					return content;
				}
			}	
		}

		//time,priority,task(maybe)
		else {
			if (task == -1) {
				if (time > priority) {
					return content.substring(time)+ " " +content.substring(0, time - 1);
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
								content.substring(time) + " " + 
								content.substring(priority,time - 1);
					}
				}
				//priority-task-time
				else if (task < time) {
					return content.substring(task) + " " +
							content.substring(priority,task - 1);
				}
				//time-task-priority
				else if (task < priority) {
					return content.substring(task, priority - 1) + " " 
							+ content.substring(time, task - 1) + " " +
							content.substring(priority);
				}
				//task is the last
				else {
					//time-priority-task
					if (time < priority) {
						return content.substring(task) + " " 
								+ content.substring(time, priority - 1) + " " +
								content.substring(priority, task - 1);
					}
					//priority-time-task
					else {
						return content.substring(task) + " " 
								+ content.substring(time, task - 1) + " " +
								content.substring(priority, time - 1);
					}

				}
			}


		}

	}



	public static void main(String[] args)
	{
		PrettyTimeParser pars = new PrettyTimeParser();

		//String[] a = getTimeSpecifics("Sun Dec 12 13:45:12 CET 2013");
		CommandParser par = new CommandParser();
		String str = "by by by monster on mon";
		//int time = par.getStartingIndexOfIdentifier(str);
		//int priority = par.getStartingIndexOfPriority(str);
		//int task = par.getStartingIndexOfTask(str, time, priority);
		//System.out.println(par.formatToStandardCommandContent(str));
		//System.out.print("task:" + task + "; " + "time:" + time + "; " +
		//	"priority:" + priority + ";");
		//Command command = new Command("edit more, #yellow by mon to do sth");
		//command = par.parseCommand(command);
		//Task task = command.createTask();
		//System.out.println(par.searchWord("I am in EUROPE", "EUROPE"));
		//System.out.println(par.formatToStandardCommandContent("take selfie with my kitten to post on mon on instagram"));

	}
	public static ArrayList<Task> parseEditTask(Task task) {
		Task task_A;
		Task task_B;
		String toDo_A, toDo_B;
		String time_A, time_B;
		String priority_A, priority_B;
		String type_A, type_B;

		toDo_A = task.getTask().split(EDIT_COMMAND_SEPARATOR)[0].trim();
		toDo_B = task.getTask().split(EDIT_COMMAND_SEPARATOR)[1].trim();
		time_A = task.getTime().split(EDIT_COMMAND_SEPARATOR)[0].trim();
		time_B = task.getTime().split(EDIT_COMMAND_SEPARATOR)[1].trim();

		priority_A = task.getPriority().split(EDIT_COMMAND_SEPARATOR)[0].trim();
		priority_B = task.getPriority().split(EDIT_COMMAND_SEPARATOR)[1].trim();

		type_A = task.getType().split(EDIT_COMMAND_SEPARATOR)[0].trim();
		type_B = task.getType().split(EDIT_COMMAND_SEPARATOR)[1].trim();

		task_A = new Task(toDo_A, time_A, priority_A, type_A);
		task_B = new Task(toDo_B, time_B, priority_B, type_B);

		//ArrayList<Task> result = temp.searchTemp(task_A);
		ArrayList<Task> result = new ArrayList<Task>();
		result.add(task_A);
		result.add(task_B);
		return result;
	}

	private boolean isOverdue(Date time) {
		return time.before(new Date());
	}

	private static String[] getTimeSpecifics(String unformattedTime) {

		String[] segments = unformattedTime.split(WHITE_SPACE);
		String time = segments[3];
		time = time.substring(time.indexOf(TIME_SEPARATOR), 
				time.indexOf(TIME_SEPARATOR, time.indexOf(TIME_SEPARATOR) + 1) - 1);
		String[] parameters = new String[4];
		parameters[DAY_OF_WEEK] = segments[0];
		parameters[MONTH] = segments[1];
		parameters[DAY_OF_MONTH] = segments[2];
		parameters[CLOCK_TIME] = time;

		return parameters ;
	}

	private String getRoughTime(String time) {
		String[] segments = time.split(TIME_SEPARATOR);
		time = segments[0] + segments[1] + segments[2].substring(2);
		return time;
	}

	private int getStartingIndexOfPriority(String content) {

		return content.indexOf("#");
	}


	private int getStartingIndexOfTask(String content, int timeIndex, int priorityIndex) {
		//no time, no tag -> must have and only task
		if (timeIndex == -1 && priorityIndex == -1) {
			return 0;
		}
		//no time, has tag -> tag-task/task-tag/tag
		else if (timeIndex == -1) {
			//tag
			if (!content.contains(WHITE_SPACE)) {
				return -1;
			}
			else {
				//tag-task
				if (content.substring(0,1).equals("#")) {
					return content.indexOf(WHITE_SPACE) + 1;
				}
				//task-tag
				else {
					return 0;
				}
			}
		}

		//no priority, has time -> time-task/task-time/time
		else if (priorityIndex == -1) {

			//<time>/<time-task>
			if (timeIndex == 0) {
				//fill in
				return locateTaskIndexInSegment(content);	
			}
			//task-time
			else {
				//System.out.println(content + "me");
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
					//System.out.println("here");
					return content.indexOf(WHITE_SPACE,content.indexOf("#")) + 1;
				}
				//3,8   <time-task>-priority/<time>-priority
				else {
					//fill in
					String segment = content.substring(0,content.indexOf("#")-1);
					//System.out.println(segment);
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
					//System.out.println(locateTaskIndexInSegment(segment));
					int val = locateTaskIndexInSegment(segment);
					if (val == -1) {
						return -1;
					}
					return baseIndex + val;

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
		int result = -1;
		//input: time/time-task
		int count = StringUtils.countMatches(content, WHITE_SPACE);
		//System.out.println(count);
		//
		if (count == 1) {
			return -1;
		}

		else {
			String oldTime = getRoughTime(parser.parse(content).toString());
			for (int i = 2; i <= count; i++) {
				int index = StringUtils.ordinalIndexOf(content, WHITE_SPACE, i);
				String newTime = content.substring(0, index);
				newTime = getRoughTime(parser.parse(newTime).toString());
				if (newTime.equals(oldTime)) {
					result = index + 1;
					break;
				}
			}
			return result;
		}

	}



	private int getStartingIndexOfIdentifier(String content) {
		String[] segments = content.split(WHITE_SPACE);
		int numSpace = segments.length - 1;

		//no time
		if (numSpace == 0) {
			return -1;
		}

		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		int pointer = 0;


		for (int i = 1; i <= numSpace; i++) {
			int index = StringUtils.ordinalIndexOf(content, WHITE_SPACE, i);
			//System.out.println("wp is at " + index);

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

		System.out.println("list size is " + list.size());

		//no time
		if (list.size() == 0) {
			return -1;
		}

		//the only one match is the real identifier
		if (list.size() == 1) {
			if (!parser.parse(content).toString().equals("[]")) {
				return list.get(0);
			}
			else {
				return -1;
			}
		}

		if (list.size() == 2) {
			if (!parser.parse(content.substring(list.get(0), list.get(1)))
					.toString().equals("[]")) {
				return list.get(0);
			}

			else if (!parser.parse(content.substring(list.get(1)))
					.toString().equals("[]")) {
				return list.get(1);
			}

			else {
				return -1;
			}
		}

		//list size is at least 3
		//check substrings to determine the starting index of the real identifier
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() - 1) {
				String substring = content.substring(list.get(i),list.get(i + 1));
				if (!parser.parse(substring).toString().equals("[]")) {
					return list.get(i);
				}
			}

			else {
				String substring = content.substring(list.get(i));
				if (!parser.parse(substring).toString().equals("[]")) {
					return list.get(i);
				}
			}
		}


		return -1;
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

}
