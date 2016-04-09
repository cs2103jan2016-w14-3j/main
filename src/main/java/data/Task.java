package main.java.data;

import java.util.Date;
import java.util.List;

public class Task {
	
	private String task;
	private List<Date> time;
	private TaskType type;
	private PriorityLevel priority;
	private TaskStatus status;
	private transient boolean isLastModified;
	
	public Task(String task, List<Date> time, PriorityLevel priority, 
			TaskType type, TaskStatus status) {
		this.task = task;
		this.time = time;
		this.priority = priority;
		this.type = type;
		this.status = status;
		this.isLastModified = false;
		
	}
	
	public TaskType getType() {
		return type;
	}
	
	public void setType(TaskType type) {
		this.type = type;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public List<Date> getTime() {
		return time;
	}

	public void setTime(List<Date> time) {
		this.time = time;
	}

	public PriorityLevel getPriority() {
		return priority;
	}

	public void setPriority(PriorityLevel priority) {
		this.priority = priority;
	}
	
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	public TaskStatus getStatus() {
		return status;
	}
	
	public boolean getLastModified() {
		return this.isLastModified;
	}
	public void setLastModified(boolean isLastModified) {
		this.isLastModified = isLastModified;
	}
}

