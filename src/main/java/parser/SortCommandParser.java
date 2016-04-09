/* @@author A0127481E */
package main.java.parser;

public class SortCommandParser {
		
	private static final int TASK = 0;


	public SortCommandParser() {}

	public String[] determineParameters(String commandContent) 
			throws InvalidInputFormatException {
		assert commandContent != null;
		
		String[] parameters = new String[5];
		
		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException("Cannot sort by nothing!");
		}
		
		else {
			String parameter = commandContent.toLowerCase();
			
			if (parameter.equals("time") || parameter.equals("name") ||
					parameter.equals("priority")) {
				parameters[TASK] = commandContent;
			}
			
			else {
				throw new InvalidInputFormatException(
						"Sort by \"name\", \"time\" or \"priority\" only!");
			}
		}
		
		return parameters;
	}

}
/* @@author A0127481E */