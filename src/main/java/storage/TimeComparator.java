package main.java.storage;

import java.util.Comparator;

import main.java.data.Task;

public class TimeComparator implements Comparator<Task>{

	/*
	 * Compares in the order: Time, Name, Priority
	 */
	@Override
	public int compare(Task task1, Task task2) {

		//		if((task1.getTime().isEmpty()) || (task2.getTime().isEmpty()) || task1.getTime().equals(task2.getTime())) {
		//			
		//			if(task1.getPriority() != null || task2.getPriority() != null || task1.getTask().equalsIgnoreCase(task2.getTask())) {
		//				return task1.getPriority().compareTo(task2.getPriority());
		//			}
		//			else {
		//				return task1.getTask().compareTo(task2.getTask());
		//			}
		//		}	
		//		else {	
		//			return task1.getTime().get(0).compareTo(task2.getTime().get(0));
		//		}
		//	}
		if(!task1.getTime().isEmpty() && task2.getTime().isEmpty()) {
			return 1;
		}
		else if(task1.getTime().isEmpty() && !task2.getTime().isEmpty()) {
			return -1;
		}
		else if(!task1.getTime().isEmpty() && !task2.getTime().isEmpty()) {
			return task1.getTime().get(0).compareTo(task2.getTime().get(0));
		}
		else
			return 1;
	}
}
