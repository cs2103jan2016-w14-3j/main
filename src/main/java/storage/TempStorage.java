package main.java.storage;

import java.util.ArrayList;
import java.util.Stack;

import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.data.Task;
import main.java.gui.TasksItemController;

public class TempStorage {

	private ArrayList<Task> taskList;
	private ArrayList<Task> tempList;
	private Stack< ArrayList<Task> > undoStack;
	private PermStorage permStorage;
	
	private static final String SPACE = " ";
	private static final String SPLIT = "\\s+";
	private static final int COMMAND_INDEX = 0;
	private boolean isFeedback = false;
	
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

	public ArrayList<Task> searchMatch(String oldValue, String newValue) {

		ArrayList<Task> searchResult = new ArrayList<Task>();
		String[] fragments = null;
		fragments = newValue.split(SPLIT);
		boolean isEdit = fragments[COMMAND_INDEX].equalsIgnoreCase("edit");
		boolean isDelete = fragments[COMMAND_INDEX].equalsIgnoreCase("delete");
		boolean isSearch = fragments[COMMAND_INDEX].equalsIgnoreCase("search");
         
		if(fragments.length==1){
			searchResult = taskList;
		}
		
		if ((isEdit || isDelete || isSearch) && fragments.length > 1) {
			newValue = fragments[1];		
			String[] parts = null;
			parts = newValue.toLowerCase().split(SPACE);
			ObservableList<TasksItemController> temp = FXCollections.observableArrayList();
            searchResult.clear();
            
			for (Task task : taskList) {
				boolean match = true;
				String taskMatch = task.getTask() + task.getPriority() + task.getTime();
				for (String part : parts) {
					String withoutComma = part.substring(0,part.length()-1);
					if(taskMatch.toLowerCase().contains(withoutComma)&& newValue.contains(",")){
						match = true;
						break;
					}
					if (!taskMatch.toLowerCase().contains(part)) {
						match = false;
						break;
					}
				}
				if (match) {
					temp.add(new TasksItemController(task));
					searchResult.add(task);
				}
			}
			
		 }
		return searchResult;
		
	}
}
