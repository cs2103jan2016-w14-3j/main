package main.java.parser;

import java.util.Date;

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
	
	
	protected boolean isOverdue(Date time) {
		return time.before(new Date());
	}
	
	protected String getRoughTime(String time) {
		String[] segments = time.split(TIME_SEPARATOR);
		time = segments[0] + segments[1] + segments[2].substring(2);
		return time;
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


