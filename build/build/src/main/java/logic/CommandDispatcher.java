/* @@author A0127481E */
package main.java.logic;

import java.nio.file.InvalidPathException;

import main.java.data.Command;
import main.java.enumeration.CommandType;
import main.java.exception.InvalidInputFormatException;
import main.java.exception.NoFileNameException;
import main.java.parser.AddCommandParser;
import main.java.parser.DeleteCommandParser;
import main.java.parser.EditCommandParser;
import main.java.parser.ShowCommandParser;
import main.java.parser.SortCommandParser;
import main.java.parser.StorageCommandParser;


/**
 * This class dispatches all commands to be parsed accordingly before execution.
 * @author Ouyang Danwen
 *
 */
public class CommandDispatcher {
	
	/* error messages used in this class */
	private static final String ERROR_MESSAGE_EMPTY_COMMAND = ""
			+ "Please enter a non-empty command!";
	private static final String ERROR_MESSAGE_INVALID_COMMAND = ""
			+ "Please enter a valid command!";

	/* expression used for string manipulation */
	private static final String EMPTY_STRING = "";
	private static final String WHITE_SPACE = " ";

	/**
	 * Empty constructor for the class.
	 */
	public CommandDispatcher() {}
	
	/**
	 * @param command
	 * @return the parsed command
	 * @throws InvalidInputFormatException
	 * @throws NoFileNameException 
	 */
	public Command parseCommand(Command command)throws InvalidInputFormatException, 
	NoFileNameException, InvalidPathException {
		assert command != null;
		String originalCommand = command.getOriginal();

		if (originalCommand.isEmpty()) {
			throw new InvalidInputFormatException(ERROR_MESSAGE_EMPTY_COMMAND);
		}

		command.setType(determineCommandType(originalCommand));
		String commandContent = retrieveCommandContent(command);
		command.setContent(commandContent);
		setParameters(command);
		return command;
	}

	/**
	 * Dispatch the commands for parsing and set their parameters respectively.
	 * @param command
	 * @throws InvalidInputFormatException
	 * @throws NoFileNameException 
	 */
	private void setParameters(Command command)throws InvalidInputFormatException, 
	NoFileNameException, InvalidPathException {
		assert command != null;

		if (command.isCommand(CommandType.ADD)) {
			AddCommandParser parser = new AddCommandParser();
			command.setParameters(parser.determineParameters
					(command.getContent()));
		}
		
		else if (command.isCommand(CommandType.EDIT)) {
			EditCommandParser parser = new EditCommandParser();
			command.setParameters(parser.determineParameters
					(command.getContent()));
		}
		
		else if (command.isCommand(CommandType.DELETE)) {
			DeleteCommandParser parser = new DeleteCommandParser();
			command.setParameters(parser.determineParameters
					(command.getContent()));
		}
		
		else if (command.isCommand(CommandType.DELETE_COMPLETE)) {
			DeleteCommandParser parser = new DeleteCommandParser();
			command.setParameters(parser.determineParameters
					(command.getContent()));
		}
		
		else if (command.isCommand(CommandType.MOVE)) {
			StorageCommandParser parser = new StorageCommandParser();
			command.setParameters(parser.determineParameters
					(command.getType(),command.getContent()));
		}
		
		else if (command.isCommand(CommandType.SAVE)) {
			StorageCommandParser parser = new StorageCommandParser();
			command.setParameters(parser.determineParameters
					(command.getType(),command.getContent()));
		}
		
		else if (command.isCommand(CommandType.OPEN)) {
			StorageCommandParser parser = new StorageCommandParser();
			command.setParameters(parser.determineParameters
					(command.getType(),command.getContent()));
		}
		
		else if (command.isCommand(CommandType.SORT)
				|| command.isCommand(CommandType.SORT_COMPLETE)) {
			SortCommandParser parser = new SortCommandParser();
			command.setParameters(parser.determineParameters
					(command.getContent()));
		}
		
		else if (command.isCommand(CommandType.SHOW) 
				|| command.isCommand(CommandType.SHOW_COMPLETE)) {
			ShowCommandParser parser = new ShowCommandParser();
			command.setParameters(parser.determineParameters
					(command.getContent()));
		}

		else if (command.isCommand(CommandType.INVALID)){
			throw new InvalidInputFormatException(ERROR_MESSAGE_INVALID_COMMAND);
		}

		//other commands which need not to be parsed
		else {
			return;
		}

	}

