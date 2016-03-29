package main.java.data;

public enum PRIORITY_LEVEL {
	HIGH("high"), MEDIUM("medium"), LOW("low"), NOT_SPECIFIED("not specified");
	
	private final String type;
	
	PRIORITY_LEVEL(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
}
