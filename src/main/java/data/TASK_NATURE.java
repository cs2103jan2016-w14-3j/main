package main.java.data;

public enum TASK_NATURE {
	
	DEADLINE("deadline"), EVENT("one-time event"),DURATION("duration");

	private final String type;

	TASK_NATURE(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
