package main.java.data;


public class Command {

	private String original;
	private String commandType;
	private String commandContent;
	private String[] commandParameters;

	private final String EMPTY_COMMAND = "empty";
	private final String ADD_COMMAND = "add";
	private final String DELETE_COMMAND = "delete";
	private final String SEARCH_COMMAND = "search";
	private final String STORE_COMMAND = "store";
	private final String DISPLAY_COMMAND = "display";
	private final String SORT_COMMAND = "sort";
	private final String CLEAR_COMMAND = "clear";
	private final String EDIT_COMMAND = "edit";
	private final String EXIT_COMMAND = "exit";

	private static final int TASK = 0;
	private static final int TIME = 1;
	private static final int PRIORITY = 2;

	public Command(String command) {
		this.original = command;
		this.commandParameters = new String[5];
	}

	public String getType() {
		return this.commandType;
	}

	public String getContent() {
		return this.commandContent;
	}

	public String getOriginal() {
		return this.original;
	}

	public void setContent(String commandContent) {
		this.commandContent = commandContent;
	}

	public void setType(String commandType) {
		this.commandType = commandType;
	}

	public void setParameters(String[] parameters) {
		this.commandParameters = parameters;
	}


	public Task createTask() {
		Task task = null;

		if (!this.isCommand(DISPLAY_COMMAND)) {

			task = new Task(commandParameters[TASK], 
					commandParameters[TIME], commandParameters[PRIORITY]);

		}

		return task;



	}

	public boolean isCommand(String type) {
		return type.equals(this.getType());
	}

}

