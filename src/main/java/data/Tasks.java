package main.java.data;

public class Tasks {
	
	private String taskName;
	
	public Tasks(String taskName){
		this.taskName = taskName;
	}

	public String getName() {
		return taskName;
	}
	
	public void setName(String taskName) {
		this.taskName = taskName;
	}

}
