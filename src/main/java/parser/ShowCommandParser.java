package main.java.parser;

import java.util.Date;
import java.util.List;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import main.java.data.PRIORITY_LEVEL;

public class ShowCommandParser extends Parser {

	public ShowCommandParser() {
		super();
	}

	public String[] determineParameters(String commandContent) throws InvalidInputFormatException {
		//assert commandType != null;
		//assert 1==2;
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
		//System.out.println("haha");
		commandContent = commandContent.toLowerCase();
		commandContent = commandContent.replaceAll("tmr", "tomorrow");
		PrettyTimeParser timeParser = new PrettyTimeParser();
		List<Date> dates = timeParser.parse(commandContent);
		//System.out.println("size is: " + dates.size());
		
		if (dates.size() == 1) {
			parameters[TIME] = dates.toString();
		}
		else if (commandContent.equals("h") || commandContent.equals("high")) {
			parameters[PRIORITY] = PRIORITY_LEVEL.HIGH.getType();
			
		}
		else if (commandContent.equals("med") || commandContent.equals("m") 
				|| commandContent.equals("medium")) {
			parameters[PRIORITY] = PRIORITY_LEVEL.MEDIUM.getType();
		}
		else if (commandContent.equals("low") || commandContent.equals("l")) {
			parameters[PRIORITY] = PRIORITY_LEVEL.LOW.getType();
		}
		else {
			throw new InvalidInputFormatException("Please choose a valid filter!");
		}
		
	}

}
