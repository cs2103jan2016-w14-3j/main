package main.java.parser;

public class SortCommandParser extends Parser {
	
	public SortCommandParser() {
		super();
	}
	
	public String[] determineParameters(String commandType, String commandContent) {
		assert commandType != null;
		//assert 1==2;
		String[] parameters = new String[5];
		if (!commandContent.isEmpty()) {
			parameters[TASK] = commandContent;
		}
		
		return parameters;
	}

}
