/* @@author A0127481E */
package main.java.logic;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import main.java.data.COMMAND_TYPE;
import main.java.data.Command;
import main.java.parser.AddCommandParser;
import main.java.parser.InvalidInputFormatException;

public class TestCommandDispatcher {
	private static CommandDispatcher dispatcher;
	private static AddCommandParser addParser;

	@Before
	public void setUp() throws Exception {
		dispatcher = new CommandDispatcher();
		addParser = new AddCommandParser();
	}

	@Test
	//cover a case without the command keyword
	public void test() throws InvalidInputFormatException {
		
		String userInput = "on mon 5pm to 8pm go shopping #low";
		Command command = new Command(userInput);
		Command actualCommand = dispatcher.parseCommand(command);
		
		String[] expectedPara = addParser.determineParameters
				(userInput);
		
		Command expectedCommand = new Command(userInput, 
				COMMAND_TYPE.ADD, "on mon 5pm to 8pm go shopping #low", expectedPara);
		
		
		assertEquals(expectedCommand.getOriginal(), actualCommand.getOriginal());
		assertEquals(expectedCommand.getType(), actualCommand.getType());
		assertEquals(expectedCommand.getContent(), actualCommand.getContent());
		assertArrayEquals(expectedCommand.getParameters(), actualCommand.getParameters());
	}
	
	@Test
	//cover a case with the command keyword
	public void test1() throws InvalidInputFormatException {
		String userInput = "add on mon 5pm to 8pm go shopping #low";
		Command command = new Command(userInput);
		Command actualCommand = dispatcher.parseCommand(command);
		
		String[] expectedPara = addParser.determineParameters
				("on mon 5pm to 8pm go shopping #low");
		
		Command expectedCommand = new Command(userInput, 
				COMMAND_TYPE.ADD, "on mon 5pm to 8pm go shopping #low", expectedPara);
		
		
		assertEquals(expectedCommand.getOriginal(), actualCommand.getOriginal());
		assertEquals(expectedCommand.getType(), actualCommand.getType());
		assertEquals(expectedCommand.getContent(), actualCommand.getContent());
		assertArrayEquals(expectedCommand.getParameters(), actualCommand.getParameters());
	}
	
	

}
/* @@author A0127481E */