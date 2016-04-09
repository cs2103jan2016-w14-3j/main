//@@author A0125084L
package main.java.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import main.java.data.PriorityLevel;
import main.java.data.TaskStatus;
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
		taskList = new ArrayList<Task>(retrieveListFromFile());
		undoStack.push(new ArrayList<Task>(taskList));
		redoStack = new Stack<ArrayList<Task>>();
		searchHistory = new Stack<ArrayList<Task>>();
		searchHistory.push(taskList);
		prevSearch = "";
	}

	public void writeToTemp(Task task) {

		Task taskCopy = new Task(task.getTask(), task.getTime(), task.getPriority(), 
				task.getType(), task.getStatus());
		
		taskCopy.setLastModified(true);
		taskList.add(taskCopy);
		Collections.sort(taskList, new TimeComparator());
		undoStack.push(new ArrayList<Task>(taskList));
		permStorage.writeToFile(taskCopy);
		isPreviousUndo = false;
	}

	public ArrayList<Task> displayTemp() {
		return taskList;
	}

	public void editToTemp(Task taskToEdit, Task editedTask) {

		int indexOfTaskToEdit = searchTemp(taskToEdit);
		
		Task editedTaskCopy = new Task(editedTask.getTask(), editedTask.getTime(), editedTask.getPriority(), 
				editedTask.getType(), editedTask.getStatus());
		
		editedTaskCopy.setLastModified(true);
		taskList.set(indexOfTaskToEdit, editedTaskCopy);
		Collections.sort(taskList, new TimeComparator());
		undoStack.push(new ArrayList<Task>(taskList));
		permStorage.editToFile(indexOfTaskToEdit, editedTaskCopy);
		isPreviousUndo = false;
	}

	public void deleteFromTemp(Task task) {

		int indexOfTaskToDelete = searchTemp(task);
		taskList.remove(taskList.get(indexOfTaskToDelete));
		undoStack.push(new ArrayList<Task>(taskList));
		permStorage.deleteFromFile(indexOfTaskToDelete);
		isPreviousUndo = false;
	}

	public void clearTemp() {

		taskList.clear();
		undoStack.push(new ArrayList<Task>(taskList));
		permStorage.clearFile();
		isPreviousUndo = false;
	}
	
	public void clearUpcoming() {
		for(int i=taskList.size()-1; i>=0; i--) {
			Task task = taskList.get(i);
			if(task.getStatus().equals(TaskStatus.UPCOMING)) {
				taskList.remove(i);
				permStorage.deleteFromFile(i);
			}
		}
		undoStack.push(new ArrayList<Task>(taskList));
		isPreviousUndo = false;
	}
	
	public void clearFloating() {
		for(int i=taskList.size()-1; i>=0; i--) {
			Task task = taskList.get(i);
			if(task.getStatus().equals(TaskStatus.FLOATING)) {
				taskList.remove(i);
				permStorage.deleteFromFile(i);
			}
		}
		undoStack.push(new ArrayList<Task>(taskList));
		isPreviousUndo = false;
	}
	
	public void clearOverdue() {
		for(int i=taskList.size()-1; i>=0; i--) {
			Task task = taskList.get(i);
			if(task.getStatus().equals(TaskStatus.OVERDUE)) {
				taskList.remove(i);
				permStorage.deleteFromFile(i);
			}
		}
		undoStack.push(new ArrayList<Task>(taskList));
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
		undoStack.push(new ArrayList<Task>(taskList));
		permStorage.copyAllToFile((taskList));
		isPreviousUndo = false;
	}

	public void sortByTime() {

		Collections.sort(taskList, new TimeComparator());
		undoStack.push(new ArrayList<Task>(taskList));
		permStorage.copyAllToFile((taskList));
		isPreviousUndo = false;
	}

	public void sortByPriority() {

		Collections.sort(taskList, new PriorityComparator());
		undoStack.push(new ArrayList<Task>(taskList));
		permStorage.copyAllToFile((taskList));
		isPreviousUndo = false;
	}

	public void moveToLocation(String path) throws IOException {
		permStorage.moveToLocation(path);
	}

	public void loadFromFile(String path) {

		permStorage.loadFromFile(path);
		taskList.clear();
		taskList = new ArrayList<Task>(retrieveListFromFile());
		undoStack.clear();
		undoStack.push(new ArrayList<Task>(taskList));
	}

	public void saveToLocation(String path) throws Exception {
		permStorage.saveToLocation(path);
	}
	
	public ArrayList<Task> checkOverdue(Date date) {
		
		ArrayList<Task> overdueList = new ArrayList<Task>();
		
		for(int i=taskList.size()-1; i>=0; i--) {
			Task task = taskList.get(i);
			
			if(task.getStatus().equals(TaskStatus.UPCOMING) && task.getTime().get(0).before(date)) {
				task.setStatus(TaskStatus.OVERDUE);
				permStorage.editToFile(i, task);
				taskList.remove(i);
				taskList.add(task);
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
			} else if(task.getTime().size() == 2) {
				if(date.after(task.getTime().get(0)) && date.before(task.getTime().get(1))) {
					searchResults.add(task);
				}
			}
		}
		return searchResults;
	}
	
	public ArrayList<Task> showAllByPriority(PriorityLevel priority) {
		
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

		if (newValue.contains(",")) {
			newValue = newValue.substring(0, newValue.indexOf(","));
		}

		if (!newValue.trim().contains(" ")) {
			newValue = "";
			
			searchHistory.clear();
		    searchHistory.push(taskList);
			prevSearch = "";

			return taskList;
		} else {
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
			searchResult.clear();

			for (Task task : currList) {
				boolean match = true;
				String taskMatch = task.getTask();
						for (String part : parts) {
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
//@@author A0125084L