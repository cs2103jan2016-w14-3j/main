//@@author A0125084L
package main.java.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Enumeration.PriorityLevel;
import Enumeration.TaskStatus;
import main.java.data.Task;
 
/**
 * Contains methods that modifies the tasks contents in the Pending and Completed tasks storages
 * @author Hou Bo Wen
 *
 */
public class StorageController {

	private static final int TASK_PENDING = 0;
	private static final int TASK_COMPLETED = 1;
	private static final int TASK_BOTH = 2;
	
	private PendingTaskTempStorage pendingTemp;
	private CompletedTaskTempStorage completedTemp;
	private int lastAction;

	/**
	 * Creates a StorageController instance
	 * @throws IOException If an I/O error occurs
	 */
	public StorageController() throws IOException {	
		pendingTemp = new PendingTaskTempStorage();
		completedTemp = new CompletedTaskTempStorage();
	}

	/**
	 * Adds a new task
	 * @param task The task to be added
	 * @throws IOException If an I/O error occurs
	 */
	public void addTask(Task task) throws IOException {
		assert task != null;
		
		task.setLastModified(true);
		pendingTemp.addToTemp(task);
		lastAction = TASK_PENDING;
	}
	
	/**
	 * Returns the list of pending tasks
	 * @return The list of pending tasks
	 */
	public ArrayList<Task> displayPendingTasks() {
		return pendingTemp.displayTemp();
	}
	
	/**
	 * Returns the list of completed tasks
	 * @return The list of completed tasks
	 */
	public ArrayList<Task> displayCompletedTasks() {
		return completedTemp.displayTemp();
	}
	
	/**
	 * Edits the existing pending task to another specified task
	 * @param taskToEdit The existing task to edit
	 * @param editedTask The task that has been edited
	 * @throws IOException If an I/O error occurs
	 */
	public void editPendingTask(Task taskToEdit, Task editedTask) throws IOException {
		assert taskToEdit != null;
		assert editedTask != null;
		
		editedTask.setLastModified(true);
		pendingTemp.editToTemp(taskToEdit, editedTask);
		lastAction = TASK_PENDING;
	}
	
	/**
	 * Edits the existing completed task to another specified task
	 * @param taskToEdit The existing task to edit
	 * @param editedTask The task that has been edited
	 * @throws IOException If an I/O error occurs
	 */
	public void editCompletedTask(Task taskToEdit, Task editedTask) throws IOException {
		assert taskToEdit != null;
		assert editedTask != null;
		
		editedTask.setLastModified(true);
		completedTemp.editToTemp(taskToEdit, editedTask);
		lastAction = TASK_COMPLETED;
	}
	
	/**
	 * Removes a pending task
	 * @param task The task to be deleted
	 * @throws IOException If an I/O error occurs
	 */
	public void deletePendingTask(Task task) throws IOException {
		assert task != null;
		
		pendingTemp.deleteFromTemp(task);
		lastAction = TASK_PENDING;
	}
	
	/**
	 * Removes a completed task
	 * @param task The task to be deleted
	 * @throws IOException If an I/O error occurs
	 */
	public void deleteCompletedTask(Task task) throws IOException {
		assert task != null;
		
		completedTemp.deleteFromTemp(task);
		lastAction = TASK_COMPLETED;
	}
	
