package main.java.data;

public enum COMMAND_TYPE {
	
	ADD("add"), EDIT("edit"), DELETE("delete"), UNDO("undo"), 
	REDO("redo"),
	SORT("sort"), MOVE("move"), SEARCH("search"), 
	MARK("mark"), UNMARK("unmark"),
	CLEAR_UPCOMING("clearUpcoming"), CLEAR_COMPLETE("clearComplete");
	
	private final String type; 
	
	COMMAND_TYPE(String type) { 
		this.type = type; 
	}  

	public String getType() { 
		return this.type; 
	} 
}




