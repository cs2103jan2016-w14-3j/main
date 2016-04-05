package main.java.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import main.java.data.PRIORITY_LEVEL;
import main.java.data.TASK_STATUS;
import main.java.data.Task;

public class TempStorage {

	private ArrayList<Task> taskList;
	private Stack<ArrayList<Task>> undoStack;
	private Stack<ArrayList<Task>> redoStack;
	private PermStorage permStorage;
	private Stack<ArrayList<Task>> searchHistory;
	private String prevSearch;
	private boolean isPreviousUndo;

	private static final String SPACE = " ";

	public TempStorage () {

	}

	public TempStorage(PermStorage permStorage) {
		this.permStorage = permStorage;
		undoStack = new Stack<ArrayList<Task>>();
		taskList = retrieveListFromFile();
		ArrayList<Task> tempList = new ArrayList<Task>(taskList);	
		undoStack.push(tempList);
		redoStack = new Stack<ArrayList<Task>>();
		searchHistory = new Stack<ArrayList<Task>>();
		searchHistory.push(taskList);
		prevSearch = "";
	}

	public void writeToTemp(Task task) {

		taskList.add(task);
		Collections.sort(taskList, new TimeComparator());
		ArrayList<Task> tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.writeToFile(task);
		isPreviousUndo = false;
	}

	public ArrayList<Task> displayTemp() {
		return taskList;
	}

	public void editToTemp(Task taskToEdit, Task editedTask) {

		int indexOfTaskToEdit = searchTemp(taskToEdit);
		taskList.set(indexOfTaskToEdit, editedTask);
		Collections.sort(taskList, new TimeComparator());
		ArrayList<Task> tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.editToFile(indexOfTaskToEdit, editedTask);
		isPreviousUndo = false;
	}

	public void deleteFromTemp(Task task) {

		int indexOfTaskToDelete = searchTemp(task);
		taskList.remove(taskList.get(indexOfTaskToDelete));
		ArrayList<Task> tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.deleteFromFile(indexOfTaskToDelete);
		isPreviousUndo = false;
	}

	public void clearTemp() {

		taskList.clear();
		ArrayList<Task> tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.clearFile();
		isPreviousUndo = false;
	}
	
	public void clearUpcoming() {
		for(int i=taskList.size()-1; i>=0; i--) {
			Task task = taskList.get(i);
			if(task.getStatus().equals(TASK_STATUS.UPCOMING)) {
				taskList.remove(i);
				permStorage.deleteFromFile(i);
			}
		}
		ArrayList<Task> tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		isPreviousUndo = false;
	}
	
	public void clearFloating() {
		for(int i=taskList.size()-1; i>=0; i--) {
			Task task = taskList.get(i);
			if(task.getStatus().equals(TASK_STATUS.FLOATING)) {
				taskList.remove(i);
				permStorage.deleteFromFile(i);
			}
		}
		ArrayList<Task> tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		isPreviousUndo = false;
	}
	
	public void clearOverdue() {
		for(int i=taskList.size()-1; i>=0; i--) {
			Task task = taskList.get(i);
			if(task.getStatus().equals(TASK_STATUS.OVERDUE)) {
				taskList.remove(i);
				permStorage.deleteFromFile(i);
			}
		}
		ArrayList<Task> tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		isPreviousUndo = false;
	}

	public void undoPrevious() {
		if(undoStack.size() >= 2) {
			ArrayList<Task> currentState = new ArrayList<Task>(undoStack.pop());
			redoStack.push(currentState);
			taskList = new ArrayList<Task>(undoStack.peek());
			permStorage.copyAllToFile(taskList);
			isPreviousUndo = true;
		}
	}

	public void redoPrevious() {
		if(isPreviousUndo == false) {
			redoStack.clear();
		}
		if(redoStack.size() != 0) {
			ArrayList<Task> currentState = new ArrayList<Task>(redoStack.pop());
			undoStack.push(currentState);
			taskList = new ArrayList<Task>(currentState);
			permStorage.copyAllToFile(taskList);
		}
	}

