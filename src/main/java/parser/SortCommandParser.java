/* @@author A0127481E */
package main.java.parser;

public class SortCommandParser {

	private static final String ERROR_MESSAGE_SORT_BY_NOTHING = ""
			+ "Please specify a parameter for sorting!";
	private static final String EORROR_MESSAGE_INVALID_SORTING_PARAMETER = ""
			+ "Sort by \"name\", \"time\" or \"priority\" only!";
	
	private static final String STRING_TIME = "time";
	private static final String STRING_PRIORITY = "priority";
	private static final String STRING_NAME = "name";
	
	private static final int TASK = 0;


	public SortCommandParser() {}

	public String[] determineParameters(String commandContent) 
			throws InvalidInputFormatException {
		assert commandContent != null;

		String[] parameters = new String[5];

		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException(ERROR_MESSAGE_SORT_BY_NOTHING);
		}

		else {
			String parameter = commandContent.toLowerCase();

			if (parameter.equals(STRING_TIME) || parameter.equals(STRING_NAME) ||
					parameter.equals(STRING_PRIORITY)) {
				parameters[TASK] = commandContent;
			}

			else {
				throw new InvalidInputFormatException(
						EORROR_MESSAGE_INVALID_SORTING_PARAMETER);
			}
		}

		return parameters;
	}

}
/* @@author A0127481E */