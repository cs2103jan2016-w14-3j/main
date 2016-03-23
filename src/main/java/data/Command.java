package main.java.data;

public class Command {

	private String original;
	private COMMAND_TYPE commandType;
	private String commandContent;
	private String[] commandParameters;
	private static final int TASK = 0;
	private static final int TIME = 1;
	private static final int PRIORITY = 2;
	private static final int TASK_TYPE = 3;
	private static final int STATUS = 4;


	public Command(String command) {
		this.original = command.trim();
		//this.commandParameters = new String[4];
	}

	public Command(String original, COMMAND_TYPE commandType, String commandContent,
			String[] commandParameters) {
		this.original = original;
		this.commandType = commandType;
		this.commandContent = commandContent;
		this.commandParameters = commandParameters;
	}

	public COMMAND_TYPE getType() {
		return this.commandType;
	}

	public String getContent() {
		return this.commandContent;
	}

	public String getOriginal() {
		return this.original;
	}

	public void setContent(String commandContent) {
		this.commandContent = commandContent;
	}

	public void setType(COMMAND_TYPE commandType) {
		this.commandType = commandType;
	}

	public void setParameters(String[] parameters) {
		this.commandParameters = parameters;
	}

	public String[] getParameters() {
		return this.commandParameters;
	}


	public Task createTask() {

		Task task = new Task(commandParameters[TASK], 
				commandParameters[TIME], commandParameters[PRIORITY], 
				commandParameters[TASK_TYPE], commandParameters[STATUS]);

		return task;

	}
	
	/*private  PRIORITY_LEVEL determinePriority(String priority) {
		
		if (priority.equalsIgnoreCase("high")) {
			return PRIORITY_LEVEL.HIGH;
		}
		else if (priority.equalsIgnoreCase("medium")) {
			return PRIORITY_LEVEL.MEDIUM;
		}
		else {
			return PRIORITY_LEVEL.LOW;
		}
	}*/


	public boolean isCommand(COMMAND_TYPE type) {
		return type ==(this.getType());
	}

}