	public void sortByTaskName() {

		Collections.sort(taskList, new TaskNameComparator());
		ArrayList<Task> tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.copyAllToFile((taskList));
		isPreviousUndo = false;
	}

	public void sortByTime() {

		Collections.sort(taskList, new TimeComparator());
		ArrayList<Task> tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.copyAllToFile((taskList));
		isPreviousUndo = false;
	}

	public void sortByPriority() {

		Collections.sort(taskList, new PriorityComparator());
		ArrayList<Task> tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.copyAllToFile((taskList));
		isPreviousUndo = false;
	}

	public void moveToLocation(String path) {

		permStorage.moveToLocation(path);
	}

	public void loadFromFile(String path) {

		permStorage.loadFromFile(path);
		taskList.clear();
		taskList = retrieveListFromFile();
		undoStack.clear();
		ArrayList<Task> tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
	}

	public void saveToLocation(String path) {

		permStorage.saveToLocation(path);
	}
	
	public ArrayList<Task> checkOverdue(Date date) {
		ArrayList<Task> overdueList = new ArrayList<Task>();
		
		for(int i=taskList.size()-1; i>=0; i--) {
			Task task = taskList.get(i);
			
			if(task.getStatus().equals(TASK_STATUS.UPCOMING) && task.getTime().get(0).before(date)) {
				task.setStatus(TASK_STATUS.OVERDUE);
				taskList.set(i, task);
				permStorage.editToFile(i, task);
				overdueList.add(task);
			}
		}
		return overdueList;
	}
	
	public ArrayList<Task> showAllByDate(Date date) {
		ArrayList<Task> searchResults = new ArrayList<Task>();
		String dateString = date.toString().substring(0, 9);
		
		for(int i=0; i<taskList.size(); i++) {
			Task task = taskList.get(i);
			
			if(task.getTime().size() == 1) {
				if(task.getTime().get(0).toString().substring(0, 9).equals(dateString)) {
					searchResults.add(task);
				}
			}
			else if(task.getTime().size() == 2) {
				if(date.after(task.getTime().get(0)) && date.before(task.getTime().get(1))) {
					searchResults.add(task);
				}
			}
		}
		return searchResults;
	}
	
	public ArrayList<Task> showAllByPriority(PRIORITY_LEVEL priority) {
		ArrayList<Task> searchResults = new ArrayList<Task>();
		
		for(int i=0; i<taskList.size(); i++) {
			Task task = taskList.get(i);
			
			if(task.getPriority().equals(priority)) {
				searchResults.add(task);
			}
		}
		return searchResults;
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
		Collections.sort(list, new TimeComparator());
		return list;
	}
	
	public ArrayList<Task> searchMatch(String newValue) {
		//handle edit
		if (newValue.contains(",")) {
			newValue = newValue.substring(0, newValue.indexOf(","));
		}
		//newValue = newValue.trim();
		//System.out.println(newValue);
		if (!newValue.trim().contains(" ")) {
			newValue = "";
			
			searchHistory.clear();
		    searchHistory.push(taskList);
			prevSearch = "";

			return taskList;
		}
		else {
			newValue = newValue.substring(newValue.indexOf(" ") + 1);
		}

		ArrayList<Task> currList;
		if (newValue.length() < prevSearch.length()) {
			//System.out.print("true");
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
//				String taskMatch = taskNumber + " " + task.getTask() + task.getPriority().getType() + 
//						task.getTime().toString().replaceAll("SGT", "");;
				String taskMatch = task.getTask();
						taskNumber++;

						for (String part : parts) {
							//String withoutComma = part.substring(0,part.length()-1);
//							if(taskMatch.toLowerCase().contains(part.replaceAll(",", ""))&& part.contains(",")){
//								System.out.println("here");
//								match = true;
//								break;
//							}
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
			//System.out.println("list size: " + searchResult.size());
			//System.out.println("stack size: " + searchHistory.size());
			return searchResult;
		}
	}
}
