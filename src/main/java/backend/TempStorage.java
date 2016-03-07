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

	enum COMMAND_TYPE {
		ADD_TASK, DISPLAY_ALL, EDIT_TASK, DELETE_TASK, CLEAR_ALL, SEARCH_WORD,
		SORT
	};
	
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_UPDATE = "update";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_SEARCH = "search";
	private static final String COMMAND_SORT = "sort";
	
	private ArrayList<Task> taskList;
	private Storage storage;
	private String currentCommand;
	private Task currentTask;
	
	public TempStorage() throws Exception {
		taskList = new ArrayList<Task>();
		storage = new Storage();
	}
	
	// for logic to call to process the command
	public void addNewCommand(Command command) throws Exception{
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
				searchTemp();
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
	}
	
	private void writeToTemp(Task task) throws Exception {
		taskList.add(task);
		storage.writeToFile(task);
	}
	
	private ArrayList<Task> displayTemp() {
		return taskList;
	}
	
	private void editToTemp(Task task) throws Exception {
		
		storage.editToFile(task);
	}
	
	private void deleteFromTemp(Task task) throws Exception {
		
		for(int i=0; i<taskList.size(); i++) {
			if(taskList.get(i).getTime().equals(task.getTime())) {
				taskList.remove(i);
			}
		}
		storage.deleteFromFile(task.getTime());
	}
	
	private void clearTemp() throws Exception {
		taskList.clear();
		storage.clearFile();
	}
	
	private void searchTemp() {
		
	}
	
	private void sortTemp() {
		
	}
}
