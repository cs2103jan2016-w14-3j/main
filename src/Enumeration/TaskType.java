package Enumeration;

/**
 * This enum categorizes task type into 4 categories as below.
 * @author Ouyang Danwen
 *
 */
public enum TaskType {
	
	DEADLINE("deadline"), EVENT("one-time event"),DURATION("duration");

	private final String type;

	/**
	 * @param type
	 */
	TaskType(String type) {
		assert type != null;
		this.type = type;
	}

	/**
	 * @return task type in string
	 */
	public String getType() {
		return type;
	}
}
