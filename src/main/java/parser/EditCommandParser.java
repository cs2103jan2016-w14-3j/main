/* @@author A0127481E */
package main.java.parser;

import java.util.ArrayList;


import main.java.data.Task;
import main.java.data.TransientTask;
import main.java.data.Command;

public class EditCommandParser extends AddCommandParser {
	
	private static final String TIME_SEPARATOR = ":";
	private static final String EMPTY_STRING = "";
	
	private static final int TASK = 0;
	private static final int TIME = 1;
	private static final int PRIORITY = 2;
	private static final int TASK_TYPE = 3;
	private static final int STATUS = 4;

	private static final String EDIT_COMMAND_SEPARATOR = ",";
	private static final String EDIT_TASK_SEPARATOR = " , ";
	private static final String ALTERNATIVE_EDIT_TASK_SEPARATOR = "%";

	public EditCommandParser() {}

	public String[] determineParameters(String commandContent) 
			throws InvalidInputFormatException {
		assert commandContent != null;
		
		//check validity of the edit command
		checkAndHandleInvalidCommand(commandContent);
		
		//split the two parts of an edit command
		String[] segments = commandContent.split(EDIT_COMMAND_SEPARATOR);
		
		//determine and set the parameters of the edit command
		String[] parameters = new String[5];
		parameters[TASK] = determineTaskForEditCommand(segments);
		parameters[TIME] = determineTimeForEditCommand(segments);
		parameters[PRIORITY] = determinePriorityForEditCommand(segments);
		parameters[TASK_TYPE] = determineTaskTypeForEditCommand(segments);
		parameters[STATUS] = determineStatusForEditCommand(segments);
		
		return parameters;
	}

	private void checkAndHandleInvalidCommand(String commandContent) 
			throws InvalidInputFormatException {
		
		//empty command is not allowed
		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException("Cannot edit nothing!");
		}
		
		//invalid command if the separator comma is not present
		else if (commandContent.indexOf(EDIT_COMMAND_SEPARATOR) == -1) {
			throw new InvalidInputFormatException("Invalid format for edit command!");
		}
		
		//invalid command if no task is specified to edit
		else if (commandContent.indexOf(EDIT_COMMAND_SEPARATOR) == 0) {
			throw new InvalidInputFormatException("Please specify the task to edit!");
		}
		
		//invalid command if no update information is provided
		else if (commandContent.indexOf(EDIT_COMMAND_SEPARATOR) 
				== commandContent.length() - 1) {
			throw new InvalidInputFormatException(
					"Please specify the information to be updated!");
		}
	}

	private String determineTaskForEditCommand(String[] segments) 
			throws InvalidInputFormatException {
		assert segments != null;

		String task_A = determineTask(formatToStandardCommandContent(segments[0].trim())); 
		String task_B = determineTask(formatToStandardCommandContent(segments[1].trim()));

		//no new task name to be updated -> retain the original task name
		if (task_B.equals(EMPTY_STRING)) {
			return task_A + EDIT_TASK_SEPARATOR + task_A;
		}

		//new task name is provided -> update accordingly
		else {
			return task_A + EDIT_TASK_SEPARATOR + task_B;
		}
	}

	private String determineTimeForEditCommand(String[] segments) 
			throws InvalidInputFormatException {
		assert segments != null;
		
		String time_A = determineTime(formatToStandardCommandContent(segments[0].trim())); 
		String time_B = determineTime(formatToStandardCommandContent(segments[1].trim()));
		
		return time_A + EDIT_TASK_SEPARATOR + time_B;

	}

	private String determinePriorityForEditCommand(String[] segments) 
			throws InvalidInputFormatException {
		assert segments != null;
		
		String priority_A = determinePriority(
				formatToStandardCommandContent(segments[0].trim())); 
		String priority_B = determinePriority(
				formatToStandardCommandContent(segments[1].trim()));

		return priority_A + EDIT_TASK_SEPARATOR + priority_B;
	}


	private String determineTaskTypeForEditCommand(String[] segments) {
		assert segments != null;
		
		String taskType;
		taskType = determineTaskType(formatToStandardCommandContent
				(segments[0].trim())) + EDIT_TASK_SEPARATOR +
				determineTaskType(formatToStandardCommandContent
						(segments[1].trim()));
		
		return taskType;
	}

	private String determineStatusForEditCommand(String[] segments) {
		assert segments != null;
		
		String status;
		status = determineStatus(formatToStandardCommandContent(segments[0].trim())) 
				+ EDIT_TASK_SEPARATOR + determineStatus(
						formatToStandardCommandContent(segments[1].trim()));
		
		return status;
	}



	public static ArrayList<Task> parseEditTask(TransientTask transientTask) {
		assert transientTask != null;
		
		//separate the information of the two tasks
		String taskName_A = transientTask.getTask().split(EDIT_COMMAND_SEPARATOR)[0].trim();
		String taskName_B = transientTask.getTask().split(EDIT_COMMAND_SEPARATOR)[1].trim();
		
		//determine the time segments for the two parsed tasks
		String time = transientTask.getTime().replaceAll(
				EDIT_TASK_SEPARATOR, ALTERNATIVE_EDIT_TASK_SEPARATOR);
		String time_A = time.split(ALTERNATIVE_EDIT_TASK_SEPARATOR)[0].trim();
		String time_B = time.split(ALTERNATIVE_EDIT_TASK_SEPARATOR)[1].trim();

		//determine the priority segments for the two parsed tasks
		String priority_A = transientTask.getPriority().split(
				EDIT_COMMAND_SEPARATOR)[0].trim();
		String priority_B = transientTask.getPriority().split(
				EDIT_COMMAND_SEPARATOR)[1].trim();
		
		//determine the type segments for the two parsed tasks
		String type_A = transientTask.getType().split(EDIT_COMMAND_SEPARATOR)[0].trim();
		String type_B = transientTask.getType().split(EDIT_COMMAND_SEPARATOR)[1].trim();

		//determine the status segments for the two parsed tasks
		String status_A = transientTask.getStatus().split(EDIT_COMMAND_SEPARATOR)[0].trim();
		String status_B = transientTask.getStatus().split(EDIT_COMMAND_SEPARATOR)[1].trim();

		//instantiate the two tasks with the parsed parameters
		Task task_A = new Task(taskName_A, Command.getTime(time_A), 
				Command.getPriority(priority_A), Command.getType(type_A), Command
				.getStatus(status_A));
	
		Task task_B = new Task(taskName_B, Command.getTime(time_B), 
				Command.getPriority(priority_B), Command.getType(type_B), Command
				.getStatus(status_B));

		//store the two parsed tasks in the resultant list to be returned
		ArrayList<Task> result = new ArrayList<Task>();
		result.add(task_A);
		result.add(task_B);
		
		return result;
	}

}
/* @@author A0127481E */