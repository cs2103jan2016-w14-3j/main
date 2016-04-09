/* @@author A0127481E */
package main.java.parser;

import main.java.data.CommandType;

public class StorageCommandParser {
	
	private static final int TASK = 0;
	
	public StorageCommandParser() {}

	public String[] determineParameters(CommandType commandType, String commandContent) 
			throws InvalidInputFormatException {
		assert commandType != null;
		assert commandContent != null;

		String[] parameters = new String[5];
		
		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException("Please specify a valid file path!");
		}

		else {
			parameters[TASK] = commandContent;
		}

		return parameters;
	}

}
/* @@author A0127481E */