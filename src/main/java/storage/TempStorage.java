package main.java.storage;

import java.util.ArrayList;
import java.util.Stack;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.data.Task;
import main.java.gui.TasksItemController;

public class TempStorage {

	private ArrayList<Task> taskList;
	private ArrayList<Task> tempList;
	private Stack< ArrayList<Task> > undoStack;
	private Stack< ArrayList<Task> > redoStack;
	private PermStorage permStorage;
	private Stack<ArrayList<Task>> searchHistory;
	private String prevSearch;

	private static final String SPACE = " ";
	private static final String SPLIT = "\\s+";

	public TempStorage () {

	}
	
	public void resetSearchHistory() {
		searchHistory.clear();
		searchHistory.push(taskList);
		prevSearch = "";
	}

	public TempStorage(PermStorage permStorage) {
		this.permStorage = permStorage;
		taskList = retrieveListFromFile();
		tempList = new ArrayList<Task>(taskList);
		undoStack = new Stack< ArrayList<Task> >();
		undoStack.push(tempList);
		redoStack = new Stack< ArrayList<Task> >();
		searchHistory = new Stack<ArrayList<Task>>();
		searchHistory.push(taskList);
		prevSearch = "";
	}

	public void writeToTemp(Task task) {

		taskList.add(task);
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.writeToFile(task);
	}

	public ArrayList<Task> displayTemp() {
		return taskList;
	}

	public void editToTemp(Task taskToEdit, Task editedTask) {

		int indexOfTaskToEdit = searchTemp(taskToEdit);
		taskList.set(indexOfTaskToEdit, editedTask);
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.editToFile(indexOfTaskToEdit, editedTask);
	}

	public void deleteFromTemp(Task task) {

		int indexOfTaskToDelete = searchTemp(task);
		taskList.remove(taskList.get(indexOfTaskToDelete));
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.deleteFromFile(indexOfTaskToDelete);
	}

	public void clearTemp() {

		taskList.clear();
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.clearFile();
	}

	public void undoPrevious() {
		if(undoStack.size() >= 2) {
			ArrayList<Task> currentState = undoStack.pop();
			redoStack.push(currentState);
			taskList = new ArrayList<Task>(undoStack.peek());
			permStorage.copyAllToFile(taskList);
		}
	}

	public void redoPrevious() {
		if(redoStack.size() != 0) {
			ArrayList<Task> currentState = redoStack.pop();
			undoStack.push(currentState);
			taskList = new ArrayList<Task>(currentState);
			permStorage.copyAllToFile(taskList);
		}
	}

	private int searchTemp(Task task) {

		for(int i=0; i<taskList.size(); i++) {
			Task thisTask = taskList.get(i);
			if(thisTask.getTask().equals(task.getTask()) && 
					thisTask.getTime().equals(task.getTime()) &&
					thisTask.getPriority().equals(task.getPriority())) {
				return i;
			}
		}
		return -1;
	}

	public void sortByTaskName() {

		Collections.sort(taskList, new TaskNameComparator());
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.copyAllToFile((taskList));
	}

	public void sortByTime() {

		Collections.sort(taskList, new TimeComparator());
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.copyAllToFile((taskList));
	}

	public void sortByPriority() {

		Collections.sort(taskList, new PriorityComparator());
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.copyAllToFile((taskList));
	}

	public void moveToLocation(String path) {

		permStorage.moveToLocation(path);
	}

	public void loadFromFile(String path) {

		permStorage.loadFromFile(path);
		taskList.clear();
		taskList = retrieveListFromFile();
		System.out.println(taskList.size());
		undoStack.clear();
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
	}
	
	public void saveToLocation(String path) {
		
		permStorage.saveToLocation(path);
	}

	private ArrayList<Task> retrieveListFromFile() {
		
		ArrayList<Task> list = permStorage.readFromFile();
		return list;
	}

	public ArrayList<Task> searchMatch(String newValue) {
		newValue = newValue.trim();
		if (!newValue.contains(" ")) {
			newValue = "";
			resetSearchHistory();
			return taskList;
		}
		else {
			newValue = newValue.substring(newValue.indexOf(" ") + 1);
		}
		//System.out.println(newValue);
		
		ArrayList<Task> currList;
		if (newValue.length() < prevSearch.length()) {
			searchHistory.pop();
			currList = searchHistory.pop();
		}
		else {
			currList = searchHistory.pop();
			searchHistory.push(currList);
		}
		
		
		ArrayList<Task> searchResult = new ArrayList<Task>();		
			String[] parts = null;
			int taskNumber = 1;
			parts = newValue.toLowerCase().split(SPACE);
			searchResult.clear();

			for (Task task : currList) {
				boolean match = true;
				String taskMatch = taskNumber + " " + task.getTask() + task.getPriority().getType() + 
						task.getTime().toString().replaceAll("SGT", "");;
				taskNumber++;
				
				for (String part : parts) {
					//String withoutComma = part.substring(0,part.length()-1);
					if(taskMatch.toLowerCase().contains(part.replaceAll(",", ""))&& part.contains(",")){
						match = true;
						break;
					}
					if (!taskMatch.toLowerCase().contains(part)) {
						match = false;
						break;
					}
				}
				if (match) {
					searchResult.add(task);
				}
			}
		prevSearch = newValue;
		searchHistory.push(searchResult);
		System.out.println(searchResult.size());
		return searchResult;

	}
}
