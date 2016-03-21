package main.java.data;

public enum PRIORITY_LEVEL {
	HIGH("high"), MEDIUM("medium"), LOW("low");
	
	private final String type;
	
	PRIORITY_LEVEL(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
}
