/* @@author A0127481E */
package main.java.logic;

/* import statements */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import Enumeration.CommandType;
import Enumeration.PriorityLevel;
import Exception.InvalidInputFormatException;
import Exception.NoFileNameException;
import main.java.data.Command;
import main.java.data.Task;
import main.java.data.TransientTask;
import main.java.parser.EditCommandParser;
import main.java.storage.StorageController;


/**
 * This class models the core component, Logic, of the application.
 * It handles the high level logic flow: receive user command -> parse user command
 * -> create user task -> save task to storage.
 * @author Ouyang Danwen
 *
 */
public class Logic {

	/* string constants used in this class */
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_SEARCH = "search";
	private static final String COMMAND_SAVE = "save";
	private static final String COMMAND_SORT = "sort";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_UNDO = "undo";
	private static final String COMMAND_HELP = "help";
	private static final String COMMAND_MARK = "mark";
	private static final String COMMAND_UNMARK = "unmark";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_SWITCH = "switch";
	private static final String COMMAND_REDO = "redo";
	private static final String COMMAND_THEME = "theme";
	private static final String COMMAND_SHOW = "show";
	private static final String COMMAND_OPEN = "open";
	private static final String EDIT_COMMAND_SEPARATOR = ",";
	private static final String TIME_EMPTY = "[]";
	private static final String STRING_TIME = "time";
	private static final String STRING_PRIORITY = "priority";
	private static final String STRING_NAME = "name";
	private static final String KEYWORD_UNMARK = "unmark ";
	private static final String KEYWORD_MARK = "mark ";
	private static final String KEYWORD_DELETE = "delete ";
	private static final String KEYWORD_COMPLETE_DELETE = "deleteComplete ";

	/* numeric indices to access the parameters array */
	private static final int TASK = 0;
	private static final int TIME = 1;
	private static final int PRIORITY = 2;

	/* attributes of the class */
	private static Task task;
	private static TransientTask transientTask;
	private static StorageController storageController;
	private ArrayList<Task> searchResult;
	private ArrayList<Task> searchResultCompleted;
	private static PrettyTimeParser timeParser = new PrettyTimeParser();

	/**
	 * @throws IOException
	 */
	public Logic() throws IOException {
		storageController = new StorageController();
	}

	/**
	 * @param userInput
	 * @param taskOptions
	 * @return the resultant list after executing the command
	 * @throws InvalidInputFormatException 
	 * @throws NoFileNameException 
	 * @throws IOException 
	 */
	public ArrayList<Task> handleUserCommand(String userInput,ArrayList<Task> taskOptions) 
			throws InvalidInputFormatException, IOException, NoFileNameException {
		assert userInput != null;

		CommandDispatcher dispatcher = new CommandDispatcher();
		Command command = new Command(userInput);
		command = parseCommand(dispatcher, command);

		ArrayList<Task> result = executeCommand(command, taskOptions, userInput);

		return result;

	}

	/**
	 * @param dispatcher
	 * @param command
	 * @return the parsed command 
	 * @throws InvalidInputFormatException
	 * @throws NoFileNameException 
	 */
	private Command parseCommand(CommandDispatcher dispatcher, Command command)
			throws InvalidInputFormatException, NoFileNameException {
		assert command != null;
		return dispatcher.parseCommand(command);
	}

