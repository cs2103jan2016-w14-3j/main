package main.java.data;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import java.util.Date;
import java.util.List;
public class Command {

	private String original;
	private COMMAND_TYPE commandType;
	private String commandContent;
	private String[] commandParameters;
	private static PrettyTimeParser parser = new PrettyTimeParser();
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
				getTime(commandParameters[TIME]), getPriority(commandParameters[PRIORITY]), 
				getType(commandParameters[TASK_TYPE]), getStatus(commandParameters[STATUS]));

		return task;

	}
	
	public TransientTask createTransientTask() {
		TransientTask transientTask = new TransientTask(commandParameters[TASK], 
				commandParameters[TIME], commandParameters[PRIORITY], 
				commandParameters[TASK_TYPE], commandParameters[STATUS]);
		return transientTask;
	}
	
	public  static PRIORITY_LEVEL getPriority(String priority) {
		
		if (priority.equalsIgnoreCase(PRIORITY_LEVEL.HIGH.getType())) {
			return PRIORITY_LEVEL.HIGH;
		}
		else if (priority.equalsIgnoreCase(PRIORITY_LEVEL.MEDIUM.getType())) {
			return PRIORITY_LEVEL.MEDIUM;
		}
		else {
			return PRIORITY_LEVEL.LOW;
		}
	}
	
	public static TASK_STATUS getStatus(String status) {
		if (status.equalsIgnoreCase(TASK_STATUS.OVERDUE.getType())) {
			return TASK_STATUS.OVERDUE;
		}
		else if (status.equalsIgnoreCase(TASK_STATUS.UPCOMING.getType())) {
			return TASK_STATUS.UPCOMING;
		}
		else {
			return TASK_STATUS.COMPLETED;
		}
	}
	
	public static TASK_NATURE getType(String type) {
		if (type.equalsIgnoreCase(TASK_NATURE.DEADLINE.getType())) {
			return TASK_NATURE.DEADLINE;
		}
		else if (type.equalsIgnoreCase(TASK_NATURE.EVENT.getType())) {
			return TASK_NATURE.EVENT;
		}
		else if (type.equalsIgnoreCase(TASK_NATURE.RECURRING.getType())){
			return TASK_NATURE.RECURRING;
		}
		else {
			return TASK_NATURE.DURATION;
		}
	}
	
	
	public static List<Date> getTime(String time) {
		List<Date> dates = parser.parse(time);
		return dates;
	}


	public boolean isCommand(COMMAND_TYPE type) {
		return type ==(this.getType());
	}

}

