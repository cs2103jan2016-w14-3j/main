package main.java.backend;




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
	private static TempStorage temp;

	public Logic() {

	}

	public void showWelcomeMessage() {
		System.out.println(WELCOME_MESSAGE);
	}

	private static void setupStorageEnvironment() {
		try {
			 temp = new TempStorage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		setupStorageEnvironment();
		Logic logic = new Logic();
		String userInput = logic.receiveUserCommand();
		logic.handleUserCommand(userInput);
	}

	private String receiveUserCommand() {
		return "delete something";
	}

	public Task handleUserCommand(String userInput) {

		CommandParser parser = new CommandParser();
		Command command = new Command(userInput);
		parseCommand(parser, command);
		//task = createTask(command);
		temp.addNewCommand(command);
		

		/*if (task == null) {//display command

		}

		else {//add,delete.update
			if (command.getType().equals(ADD_COMMAND)) {
				//addToStorage(task);
			}

			else if (command.getType().equals(DELETE_COMMAND)) {
				/* ArrayList<Task> options = getListOfOptions(task);
				System.out.println("Possible tasks to delete: ");
				for (int i = 0; i < options.size(); i++) {
				Task task = options.get(i);
				Sysem.out.println(i + ". " + task.getTime() + ", " + task.getTask() + ", " + task.getPriority());
				}
				int option = scanner.nextInt();
				removeTask(options.get(option));
				 * 
				 */
			}
			
			else if (command.getType().equals(EDIT_COMMAND)) {
				
			}*/


		}

		//System.out.println(task.getPriority() + " " + task.getTime() + " " + task.getTask());
		quitOnExitCommand(command);

		return task;

	}


	private Task createTask(Command command) {
		return command.createTask();
	}

	private void parseCommand(CommandParser parser, Command command) {
		parser.parseCommand(command);
	}

	private void quitOnExitCommand(Command command) {
		if (command.getType() == "exit") {
			System.exit(0);
		}
	}





}
