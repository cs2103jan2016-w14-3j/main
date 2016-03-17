package main.java.data;

public class Task {
	
	private String task;
	private String time;
	private String type;
	private String priority;
	private String status;
	
	public Task(String task, String time, String priority, String type, String status) {
		this.task = task;
		this.time = time;
		this.priority = priority;
		this.type = type;
		this.status = status;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return this.status;
	}
	
}

