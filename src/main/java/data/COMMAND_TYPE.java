package main.java.data;

public enum COMMAND_TYPE {
	
	ADD("add"), EDIT("edit"), DELETE("delete"), DELETE_COMPLETE("deleteComplete"), UNDO("undo"), 
	REDO("redo"),SORT("sort"), MOVE("move"), SEARCH("search"), 
	MARK("mark"), UNMARK("unmark"),CLEAR_UPCOMING("clearUpcoming"), 
	CLEAR_COMPLETE("clearComplete"),CLEAR_OVERDUE("clearOverdue"), 
	CLEAR_FLOATING("clearFloating"),CLEAR_ALL("clearAll"),SHOW("show"),
	SWITCH("switch"), SAVE("save"), INVALID("invalid");
	
	private final String type; 
	
	COMMAND_TYPE(String type) { 
		this.type = type; 
	}  

	public String getType() { 
		return this.type; 
	} 
}




