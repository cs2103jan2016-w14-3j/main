/* @@author A0127481E */
package main.java.parser;

import main.java.exception.InvalidInputFormatException;

/**
 * This class parses the delete command.
 * @author Ouyang Danwen
 *
 */
public class DeleteCommandParser {
	
	/* error messages used in this class */
	private static final String ERROR_MESSAGE_DELETE_NOTHING = ""
			+ "Please specify a task to delete!";
	
	/* numeric constans to access the parameters array*/
	private static final int TASK = 0;
	
	/**
	 * Empty constructor.
	 */
	public DeleteCommandParser() {}

	/**
	 * @param commandContent
	 * @return the command parameters as a string array
	 * @throws InvalidInputFormatException
	 */
	public String[] determineParameters(String commandContent) 
			throws InvalidInputFormatException {
		assert commandContent != null;
		
		
		String[] parameters = new String[5];
		
		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException(ERROR_MESSAGE_DELETE_NOTHING);
		}
		
		else {
			parameters[TASK] = commandContent;

		}
		
		return parameters;
	}

}
/* @@author A0127481E */