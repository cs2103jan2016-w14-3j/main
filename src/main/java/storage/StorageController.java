package main.java.storage;

import java.util.ArrayList;

import main.java.data.Task;

public class StorageController {

	private static final int PENDING_TASK = 0;
	private static final int COMPLETED_TASK = 1;
	private static final int BOTH_TYPE = 2;
	
	private PendingTaskTempStorage pendingTemp;
	private CompletedTaskTempStorage completedTemp;
	private PendingTaskPermStorage pendingPerm;
	private CompletedTaskPermStorage completedPerm;
	private int lastAction;

	public StorageController() {	
		pendingTemp = new PendingTaskTempStorage();
		completedTemp = new CompletedTaskTempStorage();
		pendingPerm = new PendingTaskPermStorage();
		completedPerm = new CompletedTaskPermStorage();
	}

	public void addTask(Task task) {
		assert task != null;
		pendingTemp.writeToTemp(task);
		lastAction = PENDING_TASK;
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
		pendingTemp.editToTemp(taskToEdit, editedTask);
		lastAction = PENDING_TASK;
	}
	
	public void editCompletedTask(Task taskToEdit, Task editedTask) {
		assert taskToEdit != null;
		assert editedTask != null;
		completedTemp.editToTemp(taskToEdit, editedTask);
		lastAction = COMPLETED_TASK;
	}
	
	public void deletePendingTask(Task task) {
		assert task != null;
		pendingTemp.deleteFromTemp(task);
		lastAction = PENDING_TASK;
	}
	
	public void deleteCompletedTask(Task task) {
		assert task != null;
		completedTemp.deleteFromTemp(task);
		lastAction = COMPLETED_TASK;
	}
	
	public void clearPendingTasks() {	
		pendingTemp.clearTemp();
		lastAction = PENDING_TASK;
	}
	public void clearCompletedTasks() {		
		completedTemp.clearTemp();
		lastAction = COMPLETED_TASK;
	}
	
	public void sortPendingByTaskName() {
		pendingTemp.sortByTaskName();
		lastAction = PENDING_TASK;
	}
	
	public void sortPendingByTime() {
		pendingTemp.sortByTime();
		lastAction = PENDING_TASK;
	}
	
	public void sortPendingByPriority() {
		pendingTemp.sortByPriority();
		lastAction = PENDING_TASK;
	}
	
	public void sortCompletedByTaskName() {
		completedTemp.sortByTaskName();
		lastAction = COMPLETED_TASK;
	}
	
	public void sortCompletedByTime() {
		completedTemp.sortByTime();
		lastAction = COMPLETED_TASK;
	}
	
	public void sortCompletedByPriority() {
		completedTemp.sortByPriority();
		lastAction = COMPLETED_TASK;
	}
	

	public void moveTaskToComplete(Task task) {
		assert task != null;
		pendingTemp.deleteFromTemp(task);
		completedTemp.writeToTemp(task);
		lastAction = BOTH_TYPE;
	}
	
	public void undo() {
		if(lastAction == PENDING_TASK) {
			pendingTemp.undoPrevious();
		}
		else if(lastAction == COMPLETED_TASK) {
			completedTemp.undoPrevious();
		}
		else if(lastAction == BOTH_TYPE) {
			pendingTemp.undoPrevious();
			completedTemp.undoPrevious();
		}
	}
	
	public void redo() {
		if(lastAction == PENDING_TASK) {
			pendingTemp.redoPrevious();
		}
		else if(lastAction == COMPLETED_TASK) {
			completedTemp.redoPrevious();
		}
		else if(lastAction == BOTH_TYPE) {
			pendingTemp.redoPrevious();
			completedTemp.redoPrevious();
		}
	}
	
	public ArrayList<Task> searchMatch(String oldChar, String newChar) {
		return pendingTemp.searchMatch(oldChar, newChar);
	}
	
	public void saveToFile(String path) {
		assert path != null;
		
		pendingTemp.saveToFile(path);
		//completedTemp.saveToFile(path.substring(0, path.lastIndexOf("\\")+1) + "Completed tasks.txt");
	}

	public void loadFromFile(String path) {
		assert path != null;
		
		pendingTemp.loadFromFile(path);
	}
	
	public Boolean renameFile(String name) {
		assert name != null;
		
		Boolean isSuccess = pendingPerm.renameFile(name);
		return isSuccess;
	}
}
