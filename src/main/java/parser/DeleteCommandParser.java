/* @@author A0127481E */
package main.java.parser;

public class DeleteCommandParser {
	
	private static final String ERROR_MESSAGE_DELETE_NOTHING = "Cannot delete nothing!";
	
	private static final int TASK = 0;
	
	public DeleteCommandParser() {}

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