package main.java.data;

public class Task {
	private String task;
	private String time;
	private String priority;
	
	public Task(String task, String time, String priority) {
		this.task = task;
		this.time = time;
		this.priority = priority;
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
	
}