	/**
	 * @param command
	 * @param taskOptions
	 * @param userInput
	 * @return resultant task list after execution of the command
	 * @throws IOException 
	 * @throws NoFileNameException
	 */
	private ArrayList<Task> executeCommand(Command command, ArrayList<Task> taskOptions,
			String userInput) throws IOException, NoFileNameException  {
		assert command != null;
		assert userInput != null;

		ArrayList<Task> result = new ArrayList<Task>();

		if (command.isCommand(CommandType.ADD)) {
			result = handleAddCommand(command);
		}

		else if (command.isCommand(CommandType.CLEAR_ALL)){
			result = handleClearAllCommand();
		}

		else if (command.isCommand(CommandType.CLEAR_FLOATING)){
			result = handleClearFloatingTaskCommand();
		}

		else if (command.isCommand(CommandType.CLEAR_UPCOMING)){
			result = handleClearUpcomingTaskCommand();
		}

		else if (command.isCommand(CommandType.CLEAR_OVERDUE)){
			result = handleClearOverdueTaskCommand();
		}

		else if (command.isCommand(CommandType.CLEAR_COMPLETE)){
			result = handleClearCompleteTaskCommand();
		}

		else if (command.isCommand(CommandType.DELETE)) {
			handleDeleteTaskCommand(userInput);
		}

		else if (command.isCommand(CommandType.DELETE_COMPLETE)) {
			handleDeleteCompleteTaskCommand(userInput);
		}

		else if (command.isCommand(CommandType.EDIT)) {
			handleEditCommand(result, userInput, command);
		}

		else if (command.isCommand(CommandType.MOVE)) {
			handleMoveCommand(command);
		}

		else if (command.isCommand(CommandType.SAVE)) {
			handleSaveCommand(command);
		}
		
		else if (command.isCommand(CommandType.OPEN)) {
			handleOpenCommand(command);
		}
		
		else if (command.isCommand(CommandType.MARK)) {
			handleMarkCommand(userInput);
		}

		else if (command.isCommand(CommandType.UNMARK)) {
			handleUnmarkCommand(userInput);
		}

		else if (command.isCommand(CommandType.SHOW)) {
			result = handleShowCommand(command);
		}

		else if (command.isCommand(CommandType.SHOW_COMPLETE)) {
			result = handleShowCompleteCommand(command);
		}

		else if (command.isCommand(CommandType.SORT)) {
			handleSortCommand(command);
		}

		else if (command.isCommand(CommandType.SORT_COMPLETE)) {
			handleSortCompleteCommand(command);
		}


		else if (command.isCommand(CommandType.UNDO)) {
			handleUndoCommand();
		}

		else if (command.isCommand(CommandType.REDO)) {
			handleRedoCommand();
		}

		return result;
	}

	/**
	 * @param command
	 * @throws IOException
	 */
	private void handleSortCompleteCommand(Command command) throws IOException {
		assert command != null;

		String parameter = command.getParameters()[TASK].toLowerCase();

		if (parameter.equals(STRING_TIME)) {
			storageController.sortCompletedByTime();
		}

		else if (parameter.equals(STRING_NAME)) {
			storageController.sortCompletedByTaskName();
		}

		else if (parameter.equals(STRING_PRIORITY)) {
			storageController.sortCompletedByPriority();
		} 	
	}


	/**
	 * @param command
	 * @throws IOException
	 */
	private void handleSortCommand(Command command) throws IOException {
		assert command != null;

		String parameter = command.getParameters()[TASK].toLowerCase();

		if (parameter.equals(STRING_TIME)) {
			storageController.sortPendingByTime();
		}

		else if (parameter.equals(STRING_NAME)) {
			storageController.sortPendingByTaskName();
		}

		else if (parameter.equals(STRING_PRIORITY)) {
			storageController.sortPendingByPriority();
		}	
	}


	/**
	 * @throws IOException
	 */
	private void handleRedoCommand() throws IOException {
		storageController.redo();
	}


	/**
	 * @throws IOException
	 */
	private void handleUndoCommand() throws IOException {
		storageController.undo();	
	}


	/**
	 * @param command
	 * @return the resultant task list to show
	 */
	private ArrayList<Task> handleShowCompleteCommand(Command command) {
		assert command != null;

		boolean isTime = false;
		ArrayList<Task> result;
		Date timeFilter = null;
		PriorityLevel priorityFilter = null;

		//filter by time
		if (command.getParameters()[TIME] != null) {
			isTime = true;
			timeFilter = timeParser.parse(command
					.getParameters()[TIME]).get(0);

		}

		//filter by priority
		else {
			priorityFilter = determinePriorityFilter(command);
		}

		//show by time filter
		if (isTime) {
			result = storageController.showAllCompletedByDate(timeFilter);
		}

		//show by  priority filter
		else {
			result = storageController.showAllCompletedByPriority(priorityFilter);
		}	

		return result;
	}


	/**
	 * @param command
	 * @return the resultant task list to show
	 */
	private ArrayList<Task> handleShowCommand(Command command) {
		assert command != null;

		ArrayList<Task> result;
		Date timeFilter = null;
		PriorityLevel priorityFilter = null;
		boolean isTime = false;

		//filter by time
		if (command.getParameters()[TIME] != null) {
			isTime = true;
			timeFilter = timeParser.parse(command
					.getParameters()[TIME]).get(0);
		}

		//filter by priority
		else {
			priorityFilter = determinePriorityFilter(command);
		}

		//show by time filter
		if (isTime) {
			result = storageController.showAllPendingByDate(timeFilter);
		}

		//show by priority filter
		else {
			result = storageController.showAllPendingByPriority(priorityFilter);
		}

		return result;

	}


