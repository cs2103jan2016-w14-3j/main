package main.java.parser;

import java.util.ArrayList;

import main.java.data.Task;

public class EditCommandParser extends AddCommandParser {

	private static final String EDIT_COMMAND_SEPARATOR = ",";
	private static final String EDIT_TASK_SEPARATOR = " , ";

	public EditCommandParser() {
		super();
	}

	public String[] determineParameters(String commandContent)throws InvalidInputFormatException {
		assert commandContent != null;
		//assert 1==2;
		checkAndHandleInvalidCommand(commandContent);
		String[] parameters = new String[5];
		String[] segments = commandContent.split(EDIT_COMMAND_SEPARATOR);
		parameters[TASK] = determineTaskForEditCommand(segments);
		parameters[TIME] = determineTimeForEditCommand(segments);
		parameters[PRIORITY] = determinePriorityForEditCommand(segments);
		parameters[TASK_TYPE] = determineTaskTypeForEditCommand(segments);
		parameters[STATUS] = determineStatusForEditCommand(segments);
		return parameters;
	}

	private void checkAndHandleInvalidCommand(String commandContent) throws InvalidInputFormatException {
		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException("Cannot edit nothing!");
		}
		else if (commandContent.indexOf(EDIT_COMMAND_SEPARATOR) == -1) {
			throw new InvalidInputFormatException("Invalid format for edit command!");
		}
		else if (commandContent.indexOf(EDIT_COMMAND_SEPARATOR) == 0) {
			throw new InvalidInputFormatException("Please specify the task to edit!");
		}
		else if (commandContent.indexOf(EDIT_COMMAND_SEPARATOR) == commandContent.length() - 1) {
			throw new InvalidInputFormatException("Please specify the information to be updated!");
		}
	}

	private String determineTaskForEditCommand(String[] segments) {

		String task_A = determineTask(formatToStandardCommandContent(segments[0].trim())); 
		String task_B = determineTask(formatToStandardCommandContent(segments[1].trim()));

		if (task_B.equals(EMPTY_STRING)) {
			return task_A + EDIT_TASK_SEPARATOR + task_A;
		}

		else {
			return task_A + EDIT_TASK_SEPARATOR + task_B;
		}
	}

	private String determineTimeForEditCommand(String[] segments) {

		String time_A = determineTime(formatToStandardCommandContent(segments[0].trim())); 
		String time_B = determineTime(formatToStandardCommandContent(segments[1].trim()));

		if (!time_A.equals(EMPTY_STRING) && !time_B.equals(EMPTY_STRING)) {
			//change A to B

		}
		else if (!time_A.equals(EMPTY_STRING)) {
			//retain A
		}
		else if (!time_B.equals(EMPTY_STRING)) {
			//change A to B

		}
		else {
			//do nothing
		}
		return time_A + EDIT_TASK_SEPARATOR + time_B;

	}

	private String determinePriorityForEditCommand(String[] segments) throws InvalidInputFormatException {
		//assert content != null;
		String priority_A = determinePriority(formatToStandardCommandContent(segments[0].trim())); 
		String priority_B = determinePriority(formatToStandardCommandContent(segments[1].trim()));

		if (!priority_A.equals(EMPTY_STRING) && !priority_B.equals(EMPTY_STRING)) {
			//change A to B
		}
		else if (!priority_A.equals(EMPTY_STRING)) {
			//retain A
		}
		else if (!priority_B.equals(EMPTY_STRING)) {
			//change A to B
		}
		else {
			//do nothing
		}
		return priority_A + EDIT_TASK_SEPARATOR + priority_B;
	}


	private String determineTaskTypeForEditCommand(String[] segments) {
		String taskType;
		taskType = determineTaskType(formatToStandardCommandContent
				(segments[0].trim())) + EDIT_TASK_SEPARATOR +
				determineTaskType(formatToStandardCommandContent
						(segments[1].trim()));
		return taskType;
	}
	
	private String determineStatusForEditCommand(String[] segments) {
		String status;
		status = determineStatus(formatToStandardCommandContent(segments[0].trim())) +
				EDIT_TASK_SEPARATOR + determineStatus(
						formatToStandardCommandContent(segments[1].trim()));
		return status;
	}



	public static ArrayList<Task> parseEditTask(Task task) {
		Task task_A;
		Task task_B;
		String toDo_A, toDo_B;
		String time_A, time_B;
		String priority_A, priority_B;
		String type_A, type_B;
		String status_A, status_B;

		toDo_A = task.getTask().split(EDIT_COMMAND_SEPARATOR)[0].trim();
		toDo_B = task.getTask().split(EDIT_COMMAND_SEPARATOR)[1].trim();
		time_A = task.getTime().split(EDIT_COMMAND_SEPARATOR)[0].trim();
		time_B = task.getTime().split(EDIT_COMMAND_SEPARATOR)[1].trim();

		priority_A = task.getPriority().split(EDIT_COMMAND_SEPARATOR)[0].trim();
		priority_B = task.getPriority().split(EDIT_COMMAND_SEPARATOR)[1].trim();

		type_A = task.getType().split(EDIT_COMMAND_SEPARATOR)[0].trim();
		type_B = task.getType().split(EDIT_COMMAND_SEPARATOR)[1].trim();
		
		status_A = task.getStatus().split(EDIT_COMMAND_SEPARATOR)[0].trim();
		status_B = task.getStatus().split(EDIT_COMMAND_SEPARATOR)[1].trim();

		task_A = new Task(toDo_A, time_A, priority_A, type_A, status_A);
		task_B = new Task(toDo_B, time_B, priority_B, type_B, status_B);
		

		ArrayList<Task> result = new ArrayList<Task>();
		result.add(task_A);
		result.add(task_B);
		return result;
	}

}
