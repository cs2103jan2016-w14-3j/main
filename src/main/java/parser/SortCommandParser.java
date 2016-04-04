package main.java.parser;

import main.java.data.COMMAND_TYPE;

public class SortCommandParser extends Parser {

	public SortCommandParser() {
		super();
	}

	public String[] determineParameters(String commandContent) throws InvalidInputFormatException {
		//assert commandType != null;
		//assert 1==2;
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
				throw new InvalidInputFormatException("Sort by \"name\", \"time\" or \"priority\" only!");
			}
		}
		return parameters;
	}

}
