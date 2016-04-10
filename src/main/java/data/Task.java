package main.java.data;

import java.util.Date;
import java.util.List;

/**
 * This class models the task object and handles all related operations.
 * @author Ouyang Danwen
 *
 */
public class Task {
	
	private String task;
	private List<Date> time;
	private TaskType type;
	private PriorityLevel priority;
	private TaskStatus status;
	private transient boolean isLastModified;
	
	
	/**
	 * @param task
	 * @param time
	 * @param priority
	 * @param type
	 * @param status
	 */
	public Task(String task, List<Date> time, PriorityLevel priority, 
			TaskType type, TaskStatus status) {
		assert task != null;
		assert time != null;
		assert priority != null;
		assert type != null;
		assert status != null;
		
		this.task = task;
		this.time = time;
		this.priority = priority;
		this.type = type;
		this.status = status;
		this.isLastModified = false;
		
	}
	
	/**
	 * @return task type
	 */
	public TaskType getType() {
		return type;
	}
	
	/**
	 * @param type
	 */
	public void setType(TaskType type) {
		assert type != null;
		this.type = type;
	}

	/**
	 * @return task name
	 */
	public String getTask() {
		return task;
	}

	/**
	 * @param task
	 */
	public void setTask(String task) {
		assert task != null;
		this.task = task;
	}

	/**
	 * @return time of the task
	 */
	public List<Date> getTime() {
		return time;
	}

	/**
	 * @param time
	 */
	public void setTime(List<Date> time) {
		assert time != null;
		this.time = time;
	}

	/**
	 * @return task priority
	 */
	public PriorityLevel getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 */
	public void setPriority(PriorityLevel priority) {
		assert priority != null;
		this.priority = priority;
	}
	
	/**
	 * @param status
	 */
	public void setStatus(TaskStatus status) {
		assert status != null;
		this.status = status;
	}
	
	/**
	 * @return task status
	 */
	public TaskStatus getStatus() {
		return status;
	}
	
	/**
	 * @return the boolean variable isLastModified
	 */
	public boolean getLastModified() {
		return this.isLastModified;
	}
	
	/**
	 * @param isLastModified
	 */
	public void setLastModified(boolean isLastModified) {
		this.isLastModified = isLastModified;
	}
}

