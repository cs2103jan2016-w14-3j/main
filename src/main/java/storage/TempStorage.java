//@@author A0125084L
package main.java.storage;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import Enumeration.PriorityLevel;
import Enumeration.TaskStatus;
import Exception.NoFileNameException;
import main.java.data.Task;

/**
 * Contains methods that read and write tasks to a list before
 * passing the tasks to the permanent storage.
 * @author Hou Bo Wen
 *
 */
public class TempStorage {

	private boolean isPreviousUndo;
	private PermStorage permStorage;
	private String prevSearch;
	private Stack<ArrayList<Task>> undoStack;
	private Stack<ArrayList<Task>> redoStack;
	private Stack<ArrayList<Task>> searchHistory;
	private ArrayList<Task> taskList;

	/**
	 * Creates a TempStorage instance
	 */
	public TempStorage () {

	}

	/**
	 * Creates a TempStorage instance
	 * @param permStorage The permanent storage to be linked to, ie. pending or completed
	 * @throws IOException If an I/O error occurs
	 */
	public TempStorage(PermStorage permStorage) throws IOException {
		this.permStorage = permStorage;
		undoStack = new Stack<ArrayList<Task>>();
		taskList = new ArrayList<Task>(retrieveListFromFile());
		undoStack.push(new ArrayList<Task>(taskList));
		redoStack = new Stack<ArrayList<Task>>();
		
		searchHistory = new Stack<ArrayList<Task>>();
		searchHistory.push(taskList);
		prevSearch = "";
	}

	/**
	 * Adds a task to the tasks list
	 * @param task The task to be added
	 * @throws IOException If an I/O error occurs
	 */
	public void addToTemp(Task task) throws IOException {
		
		Task taskCopy = new Task(task.getTask(), task.getTime(), task.getPriority(), 
					task.getType(), task.getStatus());
		
		taskCopy.setLastModified(true);
		taskList.add(taskCopy);
		Collections.sort(taskList, new TimeComparator());
		undoStack.push(new ArrayList<Task>(taskList));
		permStorage.writeToFile(taskCopy);
		isPreviousUndo = false;
	}

	/**
	 * Returns a list containing all the tasks
	 * @return The list containing all the tasks
	 */
	public ArrayList<Task> displayTemp() {
		return taskList;
	}