	/**
	 * @param originalCommand
	 * @return command type
	 */
	private CommandType determineCommandType(String originalCommand) {
		assert originalCommand != null;

		CommandType type = getCommandKeyword(originalCommand);
		return type;
	}

	/**
	 * @param command
	 * @return the command keyword
	 */
	private CommandType getCommandKeyword(String command) {
		assert command != null;

		String firstWord = getFirstKeyword(command);

		if (isCommand(CommandType.ADD, firstWord)) {
			return CommandType.ADD;
		}

		else if (isCommand(CommandType.DELETE, firstWord)) {
			return CommandType.DELETE;
		}

		else if (isCommand(CommandType.DELETE_COMPLETE, firstWord)) {
			return CommandType.DELETE_COMPLETE;
		}

		else if (isCommand(CommandType.SEARCH, firstWord)) {
			return CommandType.SEARCH;
		}

		else if (isCommand(CommandType.MOVE, firstWord)) {
			return CommandType.MOVE;
		}
		else if (isCommand(CommandType.SAVE, firstWord)) {
			return CommandType.SAVE;
		}

		else if (isCommand(CommandType.SORT, firstWord)) {
			return CommandType.SORT;
		}

		else if (isCommand(CommandType.SORT_COMPLETE, firstWord)) {
			return CommandType.SORT_COMPLETE;
		}

		else if (isCommand(CommandType.CLEAR_UPCOMING, firstWord)) {
			return CommandType.CLEAR_UPCOMING;
		}

		else if (isCommand(CommandType.CLEAR_ALL, firstWord)) {
			return CommandType.CLEAR_ALL;
		}

		else if (isCommand(CommandType.CLEAR_FLOATING, firstWord)) {
			return CommandType.CLEAR_FLOATING;
		}

		else if (isCommand(CommandType.CLEAR_OVERDUE, firstWord)) {
			return CommandType.CLEAR_OVERDUE;
		}

		else if (isCommand(CommandType.CLEAR_COMPLETE, firstWord)) {
			return CommandType.CLEAR_COMPLETE;
		}

		else if (isCommand(CommandType.EDIT, firstWord)) {
			return CommandType.EDIT;
		}

		else if (isCommand(CommandType.UNDO, firstWord)) {
			return CommandType.UNDO;
		}

		else if (isCommand(CommandType.REDO, firstWord)) {
			return CommandType.REDO;
		}

		else if (isCommand(CommandType.MARK, firstWord)) {
			return CommandType.MARK;
		}

		else if (isCommand(CommandType.UNMARK, firstWord)) {
			return CommandType.UNMARK;
		}

		else if (isCommand(CommandType.SWITCH, firstWord)) {
			return CommandType.SWITCH;
		}

		else if (isCommand(CommandType.SHOW, firstWord)) {
			return CommandType.SHOW;
		}

		else if (isCommand(CommandType.SHOW_COMPLETE, firstWord)) {
			return CommandType.SHOW_COMPLETE;
		}
		
		else if (isCommand(CommandType.OPEN, firstWord)) {
			return CommandType.OPEN;
		}

		else {
			return CommandType.INVALID;
		}

	}

	/**
	 * @param command
	 * @return the first word of each command
	 */
	private String getFirstKeyword(String command) {
		assert command != null;

		//only one word in command -> it is the keyword required
		if (!command.contains(WHITE_SPACE)) {
			return command;
		}

		return command.substring(0,command.indexOf(WHITE_SPACE)).trim();
	}

	/**
	 * Determine whether the command has a certain type.
	 * Return true if it is of the type or false otherwise.
	 * @param type
	 * @param keyword
	 * @return true or false
	 */
	private boolean isCommand(CommandType type, String keyword) {
		assert keyword != null;

		return type.getType().equalsIgnoreCase(keyword);
	}

	/**
	 * @param command
	 * @return the command content excluding the command keyword
	 */
	private String retrieveCommandContent(Command command) {
		assert command != null;

		String original = command.getOriginal();

		//command content is empty
		if (!original.contains(WHITE_SPACE)) {
			return EMPTY_STRING;
		}

		//command content is not empty
		else {
			String content = original.substring(original.indexOf(WHITE_SPACE) + 1);
			return content.trim();
		}
	}
}
/* @@author A0127481E */