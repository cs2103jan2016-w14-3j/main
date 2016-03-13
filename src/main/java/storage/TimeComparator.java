package main.java.storage;

import java.util.Comparator;

import main.java.data.Task;

public class TimeComparator implements Comparator<Task>{

	/*
	 * Compares in the order: Time, Name, Priority
	 */
	@Override
	public int compare(Task task1, Task task2) {
		
		if(task1.getTime().equals(task2.getTime())) {
			
			if(task1.getTask().equalsIgnoreCase(task2.getTask())) {
				return task1.getPriority().compareTo(task2.getPriority());
			}
			else {
				return task1.getTask().compareTo(task2.getTask());
			}
		}	
		else {	
			return task1.getTime().compareTo(task2.getTime());
		}
	}
}
