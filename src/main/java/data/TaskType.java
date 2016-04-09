package main.java.data;

public enum TaskType {
	
	DEADLINE("deadline"), EVENT("one-time event"),DURATION("duration");

	private final String type;

	TaskType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
