//@@author A0125084L
package main.java.storage;

import java.util.Comparator;

import main.java.data.Task;

/**
 * A Comparator for Task objects that can sort tasks based on their name
 * @author Hou Bo Wen
 *
 */
public class TaskNameComparator implements Comparator<Task>{

	@Override
	public int compare(Task task1, Task task2) {

		return task1.getTask().compareToIgnoreCase(task2.getTask());
	}
}
//@@author A0125084L