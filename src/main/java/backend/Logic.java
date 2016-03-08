package main.java.backend;




import java.util.ArrayList;
import java.util.Date;
import main.java.backend.*;
import java.util.List;
import java.util.Scanner;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import main.java.data.*;

public class Logic {

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

	private final String WELCOME_MESSAGE = "Welcome to Flashpoint!";
	private Task task;
	private Scanner scanner = new Scanner(System.in);
	private TempStorage temp;

	public Logic() {
		try {
			temp = new TempStorage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void showWelcomeMessage() {
		System.out.println(WELCOME_MESSAGE);
	}



	public static void main(String[] args) throws Exception
	{

	}

	public void initLogic() throws Exception{
		Logic logic = new Logic();


	}

	public Task handleUserCommand(String userInput) throws Exception {

		CommandParser parser = new CommandParser();
		Command command = new Command(userInput);
		command = parseCommand(parser, command);
		task = createTask(command);
		ArrayList<Task> result;


		if (command.isCommand(ADD_COMMAND)) {
			handleAddCommand(task);
		}

		else if (command.isCommand(DELETE_COMMAND)) {
			result = handleDeleteCommand(task);
		}

		else if (command.isCommand("DISPLAY_COMMAND")) {
			result = handleDisplayCommand();
		}

		else if (command.isCommand("EDIT_COMMAND")) {
			result = handleEditCommand(task);
		}
		//System.out.println(task.getPriority() + " " + task.getTime() + " " + task.getTask());
		//quitOnExitCommand(command);

		return task;

	}
	
	private void handleAddCommand(Task task) {
		temp.writeToTemp(task);
	}
	
	private ArrayList<Task> handleDeleteCommand(Task task) {
		return temp.searchTemp(task);
	}
	
	private ArrayList<Task> handleDisplayCommand() {
		return temp.displayTemp();
	}
	
	private ArrayList<Task> handleEditCommand(Task task) {
		Task Task_A;
		Task Task_B;
		Task_A = new Task(task.getTask(),task.getTime(),task.getPriority());
		return temp.editToTemp();
	}
	


	private Task createTask(Command command) {
		return command.createTask();
	}

	private Command parseCommand(CommandParser parser, Command command) {
		return parser.parseCommand(command);
	}

	private void quitOnExitCommand(Command command) {
		if (command.getType() == "exit") {
			System.exit(0);
		}
	}


}
