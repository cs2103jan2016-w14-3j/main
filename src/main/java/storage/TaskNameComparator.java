package main.java.storage;

import java.util.Comparator;

import main.java.data.Task;

public class TaskNameComparator implements Comparator<Task>{

	@Override
	public int compare(Task task1, Task task2) {

		return task1.getTask().compareToIgnoreCase(task2.getTask());
	}
}
