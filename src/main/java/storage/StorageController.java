package main.java.storage;

import java.util.ArrayList;

import main.java.data.Task;

public class StorageController {

	private static final int PENDING_TASK = 0;
	private static final int COMPLETED_TASK = 1;
	private static final int MOVE_TASK_TO_COMPLETE = 2;
	
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
	}
	
	public void editCompletedTask(Task taskToEdit, Task editedTask) {
		assert taskToEdit != null;
		assert editedTask != null;
		completedTemp.editToTemp(taskToEdit, editedTask);
	}
	
	public void deletePendingTask(Task task) {
		assert task != null;
		pendingTemp.deleteFromTemp(task);
	}
	
	public void deleteCompletedTask(Task task) {
		assert task != null;
		completedTemp.deleteFromTemp(task);
	}
	
	public void clearPendingTasks() {	
		pendingTemp.clearTemp();
	}
	public void clearCompletedTasks() {		
		completedTemp.clearTemp();
	}
	
	public void sortPendingByTaskName() {
		pendingTemp.sortByTaskName();
	}
	
	public void sortPendingByTime() {
		pendingTemp.sortByTime();
	}
	
	public void sortPendingByPriority() {
		pendingTemp.sortByPriority();
	}
	
	public void sortCompletedByTaskName() {
		completedTemp.sortByTaskName();
	}
	
	public void sortCompletedByTime() {
		completedTemp.sortByTime();
	}
	
	public void sortCompletedByPriority() {
		completedTemp.sortByPriority();
	}
	

	public void moveTaskToComplete(Task task) {
		assert task != null;
		pendingTemp.deleteFromTemp(task);
		completedTemp.writeToTemp(task);
	}
	
	public void undo() {
		if(lastAction == PENDING_TASK) {
			pendingTemp.undoPrevious();
		}
		else if(lastAction == COMPLETED_TASK) {
			completedTemp.undoPrevious();
		}
		else if(lastAction == MOVE_TASK_TO_COMPLETE) {
			pendingTemp.undoPrevious();
			completedTemp.undoPrevious();
		}
	}
	
	public void changeDirectory(String path) {
		assert path != null;
		
		pendingPerm.changeDirectory(path);
		completedPerm.changeDirectory(path);
	}
	
	public Boolean renameFile(String name) {
		assert name != null;
		
		Boolean isSuccess = pendingPerm.renameFile(name);
		return isSuccess;
	}
}
