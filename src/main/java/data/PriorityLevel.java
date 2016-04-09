package main.java.data;

public enum PriorityLevel {
	HIGH("high"), MEDIUM("medium"), LOW("low"), NOT_SPECIFIED("not specified");
	
	private final String type;
	
	PriorityLevel(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
}
