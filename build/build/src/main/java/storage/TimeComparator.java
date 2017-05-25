//@@author A0125084L
package main.java.storage;

import java.util.Comparator;

import main.java.data.Task;

/**
 * A Comparator for Task objects that can sort tasks based on their time
 * @author Hou Bo Wen
 *
 */
public class TimeComparator implements Comparator<Task>{

	@Override
	public int compare(Task task1, Task task2) {

		//Time is specified for both tasks
		if (!task1.getTime().isEmpty() && !task2.getTime().isEmpty()) {
			if (task1.getStatus().compareTo(task2.getStatus()) == 0) {

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
		//Time is specified for only task1
		else if (!task1.getTime().isEmpty()) {
			if (task1.getStatus().compareTo(task2.getStatus()) == 0) {
				return 1;
			}
			else {
				return task1.getStatus().compareTo(task2.getStatus());
			}
		}
		//Time is specified for only task2
		else if (!task2.getTime().isEmpty()) {
			if (task1.getStatus().compareTo(task2.getStatus()) == 0) {
				return -1;
			}
			else {
				return task1.getStatus().compareTo(task2.getStatus());
			}
		}
		//Time is not specified for both tasks
		else {
			return task1.getTask().compareTo(task2.getTask());

		}
	}
}
//@@author A0125084L