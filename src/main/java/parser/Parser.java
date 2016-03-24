package main.java.parser;
import main.java.data.*;
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
	
	
	public String[] determineParameters(COMMAND_TYPE commandType, String commandContent) 
			throws InvalidInputFormatException {
		assert commandType != null;
		assert commandContent != null;
		//assert 1==2;
		//System.out.println(commandType);
		//System.out.println(commandContent + "haha");
		if (commandType != COMMAND_TYPE.CLEAR_COMPLETE && 
				commandType != COMMAND_TYPE.CLEAR_UPCOMING && 
				commandType != COMMAND_TYPE.UNDO &&
				commandType != COMMAND_TYPE.REDO) {
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