	/**
	 * @param command
	 * @return the priority filter for the sort command
	 */
	private PriorityLevel determinePriorityFilter(Command command) {
		assert command != null;

		String priority = command.getParameters()[PRIORITY];
		PriorityLevel priorityFilter;

		if (priority.equals(PriorityLevel.HIGH.getType())) {
			priorityFilter = PriorityLevel.HIGH;
		}

		else if (priority.equals(PriorityLevel.MEDIUM.getType())) {
			priorityFilter = PriorityLevel.MEDIUM;
		}

		else {
			priorityFilter = PriorityLevel.LOW;
		}

		return priorityFilter;
	}


	/**
	 * @param userInput
	 * @throws IOException 
	 */
	private void handleUnmarkCommand(String userInput) throws IOException {
		assert userInput != null;

		for (Task temp : searchResultCompleted) {

			//only unmark when the command is valid and there is only one match
			if (userInput.equalsIgnoreCase(KEYWORD_UNMARK + temp.getTask()) 
					|| searchResultCompleted.size()==1) {
				unmark(temp);			
				break;
			}			
		}	
	}


	/**
	 * @param userInput
	 * @throws IOException 
	 */
	private void handleMarkCommand(String userInput) throws IOException {
		assert userInput != null;

		for (Task temp : searchResult) {

			//only mark when the command is valid and there is only one match
			if (userInput.equalsIgnoreCase(KEYWORD_MARK + temp.getTask()) 
					|| searchResult.size()==1) {
				mark(temp);			
				break;
			}			
		}	
	}


	/**
	 * Save file to a different location.
	 * @param command
	 * @throws IOException 
	 * @throws NoFileNameException 
	 */
	private void handleSaveCommand(Command command) throws NoFileNameException, IOException {
		assert command != null;
		saveToLocation(command.getParameters()[TASK]);
	}


	/**
	 * Move saved file to a different location.
	 * @param command
	 * @throws IOException 
	 */
	private void handleMoveCommand(Command command) throws IOException {
		assert command != null;
		moveToLocation(command.getParameters()[TASK]);	
	}
	
	/**
	 * Open a file from a specified location.
	 * @param command
	 * @throws IOException
	 */
	private void handleOpenCommand(Command command) throws IOException {
		assert command != null;
		loadFilename(command.getParameters()[TASK]);
	}


	/**
	 * @param result
	 * @param userInput
	 * @param command
	 * @throws IOException 
	 */
	private void handleEditCommand(ArrayList<Task> result, String userInput, 
			Command command) throws IOException {
		assert result != null;
		assert userInput != null;
		assert command != null;

		ArrayList<Task> finalResult = new ArrayList<Task>(); 

		//create the transient task for parsing
		transientTask = createTransientTask(command);

		//parse the editCommand
		result = parseEditCommand(transientTask);

		//get the original task information
		String originalTask = userInput.substring(5, userInput.indexOf(EDIT_COMMAND_SEPARATOR));

		executeEditCommand(userInput, finalResult, result, originalTask);

	}


	/**
	 * @param userInput
	 * @param finalResult
	 * @param result
	 * @param originalTask
	 * @throws IOException 
	 */
	private void executeEditCommand(String userInput, ArrayList<Task> finalResult, 
			ArrayList<Task> result, String originalTask) throws IOException {
		assert userInput != null;
		assert finalResult != null;
		assert result != null;
		assert originalTask != null;

		for (Task temp : searchResult) {

			//check if it is the right task to edit
			if (temp.getTask().contains(originalTask)) {				
				finalResult.add(temp);	  
				finalResult.add(result.get(1));

				Task original = finalResult.get(0);

				Task updated = finalResult.get(1);

				//not update time -> retain the original time, type and status
				if(updated.getTime().toString().equals(TIME_EMPTY)){
					updated.setTime(original.getTime());
					updated.setType(original.getType());
					updated.setStatus(original.getStatus());
				}

				//not update priority -> retain the original priority
				if(updated.getPriority()== PriorityLevel.NOT_SPECIFIED){
					updated.setPriority(original.getPriority());
				}

				edit(finalResult);
				break;
			}
		}

	}


