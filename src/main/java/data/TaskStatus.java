package main.java.data;

public enum TaskStatus {
	OVERDUE("overdue"), UPCOMING("upcoming"), FLOATING("floating"), COMPLETED("completed");

	private final String type;

	TaskStatus(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
