/* @@author A0127481E */
package main.java.parser;

import java.util.Date;
import java.util.List;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import main.java.data.PriorityLevel;

public class ShowCommandParser {
	
	private static final int TIME = 1;
	private static final int PRIORITY = 2;


	public ShowCommandParser() {}

	public String[] determineParameters(String commandContent) throws InvalidInputFormatException {
		assert commandContent != null;
		
		String[] parameters = new String[5];
		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException("Cannot show nothing!");
		}
		
		else {
			setShowFiterIfApplicable(parameters, commandContent);
		}
		
		return parameters;
	}

	private void setShowFiterIfApplicable(String[] parameters, String commandContent) throws InvalidInputFormatException {
		
		commandContent = commandContent.toLowerCase();
		commandContent = commandContent.replaceAll("tmr", "tomorrow");
		PrettyTimeParser timeParser = new PrettyTimeParser();
		List<Date> dates = timeParser.parse(commandContent);
		
		if (dates.size() == 1) {
			parameters[TIME] = dates.toString();
		}
		else if (commandContent.equals("h") || commandContent.equals("high")) {
			parameters[PRIORITY] = PriorityLevel.HIGH.getType();
			
		}
		else if (commandContent.equals("med") || commandContent.equals("m") 
				|| commandContent.equals("medium")) {
			parameters[PRIORITY] = PriorityLevel.MEDIUM.getType();
		}
		
		else if (commandContent.equals("low") || commandContent.equals("l")) {
			parameters[PRIORITY] = PriorityLevel.LOW.getType();
		}
		
		else {
			throw new InvalidInputFormatException("Please choose a valid filter!");
		}
		
	}

}
/* @@author A0127481E */