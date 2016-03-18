package main.java.parser;
public abstract class Parser {
	
	protected static final String TIME_SEPARATOR = ":";
	protected static final String EMPTY_STRING = "";
	
	protected static final int TASK = 0;
	protected static final int TIME = 1;
	protected static final int PRIORITY = 2;
	protected static final int TASK_TYPE = 3;
	protected static final int STATUS = 4;
	
	private static final String CLEAR_COMMAND = "clear";
	private static final String UNDO_COMMAND = "undo";
		
	public Parser() {
		
	}
	
	
	public String[] determineParameters(String commandType, String commandContent) 
			throws InvalidInputFormatException {
		assert commandType != null;
		assert commandContent != null;
		//assert 1==2;
		if (!commandType.equals(CLEAR_COMMAND) && !commandType.equals(UNDO_COMMAND)) {
			if (commandContent.isEmpty()) {
				throw new InvalidInputFormatException("This command requires parameter!");
			}
		}
		String[] parameters = new String[5];
		if (!commandContent.isEmpty()) {
			parameters[TASK] = commandContent;
		}

		return parameters;
	}

}


