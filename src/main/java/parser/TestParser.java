/* @@author A0127481E */
package main.java.parser;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import main.java.exception.InvalidInputFormatException;

public class TestParser {
	private static AddCommandParser addParser;
	private static EditCommandParser editParser;
	private static PrettyTimeParser parser;
	private static final int TASK = 0;
	private static final int TIME = 1;
	private static final int PRIORITY = 2;
	private static final int TASK_TYPE = 3;



	@Before
	public void setUp() throws Exception {
		parser = new PrettyTimeParser();
		addParser = new AddCommandParser();
		editParser = new EditCommandParser();
	}
	
	@Test
	public void testFormatting() throws InvalidInputFormatException {
		String testInput = "#medium on tmr I can play basketball with my father";
		
		String expected = "I can play basketball with my father on tomorrow #medium";
		String actual = addParser.formatToStandardCommandContent(testInput);
		
		assertEquals(actual, expected);
	}

	@Test
	//cover a normal case with correct input
	public void testAddWithFullParameters() throws InvalidInputFormatException {
		
		String testInput = "play soccer with my friend on sunday #high";
		
		String[] parametersActual = addParser.determineParameters(testInput);
		
		String[] parametersExpected = new String[4];
		parametersExpected[TASK] = "play soccer with my friend";
		String time = parser.parse("on sunday 8am").toString();
		parametersExpected[TIME] = time.substring(1,time.length() - 1);
		parametersExpected[PRIORITY] = "high";
		parametersExpected[TASK_TYPE] = "one-time event";
		
		assertArrayEquals(parametersActual, parametersExpected);

	}
	
	
	
	
	@Test
	//cover a exception case with incorrect input
	public void testAdd2() {
		String testInput = "";
		String expectedErrorMsg = "Cannot add an empty task!";
		try {
			String[] parameters = addParser.determineParameters(testInput);
		} catch (InvalidInputFormatException e) {
			// TODO Auto-generated catch block
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}
		
	}

	@Test
	public void testEdit() throws InvalidInputFormatException {
		
		String testInput = "better to get, play soccer with my friend on sunday #high";
		
		String[] parametersActual = editParser.determineParameters(testInput);
		
		String[] parametersExpected = new String[4];
		parametersExpected[TASK] = "better to get , play soccer with my friend";
		String time = parser.parse(" , on sunday 8am").toString();
		parametersExpected[TIME] = " , " + time.substring(1,time.length() - 1);
		parametersExpected[PRIORITY] = " , high";
		parametersExpected[TASK_TYPE] = " , one-time event";
		
		assertArrayEquals(parametersActual, parametersExpected);

	}
	



}
/* @@author A0127481E */