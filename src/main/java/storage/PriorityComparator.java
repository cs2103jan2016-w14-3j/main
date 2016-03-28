package main.java.storage;

import java.util.Comparator;

import main.java.data.Task;

public class PriorityComparator implements Comparator<Task>{

	/*
	 * Compares in the order: Priority, Name, Time
	 */
	@Override
	public int compare(Task task1, Task task2) {
		
//		if(task1.getPriority() == null && task2.getPriority() == null || task1.getPriority().equals(task2.getPriority())) {
//			
//			if(!task1.getTime().isEmpty() || !task2.getTime().isEmpty() || task1.getTask().equalsIgnoreCase(task2.getTask())) {
//				return task1.getTime().get(0).compareTo(task2.getTime().get(0));
//			}
//			else {
//				return task1.getTask().compareToIgnoreCase(task2.getTask());
//			}
//		}
//		else {
//			return task2.getPriority().compareTo(task1.getPriority());
//		}
		
		
		return task1.getPriority().compareTo(task2.getPriority());
	}
}
