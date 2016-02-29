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
		ADD_TEXT, DISPLAY_ALL, DELETE_TEXT, CLEAR_ALL, SEARCH_WORD,
		SORT
	};
	
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DISPLAY = "display";
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
	public void addNewCommand(Command command) {
		processCommand(command);
	}
	
	//obtain the command type and the task to be stored (if any)
	private void processCommand(Command command) {
		currentCommand = command.getType();
		currentTask = command.executeCommand();
	}
	
	//performs the command on the temp list
	private void performTempOperation(String command, Task task) {

		COMMAND_TYPE commandType = determineCommandType(command);

		switch (commandType) {
			case ADD_TEXT:
				writeToTemp(currentTask);
				break;
				
			case DISPLAY_ALL:
				displayTemp();
				break;
				
			case DELETE_TEXT:
				deleteFromTemp();
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
			return COMMAND_TYPE.ADD_TEXT;
		}
		else if(commandTypeString.equalsIgnoreCase(COMMAND_DISPLAY)) {
			return COMMAND_TYPE.DISPLAY_ALL;
		}
		else if(commandTypeString.equalsIgnoreCase(COMMAND_DELETE)) {
			return COMMAND_TYPE.DELETE_TEXT;
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
	
	private void deleteFromTemp(int taskNumber) throws Exception {
		taskList.remove(taskNumber-1);
		storage.deleteFromFile(taskNumber-1);
	}
	
	private void clearTemp() throws Exception {
		taskList.clear();
		storage.clearFile();
	}
	
	private void searchTemp() {
		
	}
	
	private void sortTemp() {
		
	}
	
	public static void main(String[] args) throws Exception {
		TempStorage tempStorage = new TempStorage();
	}	
}
