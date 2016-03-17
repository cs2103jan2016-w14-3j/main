package main.java.parser;
public abstract class Parser {
	
	protected static final String TIME_SEPARATOR = ":";
	protected static final String EMPTY_STRING = "";
	
	protected static final int TASK = 0;
	protected static final int TIME = 1;
	protected static final int PRIORITY = 2;
	protected static final int TASK_TYPE = 3;
	protected static final int STATUS = 4;
		
	public Parser() {
		
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


