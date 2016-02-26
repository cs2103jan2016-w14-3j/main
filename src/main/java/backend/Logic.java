package main.java.backend;

import java.util.Scanner;

public class Logic {
	
	private final String WELCOME_MESSAGE = "Welcome to Flashpoint!";
	
	
	public Logic() {
		
	}
	
	public void showWelcomeMessage() {
		System.out.println(WELCOME_MESSAGE);
	}
	
	public void handleUserCommand() {
		Scanner scanner = new Scanner(System.in);
		CommandParser parser = new CommandParser();
		
		while (true) {
			String userInput = scanner.nextLine();
			Command command = new Command(userInput);
			parseCommand(parser, command);
			Task task = executeCommand(command);
			//System.out.println(task.getPriority() + " " + task.getTime() + " " + task.getTask());
			quitOnExitCommand(command);
		}
	}
	
	private Task executeCommand(Command command) {
		return command.executeCommand();
	}
	
	private void parseCommand(CommandParser parser, Command command) {
		parser.parseCommand(command);
	}
	
	private void quitOnExitCommand(Command command) {
		if (command.getType() == "exit") {
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		
		Logic logic = new Logic();
		logic.showWelcomeMessage();
		logic.handleUserCommand();
		//Command command = new Command("ad a lot of things");
		//CommandParser parser = new CommandParser();
		//parser.parseCommand(command);
		//System.out.println(command.getContent());
		//System.out.println(command.getType());
		//System.out.println("String hello".split("S")[1]);
	}
	
	
	
}
