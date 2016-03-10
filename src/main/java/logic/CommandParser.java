package main.java.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.PrettyTime;




import main.java.data.*;
import main.java.storage.TempStorage;

public class CommandParser {

	private final String EMPTY_COMMAND = "empty";
	private final String ADD_COMMAND = "add";
	private final String DELETE_COMMAND = "delete";
	private final String SEARCH_COMMAND = "search";
	private final String STORE_COMMAND = "store";
	private final String DISPLAY_COMMAND = "display";
	private final String SORT_COMMAND = "sort";
	private final String CLEAR_COMMAND = "clear";
	private final String EDIT_COMMAND = "edit";
	private final String EXIT_COMMAND = "exit";

	private static final int TASK = 0;
	private static final int TIME = 1;
	private static final int PRIORITY = 2;


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

			if (isCommand(STORE_COMMAND, firstWord)) {
				return STORE_COMMAND;
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

			else if (isCommand(EXIT_COMMAND, firstWord)) {
				return EXIT_COMMAND;
			}

			else {
				return ADD_COMMAND;
			}
		}
	}

	private String getFirstKeyword(String command) {
		assert command != null;
		if (!command.contains(" ")) {
			return command;
		}
		return command.substring(0,command.indexOf(" ")).trim();
	}

	private boolean isCommand(String operation, String keyword) {
		assert operation != null;
		assert keyword != null;
		return operation.equals(keyword);
	}

	private String retrieveCommandContent(String originalCommand) {
		assert originalCommand != null;
		return originalCommand.substring(originalCommand.indexOf(" ") + 1).trim();
	}

	private String[] determineParameters(String commandType, String commandContent) {
		assert commandType != null;

		String[] parameters = new String[3];

		if (!commandType.equals(DISPLAY_COMMAND)) {
			if (commandType.equals(EDIT_COMMAND) && (commandContent.contains(","))) {
				//System.out.println("test");
				parameters[TASK] = determineTaskForEditCommand(commandContent);
				parameters[TIME] = determineTimeForEditCommand(commandContent);
				parameters[PRIORITY] = determinePriorityForEditCommand(commandContent);

			}
			else {
				parameters[TASK] = determineTask(commandContent);
				parameters[TIME] = determineTime(commandContent);
				parameters[PRIORITY] = determinePriority(commandContent);
			}
		}

		return parameters;


	}

	private String determineTask(String content) {
		if (content.contains("-")) {
			return content.substring(0, content.indexOf("-") - 1).trim();
		}

		else if (content.contains("#")) {
			return content.substring(0, content.indexOf("#") - 1).trim();
		}

		else {
			return content.trim();
		}
	}

	private String determineTime(String content) {
		if (!content.contains("-")) {
			return "";
		}
		PrettyTimeParser timeParser = new PrettyTimeParser();
		List<Date> dates = timeParser.parse(content);
		
			for (int i = 0; i < dates.size(); i++) {
				if (isOverdue(dates.get(i))) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(dates.get(i));
					calendar.add(Calendar.DATE, 1);  // number of days to add
					dates.set(i,calendar.getTime()); // dt is now the new date
				}
			}
		
		return dates.toString();
	}

	private String determinePriority(String content) {
		if (content.contains("#")) {
			return content.substring(content.indexOf("#") + 1).trim();
		}
		else {
			return "";
		}
	}

	private String determineTaskForEditCommand(String content) {
		assert content != null;
		String task;

		String[] segments = content.split(",");
		task = determineTask(segments[0].trim()) + " , " + 
				determineTask(segments[1].trim());

		return task;

	}

	private String determineTimeForEditCommand(String content) {
		assert content != null;
		String time;
		String[] segments = content.split(",");
		time = determineTime(segments[0].trim()) + " , " + 
				determineTime(segments[1].trim());
		return time;

	}

	private String determinePriorityForEditCommand(String content) {
		assert content != null;
		String priority;
		String[] segments = content.split(",");
		priority = determinePriority(segments[0].trim()) + " , " + 
				determinePriority(segments[1].trim());

		return priority;
	}



	public static void main(String[] args)
	{
		PrettyTimeParser parser = new PrettyTimeParser();

		//PrettyTime time = new PrettyTime();
		List<Date> dates = parser.parse("12:01am");
		DateFormat format = new SimpleDateFormat();
		//System.out.print(dates);
		//System.out.print(format.format(dates.toString().substring(1,dates.toString().length())));
		//System.out.println(dates.get(0).before(new Date()));
		//System.out.println(time.format(new Date() + 10));
		// Prints: "[Sun Dec 12 13:45:12 CET 2013]"
	}

	public static ArrayList<Task> parseEditTask(TempStorage temp, Task task) {
		Task task_A;
		Task task_B;
		String toDo_A, toDo_B;
		String time_A, time_B;
		String priority_A, priority_B;

		toDo_A = task.getTask().split(",")[0].trim();
		toDo_B = task.getTask().split(",")[1].trim();
		time_A = task.getTime().split(",")[0].trim();
		time_B = task.getTime().split(",")[1].trim();

		priority_A = task.getPriority().split(",")[0].trim();
		priority_B = task.getPriority().split(",")[1].trim();


		task_A = new Task(toDo_A, time_A, priority_A);
		task_B = new Task(toDo_B, time_B, priority_B);

		ArrayList<Task> result = temp.searchTemp(task_A);
		result.add(task_B);
		return result;
	}

	private boolean isOverdue(Date time) {
		//PrettyTime time = new PrettyTime();
		//List<Date> dates = new PrettyTimeParser().parse(time_B);

		return time.before(new Date());
	}


}