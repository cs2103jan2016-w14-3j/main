package main.java.backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import main.java.data.Command;
import main.java.data.Task;

public class TempStorage {

//	enum COMMAND_TYPE {
//		ADD_TASK, DISPLAY_ALL, EDIT_TASK, DELETE_TASK, CLEAR_ALL, SEARCH_WORD,
//		SORT
//	};
//	
//	private static final String COMMAND_ADD = "add";
//	private static final String COMMAND_DISPLAY = "display";
//	private static final String COMMAND_UPDATE = "update";
//	private static final String COMMAND_DELETE = "delete";
//	private static final String COMMAND_CLEAR = "clear";
//	private static final String COMMAND_SEARCH = "search";
//	private static final String COMMAND_SORT = "sort";
	
	private ArrayList<Task> taskList;
	private Storage storage;
	//private String currentCommand;
	//private Task currentTask;
	
	public TempStorage() throws Exception {
		storage = new Storage();
		taskList = retrieveListFromFile();
	}
	
	// for logic to call to process the command
/*	public void addNewCommand(Command command) throws Exception{
		processCommand(command);
	}
	
	//obtain the command type and the task to be stored (if any)
	private void processCommand(Command command) throws Exception{
		currentCommand = command.getType();
		currentTask = command.createTask();
		performTempOperation(currentCommand, currentTask);
	}
	
	//performs the command on the temp list
	private void performTempOperation(String command, Task task) throws Exception{

		COMMAND_TYPE commandType = determineCommandType(command);

		switch (commandType) {
			case ADD_TASK:
				writeToTemp(task);
				break;
				
			case DISPLAY_ALL:
				displayTemp();
				break;
				
			case EDIT_TASK:
				editToTemp(task);
				break;
				
			case DELETE_TASK:
				deleteFromTemp(task);
				break;
				
			case CLEAR_ALL:
				clearTemp();
				break;
				
			case SEARCH_WORD:
				searchTemp(task);
				break;
				
			case SORT:
				sortTemp();
				break;
				
			default:
				break;
			}
	}
	
	public static COMMAND_TYPE determineCommandType(String commandTypeString) {

		if(commandTypeString.equalsIgnoreCase(COMMAND_ADD)) {
			return COMMAND_TYPE.ADD_TASK;
		}
		else if(commandTypeString.equalsIgnoreCase(COMMAND_DISPLAY)) {
			return COMMAND_TYPE.DISPLAY_ALL;
		}
		else if(commandTypeString.equalsIgnoreCase(COMMAND_UPDATE)) {
			return COMMAND_TYPE.EDIT_TASK;
		}
		else if(commandTypeString.equalsIgnoreCase(COMMAND_DELETE)) {
			return COMMAND_TYPE.DELETE_TASK;
		}
		else if(commandTypeString.equalsIgnoreCase(COMMAND_CLEAR)) {
			return COMMAND_TYPE.CLEAR_ALL;
		}
		else if(commandTypeString.equalsIgnoreCase(COMMAND_SEARCH)) {
			return COMMAND_TYPE.SEARCH_WORD;
		}
		else if(commandTypeString.equalsIgnoreCase(COMMAND_SORT)) {
			return COMMAND_TYPE.SORT;
		}
		else {
			return null;
		}
	}*/
	
	public void writeToTemp(Task task) throws Exception {
		assert task != null;
		
		taskList.add(task);
		storage.writeToFile(task);
	}
	
	public ArrayList<Task> displayTemp() {
	
		return taskList;
	}
	
	public void editToTemp(Task taskToEdit, Task editedTask) throws Exception {
		assert taskToEdit.getTaskID() >= 0;
		
		taskList.set(taskToEdit.getTaskID(), editedTask);
		storage.editToFile(taskToEdit.getTaskID(), editedTask);
	}
	
	public void deleteFromTemp(Task task) throws Exception {
		assert task.getTaskID() >= 0;
		
		taskList.remove(task.getTaskID());			
		storage.deleteFromFile(task.getTaskID());
	}
	
	public void clearTemp() throws Exception {
		taskList.clear();
		storage.clearFile();
	}
	
	public ArrayList<Task> searchTemp(Task task) {
		ArrayList<Task> searchResults = new ArrayList<Task>();
		Task tempTask;
		Boolean isTimeSpecified = false;
		Boolean isPrioritySpecified = false;
		
		for(int i=0; i<taskList.size(); i++) {
			tempTask = taskList.get(i);
			
			if(!tempTask.getTime().isEmpty()) {
				isTimeSpecified = true;
				System.out.println(tempTask.getTime());
			}
			if(!tempTask.getPriority().isEmpty()) {
				isPrioritySpecified = true;
			}
			if((searchString(tempTask.getTask(), task.getTask()) >= 1) ||
					(isTimeSpecified && tempTask.getTime().equals(task.getTime())) ||
					(isPrioritySpecified && tempTask.getPriority().equals(task.getPriority()))) {
				tempTask.setTaskID(i);
				searchResults.add(tempTask);
			}
		}
		return searchResults;
	}
	
	public void sortTemp() {
		
	}
	
	/* compares 2 strings and return the number of word matches
	 * 
	 */
	private int searchString(String taskString, String input) {
		int numMatches = 0;
		String keyWordsArray[] = input.split(" ");

		for(int i=0; i<keyWordsArray.length; i++) {
			if(taskString.toLowerCase().contains(keyWordsArray[i].toLowerCase())) {
				numMatches++;
			}
		}
		return numMatches;
	}
	
	private ArrayList<Task> retrieveListFromFile() throws Exception {
		ArrayList<Task> list = storage.readFromFile();
		
		return list;
	}
}