	/**
	 * Replaces an existing taskToEdit task with the given editedTask task
	 * @param taskToEdit The existing task to edit
	 * @param editedTask The task that has been edited
	 * @throws IOException If an I/O error occurs
	 */
	public void editToTemp(Task taskToEdit, Task editedTask) throws IOException {
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

	/**
	 * Removes a task from the tasks list
	 * @param task The task to be deleted
	 * @throws IOException If an I/O error occurs
	 */
	public void deleteFromTemp(Task task) throws IOException {
		int indexOfTaskToDelete = searchTemp(task);
		taskList.remove(taskList.get(indexOfTaskToDelete));
		undoStack.push(new ArrayList<Task>(taskList));
		permStorage.deleteFromFile(indexOfTaskToDelete);
		isPreviousUndo = false;
	}

	/**
	 * Clears all tasks from the tasks list
	 * @throws IOException If an I/O error occurs
	 */
	public void clearAll() throws IOException {
		taskList.clear();
		undoStack.push(new ArrayList<Task>(taskList));
		permStorage.clearFile();
		isPreviousUndo = false;
	}
	
	/**
	 * Clears all upcoming tasks from the tasks list
	 * @throws IOException If an I/O error occurs
	 */
	public void clearUpcoming() throws IOException {
		
		for (int i = taskList.size() - 1; i >= 0; i--) {
			Task task = taskList.get(i);
			if (task.getStatus().equals(TaskStatus.UPCOMING)) {
				taskList.remove(i);
				permStorage.deleteFromFile(i);
			}
		}
		undoStack.push(new ArrayList<Task>(taskList));
		isPreviousUndo = false;
	}
	
	/**
	 * Clears all floating tasks from the tasks list
	 * @throws IOException If an I/O error occurs
	 */
	public void clearFloating() throws IOException {
		
		for (int i = taskList.size() - 1; i >= 0; i--) {
			Task task = taskList.get(i);
			if (task.getStatus().equals(TaskStatus.FLOATING)) {
				taskList.remove(i);
				permStorage.deleteFromFile(i);
			}
		}
		undoStack.push(new ArrayList<Task>(taskList));
		isPreviousUndo = false;
	}
	
	/**
	 * Clears all overdue tasks from the tasks list
	 * @throws IOException If an I/O error occurs
	 */
	public void clearOverdue() throws IOException {
		
		for (int i = taskList.size() - 1; i >= 0; i--) {
			Task task = taskList.get(i);
			if (task.getStatus().equals(TaskStatus.OVERDUE)) {
				taskList.remove(i);
				permStorage.deleteFromFile(i);
			}
		}
		undoStack.push(new ArrayList<Task>(taskList));
		isPreviousUndo = false;
	}

	/**
	 * Revert back to the previous state before a method is called
	 * @throws IOException If an I/O error occurs
	 */
	public void undoPrevious() throws IOException {
		if (undoStack.size() >= 2) {
			ArrayList<Task> currentState = new ArrayList<Task>(undoStack.pop());
			redoStack.push(currentState);
			taskList = new ArrayList<Task>(undoStack.peek());
			permStorage.copyAllToFile(taskList);
			isPreviousUndo = true;
		}
	}
	
	/**
	 * Negates the previous undo command 
	 * @throws IOException If an I/O error occurs
	 */
	public void redoPrevious() throws IOException {
		if (isPreviousUndo == false) {
			redoStack.clear();
		}
		
		if (redoStack.size() != 0) {
			ArrayList<Task> currentState = new ArrayList<Task>(redoStack.pop());
			undoStack.push(currentState);
			taskList = new ArrayList<Task>(currentState);
			permStorage.copyAllToFile(taskList);
		}
	}

	/**
	 * Sorts the tasks list by task name
	 * @throws IOException If an I/O error occurs
	 */
	public void sortByTaskName() throws IOException {
		Collections.sort(taskList, new TaskNameComparator());
		undoStack.push(new ArrayList<Task>(taskList));
		permStorage.copyAllToFile((taskList));
		isPreviousUndo = false;
	}

	/**
	 * Sorts the tasks list by time
	 * @throws IOException If an I/O error occurs
	 */
	public void sortByTime() throws IOException {
		Collections.sort(taskList, new TimeComparator());
		undoStack.push(new ArrayList<Task>(taskList));
		permStorage.copyAllToFile((taskList));
		isPreviousUndo = false;
	}

	/**
	 * Sorts the tasks list by priority
	 * @throws IOException If an I/O error occurs
	 */
	public void sortByPriority() throws IOException {
		Collections.sort(taskList, new PriorityComparator());
		undoStack.push(new ArrayList<Task>(taskList));
		permStorage.copyAllToFile((taskList));
		isPreviousUndo = false;
	}

	/**
	 * Changes the current working directory to the given path
	 * @param path The path of the file in the new working directory
	 * @throws IOException If an I/O error occurs
	 */
	public void moveToLocation(String path) throws IOException {
		permStorage.moveToLocation(path);
	}

	/**
	 * Loads and retrieves the tasks from the file based on the given path 
	 * @param path The path of the file to be loaded
	 * @throws IOException If an I/O error occurs
	 */
	public void loadFromFile(String path) throws IOException {
		permStorage.loadFromFile(path);
		taskList.clear();
		taskList = new ArrayList<Task>(retrieveListFromFile());
		undoStack.clear();
		undoStack.push(new ArrayList<Task>(taskList));
	}

	/**
	 * Makes a copy of the task file in the given path
	 * @param path The path of the file to be saved in
	 * @throws Exception If no file name is specified in the path
	 */
	public void saveToLocation(String path) throws NoFileNameException, InvalidPathException, IOException {
		permStorage.saveToLocation(path);
	}
	
	/**
	 * Returns a list of all overdue tasks relative to the given date
	 * @param date The date for the tasks to be checked overdue against
	 * @return The list of all overdue tasks relative to the given date
	 * @throws IOException If an I/O error occurs
	 */
	public ArrayList<Task> checkOverdue(Date date) throws IOException {
		ArrayList<Task> overdueList = new ArrayList<Task>();
		
		for (int i = taskList.size() - 1; i >= 0; i--) {
			Task task = taskList.get(i);
			
			if (task.getStatus().equals(TaskStatus.UPCOMING) && task.getTime().get(0).before(date)) {
				task.setStatus(TaskStatus.OVERDUE);
				permStorage.editToFile(i, task);
				taskList.remove(i);
				taskList.add(task);
				overdueList.add(task);
			}
		}
		return overdueList;
	}
	
	/**
	 * Returns a list of tasks that start or are ongoing on the given date
	 * @param date The date for the tasks to be shown
	 * @return The list of tasks that start or are ongoing on the given date
	 */
	public ArrayList<Task> showAllByDate(Date date) {
		ArrayList<Task> searchResults = new ArrayList<Task>();
		String dateString = date.toString().substring(0, 9);
		
		for (int i = 0; i < taskList.size(); i++) {
			Task task = taskList.get(i);
			
			if (task.getTime().size() == 1) {
				if (task.getTime().get(0).toString().substring(0, 9).equals(dateString)) {
					searchResults.add(task);
				}
			} else if (task.getTime().size() == 2) {
				if (date.after(task.getTime().get(0)) && date.before(task.getTime().get(1))) {
					searchResults.add(task);
				}
			}
		}
		return searchResults;
	}
	
	/**
	 * Returns a list of pending tasks that have the given priority level
	 * @param priority The priority for the tasks to be shown
	 * @return The list of tasks that have the given priority level
	 */
	public ArrayList<Task> showAllByPriority(PriorityLevel priority) {
		ArrayList<Task> searchResults = new ArrayList<Task>();
		
		for (int i = 0; i < taskList.size(); i++) {
			Task task = taskList.get(i);
			
			if (task.getPriority().equals(priority)) {
				searchResults.add(task);
			}
		}
		return searchResults;
	}
	
	/**
	 * Returns a list of tasks that matches the given string
	 * @param stringToSearch The string to be searched
	 * @return The list of completed tasks that matches the given string 
	 */
	public ArrayList<Task> searchMatch(String stringToSearch) {
		if (stringToSearch.contains(",")) {
			stringToSearch = stringToSearch.substring(0, stringToSearch.indexOf(","));
		}

		if (!stringToSearch.trim().contains(" ")) {
			stringToSearch = "";	
			searchHistory.clear();
		    searchHistory.push(taskList);
			prevSearch = "";

			return taskList;
		} else {
			stringToSearch = stringToSearch.substring(stringToSearch.indexOf(" ") + 1);
		}

		ArrayList<Task> currList;
		
		if (stringToSearch.length() < prevSearch.length()) {
			searchHistory.pop();
			prevSearch = stringToSearch;
			
			return searchHistory.peek();
		}
		else {
			currList = searchHistory.peek();
			ArrayList<Task> searchResult = new ArrayList<Task>();	
			String[] parts = stringToSearch.toLowerCase().split(" ");
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
			prevSearch = stringToSearch;
			searchHistory.push(searchResult);

			return searchResult;
		}
	}
	
	/*
	 * Returns the index of the given task in the list
	 */
	private int searchTemp(Task task) {

		for (int i = 0; i < taskList.size(); i++) {
			Task thisTask = taskList.get(i);
			if (thisTask.getTask().equals(task.getTask()) && 
					thisTask.getTime().equals(task.getTime()) &&
					thisTask.getPriority().equals(task.getPriority())) {
				return i;
			}
		}
		return -1;
	}
	
	/*
	 * Returns a list of tasks retrieved from permanent storage
	 */
	private ArrayList<Task> retrieveListFromFile() throws IOException {
		ArrayList<Task> list = permStorage.readFromFile();
		Collections.sort(list, new TimeComparator());
		
		return list;
	}
}
//@@author A0125084L