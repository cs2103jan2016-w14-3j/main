package main.java.storage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import main.java.data.Task;

public class TempStorage {

	private static final double STR_SIMILARITY_THRESHOLD = 0.55;
	private ArrayList<Task> taskList;
	private Storage storage;
	private Stack< ArrayList<Task> > undoStack; 
	
	public TempStorage() {
		
		storage = new Storage();
		undoStack =  new Stack< ArrayList<Task> >(); 
		taskList = retrieveListFromFile();
		undoStack.push(taskList);
	}

	public void changeDirectory(String path) {
		assert path != null;
		
		storage.changeDirectory(path);
	}
	
	public Boolean renameFile(String name) {
		assert name != null;
		
		Boolean isSuccess = storage.renameFile(name);
		return isSuccess;
	}
	
	public void writeToTemp(Task task) {
		assert task != null;
		
		taskList.add(task);
		undoStack.push(taskList);
		storage.writeToFile(task);
	}
	
	public ArrayList<Task> displayTemp() {
	
		return taskList;
	}
	
	public void editToTemp(Task taskToEdit, Task editedTask) {
		assert taskToEdit != null;
		assert taskToEdit.getTaskID() >= 0;
		
		taskList.set(taskToEdit.getTaskID(), editedTask);
		undoStack.push(taskList);
		storage.editToFile(taskToEdit.getTaskID(), editedTask);
	}
	
	public void deleteFromTemp(Task task) {
		assert task != null;
		assert task.getTaskID() >= 0;
		
		taskList.remove(task.getTaskID());
		undoStack.push(taskList);
		storage.deleteFromFile(task.getTaskID());
	}
	
	public void clearTemp() {
		
		taskList.clear();
		undoStack.push(taskList);
		storage.clearFile();
	}
	
	public ArrayList<Task> searchTemp(Task task) {
		assert task != null;
		
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
	
	public void sortByTaskName() {
		
		Collections.sort(taskList, new TaskNameComparator());
		undoStack.push(taskList);
		storage.copyAllToFile((taskList));
	}
	
	public void sortByTime() {
		
		Collections.sort(taskList, new TimeComparator());
		undoStack.push(taskList);
		storage.copyAllToFile((taskList));
	}
	
	public void sortByPriority() {
		
		Collections.sort(taskList, new PriorityComparator());
		undoStack.push(taskList);
		storage.copyAllToFile((taskList));
	}
	
	public void undo() {
		if(!undoStack.empty()) {
			undoStack.pop();
			storage.copyAllToFile(undoStack.peek());
		}
	}
	
	private Boolean stringCompare(String taskInList, String taskToCheck) {
		
		Boolean isSimilar = (StringUtils.getJaroWinklerDistance(taskInList, taskToCheck) >= STR_SIMILARITY_THRESHOLD);
		Boolean isContainExactWord = false;
		
		if(taskInList.contains(taskToCheck) || taskToCheck.contains(taskInList)) {
			isContainExactWord = true;
		}
		return (isSimilar || isContainExactWord);
		
	}
	
	private ArrayList<Task> retrieveListFromFile() {
		ArrayList<Task> list = storage.readFromFile();
		
		return list;
	}
}
