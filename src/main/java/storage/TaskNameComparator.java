package main.java.storage;

import java.util.Comparator;

import main.java.data.Task;

public class TaskNameComparator implements Comparator<Task>{

	/*
	 * Compares in the order: Name, Time, Priority
	 */
	@Override
	public int compare(Task task1, Task task2) {
		
//		boolean isTimePresent = false, isPriorityPresent = false; 
//		
//		if(!task1.getTime().isEmpty() && !task2.getTime().isEmpty()) {
//			isTimePresent = true;
//		}
//		
//		if(task1.getPriority() != null && task2.getPriority() != null) {
//			isPriorityPresent = true;
//		}
//	
//		if(task1.getTask().equalsIgnoreCase(task2.getTask())) {
//			
//			if(task1.getTime().equals(task2.getTime())) {
//				return task1.getPriority().compareTo(task2.getPriority());
//			}
//			else {
//				return task1.getTime().get(0).compareTo(task2.getTime().get(0));
//			}
//		}	
//		else {			
//			return task1.getTask().compareToIgnoreCase(task2.getTask());
//		}
//	}
		return task1.getTask().compareToIgnoreCase(task2.getTask());
	}
}
