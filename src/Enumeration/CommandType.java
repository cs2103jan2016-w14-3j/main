package Enumeration;

/**
 * This enum categorizes the different command types.
 * @author Ouyang Danwen
 *
 */
public enum CommandType {
	
	ADD("add"), EDIT("edit"), DELETE("delete"), DELETE_COMPLETE("deleteComplete"), UNDO("undo"), 
	REDO("redo"),SORT("sort"), SORT_COMPLETE("sortComplete"), MOVE("move"), SEARCH("search"), 
	MARK("mark"), UNMARK("unmark"), CLEAR_UPCOMING("clearUpcoming"), 
	CLEAR_COMPLETE("clearComplete"), CLEAR_OVERDUE("clearOverdue"), 
	CLEAR_FLOATING("clearFloating"), CLEAR_ALL("clearAll"), SHOW("show"),
	SHOW_COMPLETE("showComplete"), SWITCH("switch"), SAVE("save"), OPEN("open"),
	INVALID("invalid");
	
	/* attributes of the class */
	private final String type; 
	
	/**
	 * @param type
	 */
	CommandType(String type) { 
		assert type != null;
		this.type = type; 
	}  

	/**
	 * @return the type of the command in string
	 */
	public String getType() { 
		return this.type; 
	} 
}




