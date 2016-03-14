package main.java.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import main.java.data.*;
import main.java.storage.TempStorage;
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

	private static final String WHITE_SPACE = " ";
	private static final String DEADLINE_FLAG = "by";
	private static final String EVENT_FLAG = "on";
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
		assert operation != null;
		assert keyword != null;
		return operation.equalsIgnoreCase(keyword);
	}

	private String retrieveCommandContent(String originalCommand) {
		assert originalCommand != null;
		return originalCommand.substring(originalCommand.indexOf(WHITE_SPACE) + 1).trim();
	}

	private String[] determineParameters(String commandType, String commandContent) {
		assert commandType != null;

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
			parameters[TASK] = commandContent.trim().toLowerCase();	
		}

		else if (commandType.equalsIgnoreCase(DELETE_COMMAND)) {

		}

		return parameters;


	}

	private String formatToStandardCommandContent(String content) {

		String timeIdentifier = EMPTY_STRING;
		if (searchWord(content, DEADLINE_FLAG) != -1) {
			timeIdentifier = DEADLINE_FLAG;
		}

		else if (searchWord(content, EVENT_FLAG) != -1) {
			timeIdentifier = EVENT_FLAG;

		}

		//task only
		if (timeIdentifier.equals(EMPTY_STRING) && 
				content.indexOf(PRIORITY_FLAG) == -1) {
			if (content.isEmpty()) {
				return null; //if nothing, return null
			}
			return content.trim();
		}


		//task and tag only
		else if (timeIdentifier.equals(EMPTY_STRING)) {
			//task-tag
			if (content.indexOf(PRIORITY_FLAG) != 0) {
				return content.trim();
			}
			//tag-task
			else {
				//if no task
				if (!content.trim().contains(WHITE_SPACE)) {
					return content.trim();
				}

				//have task
				content = content.substring(content.indexOf(WHITE_SPACE) + 1).trim() 
						+ WHITE_SPACE + content.substring(0, 
								(content.indexOf(WHITE_SPACE))).trim();
				return content.trim();
			}
		}

		//task and time only
		else if (!content.contains(PRIORITY_FLAG)) {
			//task-time
			if (!content.substring(0,2).equalsIgnoreCase(timeIdentifier)) {
				return content.trim();
			}
			//time-task
			else {
				//if have task
				if (containsTask(content, timeIdentifier)) {
					content = content.substring((getTaskStartingIndex
							(content, timeIdentifier))).trim() 
							+ WHITE_SPACE + content.substring(0, 
									(getTaskStartingIndex(content, timeIdentifier))).trim();
				}
				//no task only time
				return content.trim();
			}	
		}

		//task, time and tag
		else {
			//task-time-tag or task-tag-time
			if (searchWord(content, timeIdentifier) != 0 && 
					!content.substring(0,1).equals(PRIORITY_FLAG)) {

				//task-time-tag
				if (searchWord(content, timeIdentifier) < content.indexOf(PRIORITY_FLAG)) {
					return content.trim();
				}
				//task-tag-time
				else {
					String task = content.substring(0, content.indexOf(PRIORITY_FLAG));
					String tag = content.substring(content.indexOf(PRIORITY_FLAG), searchWord(content, timeIdentifier) - 1);
					String time = content.substring(searchWord(content, timeIdentifier));

					//if no task
					if (task.isEmpty()) {
						return time.trim() + WHITE_SPACE + tag.trim();
					}

					return task.trim() + WHITE_SPACE + time.trim() + WHITE_SPACE + tag.trim();
				}
			}
			//time-task-tag or time-tag-task
			else if (!content.substring(0,1).equalsIgnoreCase(PRIORITY_FLAG)) {

				//time-tag-task
				if (content.substring(content.indexOf(PRIORITY_FLAG)).trim().contains(WHITE_SPACE)) {
					String time = content.substring(0, content.indexOf(PRIORITY_FLAG) - 1);
					String rest = content.substring(content.indexOf(PRIORITY_FLAG)).trim();
					//no task
					if (!rest.contains(WHITE_SPACE)) {
						return time.trim() + WHITE_SPACE + rest.trim();
					}
					//have task
					String tag = rest.substring(0, rest.indexOf(WHITE_SPACE));
					String task = rest.substring(rest.indexOf(WHITE_SPACE) + 1);
					return task.trim() + WHITE_SPACE + time.trim() + WHITE_SPACE + tag.trim();
				}
				//time-task-tag
				else {
					//no task
					if (!containsTask(content.substring(0, 
							content.indexOf(PRIORITY_FLAG) - 1), timeIdentifier)) {

						String time = content.substring(0, content.indexOf(PRIORITY_FLAG) - 1);
						String tag = content.substring(content.indexOf(PRIORITY_FLAG));

						return time.trim() + WHITE_SPACE + tag.trim();
					}

					//have task
					int taskStartingIndex = getTaskStartingIndex(content.substring(0,
							content.indexOf(PRIORITY_FLAG) - 1).trim(), timeIdentifier);
					String time = content.substring(0, taskStartingIndex - 1);
					String task = content.substring(taskStartingIndex, 
							content.indexOf(PRIORITY_FLAG) - 1);
					String tag = content.substring(content.indexOf(PRIORITY_FLAG));
					return task.trim() + WHITE_SPACE + time.trim() + WHITE_SPACE + tag.trim();
				}

			}

			//tag-task-time or tag-time-task
			else {

				//tag-time-task
				if (searchWord(content, timeIdentifier) == content.indexOf(WHITE_SPACE) + 1) {

					String tag = content.substring(0, content.indexOf(WHITE_SPACE));
					String rest = content.substring(searchWord(content, 
							timeIdentifier)).trim();

					//no task
					if (!containsTask(rest, timeIdentifier)) {
						return rest.trim() + WHITE_SPACE + tag.trim();
					}
					//have task
					int taskStartingIndex = getTaskStartingIndex(rest, timeIdentifier);
					String time = rest.substring(0, taskStartingIndex - 1);
					String task = rest.substring(taskStartingIndex);
					return task.trim() + WHITE_SPACE + time.trim() + WHITE_SPACE + tag.trim();
				}

				//tag-task-time
				else {

					String tag = content.substring(0, content.indexOf(WHITE_SPACE));
					//no task
					if (searchWord(content, timeIdentifier) == content.indexOf(WHITE_SPACE) + 1) {
						return content.substring(content.indexOf(WHITE_SPACE) + 1).trim() +
								content.substring(0, content.indexOf(WHITE_SPACE));
					}
					//have task
					String task = content.substring(content.indexOf(WHITE_SPACE) + 1, 
							searchWord(content, timeIdentifier) - 1);
					String time = content.substring(searchWord(content, timeIdentifier));
					return task.trim() + WHITE_SPACE + time.trim() + WHITE_SPACE + tag.trim();
				}

			}
		}
	}

	private String determineTask(String content) {
		String timeIdentifier = EMPTY_STRING;
		if (searchWord(content, DEADLINE_FLAG) != -1) {
			timeIdentifier = DEADLINE_FLAG;
		}

		else if (searchWord(content, EVENT_FLAG) != -1) {
			timeIdentifier = EVENT_FLAG;
		}


		if (!timeIdentifier.equals(EMPTY_STRING)) {
			if (content.substring(0,2).equalsIgnoreCase(timeIdentifier)) {
				return EMPTY_STRING;
			}
			return content.substring(0, searchWord(content, timeIdentifier) - 1).trim();
		}

		else if (content.contains(PRIORITY_FLAG)) {
			if (content.substring(0,1).equals(PRIORITY_FLAG)) {
				return EMPTY_STRING;
			}
			return content.substring(0, content.indexOf(PRIORITY_FLAG) - 1).trim();
		}

		else {
			if (content.trim().isEmpty()) {
				return EMPTY_STRING;
			}
			return content.trim();
		}
	}

	private String determineTime(String content) {
		String timeIdentifier = EMPTY_STRING;

		if (searchWord(content, DEADLINE_FLAG) != -1) {
			timeIdentifier = DEADLINE_FLAG;
		}

		else if (searchWord(content, EVENT_FLAG) != -1) {
			timeIdentifier = EVENT_FLAG;
		}


		//no time
		if (timeIdentifier.equals(EMPTY_STRING)) {
			return EMPTY_STRING;
		}

		//has tag
		if (content.contains(PRIORITY_FLAG)) {
			content = content.substring(searchWord(content, timeIdentifier), 
					content.indexOf(PRIORITY_FLAG));
		}
		//no tag
		else {
			content = content.substring(searchWord(content, timeIdentifier));
		}

		List<Date> dates = parser.parse(content);

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
		if (searchWord(content, DEADLINE_FLAG) != -1) {
			return DEADLINE_TASK;
		}
		else {
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



	public static void main(String[] args)
	{
		//PrettyTimeParser parser = new PrettyTimeParser();

		//String[] a = getTimeSpecifics("Sun Dec 12 13:45:12 CET 2013");
		CommandParser par = new CommandParser();
		String good = null;
		System.out.println(good + " , " + good);
		//Command command = new Command("edit more, #yellow by mon to do sth");
		//command = par.parseCommand(command);
		//Task task = command.createTask();
		//System.out.println(par.searchWord("I am in EUROPE", "EUROPE"));
		System.out.println(par.formatToStandardCommandContent("to do sth okay or not"));

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

	private int getTaskStartingIndex(String content, String timeIdentifier) {

		//content = time-task
		int count = StringUtils.countMatches(content, WHITE_SPACE);
		String timeCurr = EMPTY_STRING;
		for (int i = 2; i <= count; i++) {
			String check = content.substring(content.indexOf(timeIdentifier) + 3, 
					StringUtils.ordinalIndexOf(content, WHITE_SPACE, i));
			String timeNext = parser.parse(check).toString();

			if (!timeNext.equals("[]")) {
				timeNext = getRoughTime(timeNext);
			}

			else {
				timeNext = "" + i;
			}

			if (timeNext.equals(timeCurr)) {
				return StringUtils.ordinalIndexOf(content, WHITE_SPACE, i - 1) + 1;
			}
			timeCurr = timeNext;
		}
		return content.lastIndexOf(WHITE_SPACE) + 1;
	}

	private boolean containsTask(String content, String timeIdentifier) {

		int index = content.lastIndexOf(WHITE_SPACE);

		if (index == 3) {
			return false;
		}
		//System.out.println(content);


		String curr;
		String check = parser.parse(content).toString();
		check = getRoughTime(check);
		//System.out.println(check);

		int num = StringUtils.countMatches(content, WHITE_SPACE);
		for (int i = 1; i <= num; i++) {
			curr = content.substring(0, StringUtils.ordinalIndexOf(content, WHITE_SPACE, 
					i));

			String time = parser.parse(curr).toString();

			if (!time.equals("[]")) {
				time = getRoughTime(time);
				if (check.equals(time)) {
					System.out.println("Task found");
					return true;
				}
			}

		}
		//System.out.println(check);

		return false;
	}

	private int searchWord(String content, String word) {
		String[] segments = content.split(WHITE_SPACE);
		int index = 0;
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].matches(".*\\b" + word + "\\b.*")) {
				return index;
			}

			index += segments[i].length() + 1;
		}

		return -1;
	}

	private String getRoughTime(String time) {
		String[] segments = time.split(TIME_SEPARATOR);
		time = segments[0] + segments[1] + segments[2].substring(2);
		return time;
	}


}
