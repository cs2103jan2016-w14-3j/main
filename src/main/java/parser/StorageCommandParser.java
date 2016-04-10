/* @@author A0127481E */
package main.java.parser;

import main.java.data.CommandType;

/**
 * This parser parses storage related commands including "move" and "save".
 * @author Ouyang Danwen
 *
 */
public class StorageCommandParser {
	
	/* error messages used in this class */
	private static final String ERROR_MESSAGE_INVALID_PATH = "Please specify a valid file path!";
	
	/* numeric indices to access parameters array */
	private static final int TASK = 0;
	
	/**
	 * Empty constructor.
	 */
	public StorageCommandParser() {}

	/**
	 * @param commandType
	 * @param commandContent
	 * @return the command parameters as a string array
	 * @throws InvalidInputFormatException
	 */
	public String[] determineParameters(CommandType commandType, String commandContent) 
			throws InvalidInputFormatException {
		assert commandType != null;
		assert commandContent != null;

		String[] parameters = new String[5];
		
		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException(ERROR_MESSAGE_INVALID_PATH);
		}

		else {
			parameters[TASK] = commandContent;
		}

		return parameters;
	}

}
/* @@author A0127481E */