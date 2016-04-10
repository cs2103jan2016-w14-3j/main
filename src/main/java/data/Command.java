package main.java.data;

/* import statements */
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class models the command object and handles all related operations.
 * @author Ouyang Danwen
 *
 */
public class Command {

	/* attributes of the class */
	private String original;
	private CommandType commandType;
	private String commandContent;
	private String[] commandParameters;
	private static PrettyTimeParser parser = new PrettyTimeParser();
	
	/* numeric indices used to access the parameters array */
	private static final int TASK = 0;
	private static final int TIME = 1;
	private static final int PRIORITY = 2;
	private static final int TASK_TYPE = 3;
	private static final int STATUS = 4;
	
	/* expressions for string manipulation */
	private static final String WHITE_SPACE = " ";
	private static final String TIME_EMPTY = "[]";
	private static final String EDIT_COMMAND_FULL_SEPARATOR = ", ";


	/**
	 * Instantiate raw command.
	 * @param command
	 */
	public Command(String command) {
		assert command != null;
		this.original = command.trim();
	}

	/**
	 * Instantiate parsed command.
	 * @param original
	 * @param commandType
	 * @param commandContent
	 * @param commandParameters
	 */
	public Command(String original, CommandType commandType, String commandContent,
			String[] commandParameters) {
		assert original != null;
		assert commandType != null;
		assert commandContent != null;
		assert commandParameters != null;
		
		this.original = original;
		this.commandType = commandType;
		this.commandContent = commandContent;
		this.commandParameters = commandParameters;
	}

	/**
	 * @return commandType
	 */
	public CommandType getType() {
		return this.commandType;
	}

	/**
	 * @return commandConetnt
	 */
	public String getContent() {
		return this.commandContent;
	}

	/**
	 * @return original command in full
	 */
	public String getOriginal() {
		return this.original;
	}

	/**
	 * @param commandContent
	 */
	public void setContent(String commandContent) {
		assert commandContent != null;
		this.commandContent = commandContent;
	}

	/**
	 * @param commandType
	 */
	public void setType(CommandType commandType) {
		assert commandType != null;
		this.commandType = commandType;
	}

	/**
	 * @param parameters
	 */
	public void setParameters(String[] parameters) {
		assert parameters != null;
		this.commandParameters = parameters;
	}

	/**
	 * @return parameters of the command in an string array
	 */
	public String[] getParameters() {
		return this.commandParameters;
	}


	/**
	 * Create a task object based on the parsed command.
	 * @return a task object
	 */
	public Task createTask() {

		Task task = new Task(commandParameters[TASK], 
				getTime(commandParameters[TIME]), getPriority(commandParameters[PRIORITY]), 
				getType(commandParameters[TASK_TYPE]), getStatus(commandParameters[STATUS]));

		return task;

	}
	
	/**
	 * Create a transient task object to parse edit command.
	 * @return a transient task object
	 */
	public TransientTask createTransientTask() {
		TransientTask transientTask = new TransientTask(commandParameters[TASK], 
				commandParameters[TIME], commandParameters[PRIORITY], 
				commandParameters[TASK_TYPE], commandParameters[STATUS]);
		return transientTask;
	}
	
	/**
	 * @param priority
	 * @return priority level
	 */
	public static PriorityLevel getPriority(String priority) {
		assert priority != null;
		
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
	
	/**
	 * @param status
	 * @return status in the type TaskStatus
	 */
	public static TaskStatus getStatus(String status) {
		assert status != null;
		
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
	
	/**
	 * @param type
	 * @return task type in the type of TaskType
	 */
	public static TaskType getType(String type) {
		assert type != null;
		
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
	
	
	/**
	 * @param time
	 * @return parsed time
	 */
	public static List<Date> getTime(String time) {
		assert time != null;
		
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


	/**
	 * @param time
	 * @return formatted time
	 */
	private static String format(String time) {
		assert time != null;
		
		String formattedTime = time.substring(0, 10) + WHITE_SPACE + 
				time.substring(24, 28) + time.substring(10, 23);
		return formattedTime;
	}

	
	/**
	 * Check whether the command is of a particular type.
	 * Return true if it is of the type or return false otherwise.
	 * @param type
	 * @return true or false
	 */
	public boolean isCommand(CommandType type) {
		return type ==(this.getType());
	}

}

