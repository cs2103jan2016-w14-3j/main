package main.java.logic;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import main.java.data.*;
import main.java.parser.*;

public class CommandDispatcher {

	private static final String ADD_COMMAND = "add";
	private static final String DELETE_COMMAND = "delete";
	private static final String SEARCH_COMMAND = "search";
	private static final String CHANGE_DIRECTORY_COMMAND = "move";
	private static final String SORT_COMMAND = "sort";
	private static final String CLEAR_COMMAND = "clear";
	private static final String EDIT_COMMAND = "edit";
	private static final String UNDO_COMMAND = "undo";
	private static final String EMPTY_STRING = "";
	private static final String WHITE_SPACE = " ";

	public CommandDispatcher() {

	}
	public Command parseCommand(Command command) {
		assert command != null;
		String originalCommand = command.getOriginal();
		command.setType(determineCommandType(originalCommand));

		String commandContent = retrieveCommandContent(originalCommand);
		command.setContent(commandContent);

		setParameters(command);
		return command;
	}

	private void setParameters(Command command) {

		if (command.isCommand(ADD_COMMAND)) {
			AddCommandParser parser = new AddCommandParser();
			command.setParameters(parser.determineParameters
					(command.getType(),command.getContent()));
		}
		else if (command.isCommand(EDIT_COMMAND)) {
			EditCommandParser parser = new EditCommandParser();
			command.setParameters(parser.determineParameters
					(command.getType(),command.getContent()));
		}
		else if (command.isCommand(CLEAR_COMMAND)) {

		}
		else if (command.isCommand(CHANGE_DIRECTORY_COMMAND)) {
			MoveCommandParser parser = new MoveCommandParser();
			command.setParameters(parser.determineParameters
					(command.getType(),command.getContent()));
		}
		else if (command.isCommand(SEARCH_COMMAND)) {
			SearchCommandParser parser = new SearchCommandParser();
			command.setParameters(parser.determineParameters
					(command.getType(),command.getContent()));
		}
		else if (command.isCommand(UNDO_COMMAND)) {

		}
		else if (command.isCommand(SORT_COMMAND)) {
			SortCommandParser parser = new SortCommandParser();
			command.setParameters(parser.determineParameters
					(command.getType(),command.getContent()));
		}

	}

	private String determineCommandType(String originalCommand) {
		assert originalCommand != null;
		String keyword = getCommandKeyword(originalCommand);
		return keyword;
	}

	private String getCommandKeyword(String command) {
		assert command != null;
		String firstWord = getFirstKeyword(command);

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



	public static void main(String[] args)
	{
		PrettyTimeParser pars = new PrettyTimeParser();

		//String[] a = getTimeSpecifics("Sun Dec 12 13:45:12 CET 2013");
		//CommandParser par = new CommandParser();
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
}
