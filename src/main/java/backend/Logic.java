package main.java.backend;

import java.util.List;
import java.util.Scanner;

import com.joestelmach.natty.Parser;

import main.java.data.*;

public class Logic {
	
	private final String WELCOME_MESSAGE = "Welcome to Flashpoint!";
	private Task task;
	
	public Logic() {
		
	}
	
	public void showWelcomeMessage() {
		System.out.println(WELCOME_MESSAGE);
	}
	
	public Task handleUserCommand(String userInput) {

		CommandParser parser = new CommandParser();
		Command command = new Command(userInput);
		parseCommand(parser, command);
		task = executeCommand(command);
		//System.out.println(task.getPriority() + " " + task.getTime() + " " + task.getTask());
		quitOnExitCommand(command);
		
		return task;
		
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
	
	
	

	
}
