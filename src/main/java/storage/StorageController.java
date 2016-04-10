//@@author A0125084L
package main.java.storage;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.java.Log.EventLog;
import main.java.data.PriorityLevel;
import main.java.data.TaskType;
import main.java.data.TaskStatus;
import main.java.data.Task;

/**
 * 
 * @author Bowen
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
	 * 
	 * @throws IOException
	 */
	public StorageController() throws IOException {	
		pendingTemp = new PendingTaskTempStorage();
		completedTemp = new CompletedTaskTempStorage();
	}

	/**
	 * 
	 * @param task
	 * @throws IOException
	 */
	public void addTask(Task task) throws IOException {
		assert task != null;
		
		task.setLastModified(true);
		pendingTemp.writeToTemp(task);
		lastAction = TASK_PENDING;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Task> displayPendingTasks() {
		return pendingTemp.displayTemp();
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Task> displayCompletedTasks() {
		return completedTemp.displayTemp();
	}
	
	/**
	 * 
	 * @param taskToEdit
	 * @param editedTask
	 * @throws IOException
	 */
	public void editPendingTask(Task taskToEdit, Task editedTask) throws IOException {
		assert taskToEdit != null;
		assert editedTask != null;
		
		editedTask.setLastModified(true);
		pendingTemp.editToTemp(taskToEdit, editedTask);
		lastAction = TASK_PENDING;
	}
	
	/**
	 * 
	 * @param taskToEdit
	 * @param editedTask
	 * @throws IOException
	 */
	public void editCompletedTask(Task taskToEdit, Task editedTask) throws IOException {
		assert taskToEdit != null;
		assert editedTask != null;
		
		editedTask.setLastModified(true);
		completedTemp.editToTemp(taskToEdit, editedTask);
		lastAction = TASK_COMPLETED;
	}
	
	/**
	 * 
	 * @param task
	 * @throws IOException
	 */
	public void deletePendingTask(Task task) throws IOException {
		assert task != null;
		
		pendingTemp.deleteFromTemp(task);
		lastAction = TASK_PENDING;
	}
	
	/**
	 * 
	 * @param task
	 * @throws IOException
	 */
	public void deleteCompletedTask(Task task) throws IOException {
		assert task != null;
		
		completedTemp.deleteFromTemp(task);
		lastAction = TASK_COMPLETED;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void clearAllPendingTasks() throws IOException {	
		pendingTemp.clearTemp();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void clearUpcomingTasks() throws IOException {
		pendingTemp.clearUpcoming();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void clearFloatingTasks() throws IOException {
		pendingTemp.clearFloating();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void clearOverdueTasks() throws IOException {
		pendingTemp.clearOverdue();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void clearCompletedTasks() throws IOException {		
		completedTemp.clearTemp();
		lastAction = TASK_COMPLETED;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void sortPendingByTaskName() throws IOException {
		pendingTemp.sortByTaskName();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void sortPendingByTime() throws IOException {
		pendingTemp.sortByTime();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void sortPendingByPriority() throws IOException {
		pendingTemp.sortByPriority();
		lastAction = TASK_PENDING;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void sortCompletedByTaskName() throws IOException {
		completedTemp.sortByTaskName();
		lastAction = TASK_COMPLETED;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void sortCompletedByTime() throws IOException {
		completedTemp.sortByTime();
		lastAction = TASK_COMPLETED;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void sortCompletedByPriority() throws IOException {
		completedTemp.sortByPriority();
		lastAction = TASK_COMPLETED;
	}
	
	/**
	 * 
	 * @param task
	 * @throws IOException
	 */
	public void moveTaskToComplete(Task task) throws IOException {
		assert task != null;
		
		pendingTemp.deleteFromTemp(task);
		
		Task taskCopy = new Task(task.getTask(), task.getTime(), task.getPriority(), 
				task.getType(), TaskStatus.COMPLETED);
		
		taskCopy.setLastModified(true);	
		completedTemp.writeToTemp(taskCopy);	
		lastAction = TASK_BOTH;
	}
	
	/**
	 * 
	 * @param task
	 * @throws IOException
	 */
	public void moveTaskToPending(Task task) throws IOException {
		assert task != null;
		
		completedTemp.deleteFromTemp(task);
		
		Task taskCopy = new Task(task.getTask(), task.getTime(), task.getPriority(), 
				task.getType(), determineStatus(task.getTime()));
		
		taskCopy.setLastModified(true);
		pendingTemp.writeToTemp(taskCopy);		
		lastAction = TASK_BOTH;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void undo() throws IOException {
		if(lastAction == TASK_PENDING) {
			pendingTemp.undoPrevious();
		}
		else if(lastAction == TASK_COMPLETED) {
			completedTemp.undoPrevious();
		}
		else if(lastAction == TASK_BOTH) {
			pendingTemp.undoPrevious();
			completedTemp.undoPrevious();
		}
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void redo() throws IOException {
		if(lastAction == TASK_PENDING) {
			pendingTemp.redoPrevious();
		}
		else if(lastAction == TASK_COMPLETED) {
			completedTemp.redoPrevious();
		}
		else if(lastAction == TASK_BOTH) {
			pendingTemp.redoPrevious();
			completedTemp.redoPrevious();
		}
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	public ArrayList<Task> showAllPendingByDate(Date date) {
		return pendingTemp.showAllByDate(date);
	}
	
	/**
	 * 
	 * @param priority
	 * @return
	 */
	public ArrayList<Task> showAllPendingByPriority(PriorityLevel priority) {
		return pendingTemp.showAllByPriority(priority);
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	public ArrayList<Task> showAllCompletedByDate(Date date) {
		return completedTemp.showAllByDate(date);
	}
	
	/**
	 * 
	 * @param priority
	 * @return
	 */
	public ArrayList<Task> showAllCompletedByPriority(PriorityLevel priority) {
		return completedTemp.showAllByPriority(priority);
	}
	
	/**
	 * 
	 * @param newValue
	 * @return
	 */
	public ArrayList<Task> searchMatchPending(String newValue) {
		return pendingTemp.searchMatch(newValue);
	}
	
	/**
	 * 
	 * @param newValue
	 * @return
	 */
	public ArrayList<Task> searchMatchCompleted(String newValue) {
		return completedTemp.searchMatch(newValue);
	}
	
	/**
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void moveToLocation(String path) throws IOException {
		assert path != null;
		
		pendingTemp.moveToLocation(path);
	}

	/**
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void loadFromFile(String path) throws IOException {
		assert path != null;
		
		pendingTemp.loadFromFile(path);
	}
	
	/**
	 * 
	 * @param path
	 * @throws Exception
	 */
	public void saveToLocation(String path) throws Exception {
		assert path != null;
		
		pendingTemp.saveToLocation(path);
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 * @throws IOException
	 */
	public ArrayList<Task> checkOverdue(Date date) throws IOException {
		return pendingTemp.checkOverdue(date);
	}
	
	/*
	 * 
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