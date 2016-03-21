package main.java.data;

public enum PRIORITY {


	HIGH("high"), MEDIUM("medium"), LOW("low");

	private final String type; 

	PRIORITY(String type) { 
		this.type = type; 
	}  

	public String getType() { 
		return this.type; 
	} 


	public static void main(String[] args) {
		System.out.println(COMMAND_TYPE.ADD);
	}
}
