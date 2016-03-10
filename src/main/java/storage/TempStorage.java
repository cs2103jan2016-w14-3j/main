package main.java.storage;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import main.java.data.Task;

public class TempStorage {

	private static final double STR_SIMILARITY_THRESHOLD = 0.55;
	private ArrayList<Task> taskList;
	private Storage storage;
	
	public TempStorage() throws Exception {
		storage = new Storage();
		taskList = retrieveListFromFile();
	}

	public void writeToTemp(Task task) throws Exception {
		assert task != null;
		
		taskList.add(task);
		storage.writeToFile(task);
	}
	
	public ArrayList<Task> displayTemp() {
	
		return taskList;
	}
	
	public void editToTemp(Task taskToEdit, Task editedTask) throws Exception {
		assert taskToEdit.getTaskID() >= 0;
		
		taskList.set(taskToEdit.getTaskID(), editedTask);
		storage.editToFile(taskToEdit.getTaskID(), editedTask);
	}
	
	public void deleteFromTemp(Task task) throws Exception {
		assert task.getTaskID() >= 0;
		
		taskList.remove(task.getTaskID());			
		storage.deleteFromFile(task.getTaskID());
	}
	
	public void clearTemp() throws Exception {
		taskList.clear();
		storage.clearFile();
	}
	
	public ArrayList<Task> searchTemp(Task task) {
		ArrayList<Task> searchResults = new ArrayList<Task>();
		
		for(int i=0; i<taskList.size(); i++) {
			Task thisTask = taskList.get(i);

			if(stringCompare(thisTask.getTask(), task.getTask()) ||
					(!thisTask.getTime().isEmpty() && thisTask.getTime().equals(task.getTime())) ||
					(!thisTask.getPriority().isEmpty() && thisTask.getPriority().equals(task.getPriority()))) {
				thisTask.setTaskID(i);
				searchResults.add(thisTask);
			}
		}
		return searchResults;
	}
	
	public void sortTemp() {
		
	}
	
	public void undo() {
		
	}
	
	private Boolean stringCompare(String taskInList, String taskToCheck) {
		
		Boolean isSimilar = (StringUtils.getJaroWinklerDistance(taskInList, taskToCheck) >= STR_SIMILARITY_THRESHOLD);
		Boolean isContainExactWord = false;
		
		if(taskInList.contains(taskToCheck) || taskToCheck.contains(taskInList)) {
			isContainExactWord = true;
		}
		return (isSimilar || isContainExactWord);
		
	}
	
	private ArrayList<Task> retrieveListFromFile() throws Exception {
		ArrayList<Task> list = storage.readFromFile();
		
		return list;
	}
}
