package main.java.storage;

import java.util.Comparator;

import main.java.data.Task;

public class TimeComparator implements Comparator<Task>{

	@Override
	public int compare(Task task1, Task task2) {

		//both have time
		if(!task1.getTime().isEmpty() && !task2.getTime().isEmpty()) {
			if (task1.getStatus().compareTo(task2.getStatus())  == 0) {

				if (task1.getTime().get(0).equals(task2.getTime().get(0))) {
					return task1.getTask().compareTo(task2.getTask());
				}
				else {
					return task1.getTime().get(0).compareTo(task2.getTime().get(0));
				}
			}
			else {
				return task1.getStatus().compareTo(task2.getStatus());
			}
		}
		//only task1 has time
		else if(!task1.getTime().isEmpty()) {
			if (task1.getStatus().compareTo(task2.getStatus())  == 0) {
				return 1;
			}
			else {
				return task1.getStatus().compareTo(task2.getStatus());
			}
		}
		//only task2 has time
		else if(!task2.getTime().isEmpty()) {
			if (task1.getStatus().compareTo(task2.getStatus())  == 0) {
				return -1;
			}
			else {
				return task1.getStatus().compareTo(task2.getStatus());
			}
		}
		//neither has time
		else {
			return task1.getTask().compareTo(task2.getTask());

		}
	}
}
