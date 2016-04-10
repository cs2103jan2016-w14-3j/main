package main.java.data;

/**
 * This class models the transient task object solely used to parse edit command.
 * @author Ouyang Danwen
 *
 */
public class TransientTask {
	private String task;
	private String time;
	private String type;
	private String priority;
	private String status;
	
	
	/**
	 * @param task
	 * @param time
	 * @param priority
	 * @param type
	 * @param status
	 */
	public TransientTask(String task, String time, String priority, 
			String type, String status) {
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
		
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type
	 */
	public void setType(String type) {
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
	 * @return task time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time
	 */
	public void setTime(String time) {
		assert time != null;
		this.time = time;
	}

	/**
	 * @return task priority
	 */
	public String getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 */
	public void setPriority(String priority) {
		assert priority != null;
		this.priority = priority;
	}
	
	/**
	 * @param status
	 */
	public void setStatus(String status) {
		assert status != null;
		this.status = status;
	}
	
	/**
	 * @return task status
	 */
	public String getStatus() {
		return status;
	}
}
