package main.java.logic;




import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import main.java.data.*;
import main.java.storage.TempStorage;

public class Logic {

	private final String ADD_COMMAND = "add";
	private final String DELETE_COMMAND = "delete";
	private final String SEARCH_COMMAND = "search";
	private final String STORE_COMMAND = "store";
	private final String DISPLAY_COMMAND = "display";
	private final String SORT_COMMAND = "sort";
	private final String CLEAR_COMMAND = "clear";
	private final String EDIT_COMMAND = "edit";
	private final String CONFIRM_COMMAND = "confirm";
	private final String UNDO_COMMAND = "undo";

	private Task task;
	private TempStorage temp;

	public Logic() {
		try {
			temp = new TempStorage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception
	{
		Logic logic = new Logic();
		ArrayList<Task> list = logic.handleUserCommand("edit okay, no -9pm #h",null);
	}

	public ArrayList<Task> initLogic() throws Exception{
		Logic logic = new Logic();
		return display();

	}

	public ArrayList<Task> handleUserCommand(String userInput,ArrayList<Task> taskOptions) throws Exception {
		assert userInput != null;

		CommandParser parser = new CommandParser();
		Command command = new Command(userInput);
		command = parseCommand(parser, command);
		task = createTask(command);

		ArrayList<Task> result = effectTask(command, task, taskOptions);

		//quitOnExitCommand(command);

		return result;

	}

	private void handleAddCommand(Task task) throws Exception {
		assert task != null;
		temp.writeToTemp(task);
	}

	private ArrayList<Task> handleDeleteCommand(Task task) {
		assert task != null;
		return temp.searchTemp(task);
	}

	private ArrayList<Task> handleDisplayCommand() {
		return temp.displayTemp();
	}

	private ArrayList<Task> handleEditCommand(Task task) throws Exception {

		return CommandParser.parseEditTask(temp, task);
	}



	private Task createTask(Command command) {
		assert command != null;
		return command.createTask();
	}

	private Command parseCommand(CommandParser parser, Command command) {
		assert command != null;
		return parser.parseCommand(command);
	}

	private ArrayList<Task> effectTask(Command command, Task task, ArrayList<Task> taskOptions) throws NumberFormatException, Exception {

		ArrayList<Task> result = new ArrayList<Task>();

		if (command.isCommand(ADD_COMMAND)) {
			handleAddCommand(task);
			result = temp.displayTemp();
		}

		else if (command.isCommand(CLEAR_COMMAND)){
			temp.clearTemp();
			result = temp.displayTemp();
		}

		else if (command.isCommand(DELETE_COMMAND)) {
			result = handleDeleteCommand(task);
			// return the list to UI

			for (Task temp : result) {
				temp.setShowToUserDelete(true);
			}
		}
		
		else if (command.isCommand(CONFIRM_COMMAND)) {   
			temp.deleteFromTemp(taskOptions.get(Integer.parseInt( task.getTask() )-1) );
			temp.deleteFromTemp(task);
			result = temp.displayTemp();
		}


		else if (command.isCommand(DISPLAY_COMMAND)) {
			result = handleDisplayCommand();
		}

		else if (command.isCommand(EDIT_COMMAND)) {
			result = handleEditCommand(task);
			for (Task temp : result) {
				temp.setShowToUserDelete(true);
			}
		}
		
		else if (command.isCommand(SEARCH_COMMAND)) {
			
		}
		
		else if (command.isCommand(STORE_COMMAND)) {
			Path path = Paths.get(task.getTask());
		}
		
		else if (command.isCommand(SORT_COMMAND)) {
			
		}
		
		else if (command.isCommand(UNDO_COMMAND)) {
				
		}
		

		return result;
	}

	public void delete(Task task) throws Exception {
		temp.deleteFromTemp(task);
	}
	public ArrayList<Task> display()throws Exception{

		ArrayList<Task> result = temp.displayTemp();

		for (Task abc : result) {
			abc.setShowToUserDelete(false);
		}
		return result;
	}
	
	public ArrayList<Task> display1()throws Exception{

		ArrayList<Task> result = temp.displayTemp();
		return result;
	}

	public void edit(ArrayList<Task> result)throws Exception{
		temp.editToTemp(result.get(1), result.get(0));
	}

	public boolean isDeleteCommand(String userInput) {
		return userInput.substring(0,userInput.indexOf(" ")).equalsIgnoreCase(DELETE_COMMAND);
	}

	public boolean isEditCommand(String userInput) {
		return userInput.substring(0,userInput.indexOf(" ")).equalsIgnoreCase(EDIT_COMMAND);
	}

}