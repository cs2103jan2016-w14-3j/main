package main.java.data;

public enum TASK_TYPE {
	
	DEADLINE_TASK("deadline"), EVENT_TASK("one-time event"), RECURRING_TASK("recurring"),
	DURATION_TASK("duration");

	private final String type;

	TASK_TYPE(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