	/**
	 * @param userInput
	 * @throws IOException 
	 */
	private void handleDeleteTaskCommand(String userInput) throws IOException{
		assert userInput != null;

		for (Task temp : searchResult) {

			//delete the task only if there is one match and the command is valid
			if (userInput.equalsIgnoreCase(KEYWORD_DELETE + temp.getTask()) 
					|| searchResult.size()==1) {
				delete(temp);			
				break;
			}			
		}
	}


	/**
	 * @param userInput
	 * @throws IOException 
	 */
	private void handleDeleteCompleteTaskCommand(String userInput) throws IOException {
		assert userInput != null;

		for (Task temp : searchResultCompleted) {

			//delete the task only if there is one match and the command is valid
			if (userInput.equalsIgnoreCase(KEYWORD_COMPLETE_DELETE + temp.getTask())
					|| searchResultCompleted.size()==1) {
				deleteComplete(temp);			
				break;
			}			
		}
	}

	/**
	 * @return the resultant task list for display under "completed tasks" tab
	 * @throws IOException
	 */
	private ArrayList<Task> handleClearCompleteTaskCommand() throws IOException {
		storageController.clearCompletedTasks();
		return storageController.displayCompletedTasks();
	}


	/**
	 * @return the resultant task list for display under "overdue tasks" tab
	 * @throws IOException
	 */
	private ArrayList<Task> handleClearOverdueTaskCommand() throws IOException {
		storageController.clearOverdueTasks();
		return storageController.displayPendingTasks();
	}


	/**
	 * @return the resultant task list for display under "pending tasks" tab
	 * @throws IOException
	 */
	private ArrayList<Task> handleClearUpcomingTaskCommand() throws IOException {
		storageController.clearUpcomingTasks();
		return storageController.displayPendingTasks();
	}


	/**
	 * @return the resultant task list for display under "floating tasks" tab
	 * @throws IOException
	 */
	private ArrayList<Task> handleClearFloatingTaskCommand() throws IOException {
		storageController.clearFloatingTasks();
		return storageController.displayPendingTasks();
	}


	/**
	 * @return the resultant task list for display under "all tasks" tab
	 * @throws IOException
	 */
	private ArrayList<Task> handleClearAllCommand() throws IOException {
		storageController.clearAllPendingTasks();
		return storageController.displayPendingTasks();
	}


	/**
	 * @param command
	 * @return the resultant task list after adding the task
	 * @throws IOException 
	 */
	private ArrayList<Task> handleAddCommand(Command command) throws IOException {
		assert task != null;

		task = createTask(command);
		storageController.addTask(task);

		return storageController.displayPendingTasks();
	}


	/**
	 * @param task
	 * @return the parsed edit command to  return two tasks
	 */
	private ArrayList<Task> parseEditCommand(TransientTask task) {
		assert task != null;
		return EditCommandParser.parseEditTask(task);
	}

	/**
	 * @param command
	 * @return the task object created from the parsed command
	 */
	private Task createTask(Command command) {
		assert command != null;
		return command.createTask();
	}

	/**
	 * @param command
	 * @return the transient task object from the parsed edit command
	 */
	private TransientTask createTransientTask(Command command) {
		assert command != null;
		return command.createTransientTask();
	}

	/**
	 * Delete a completed task.
	 * @param task
	 * @throws IOException 
	 */
	public void deleteComplete(Task task) throws IOException {
		assert task != null;
		storageController.deleteCompletedTask(task);
	}

	/**
	 * Delete a task.
	 * @param task
	 * @throws IOException  
	 */
	public void delete(Task task) throws IOException {
		assert task != null;
		storageController.deletePendingTask(task);
	}

	/**
	 * Mark a task as completed.
	 * @param task
	 * @throws IOException 
	 */
	public void mark(Task task) throws IOException {
		assert task != null;
		storageController.moveTaskToComplete(task);	
	}

	/**
	 * Unmark a completed task as uncompleted.
	 * @param task
	 * @throws IOException 
	 */
	public void unmark(Task task) throws IOException {
		assert task != null;
		storageController.moveTaskToPending(task);
	}

