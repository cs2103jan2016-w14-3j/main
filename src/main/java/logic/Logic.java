package main.java.logic;


import java.util.ArrayList;
import java.util.Date;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import main.java.data.*;
import main.java.parser.*;
import main.java.storage.*;

public class Logic {

	private static final String ADD_COMMAND = "add";
	private static final String DELETE_COMMAND = "delete";
	private static final String SEARCH_COMMAND = "search";
	private static final String CHANGE_DIRECTORY_COMMAND = "move";
	private static final String SORT_COMMAND = "sort";
	private static final String EDIT_COMMAND = "edit";
	private static final String UNDO_COMMAND = "undo";
	private static final String HELP_COMMAND = "help";
	private static final String MARK_COMMAND = "mark";
	private static final String UNMARK_COMMAND = "unmark";
	private static final String CLEAR_COMMAND = "clear";
	private static final String SWITCH_COMMAND = "switch";
	private static final String REDO_COMMAND = "redo";
	private static final String THEME_COMMAND = "theme";
	private static final String SHOW_COMMAND = "show";
	private static final String EMPTY_STRING = "";


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
		try {
			storageController = new StorageController();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public ArrayList<Task> initLogic() throws Exception{
		Logic logic = new Logic();
		searchResult = new ArrayList<Task>();
		searchResultCompleted = new ArrayList<Task>();
		return displayPending();

	}

	public ArrayList<Task> handleUserCommand(String userInput,ArrayList<Task> taskOptions) throws Exception {
		assert userInput != null;

		CommandDispatcher dispatcher = new CommandDispatcher();
		Command command = new Command(userInput);
		command = parseCommand(dispatcher, command);
		//		System.out.println("task" + command.getParameters()[TASK]);
		//		System.out.println("time" + command.getParameters()[1]);
		//		System.out.println("priority" + command.getParameters()[2]);
		//		System.out.println("type" + command.getParameters()[3]);
		System.out.println(retrieveTaskIndex(command));


		ArrayList<Task> result = executeTask(command, taskOptions, userInput);

		return result;

	}

	private void handleAddCommand(Task task) throws Exception {
		assert task != null;
		storageController.addTask(task);
	}

	//	private ArrayList<Task> handleDeleteCommand(Task task) {
	//		assert task != null;
	//		return temp.searchTemp(task);
	//	}

	private ArrayList<Task> handleEditCommand(TransientTask task) throws Exception {

		return EditCommandParser.parseEditTask(task);
	}



	private Task createTask(Command command) {
		assert command != null;
		return command.createTask();
	}

	private Command parseCommand(CommandDispatcher dispatcher, Command command)throws InvalidInputFormatException {
		assert command != null;
		return dispatcher.parseCommand(command);
	}

	private ArrayList<Task> executeTask(Command command, ArrayList<Task> taskOptions,String userInput) throws NumberFormatException, Exception {

		ArrayList<Task> result = new ArrayList<Task>();

		if (command.isCommand(COMMAND_TYPE.ADD)) {

			task = createTask(command);
			if (!task.getTask().equals(EMPTY_STRING)) {
				handleAddCommand(task);
			}
			result = storageController.displayPendingTasks();
		}

		else if (command.isCommand(COMMAND_TYPE.CLEAR_ALL)){
			storageController.clearPendingTasks();
			result = storageController.displayPendingTasks();
		}

		else if (command.isCommand(COMMAND_TYPE.CLEAR_FLOATING)){
			storageController.clearFloatingTasks();
			result = storageController.displayPendingTasks();
		}

		else if (command.isCommand(COMMAND_TYPE.CLEAR_UPCOMING)){
			storageController.clearUpcomingTasks();
			result = storageController.displayPendingTasks();
		}

		else if (command.isCommand(COMMAND_TYPE.CLEAR_OVERDUE)){
			System.out.println("clear pending");
			storageController.clearOverdueTasks();
			result = storageController.displayPendingTasks();
		}

		else if (command.isCommand(COMMAND_TYPE.CLEAR_COMPLETE)){
			System.out.println("clear complete");
			storageController.clearCompletedTasks();
			result = storageController.displayCompletedTasks();
		}

		else if (command.isCommand(COMMAND_TYPE.DELETE)) {

			for (Task temp : searchResult) {
				if (userInput.equalsIgnoreCase("delete " + temp.getTask()) 
						|| searchResult.size()==1) {
					delete(temp);			
					break;
				}			
			}
		}

		else if (command.isCommand(COMMAND_TYPE.DELETE_COMPLETE)) {
			for (Task temp : searchResultCompleted) {
				if (userInput.equalsIgnoreCase("deleteComplete " + temp.getTask())
						|| searchResultCompleted.size()==1) {
					deleteComplete(temp);			
					break;
				}			
			}
		}


		else if (command.isCommand(COMMAND_TYPE.EDIT)) {

			ArrayList<Task> finalResult = new ArrayList<Task>(); 
			transientTask = createTransientTask(command);
			result = handleEditCommand(transientTask);
			String sub = userInput.substring(5, userInput.indexOf(","));

			for (Task temp : searchResult) {
				System.out.println("here"+temp.getTask());
				if (temp.getTask().contains(sub)) {				
					finalResult.add(temp);	  
					finalResult.add(result.get(1));

					Task original = finalResult.get(0);

					Task updated = finalResult.get(1);


					if(updated.getTime().toString().equals("[]")){
						updated.setTime(original.getTime());
						updated.setType(original.getType());
						updated.setStatus(original.getStatus());
					}
					if(updated.getPriority()== PRIORITY_LEVEL.NOT_SPECIFIED){
						updated.setPriority(original.getPriority());
					}

					edit(finalResult);

					break;
				}

			}
		}

		else if (command.isCommand(COMMAND_TYPE.MOVE)) {
			moveToLocation(command.getParameters()[TASK]);
		}
		else if (command.isCommand(COMMAND_TYPE.SAVE)) {
			saveToLocation(command.getParameters()[TASK]);
		}
		else if (command.isCommand(COMMAND_TYPE.MARK)) {

			for (Task temp : searchResult) {
				if (userInput.equalsIgnoreCase("mark " + temp.getTask()) || searchResult.size()==1) {
					mark(temp);			
					break;
				}			
			}

		}
		else if (command.isCommand(COMMAND_TYPE.UNMARK)) {

			for (Task temp : searchResultCompleted) {
				if (userInput.equalsIgnoreCase("unmark " + temp.getTask()) || searchResultCompleted.size()==1) {
					unmark(temp);			
					break;
				}			
			}
		}
		
		else if (command.isCommand(COMMAND_TYPE.SHOW) ||
				command.isCommand(COMMAND_TYPE.SHOW_COMPLETE)) {
			
			boolean isTime = false;
			Date timeFilter = null;
			PRIORITY_LEVEL priorityFilter = null;
			if (command.getParameters()[TIME] != null) {
				isTime = true;
				timeFilter = timeParser.parse(command.getParameters()
						[TIME]).get(0);

			}
			
			else {
				String priority = command.getParameters()[PRIORITY];
				
				if (priority.equals(PRIORITY_LEVEL.HIGH.getType())) {
					priorityFilter = PRIORITY_LEVEL.HIGH;
				}
				else if (priority.equals(PRIORITY_LEVEL.MEDIUM.getType())) {
					priorityFilter = PRIORITY_LEVEL.MEDIUM;
				}
				else {
					priorityFilter = PRIORITY_LEVEL.LOW;
				}

			}

			if (command.isCommand(COMMAND_TYPE.SHOW)) {
				
				if (isTime) {
					result = storageController.showAllPendingByDate(timeFilter);
				}
				//filter by priority
				else {
					result = storageController.showAllPendingByPriority(priorityFilter);
				}
				
			}
			//showComplete
			else {
				
				if (isTime) {
					result = storageController.showAllCompletedByDate(timeFilter);
				}
				
				else {
					result = storageController.showAllCompletedByPriority(priorityFilter);
				}	
			}
		}

		else if (command.isCommand(COMMAND_TYPE.SORT) 
				|| command.isCommand(COMMAND_TYPE.SORT_COMPLETE)) {
			String parameter = command.getParameters()[TASK].toLowerCase();
			if (command.isCommand(COMMAND_TYPE.SORT)) {
				if (parameter.equals("time")) {
					storageController.sortPendingByTime();
				}

				else if (parameter.equals("name")) {
					storageController.sortPendingByTaskName();
				}

				else if (parameter.equals("priority")) {
					storageController.sortPendingByPriority();
				}
			}
			else if (command.isCommand(COMMAND_TYPE.SORT_COMPLETE)) {
				if (parameter.equals("time")) {
					storageController.sortCompletedByTime();
				}

				else if (parameter.equals("name")) {
					storageController.sortCompletedByTaskName();
				}

				else if (parameter.equals("priority")) {
					storageController.sortCompletedByPriority();
				} 
			}
		}

		else if (command.isCommand(COMMAND_TYPE.UNDO)) {
			storageController.undo();
		}

		else if (command.isCommand(COMMAND_TYPE.REDO)) {
			storageController.redo();
			//System.out.println("UNDO IS HERE !!!!");
		}

		return result;
	}

	private TransientTask createTransientTask(Command command) {
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

	public void moveToLocation(String path){	
		
		storageController.moveToLocation(path);
	}

	public void loadFilename(String filename){	
		
		storageController.loadFromFile(filename);
	}

	public void saveToLocation(String path) {
		storageController.saveToLocation(path);
	}

	public boolean isCommand(String commandWord) {
		commandWord = commandWord.toLowerCase();
		if(commandWord.equals(ADD_COMMAND)||commandWord.equals(DELETE_COMMAND)||
				commandWord.equals(EDIT_COMMAND)||commandWord.equals(SEARCH_COMMAND)||
				commandWord.equals(SORT_COMMAND)||commandWord.equals(CHANGE_DIRECTORY_COMMAND)||
				commandWord.equals(CLEAR_COMMAND)||commandWord.equals(UNDO_COMMAND)||commandWord.equals(HELP_COMMAND)||
				commandWord.equals(MARK_COMMAND)||commandWord.equals(REDO_COMMAND) || commandWord.equals(SWITCH_COMMAND)|| 
				commandWord.equals(UNMARK_COMMAND)||commandWord.equals(THEME_COMMAND) || commandWord.equals(SHOW_COMMAND))
			return true;
		return false;

	}


	public ArrayList<Task> handleSearchPending(String oldValue, String newValue) throws Exception {
		//System.out.println("new val: " + newValue);
		//System.out.println("old val: " + oldValue);
		searchResult = storageController.searchMatchPending(newValue);	
		return searchResult;
	}


	public ArrayList<Task> handleSearchCompleted(String oldValue, String newValue) throws Exception {
		//System.out.println("new val: " + newValue);
		//System.out.println("old val: " + oldValue);
		searchResultCompleted = storageController.searchMatchCompleted(newValue);	
		return searchResultCompleted;
	}


	public ArrayList<Task> checkOverdue() {

		return storageController.checkOverdue(new Date());
	}

	public int retrieveTaskIndex(Command command) {
		COMMAND_TYPE type = command.getType();
		String content = command.getContent();
		if (type == COMMAND_TYPE.DELETE) {
			try {
				return Integer.parseInt(content);
			} catch (NumberFormatException e) {
				return -1;
			}
		}
		else if (type == COMMAND_TYPE.EDIT) {
			content = content.substring(0, content.indexOf(","));
			try {
				return Integer.parseInt(content);
			} catch (NumberFormatException e) {
				return -1;
			}
		}
		else {
			return -1;
		}
	}
	public static void main(String[] args) {
		Command command = new Command("delete good");
		command.setContent("good");
		Logic l = new Logic();
		System.out.println(l.retrieveTaskIndex(command));
	}
}
