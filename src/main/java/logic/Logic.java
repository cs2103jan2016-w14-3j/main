/* @@author A0127481E */
package main.java.logic;

import java.util.ArrayList;
import java.util.Date;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import main.java.data.CommandType;
import main.java.data.Command;
import main.java.data.PriorityLevel;
import main.java.data.Task;
import main.java.data.TransientTask;
import main.java.parser.EditCommandParser;
import main.java.parser.InvalidInputFormatException;
import main.java.storage.StorageController;


public class Logic {

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
	
	private static final String EDIT_COMMAND_SEPARATOR = ",";
	private static final String TIME_EMPTY = "[]";
	
	private static final String STRING_TIME = "time";
	private static final String STRING_PRIORITY = "priority";
	private static final String STRING_NAME = "name";
	private static final String KEYWORD_UNMARK = "unmark ";
	private static final String KEYWORD_MARK = "mark ";
	private static final String KEYWORD_DELETE = "delete ";
	private static final String KEYWORD_COMPLETE_DELETE = "deleteComplete ";
	
	


	private static final int TASK = 0;
	private static final int TIME = 1;
	private static final int PRIORITY = 2;


	private static Task task;
	private static TransientTask transientTask;
	private static StorageController storageController;
	private ArrayList<Task> searchResult;
	private ArrayList<Task> searchResultCompleted;
	private static PrettyTimeParser timeParser = new PrettyTimeParser();

	public Logic() {
			storageController = new StorageController();
	}

	public ArrayList<Task> handleUserCommand(String userInput,ArrayList<Task> taskOptions) 
			throws Exception {
		assert userInput != null;

		CommandDispatcher dispatcher = new CommandDispatcher();
		Command command = new Command(userInput);
		command = parseCommand(dispatcher, command);

		ArrayList<Task> result = executeTask(command, taskOptions, userInput);

		return result;

	}

	private Command parseCommand(CommandDispatcher dispatcher, Command command)
			throws InvalidInputFormatException {
		assert command != null;
		
		return dispatcher.parseCommand(command);
	}

	private ArrayList<Task> executeTask(Command command, ArrayList<Task> taskOptions,
			String userInput) throws NumberFormatException, Exception {
		assert command != null;

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

	private void handleSortCompleteCommand(Command command) {
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


	private void handleSortCommand(Command command) {
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


	private void handleRedoCommand() {
		storageController.redo();
	}


	private void handleUndoCommand() {
		storageController.undo();	
	}


	private ArrayList<Task> handleShowCompleteCommand(Command command) {
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


	private void handleUnmarkCommand(String userInput) throws Exception {
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


	private void handleMarkCommand(String userInput) throws Exception {
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


	private void handleSaveCommand(Command command) throws Exception {
		assert command != null;

		saveToLocation(command.getParameters()[TASK]);
	}


	private void handleMoveCommand(Command command) throws Exception {
		assert command != null;

		moveToLocation(command.getParameters()[TASK]);	
	}


	private void handleEditCommand(ArrayList<Task> result, String userInput, 
			Command command) throws Exception {
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


	private void executeEditCommand(String userInput, ArrayList<Task> finalResult, 
			ArrayList<Task> result, String originalTask) throws Exception {

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


	private void handleDeleteTaskCommand(String userInput) throws Exception {
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


	private void handleDeleteCompleteTaskCommand(String userInput) throws Exception {
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


	private ArrayList<Task> handleClearCompleteTaskCommand() {
		storageController.clearCompletedTasks();
		return storageController.displayCompletedTasks();
	}


	private ArrayList<Task> handleClearOverdueTaskCommand() {
		storageController.clearOverdueTasks();
		return storageController.displayPendingTasks();
	}


	private ArrayList<Task> handleClearUpcomingTaskCommand() {
		storageController.clearUpcomingTasks();
		return storageController.displayPendingTasks();
	}


	private ArrayList<Task> handleClearFloatingTaskCommand() {
		storageController.clearFloatingTasks();
		return storageController.displayPendingTasks();
	}


	private ArrayList<Task> handleClearAllCommand() {
		storageController.clearPendingTasks();
		return storageController.displayPendingTasks();
	}


	private ArrayList<Task> handleAddCommand(Command command) throws Exception {
		assert task != null;

		task = createTask(command);
		storageController.addTask(task);

		return storageController.displayPendingTasks();
	}


	private ArrayList<Task> parseEditCommand(TransientTask task) throws Exception {
		assert task != null;

		return EditCommandParser.parseEditTask(task);
	}

	private Task createTask(Command command) {
		assert command != null;

		return command.createTask();
	}

	private TransientTask createTransientTask(Command command) {
		assert command != null;

		return command.createTransientTask();
	}

	public void deleteComplete(Task task) throws Exception {
		storageController.deleteCompletedTask(task);
	}

	public void delete(Task task) throws Exception {
		storageController.deletePendingTask(task);
	}

	public void mark(Task task) throws Exception {
		storageController.moveTaskToComplete(task);	
	}

	public void unmark(Task task) throws Exception {
		storageController.moveTaskToPending(task);
	}

	public ArrayList<Task> displayPending()throws Exception{
		ArrayList<Task> result = storageController.displayPendingTasks();
		return result;
	}

	public ArrayList<Task> displayComplete()throws Exception{
		ArrayList<Task> result = storageController.displayCompletedTasks();
		return result;
	}

	public void edit(ArrayList<Task> result)throws Exception{

		storageController.editPendingTask(result.get(0), result.get(1));
	}

	public void moveToLocation(String path) throws Exception{	
		assert path != null;

		storageController.moveToLocation(path);
	}

	public void loadFilename(String fileName){	
		assert fileName != null;

		storageController.loadFromFile(fileName);
	}

	public void saveToLocation(String path) throws Exception {
		assert path != null;

		storageController.saveToLocation(path);
	}

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
				|| commandWord.equals(COMMAND_SAVE)) {
			return true;
		}

		else {
			return false;
		}
	}


	public ArrayList<Task> handleSearchPending(String oldValue, String newValue) 
			throws Exception {
		assert oldValue != null;
		assert newValue != null;

		searchResult = storageController.searchMatchPending(newValue);	

		return searchResult;
	}


	public ArrayList<Task> handleSearchCompleted(String oldValue, String newValue) 
			throws Exception {
		assert oldValue != null;
		assert newValue != null;

		searchResultCompleted = storageController.searchMatchCompleted(newValue);	

		return searchResultCompleted;
	}


	public ArrayList<Task> checkOverdue() {
		return storageController.checkOverdue(new Date());
	}

	public int retrieveTaskIndex(Command command) {
		assert command != null;

		CommandType type = command.getType();
		String content = command.getContent();

		//delete by index
		if (type == CommandType.DELETE) {
			try {
				return Integer.parseInt(content);
			} catch (NumberFormatException e) {
				return -1;
			}
		}

		//edit by index
		else if (type == CommandType.EDIT) {
			content = content.substring(0, content.indexOf(EDIT_COMMAND_SEPARATOR));
			try {
				return Integer.parseInt(content);
			} catch (NumberFormatException e) {
				return -1;
			}
		}

		//other commands do not execute by index
		else {
			return -1;
		}
	}
}
/* @@author A0127481E */