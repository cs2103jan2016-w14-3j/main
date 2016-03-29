package main.java.storage;

import java.util.ArrayList;
import java.util.Stack;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import main.java.data.TASK_STATUS;
import main.java.data.Task;

public class TempStorage {

	private ArrayList<Task> taskList;
	private ArrayList<Task> tempList;
	private Stack<ArrayList<Task>> undoStack;
	private Stack<ArrayList<Task>> redoStack;
	private PermStorage permStorage;
	private ArrayList<Task> upcomingList;
	private ArrayList<Task> floatingList;
	private ArrayList<Task> overdueList;
	private Stack<ArrayList<Task>> searchHistoryAll;
	private Stack<ArrayList<Task>> searchHistoryUpcoming;
	private Stack<ArrayList<Task>> searchHistoryFloating;
	private Stack<ArrayList<Task>> searchHistoryOverdue;
	private String prevSearch;

	private static final String SPACE = " ";

	public TempStorage () {

	}

	public TempStorage(PermStorage permStorage) {
		this.permStorage = permStorage;
		taskList = retrieveListFromFile();

		tempList = new ArrayList<Task>(taskList);
		undoStack = new Stack<ArrayList<Task>>();
		undoStack.push(tempList);
		redoStack = new Stack<ArrayList<Task>>();
		
		
		upcomingList = new ArrayList<Task>();
		floatingList = new ArrayList<Task>();
		overdueList = new ArrayList<Task>();
		categoriseTasks();
		
		searchHistoryAll = new Stack<ArrayList<Task>>();
		searchHistoryAll.push(taskList);
		searchHistoryUpcoming = new Stack<ArrayList<Task>>();
		searchHistoryUpcoming.push(upcomingList);
		searchHistoryFloating = new Stack<ArrayList<Task>>();
		searchHistoryFloating.push(floatingList);
		searchHistoryOverdue = new Stack<ArrayList<Task>>();
		searchHistoryOverdue.push(overdueList);
		prevSearch = "";
	}

	public void writeToTemp(Task task) {

		taskList.add(task);
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.writeToFile(task);
		categoriseTasks();
	}

	public ArrayList<Task> displayTemp() {
		return taskList;
	}

	public ArrayList<Task> displayUpcoming() {
		return upcomingList;
	}
	
	public ArrayList<Task> displayFloating() {
		return floatingList;
	}
	
	public ArrayList<Task> displayOverdue() {
		return overdueList;
	}
	
	public void editToTemp(Task taskToEdit, Task editedTask) {

		int indexOfTaskToEdit = searchTemp(taskToEdit);
		taskList.set(indexOfTaskToEdit, editedTask);
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.editToFile(indexOfTaskToEdit, editedTask);
		categoriseTasks();
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
	
	public void clearUpcoming() {
		for(int i=0; i<taskList.size(); i++) {
			Task task = taskList.get(i);
			if(task.getStatus().equals(TASK_STATUS.UPCOMING)) {
				taskList.remove(i);
				permStorage.deleteFromFile(i);
			}
		}
		upcomingList.clear();
	}
	
	public void clearFloating() {
		for(int i=0; i<taskList.size(); i++) {
			Task task = taskList.get(i);
			if(task.getStatus().equals(TASK_STATUS.FLOATING)) {
				taskList.remove(i);
				permStorage.deleteFromFile(i);
			}
		}
		floatingList.clear();
	}
	
	public void clearOverdue() {
		for(int i=0; i<taskList.size(); i++) {
			Task task = taskList.get(i);
			if(task.getStatus().equals(TASK_STATUS.OVERDUE)) {
				taskList.remove(i);
				permStorage.deleteFromFile(i);
			}
		}
		overdueList.clear();
	}

	public void undoPrevious() {
		if(undoStack.size() >= 2) {
			ArrayList<Task> currentState = undoStack.pop();
			redoStack.push(currentState);
			taskList = new ArrayList<Task>(undoStack.peek());
			permStorage.copyAllToFile(taskList);
			categoriseTasks();
		}
	}

	public void redoPrevious() {
		if(redoStack.size() != 0) {
			ArrayList<Task> currentState = redoStack.pop();
			undoStack.push(currentState);
			taskList = new ArrayList<Task>(currentState);
			permStorage.copyAllToFile(taskList);
			categoriseTasks();
		}
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
		undoStack.clear();
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		categoriseTasks();
	}

	public void saveToLocation(String path) {

		permStorage.saveToLocation(path);
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
	
	private ArrayList<Task> retrieveListFromFile() {

		ArrayList<Task> list = permStorage.readFromFile();
		return list;
	}
	
	private void categoriseTasks() {
		
		for(int i=0; i<taskList.size(); i++) {
			Task task = taskList.get(i);
			if(task.getStatus().equals(TASK_STATUS.UPCOMING)) {
				upcomingList.add(task);
			}
			else if(task.getStatus().equals(TASK_STATUS.FLOATING)) {
				floatingList.add(task);
			}
			else if(task.getStatus().equals(TASK_STATUS.OVERDUE)) {
				overdueList.add(task);
			}
		}
		assert taskList.size() == upcomingList.size() + floatingList.size() + overdueList.size();
	}
	
	public ArrayList<Task> searchMatch(String newValue, String taskStatus) {
		ArrayList<Task> listToSearch = null;
		Stack<ArrayList<Task>> searchHistory = null;
		
		if(taskStatus.equals("all")) {
			listToSearch = taskList;
			searchHistory = searchHistoryAll;
		}
		else if(taskStatus.equals("upcoming")) {
			listToSearch = upcomingList;
			searchHistory = searchHistoryUpcoming;
		}
		else if(taskStatus.equals("floating")) {
			listToSearch = floatingList;
			searchHistory = searchHistoryFloating;
		}
		else if(taskStatus.equals("overdue")) {
			listToSearch = overdueList;
			searchHistory = searchHistoryOverdue;
		}
		
		newValue = newValue.trim();
		if (!newValue.contains(" ")) {
			newValue = "";
			
			searchHistory.clear();
			searchHistory.push(listToSearch);
			prevSearch = "";

			return listToSearch;
		}
		else {
			newValue = newValue.substring(newValue.indexOf(" ") + 1);
		}

		ArrayList<Task> currList;
		if (newValue.length() < prevSearch.length()) {
			searchHistory.pop();
			prevSearch = newValue;
			return searchHistory.peek();
		}
		else {
			currList = searchHistory.peek();

			ArrayList<Task> searchResult = new ArrayList<Task>();		
			String[] parts = newValue.toLowerCase().split(SPACE);
			int taskNumber = 1;
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
			return searchResult;
		}
	}
}
