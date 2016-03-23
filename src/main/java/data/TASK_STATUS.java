package main.java.data;

public enum TASK_STATUS {
	OVERDUE("overdue"), UPCOMING("upcoming"), COMPLETED("completed");

	private final String type;

	TASK_STATUS(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
