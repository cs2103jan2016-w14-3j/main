package main.java.backend;

import java.util.Date;
import java.util.List;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;


import main.java.data.*;

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

	public void parseCommand(Command command) {
		String originalCommand = command.getOriginal();
		command.setType(determineCommandType(originalCommand));

		String commandContent = retrieveCommandContent(originalCommand);
		command.setContent(commandContent);
		command.setParameters(determineParameters(command.getType(),commandContent));
	}

	private String determineCommandType(String originalCommand) {
		String keyword = getCommandKeyword(originalCommand);
		return keyword;
	}

	private String getCommandKeyword(String command) {
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
		return command.substring(0,command.indexOf(" ")).trim();
	}

	private boolean isCommand(String operation, String keyword) {
		return operation == keyword;
	}

	private String retrieveCommandContent(String originalCommand) {
		return originalCommand.substring(originalCommand.indexOf(" ") + 1).trim();
	}

	private String[] determineParameters(String commandType, String commandContent) {

		String[] parameters = new String[3];

		if (!commandType.equals(DISPLAY_COMMAND)) {
			parameters[TASK] = determineTask(commandContent);
			parameters[TIME] = determineTime(commandContent);
			parameters[PRIORITY] = determinePriority(commandContent);
		}

		return parameters;


	}

	private String determineTask(String content) {
		if (hasField(content, "-")) {
			return content.substring(0, content.indexOf("-") - 1).trim();
		}

		else if (hasField(content, "#")) {
			return content.substring(0, content.indexOf("#") - 1).trim();
		}

		else {
			return content;
		}
	}

	private String determineTime(String content) {
		List<Date> dates = new PrettyTimeParser().parse(content);
		return dates.toString();
	}

	private String determinePriority(String content) {
		if (hasField(content, "#")) {
			return content.substring(content.indexOf("#") + 1).trim();
		}
		else {
			return null;
		}
	}

	private boolean hasField(String content, String flag) {
		return content.contains(flag);
	}


	public static void main(String[] args)
	{
		List<Date> dates = new PrettyTimeParser().parse("tuesday 5pm do thursday");
		System.out.println(dates);
		// Prints: "[Sun Dec 12 13:45:12 CET 2013]"
	}


}
