package main.java.data;

import java.util.Date;
import java.util.List;

public class Task {
	
	private String task;
	private List<Date> time;
	private TASK_NATURE type;
	private PRIORITY_LEVEL priority;
	private TASK_STATUS status;
	
	public Task(String task, List<Date> time, PRIORITY_LEVEL priority, 
			TASK_NATURE type, TASK_STATUS status) {
		this.task = task;
		this.time = time;
		this.priority = priority;
		this.type = type;
		this.status = status;
		
	}
	
	public TASK_NATURE getType() {
		return type;
	}
	
	public void setType(TASK_NATURE type) {
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

	public PRIORITY_LEVEL getPriority() {
		return priority;
	}

	public void setPriority(PRIORITY_LEVEL priority) {
		this.priority = priority;
	}
	
	public void setStatus(TASK_STATUS status) {
		this.status = status;
	}
	public TASK_STATUS getStatus() {
		return status;
	}
}

