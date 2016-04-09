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

public class StorageController {

	private static final int TASK_PENDING = 0;
	private static final int TASK_COMPLETED = 1;
	private static final int TASK_BOTH = 2;
	
	private PendingTaskTempStorage pendingTemp;
	private CompletedTaskTempStorage completedTemp;
	private int lastAction;

	public StorageController() {	
		pendingTemp = new PendingTaskTempStorage();
		completedTemp = new CompletedTaskTempStorage();
	}

	public void addTask(Task task) {
		assert task != null;
		
		task.setLastModified(true);
		pendingTemp.writeToTemp(task);
		lastAction = TASK_PENDING;
	}
	
	public ArrayList<Task> displayPendingTasks() {
		return pendingTemp.displayTemp();
	}
	
	public ArrayList<Task> displayCompletedTasks() {
		return completedTemp.displayTemp();
	}
	
	public void editPendingTask(Task taskToEdit, Task editedTask) {
		assert taskToEdit != null;
		assert editedTask != null;
		
		editedTask.setLastModified(true);
		pendingTemp.editToTemp(taskToEdit, editedTask);
		lastAction = TASK_PENDING;
	}
	
	public void editCompletedTask(Task taskToEdit, Task editedTask) {
		assert taskToEdit != null;
		assert editedTask != null;
		
		editedTask.setLastModified(true);
		completedTemp.editToTemp(taskToEdit, editedTask);
		lastAction = TASK_COMPLETED;
	}
	
	public void deletePendingTask(Task task) {
		assert task != null;
		
		pendingTemp.deleteFromTemp(task);
		lastAction = TASK_PENDING;
	}
	
	public void deleteCompletedTask(Task task) {
		assert task != null;
		
		completedTemp.deleteFromTemp(task);
		lastAction = TASK_COMPLETED;
	}
	
	public void clearPendingTasks() {	
		pendingTemp.clearTemp();
		lastAction = TASK_PENDING;
	}
	
	public void clearUpcomingTasks() {
		pendingTemp.clearUpcoming();
		lastAction = TASK_PENDING;
	}
	
	public void clearFloatingTasks() {
		pendingTemp.clearFloating();
		lastAction = TASK_PENDING;
	}
	
	public void clearOverdueTasks() {
		pendingTemp.clearOverdue();
		lastAction = TASK_PENDING;
	}
	
	public void clearCompletedTasks() {		
		completedTemp.clearTemp();
		lastAction = TASK_COMPLETED;
	}
	
	public void sortPendingByTaskName() {
		pendingTemp.sortByTaskName();
		lastAction = TASK_PENDING;
	}
	
	public void sortPendingByTime() {
		pendingTemp.sortByTime();
		lastAction = TASK_PENDING;
	}
	
	public void sortPendingByPriority() {
		pendingTemp.sortByPriority();
		lastAction = TASK_PENDING;
	}
	
	public void sortCompletedByTaskName() {
		completedTemp.sortByTaskName();
		lastAction = TASK_COMPLETED;
	}
	
	public void sortCompletedByTime() {
		completedTemp.sortByTime();
		lastAction = TASK_COMPLETED;
	}
	
	public void sortCompletedByPriority() {
		completedTemp.sortByPriority();
		lastAction = TASK_COMPLETED;
	}
	
	public void moveTaskToComplete(Task task) {
		assert task != null;
		
		pendingTemp.deleteFromTemp(task);
		
		Task taskCopy = new Task(task.getTask(), task.getTime(), task.getPriority(), 
				task.getType(), TaskStatus.COMPLETED);
		
		taskCopy.setLastModified(true);	
		completedTemp.writeToTemp(taskCopy);	
		lastAction = TASK_BOTH;
	}
	
	public void moveTaskToPending(Task task) {
		assert task != null;
		
		completedTemp.deleteFromTemp(task);
		
		Task taskCopy = new Task(task.getTask(), task.getTime(), task.getPriority(), 
				task.getType(), determineStatus(task.getTime()));
		
		taskCopy.setLastModified(true);
		pendingTemp.writeToTemp(taskCopy);		
		lastAction = TASK_BOTH;
	}
	
	public void undo() {
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
	
	public void redo() {
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
	
	public ArrayList<Task> showAllPendingByDate(Date date) {
		return pendingTemp.showAllByDate(date);
	}
	
	public ArrayList<Task> showAllPendingByPriority(PriorityLevel priority) {
		return pendingTemp.showAllByPriority(priority);
	}
	
	public ArrayList<Task> showAllCompletedByDate(Date date) {
		return completedTemp.showAllByDate(date);
	}
	
	public ArrayList<Task> showAllCompletedByPriority(PriorityLevel priority) {
		return completedTemp.showAllByPriority(priority);
	}
	
	public ArrayList<Task> searchMatchPending(String newValue) {
		return pendingTemp.searchMatch(newValue);
	}
	
	public ArrayList<Task> searchMatchCompleted(String newValue) {
		return completedTemp.searchMatch(newValue);
	}
	
	public void moveToLocation(String path) throws IOException {
		assert path != null;
		
		pendingTemp.moveToLocation(path);
	}

	public void loadFromFile(String path) {
		assert path != null;
		
		pendingTemp.loadFromFile(path);
	}
	
	public void saveToLocation(String path) throws Exception {
		assert path != null;
		
		pendingTemp.saveToLocation(path);
	}
	
	public ArrayList<Task> checkOverdue(Date date) {
		return pendingTemp.checkOverdue(date);
	}
	
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