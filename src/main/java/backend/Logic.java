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
		Logic logic = new Logic();
		ArrayList<Task> list = logic.handleUserCommand("edit okay, no -9pm #h");
	}

	public void initLogic() throws Exception{
		Logic logic = new Logic();


	}

	public ArrayList<Task> handleUserCommand(String userInput) throws Exception {
		assert userInput != null;

		CommandParser parser = new CommandParser();
		Command command = new Command(userInput);
		command = parseCommand(parser, command);
		task = createTask(command);
		ArrayList<Task> result = null;
		

		if (command.isCommand(ADD_COMMAND)) {
			handleAddCommand(task);
			result = temp.displayTemp();
			
			//result.add(task); // prepare the list to pass to UI
		}

		else if (command.isCommand(DELETE_COMMAND)) {
			result = handleDeleteCommand(task);
			// return the list to UI

			temp.deleteFromTemp(result.get(0));


		}


		else if (command.isCommand(DISPLAY_COMMAND)) {
			result = handleDisplayCommand();
		}

		else if (command.isCommand(EDIT_COMMAND)) {
			//System.out.println("text");
			handleEditCommand(task);
			
			//return the list to UI to select

			//		    temp.editToTemp(result.get(0), task);

			//		    result = temp.displayTemp();
			//		    //return the list to UI to display
			//		    
			//		    
			//			
			//			
			//				
			//			
		}
		
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

	private void handleEditCommand(Task task) throws Exception {
		Task task_A;
		Task task_B;
		String toDo_A, toDo_B;
		String time_A, time_B;
		String priority_A, priority_B;

		toDo_A = task.getTask().split(",")[0].trim();
		toDo_B = task.getTask().split(",")[1].trim();
		time_A = task.getTime().split(",")[0].trim();
		time_B = task.getTime().split(",")[1].trim();
		priority_A = task.getPriority().split(",")[0].trim();
		priority_B = task.getPriority().split(",")[1].trim();


		task_A = new Task(toDo_A, time_A, priority_A);
		task_B = new Task(toDo_B, time_B, priority_B);


		ArrayList<Task> result = temp.searchTemp(task_A);
		temp.editToTemp(result.get(0), task_B);
	}



	private Task createTask(Command command) {
		assert command != null;
		return command.createTask();
	}

	private Command parseCommand(CommandParser parser, Command command) {
		assert command != null;
		return parser.parseCommand(command);
	}

	private void quitOnExitCommand(Command command) {
		if (command.getType() == "exit") {
			System.exit(0);
		}
	}


}