package main.java.logic;

import java.nio.file.Path;

import java.nio.file.Paths;
import java.util.ArrayList;
import main.java.data.*;
import main.java.parser.*;
import main.java.storage.TempStorage;

public class Logic {

	private static final String ADD_COMMAND = "add";
	private static final String DELETE_COMMAND = "delete";
	private static final String SEARCH_COMMAND = "search";
	private static final String CHANGE_DIRECTORY_COMMAND = "move";
	private static final String SORT_COMMAND = "sort";
	private static final String CLEAR_COMMAND = "clear";
	private static final String EDIT_COMMAND = "edit";
	private static final String UNDO_COMMAND = "undo";
	private static final String DISPLAY_COMMAND = "display";
	private static final String HELP_COMMAND = "help";

	private static final int TASK = 0;

	private static Task task;
	private static TempStorage temp;

	public Logic() {
		try {
			temp = new TempStorage();
		} catch (Exception e) {
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

		CommandDispatcher dispatcher = new CommandDispatcher();
		Command command = new Command(userInput);
		command = parseCommand(dispatcher, command);

		ArrayList<Task> result = executeTask(command, taskOptions);

		//quitOnExitCommand(command);

		return result;

	}

	private void handleAddCommand(Task task) throws Exception {
		assert task != null;
		temp.writeToTemp(task);
	}

	//	private ArrayList<Task> handleDeleteCommand(Task task) {
	//		assert task != null;
	//		return temp.searchTemp(task);
	//	}

	private ArrayList<Task> handleDisplayCommand() {
		return temp.displayTemp();
	}

	private ArrayList<Task> handleEditCommand(Task task) throws Exception {

		return EditCommandParser.parseEditTask(task);
	}



	private Task createTask(Command command) {
		assert command != null;
		return command.createTask();
	}

	private Command parseCommand(CommandDispatcher dispatcher, Command command)throws InvalidInputFormatException {
		assert command != null;
		return dispatcher.parseCommand(command);
	}

	private ArrayList<Task> executeTask(Command command, ArrayList<Task> taskOptions) throws NumberFormatException, Exception {

		ArrayList<Task> result = new ArrayList<Task>();

		if (command.isCommand(ADD_COMMAND)) {

			task = createTask(command);
			if (!task.getTask().equals("")) {
				handleAddCommand(task);
			}
			result = temp.displayTemp();
		}

		else if (command.isCommand(CLEAR_COMMAND)){
			temp.clearTemp();
			result = temp.displayTemp();
		}

		else if (command.isCommand(DELETE_COMMAND)) {
			//result = handleDeleteCommand(task);
			// return the list to UI		
		}

		else if (command.isCommand(DISPLAY_COMMAND)) {
			result = handleDisplayCommand();
		}

		else if (command.isCommand(EDIT_COMMAND)) {
			task = createTask(command);
			result = handleEditCommand(task);

		}

		else if (command.isCommand(SEARCH_COMMAND)) {

		}

		else if (command.isCommand(CHANGE_DIRECTORY_COMMAND)) {
			Path path = Paths.get(command.getParameters()[TASK]);
		}

		else if (command.isCommand(SORT_COMMAND)) {
			if (command.getParameters()[TASK].equalsIgnoreCase("time")) {
				temp.sortByTime();
			}
			
			else if (command.getParameters()[TASK].equalsIgnoreCase("name")) {
				temp.sortByTime();
			}
			
			else if (command.getParameters()[TASK].equalsIgnoreCase("priority")) {
				temp.sortByPriority();
			}
			
			else if (command.getParameters()[TASK].equalsIgnoreCase("type")) {
				
			}
		}

		else if (command.isCommand(UNDO_COMMAND)) {
			temp.undoPrevious();
			//System.out.println("UNDO IS HERE !!!!");
		}


		return result;
	}

	public void delete(Task task) throws Exception {
		temp.deleteFromTemp(task);
	}
	public ArrayList<Task> display()throws Exception{

		ArrayList<Task> result = temp.displayTemp();

		return result;
	}

	public ArrayList<Task> display1()throws Exception{

		ArrayList<Task> result = temp.displayTemp();
		return result;
	}

	public void edit(ArrayList<Task> result)throws Exception{

		temp.editToTemp(result.get(0), result.get(1));
	}

	public boolean isDeleteCommand(String userInput) {
		return userInput.substring(0,userInput.indexOf(" ")).equalsIgnoreCase(DELETE_COMMAND);
	}

	public boolean isEditCommand(String userInput) {
		return userInput.substring(0,userInput.indexOf(" ")).equalsIgnoreCase(EDIT_COMMAND);
	}
	public boolean isDisplayCommand(String userInput) {
		return userInput.substring(0,userInput.length()).equalsIgnoreCase(DISPLAY_COMMAND);
	}

	public Task editedTask(Task temp2) {
		return temp2;
	}
	public boolean isCommand(String commandWord) {
		if(commandWord.equalsIgnoreCase(ADD_COMMAND)||commandWord.equalsIgnoreCase(DISPLAY_COMMAND)||commandWord.equalsIgnoreCase(DELETE_COMMAND)||
				commandWord.equalsIgnoreCase(EDIT_COMMAND)||commandWord.equalsIgnoreCase(SEARCH_COMMAND)||
				commandWord.equalsIgnoreCase(SORT_COMMAND)||commandWord.equalsIgnoreCase(CHANGE_DIRECTORY_COMMAND)||
				commandWord.equalsIgnoreCase(CLEAR_COMMAND)||commandWord.equalsIgnoreCase(UNDO_COMMAND)||commandWord.equalsIgnoreCase(HELP_COMMAND))
		    return true;
		return false;
		
	}

}