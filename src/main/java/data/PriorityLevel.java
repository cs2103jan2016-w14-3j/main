package main.java.data;


/**
 * This enum categorizes the priority level of task into 3 types as below.
 * @author Ouyang Danwen
 *
 */
public enum PriorityLevel {
	HIGH("high"), MEDIUM("medium"), LOW("low"), NOT_SPECIFIED("not specified");
	
	/* attributes of the class */
	private final String type;
	
	/**
	 * @param type
	 */
	PriorityLevel(String type) {
		assert type != null;
		this.type = type;
	}
	
	/**
	 * @return type of the priority level
	 */
	public String getType() {
		return type;
	}
	
}
