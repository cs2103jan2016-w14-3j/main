package main.java.parser;

import main.java.data.COMMAND_TYPE;

public class DeleteCommandParser extends Parser {

	public DeleteCommandParser() {
		super();
	}

	public String[] determineParameters(String commandContent) throws InvalidInputFormatException {
		//assert commandType != null;
		//assert 1==2;
		String[] parameters = new String[5];
		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException("Cannot delete nothing!");
		}
		else {
			parameters[TASK] = commandContent;

		}
		return parameters;
	}

}
