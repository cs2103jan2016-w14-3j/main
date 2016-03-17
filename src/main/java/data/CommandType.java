package main.java.data;

public enum CommandType {
	
	ADD("add"), EDIT("edit"), DELETE("delete"), UNDO("undo"), 
	SORT("sort"), CLEAR("clear"), MOVE("move"), SEARCH("search");
	
	private final String type; 
	
	CommandType(String type) { 
		this.type = type; 
	}  

	public String getType() { 
		return this.type; 
	} 
}