	/**
	 * Clears all pending tasks
	 * @throws IOException If an I/O error occurs
	 */
	public void clearAllPendingTasks() throws IOException {	
		pendingTemp.clearAll();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * Clears all upcoming tasks
	 * @throws IOException If an I/O error occurs
	 */
	public void clearUpcomingTasks() throws IOException {
		pendingTemp.clearUpcoming();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * Clears all floating tasks
	 * @throws IOException If an I/O error occurs
	 */
	public void clearFloatingTasks() throws IOException {
		pendingTemp.clearFloating();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * Clears all overdue tasks
	 * @throws IOException If an I/O error occurs
	 */
	public void clearOverdueTasks() throws IOException {
		pendingTemp.clearOverdue();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * Clears all completed tasks
	 * @throws IOException If an I/O error occurs
	 */
	public void clearCompletedTasks() throws IOException {		
		completedTemp.clearAll();
		lastAction = TASK_COMPLETED;
	}
	
	/**
	 * Sorts the pending tasks by their task name
	 * @throws IOException If an I/O error occurs
	 */
	public void sortPendingByTaskName() throws IOException {
		pendingTemp.sortByTaskName();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * Sorts the pending tasks by their starting time or deadline
	 * @throws IOException If an I/O error occurs
	 */
	public void sortPendingByTime() throws IOException {
		pendingTemp.sortByTime();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * Sorts the pending tasks by their priority
	 * @throws IOException If an I/O error occurs
	 */
	public void sortPendingByPriority() throws IOException {
		pendingTemp.sortByPriority();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * Sorts the completed tasks by their task name
	 * @throws IOException If an I/O error occurs
	 */
	public void sortCompletedByTaskName() throws IOException {
		completedTemp.sortByTaskName();
		lastAction = TASK_COMPLETED;
	}
	
	/**
	 * Sorts the completed tasks by their starting time or deadline
	 * @throws IOException If an I/O error occurs
	 */
	public void sortCompletedByTime() throws IOException {
		completedTemp.sortByTime();
		lastAction = TASK_COMPLETED;
	}
	
	/**
	 * Sorts the completed tasks by their priority
	 * @throws IOException If an I/O error occurs
	 */
	public void sortCompletedByPriority() throws IOException {
		completedTemp.sortByPriority();
		lastAction = TASK_COMPLETED;
	}
	
	/**
	 * Marks a task as completed and moves it to the completed tasks storage
	 * @param task The task to be marked
	 * @throws IOException If an I/O error occurs
	 */
	public void moveTaskToComplete(Task task) throws IOException {
		assert task != null;
		
		pendingTemp.deleteFromTemp(task);
		
		Task taskCopy = new Task(task.getTask(), task.getTime(), task.getPriority(), 
				task.getType(), TaskStatus.COMPLETED);
		
		taskCopy.setLastModified(true);	
		completedTemp.addToTemp(taskCopy);	
		lastAction = TASK_BOTH;
	}
	
	/**
	 * Unmarks a task and moves it back to the pending tasks storage
	 * @param task The task to be unmarked
	 * @throws IOException If an I/O error occurs
	 */
	public void moveTaskToPending(Task task) throws IOException {
		assert task != null;
		
		completedTemp.deleteFromTemp(task);
		
		Task taskCopy = new Task(task.getTask(), task.getTime(), task.getPriority(), 
				task.getType(), determineStatus(task.getTime()));
		
		taskCopy.setLastModified(true);
		pendingTemp.addToTemp(taskCopy);		
		lastAction = TASK_BOTH;
	}
	
	/**
	 * Undoes the previous command
	 * @throws IOException If an I/O error occurs
	 */
	public void undo() throws IOException {
		if (lastAction == TASK_PENDING) {
			pendingTemp.undoPrevious();
		}
		else if (lastAction == TASK_COMPLETED) {
			completedTemp.undoPrevious();
		}
		else if (lastAction == TASK_BOTH) {
			pendingTemp.undoPrevious();
			completedTemp.undoPrevious();
		}
	}
	
	/**
	 * Negates the previous undo command
	 * @throws IOException If an I/O error occurs
	 */
	public void redo() throws IOException {
		if (lastAction == TASK_PENDING) {
			pendingTemp.redoPrevious();
		}
		else if (lastAction == TASK_COMPLETED) {
			completedTemp.redoPrevious();
		}
		else if (lastAction == TASK_BOTH) {
			pendingTemp.redoPrevious();
			completedTemp.redoPrevious();
		}
	}
	
	/**
	 * Returns a list of pending tasks that start or are ongoing on the given date
	 * @param date The date for the tasks to be shown
	 * @return The list of pending tasks that start or are ongoing on the given date
	 */
	public ArrayList<Task> showAllPendingByDate(Date date) {
		return pendingTemp.showAllByDate(date);
	}
	
	/**
	 * Returns a list of pending tasks that have the given priority level
	 * @param priority The priority for the tasks to be shown
	 * @return The list of pending tasks that have the given priority level
	 */
	public ArrayList<Task> showAllPendingByPriority(PriorityLevel priority) {
		return pendingTemp.showAllByPriority(priority);
	}
	
	/**
	 * Returns a list of completed tasks that started or were ongoing on the given date
	 * @param date The date for the tasks to be shown
	 * @return The list of completed tasks that started or were ongoing on the given date
	 */
	public ArrayList<Task> showAllCompletedByDate(Date date) {
		return completedTemp.showAllByDate(date);
	}
	
	/**
	 * Returns a list of completed tasks that have the given priority level
	 * @param priority The priority for the tasks to be shown
	 * @return The list of completed tasks that have the given priority level
	 */
	public ArrayList<Task> showAllCompletedByPriority(PriorityLevel priority) {
		return completedTemp.showAllByPriority(priority);
	}
	
	/**
	 * Returns a list of pending tasks that matches the given string 
	 * @param stringToSearch The string to be searched
	 * @return The list of pending tasks that matches the given string 
	 */
	public ArrayList<Task> searchMatchPending(String stringToSearch) {
		return pendingTemp.searchMatch(stringToSearch);
	}
	
	/**
	 * Returns a list of completed tasks that matches the given string 
	 * @param stringToSearch The string to be searched
	 * @return The list of completed tasks that matches the given string 
	 */
	public ArrayList<Task> searchMatchCompleted(String stringToSearch) {
		return completedTemp.searchMatch(stringToSearch);
	}
	
	/**
	 * Changes the current working directory to the given path
	 * @param path The path of the file in the new working directory
	 * @throws IOException If an I/O error occurs
	 */
	public void moveToLocation(String path) throws IOException {
		assert path != null;
		
		pendingTemp.moveToLocation(path);
	}

	/**
	 * Loads and retrieves the tasks from the file based on the given path
	 * @param path The path of the file to be loaded
	 * @throws IOException If an I/O error occurs
	 */
	public void loadFromFile(String path) throws IOException {
		assert path != null;
		
		pendingTemp.loadFromFile(path);
	}
	
	/**
	 * Makes a copy of the task file in the given path
	 * @param path The path of the file to be saved in
	 * @throws Exception If no file name is specified in the path
	 */
	public void saveToLocation(String path) throws IOException {
		assert path != null;
		
		pendingTemp.saveToLocation(path);
	}
	
	/**
	 * Returns a list of all overdue tasks relative to the given date
	 * @param date The date for the tasks to be checked overdue against
	 * @return The list of all overdue tasks relative to the given date
	 * @throws IOException If an I/O error occurs
	 */
	public ArrayList<Task> checkOverdue(Date date) throws IOException {
		return pendingTemp.checkOverdue(date);
	}
	
	/*
	 * Determines the status of a task based on the given dates field 
	 */
	private TaskStatus determineStatus(List<Date> dates) {
		int size = dates.size();
		
		if (size == 0) {
			return TaskStatus.FLOATING;
		}
		else if (dates.get(size - 1).before(new Date())) {
			return TaskStatus.OVERDUE;
		}
		else {
			return TaskStatus.UPCOMING;
		}
	}
}
//@@author A0125084L