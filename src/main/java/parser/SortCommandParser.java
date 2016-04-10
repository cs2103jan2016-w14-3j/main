/* @@author A0127481E */
package main.java.parser;

/**
 * This class parses the sort command.
 * @author Ouyang Danwen
 *
 */
public class SortCommandParser {

	/* error messages used in this class */
	private static final String ERROR_MESSAGE_SORT_BY_NOTHING = ""
			+ "Please specify a parameter for sorting!";
	private static final String EORROR_MESSAGE_INVALID_SORTING_PARAMETER = ""
			+ "Sort by \"name\", \"time\" or \"priority\" only!";
	
	/* string constants used in this class */
	private static final String STRING_TIME = "time";
	private static final String STRING_PRIORITY = "priority";
	private static final String STRING_NAME = "name";
	
	/* numeric indices to access the parameters array */
	private static final int TASK = 0;


	/**
	 * Empty constructor.
	 */
	public SortCommandParser() {}

	/**
	 * @param commandContent
	 * @return the command parameters as a string array
	 * @throws InvalidInputFormatException
	 */
	public String[] determineParameters(String commandContent) 
			throws InvalidInputFormatException {
		assert commandContent != null;

		String[] parameters = new String[5];

		//command content is empty
		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException(ERROR_MESSAGE_SORT_BY_NOTHING);
		}
		
		//command content is not empty
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