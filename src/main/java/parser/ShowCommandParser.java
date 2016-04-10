/* @@author A0127481E */
package main.java.parser;

import java.util.Date;
import java.util.List;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import main.java.data.PriorityLevel;

public class ShowCommandParser {
	
	private static final String ERORR_MESSAGE_SHOW_NOTHING = ""
			+ "Please specify a filter for show command!";
	private static final String ERROR_MESSAGE_INVALID_FILTER = "Please choose a valid filter!";
	
	private static final String PRIORITY_HIGH_ALIAS = "h";
	private static final String PRIORITY_MEDIUM_ALIAS_1 = "med";
	private static final String PRIORITY_MEDIUM_ALIAS_2 = "m";
	private static final String PRIORITY_LOW_ALIAS = "l";
	private static final String TOMORROW_IN_FULL = "tomorrow";
	private static final String TOMORROW_IN_SHORT = "tmr";
	
	private static final int TIME = 1;
	private static final int PRIORITY = 2;
	
	public ShowCommandParser() {}

	public String[] determineParameters(String commandContent) throws InvalidInputFormatException {
		assert commandContent != null;
		
		String[] parameters = new String[5];
		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException(ERORR_MESSAGE_SHOW_NOTHING);
		}
		
		else {
			setShowFiterIfApplicable(parameters, commandContent);
		}
		
		return parameters;
	}

	private void setShowFiterIfApplicable(String[] parameters, String commandContent) 
			throws InvalidInputFormatException {
		
		commandContent = commandContent.toLowerCase();
		commandContent = commandContent.replaceAll(TOMORROW_IN_SHORT, TOMORROW_IN_FULL);
		PrettyTimeParser timeParser = new PrettyTimeParser();
		List<Date> dates = timeParser.parse(commandContent);
		
		if (dates.size() == 1) {
			parameters[TIME] = dates.toString();
		}
		else if (commandContent.equals(PRIORITY_HIGH_ALIAS) 
				|| commandContent.equals(PriorityLevel.HIGH.getType())) {
			parameters[PRIORITY] = PriorityLevel.HIGH.getType();
			
		}
		else if (commandContent.equals(PRIORITY_MEDIUM_ALIAS_1) 
				|| commandContent.equals(PRIORITY_MEDIUM_ALIAS_2) 
				|| commandContent.equals(PriorityLevel.MEDIUM.getType())) {
			parameters[PRIORITY] = PriorityLevel.MEDIUM.getType();
		}
		
		else if (commandContent.equals(PriorityLevel.LOW.getType()) 
				|| commandContent.equals(PRIORITY_LOW_ALIAS)) {
			parameters[PRIORITY] = PriorityLevel.LOW.getType();
		}
		
		else {
			throw new InvalidInputFormatException(ERROR_MESSAGE_INVALID_FILTER);
		}
		
	}

}
/* @@author A0127481E */