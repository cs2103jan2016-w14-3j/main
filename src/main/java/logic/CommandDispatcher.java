package main.java.logic;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import main.java.data.*;
import main.java.parser.*;

public class CommandDispatcher {

	private static final String EMPTY_STRING = "";
	private static final String WHITE_SPACE = " ";

	public CommandDispatcher() {

	}
	public Command parseCommand(Command command)throws InvalidInputFormatException {
		assert command != null;
		String originalCommand = command.getOriginal();
		if (originalCommand.isEmpty()) {
			throw new InvalidInputFormatException("Empty command is not allowed!");
		}
		command.setType(determineCommandType(originalCommand));

		String commandContent = retrieveCommandContent(command);
		command.setContent(commandContent);

		setParameters(command);
		//System.out.println(command.getType());
		return command;
	}

	private void setParameters(Command command)throws InvalidInputFormatException {

		if (command.isCommand(COMMAND_TYPE.ADD)) {
			AddCommandParser parser = new AddCommandParser();
			command.setParameters(parser.determineParameters
					(command.getContent()));
		}
		else if (command.isCommand(COMMAND_TYPE.EDIT)) {
			EditCommandParser parser = new EditCommandParser();
			command.setParameters(parser.determineParameters
					(command.getContent()));
		}
		else if (command.isCommand(COMMAND_TYPE.CLEAR_UPCOMING)) {

		}
		else if (command.isCommand(COMMAND_TYPE.CLEAR_COMPLETE)) {

		}
		else if (command.isCommand(COMMAND_TYPE.MOVE)) {
			MoveCommandParser parser = new MoveCommandParser();
			command.setParameters(parser.determineParameters
					(command.getType(),command.getContent()));
		}
		else if (command.isCommand(COMMAND_TYPE.SEARCH)) {
			SearchCommandParser parser = new SearchCommandParser();
			command.setParameters(parser.determineParameters
					(command.getType(),command.getContent()));
		}
		else if (command.isCommand(COMMAND_TYPE.UNDO)) {

		}
		else if (command.isCommand(COMMAND_TYPE.SORT)) {
			SortCommandParser parser = new SortCommandParser();
			command.setParameters(parser.determineParameters
					(command.getType(),command.getContent()));
		}
		else if (command.isCommand(COMMAND_TYPE.MARK)) {

		}
		else if (command.isCommand(COMMAND_TYPE.UNMARK)) {
			
		}

	}

	private COMMAND_TYPE determineCommandType(String originalCommand) {
		assert originalCommand != null;
		COMMAND_TYPE type = getCommandKeyword(originalCommand);
		return type;
	}

	private COMMAND_TYPE getCommandKeyword(String command) {
		assert command != null;
		String firstWord = getFirstKeyword(command);

		if (isCommand(COMMAND_TYPE.ADD, firstWord)) {
			return COMMAND_TYPE.ADD;
		}

		else if (isCommand(COMMAND_TYPE.DELETE, firstWord)) {
			return COMMAND_TYPE.DELETE;
		}

		else if (isCommand(COMMAND_TYPE.SEARCH, firstWord)) {
			return COMMAND_TYPE.SEARCH;
		}

		else if (isCommand(COMMAND_TYPE.MOVE, firstWord)) {
			return COMMAND_TYPE.MOVE;
		}

		else if (isCommand(COMMAND_TYPE.SORT, firstWord)) {
			return COMMAND_TYPE.SORT;
		}

		else if (isCommand(COMMAND_TYPE.CLEAR_UPCOMING, firstWord)) {
			return COMMAND_TYPE.CLEAR_UPCOMING;

		}
		else if (isCommand(COMMAND_TYPE.CLEAR_COMPLETE, firstWord)) {
			return COMMAND_TYPE.CLEAR_COMPLETE;
		}

		else if (isCommand(COMMAND_TYPE.EDIT, firstWord)) {
			return COMMAND_TYPE.EDIT;

		}

		else if (isCommand(COMMAND_TYPE.UNDO, firstWord)) {
			return COMMAND_TYPE.UNDO;
		}
		else if (isCommand(COMMAND_TYPE.MARK, firstWord)) {
			return COMMAND_TYPE.MARK;
		}
		else if (isCommand(COMMAND_TYPE.UNMARK, firstWord)) {
			return COMMAND_TYPE.UNMARK;
		}
		else {
			return COMMAND_TYPE.ADD;
		}
	}

	private String getFirstKeyword(String command) {
		assert command != null;
		if (!command.contains(WHITE_SPACE)) {
			return command;
		}
		return command.substring(0,command.indexOf(WHITE_SPACE)).trim();
	}

	private boolean isCommand(COMMAND_TYPE type, String keyword) {
		assert keyword != null;
		return type.getType().equalsIgnoreCase(keyword);
	}

	private String retrieveCommandContent(Command command) {
		assert command != null;
		String original = command.getOriginal();
		COMMAND_TYPE commandType = command.getType();
		if (commandType == null) {
			throw new IllegalArgumentException();
		}

		if (commandType == COMMAND_TYPE.ADD) {
			if (!getFirstKeyword(original).
					equalsIgnoreCase(COMMAND_TYPE.ADD.getType())){
				return original;
			}
		}
		if (!original.contains(WHITE_SPACE)) {
			return EMPTY_STRING;
		}
		String content = original.substring(original.indexOf(WHITE_SPACE) + 1);
		return content.trim();
	}



	public static void main(String[] args)
	{
		PrettyTimeParser pars = new PrettyTimeParser();
		System.out.println(pars.parse("from 8 to 9").get(1));

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
