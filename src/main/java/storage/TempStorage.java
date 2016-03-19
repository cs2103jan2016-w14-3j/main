package main.java.storage;

import java.util.ArrayList;
import java.util.Stack;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import main.java.data.Task;

public class TempStorage {

	private ArrayList<Task> taskList;
	private ArrayList<Task> tempList;
	private Stack< ArrayList<Task> > undoStack;
	private PermStorage permStorage;
	
	public TempStorage () {

	}
	
	public TempStorage(PermStorage permStorage) {
		this.permStorage = permStorage;
		taskList = retrieveListFromFile();
		tempList = new ArrayList<Task>(taskList);
		undoStack = new Stack< ArrayList<Task> >();
		undoStack.push(tempList);
	}
	
	public void writeToTemp(Task task) {
		
		taskList.add(task);
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.writeToFile(task);
	}
	
	public ArrayList<Task> displayTemp() {
		return taskList;
	}
	
	public void editToTemp(Task taskToEdit, Task editedTask) {
		
		int indexOfTaskToEdit = searchTemp(taskToEdit);
		taskList.set(indexOfTaskToEdit, editedTask);
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.editToFile(indexOfTaskToEdit, editedTask);
	}
	
	public void deleteFromTemp(Task task) {
		
		int indexOfTaskToDelete = searchTemp(task);
		taskList.remove(taskList.get(indexOfTaskToDelete));
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.deleteFromFile(indexOfTaskToDelete);
	}
	
	public void clearTemp() {
		
		taskList.clear();
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.clearFile();
	}
	
	public void undoPrevious() {
		if(undoStack.size() >= 2) {
			undoStack.pop();
			taskList = new ArrayList<Task>(undoStack.peek());
			permStorage.copyAllToFile(taskList);
		}
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
	
	public void sortByTaskName() {
		
		Collections.sort(taskList, new TaskNameComparator());
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.copyAllToFile((taskList));
	}
	
	public void sortByTime() {
		
		Collections.sort(taskList, new TimeComparator());
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.copyAllToFile((taskList));
	}
	
	public void sortByPriority() {
		
		Collections.sort(taskList, new PriorityComparator());
		tempList = new ArrayList<Task>(taskList);
		undoStack.push(tempList);
		permStorage.copyAllToFile((taskList));
	}
	
	private ArrayList<Task> retrieveListFromFile() {
		ArrayList<Task> list = permStorage.readFromFile();
		
		return list;
	}
}