	/**
	 *Display all upcoming tasks under the "pending tasks" tab.
	 * @return the list of pending tasks
	 */
	public ArrayList<Task> displayPending() {
		ArrayList<Task> result = storageController.displayPendingTasks();
		return result;
	}

	/**
	 * Display the list of completed task under the "completed tasks" tab.
	 * @return the list of the completed task
	 */
	public ArrayList<Task> displayComplete() {
		ArrayList<Task> result = storageController.displayCompletedTasks();
		return result;
	}

	/**
	 * Edit a task after locating the original task.
	 * @param result
	 * @throws IOException 
	 */
	public void edit(ArrayList<Task> result) throws IOException{
		assert result != null;
		storageController.editPendingTask(result.get(0), result.get(1));
	}

	/**
	 * Move the saved file to a new location on PC.
	 * @param path
	 * @throws IOException 
	 */
	public void moveToLocation(String path) throws IOException {	
		assert path != null;
		storageController.moveToLocation(path);
	}

	/**
	 * Load a file from PC based on the given file name.
	 * @param fileName
	 * @throws IOException
	 */
	public void loadFilename(String filename) throws IOException {	
		assert filename != null;
		storageController.loadFromFile(filename);
	}

	/**
	 * Save the file to a particular location.
	 * @param path
	 * @throws NoFileNameException 
	 * @throws IOException 
	 */
	public void saveToLocation(String path) throws NoFileNameException, IOException {
		assert path != null;
		storageController.saveToLocation(path);
	}

	/**
	 * Determine whether the command is a valid command based on the command keyword.
	 * Return true if it is a valid command or false otherwise.
	 * @param commandWord
	 * @return true or false.
	 */
	public boolean isCommand(String commandWord) {
		assert commandWord != null;

		commandWord = commandWord.toLowerCase();
		if(commandWord.equals(COMMAND_ADD)|| commandWord.equals(COMMAND_DELETE)
				|| commandWord.equals(COMMAND_EDIT)|| commandWord.equals(COMMAND_SEARCH)
				|| commandWord.equals(COMMAND_SORT) || commandWord.equals(COMMAND_CLEAR)
				|| commandWord.equals(COMMAND_UNDO) || commandWord.equals(COMMAND_HELP) 
				|| commandWord.equals(COMMAND_MARK) || commandWord.equals(COMMAND_REDO) 
				|| commandWord.equals(COMMAND_SWITCH) || commandWord.equals(COMMAND_UNMARK)
				|| commandWord.equals(COMMAND_THEME) || commandWord.equals(COMMAND_SHOW)
				|| commandWord.equals(COMMAND_SAVE) || commandWord.equals(COMMAND_OPEN)) {
			return true;
		}

		else {
			return false;
		}
	}


	/**
	 * Search the upcoming task list for matched based on the specified value.
	 * @param oldValue
	 * @return the search result from all upcoming tasks as a task list
	 */
	public ArrayList<Task> handleSearchPending(String newValue) {
		assert newValue != null;
		searchResult = storageController.searchMatchPending(newValue);	
		return searchResult;
	}


	/**
	 * Search the completed task list for matched based on the specified value.
	 * @param oldValue
	 * @return the search result from all completed tasks as a task list
	 */
	public ArrayList<Task> handleSearchCompleted(String newValue) {
		assert newValue != null;
		searchResultCompleted = storageController.searchMatchCompleted(newValue);	
		return searchResultCompleted;
	}


	/**
	 * Check through the whole upcoming task list
	 * to mark all overdue task as overdue.
	 * @return the new task list after checking
	 * @throws IOException
	 */
	public ArrayList<Task> checkOverdue() throws IOException  {
		return storageController.checkOverdue(new Date());
	}

	/**
	 * Parse the string to a numeric index.
	 * @param command
	 * @return the numeric task index
	 */
	public int retrieveTaskIndex(Command command) {
		assert command != null;

		CommandType type = command.getType();
		String content = command.getContent();

		//delete by index
		if (type == CommandType.DELETE) {
				return Integer.parseInt(content);
		}

		//edit by index
		else if (type == CommandType.EDIT) {
			content = content.substring(0, content.indexOf(EDIT_COMMAND_SEPARATOR));
				return Integer.parseInt(content);
		}

		//other commands do not execute by index
		else {
			return -1;
		}
	}
}
/* @@author A0127481E */