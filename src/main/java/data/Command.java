package main.java.data;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class Command {

	private String original;
	private CommandType commandType;
	private String commandContent;
	private String[] commandParameters;
	private static PrettyTimeParser parser = new PrettyTimeParser();
	
	private static final int TASK = 0;
	private static final int TIME = 1;
	private static final int PRIORITY = 2;
	private static final int TASK_TYPE = 3;
	private static final int STATUS = 4;
	
	private static final String WHITE_SPACE = " ";
	private static final String TIME_EMPTY = "[]";
	private static final String EDIT_COMMAND_FULL_SEPARATOR = ", ";


	public Command(String command) {
		this.original = command.trim();
	}

	public Command(String original, CommandType commandType, String commandContent,
			String[] commandParameters) {
		this.original = original;
		this.commandType = commandType;
		this.commandContent = commandContent;
		this.commandParameters = commandParameters;
	}

	public CommandType getType() {
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

	public void setType(CommandType commandType) {
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
	
	public  static PriorityLevel getPriority(String priority) {
		
		if (priority.equals(PriorityLevel.HIGH.getType())) {
			return PriorityLevel.HIGH;
		}
		
		else if (priority.equals(PriorityLevel.MEDIUM.getType())) {
			return PriorityLevel.MEDIUM;
		}
		
		else if (priority.equals(PriorityLevel.NOT_SPECIFIED.getType())) {
			return PriorityLevel.NOT_SPECIFIED;
		}
		
		else {
			return PriorityLevel.LOW;
		}
	}
	
	public static TaskStatus getStatus(String status) {
		
		if (status.equals(TaskStatus.OVERDUE.getType())) {
			return TaskStatus.OVERDUE;
		}
		
		else if (status.equals(TaskStatus.UPCOMING.getType())) {
			return TaskStatus.UPCOMING;
		}
		
		else if (status.equals(TaskStatus.FLOATING.getType())) {
			return TaskStatus.FLOATING;
		}
		
		else {
			return TaskStatus.COMPLETED;
		}
	}
	
	public static TaskType getType(String type) {
		if (type.equals(TaskType.DEADLINE.getType())) {
			return TaskType.DEADLINE;
		}
		
		else if (type.equals(TaskType.EVENT.getType())) {
			return TaskType.EVENT;
		}
		
		else {
			return TaskType.DURATION;
		}
	}
	
	
	public static List<Date> getTime(String time) {
		//time is not specified
		if (time.equals(TIME_EMPTY)) {
			return parser.parse(time);
		}
		
		//format time to facilitate manipulation
		time = time.substring(1, time.length() - 1);
		String[] segments = time.split(EDIT_COMMAND_FULL_SEPARATOR);
		
		//add formatted time into the result list
		List<Date> result = new ArrayList<Date>();
		for (int i = 0; i < segments.length; i++) {
			result.add(parser.parse(format(segments[i])).get(0));
		}
		
		return result;
	}


	private static String format(String time) {
		time = time.substring(0, 10) + WHITE_SPACE + 
				time.substring(24, 28) + time.substring(10, 23);
		
		return time;
	}

	public boolean isCommand(CommandType type) {
		return type ==(this.getType());
	}

}

