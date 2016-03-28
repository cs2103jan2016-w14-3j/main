package main.java.storage;

import java.util.Comparator;

import main.java.data.Task;

public class PriorityComparator implements Comparator<Task>{

	@Override
	public int compare(Task task1, Task task2) {

		if (task1.getPriority().equals(task2.getPriority())) {
			return task1.getTask().compareTo(task2.getTask());
		}
		else {
			return task1.getPriority().compareTo(task2.getPriority());
		}
	}
}
