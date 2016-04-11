/* @@author A0127481E */
package main.java.parser;

import static org.junit.Assert.*;

import java.nio.file.InvalidPathException;

import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import main.java.enumeration.CommandType;
import main.java.exception.InvalidInputFormatException;
import main.java.exception.NoFileNameException;

/**
 * @author Ouyang Danwen
 *
 */
public class TestParser {
	private static AddCommandParser addParser;
	private static EditCommandParser editParser;
	private static DeleteCommandParser deleteParser;
	private static ShowCommandParser showParser;
	private static SortCommandParser sortParser;
	private static StorageCommandParser storageParser;
	private static PrettyTimeParser parser = new PrettyTimeParser();
	private static final int TASK = 0;
	private static final int TIME = 1;
	private static final int PRIORITY = 2;
	private static final int TYPE = 3;
	private static final int STATUS = 4;



	@Before
	public void setUp() throws Exception {
		addParser = new AddCommandParser();
		editParser = new EditCommandParser();
		deleteParser = new DeleteCommandParser();
		showParser = new ShowCommandParser();
		sortParser = new SortCommandParser();
		storageParser = new StorageCommandParser();
	}


	/********************************** Test Add Command *******************************/

	/**
	 * Test standard format(add task-time-priority).
	 * @throws InvalidInputFormatException
	 */
	@Test
	public void testAdd1() throws InvalidInputFormatException {

		String testInput = "play soccer with my friend on sunday 4pm #high";

		String[] parametersActual = addParser.determineParameters(testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[TASK] = "play soccer with my friend";
		parametersExpected[TIME] = parser.parse("on sunday 4pm").toString();
		parametersExpected[PRIORITY] = "high";
		parametersExpected[TYPE] = "one-time event";
		parametersExpected[STATUS] = "upcoming";

		assertArrayEquals(parametersActual, parametersExpected);

	}

	/**
	 * Test non-standard format(add time-priority-task).
	 * @throws InvalidInputFormatException
	 */
	@Test	
	public void testAdd2() throws InvalidInputFormatException {
		String testInput = "on sunday from 8am to 10am #med play soccer with my friend";

		String[] parametersActual = addParser.determineParameters(testInput);
		String[] parametersExpected = new String[5];
		parametersExpected[TASK] = "play soccer with my friend";
		parametersExpected[TIME] = parser.parse("on sunday 8am, on sunday 10am")
				.toString();
		parametersExpected[PRIORITY] = "medium";
		parametersExpected[TYPE] = "duration";
		parametersExpected[STATUS] = "upcoming";

		assertArrayEquals(parametersActual, parametersExpected);
	}

	/**
	 * Test non-standard format(add time-task).
	 * @throws InvalidInputFormatException
	 */
	@Test
	public void testAdd3() throws InvalidInputFormatException {
		String testInput = "do assignment by monday 6pm";

		String[] parametersActual = addParser.determineParameters(testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[TASK] = "do assignment";
		parametersExpected[TIME] = parser.parse("by monday 6pm").toString();
		parametersExpected[PRIORITY] = "not specified";
		parametersExpected[TYPE] = "deadline";
		parametersExpected[STATUS] = "upcoming";

		assertArrayEquals(parametersActual, parametersExpected);
	}

	/**
	 * Test non-standard format(add only task).
	 * @throws InvalidInputFormatException
	 */
	@Test
	public void testAdd4() throws InvalidInputFormatException {
		String testInput = "play soccer";

		String[] parametersActual = addParser.determineParameters(testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[TASK] = "play soccer";
		parametersExpected[TIME] = "[]";
		parametersExpected[PRIORITY] = "not specified";
		parametersExpected[TYPE] = "one-time event";
		parametersExpected[STATUS] = "floating";

		assertArrayEquals(parametersActual, parametersExpected);
	}

	/**
	 * Test invalid format(add priority-time).
	 */
	@Test
	public void testAdd5() {
		String testInput = "#low play soccer";
		String expectedErrorMsg = "Task name is missing!";

		try {
			addParser.determineParameters(testInput);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}
	}

	/**
	 * Test invalid format(add empty task).
	 */
	@Test
	public void testAdd6() {
		String testInput = "";
		String expectedErrorMsg = "Cannot add an empty task!";

		try {
			addParser.determineParameters(testInput);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}

	}

	/**
	 * Test invalid format(add ambiguous time).
	 */
	@Test
	public void testAdd7() {
		String testInput = "do this on mon tue 5am to 7am";
		String expectedErrorMsg = "Ambiguous time entered!";

		try {
			addParser.determineParameters(testInput);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}

	}


	/********************************** Test Edit Command *****************************/

	/**
	 * Test editing task name(edit all fields).
	 * @throws InvalidInputFormatException
	 */
	@Test
	public void testEdit1() throws InvalidInputFormatException {

		String testInput = "do laundry from 6pm to 8pm #low, "
				+ "play soccer with my friend on sunday 10am  #high";

		String[] parametersActual = editParser.determineParameters(testInput);
		String[] parametersExpected = new String[5];
		parametersExpected[TASK] = "do laundry , play soccer with my friend";
		parametersExpected[TIME] = parser.parse("from 6pm to 8pm").toString()
				+ " , " +parser.parse("on sun 10am").toString();
		parametersExpected[PRIORITY] = "low , high";
		parametersExpected[TYPE] = "duration , one-time event";
		parametersExpected[STATUS] = "upcoming , upcoming";

		assertArrayEquals(parametersActual, parametersExpected);

	}

	/**
	 * Test not editing task name(edit priority only).
	 * @throws InvalidInputFormatException
	 */
	@Test
	public void testEdit2() throws InvalidInputFormatException {
		String testInput = "do laundry , #high";

		String[] parametersActual = editParser.determineParameters(testInput);
		String[] parametersExpected = new String[5];
		parametersExpected[TASK] = "do laundry , do laundry";
		parametersExpected[TIME] = "[] , []";
		parametersExpected[PRIORITY] = "not specified , high";
		parametersExpected[TYPE] = "one-time event , one-time event";
		parametersExpected[STATUS] = "floating , floating";

		assertArrayEquals(parametersActual, parametersExpected);
	}

	/**
	 * Test not editing task name(edit time only).
	 * @throws InvalidInputFormatException
	 */
	@Test
	public void testEdit3() throws InvalidInputFormatException {
		String testInput = "do laundry from 6pm to 8pm, "
				+ "on sunday 10am";

		String[] parametersActual = editParser.determineParameters(testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[TASK] = "do laundry , do laundry";
		parametersExpected[TIME] = parser.parse("from 6pm to 8pm").toString()
				+ " , " + parser.parse("on sunday 10am").toString();
		parametersExpected[PRIORITY] = "not specified , not specified";
		parametersExpected[TYPE] = "duration , one-time event";
		parametersExpected[STATUS] = "upcoming , upcoming";

		assertArrayEquals(parametersActual, parametersExpected);
	}


	/**
	 * Test not editing task name(edit time and priority).
	 * @throws InvalidInputFormatException
	 */
	@Test
	public void testEdit4() throws InvalidInputFormatException {
		String testInput = "do laundry from 6pm to 8pm, "
				+ "on sunday 10am #high";

		String[] parametersActual = editParser.determineParameters(testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[TASK] = "do laundry , do laundry";
		parametersExpected[TIME] = parser.parse("from 6pm to 8pm").toString()
				+ " , " + parser.parse("on sunday 10am").toString();
		parametersExpected[PRIORITY] = "not specified , high";
		parametersExpected[TYPE] = "duration , one-time event";
		parametersExpected[STATUS] = "upcoming , upcoming";

		assertArrayEquals(parametersActual, parametersExpected);
	}

	/**
	 * Test invalid editing(no original task name).
	 */
	@Test
	public void testEdit5() {
		String testInput = ", play soccer with my friend on sunday 10am #high";
		String expectedErrorMsg = "Please specify a task to be edited!";

		try {
			editParser.determineParameters(testInput);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}	

	}

	/**
	 * Test invalid editing(no comma).
	 */
	@Test
	public void testEdit6() {
		String testInput = "watch TV -> play soccer with my friend";
		String expectedErrorMsg = "Please use \",\" to separate!";

		try {
			editParser.determineParameters(testInput);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}	

	}

	/**
	 * Test invalid editing format(no update information).
	 */
	@Test
	public void testEdit7() {
		String testInput = "do this, ";
		String expectedErrorMsg = "Please specifiy update information!";

		try {
			editParser.determineParameters(testInput);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}	

	}


	/********************************** Test Delete Command ****************************/

	/**
	 * Test valid deleting.
	 * @throws InvalidInputFormatException
	 */
	@Test
	public void testDelete1() throws InvalidInputFormatException {
		String testInput = "play soccer";

		String[] parametersActual = deleteParser.determineParameters(testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[TASK] = testInput;

		assertArrayEquals(parametersActual, parametersExpected);	
	}

	/**
	 * Test invalid deleting(edit nothing).
	 */
	@Test
	public void testDelete2() {
		String testInput = "";
		String expectedErrorMsg = "Please specify a task to delete!";

		try {
			deleteParser.determineParameters(testInput);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}	
	}


	/********************************** Test Show Command *****************************/

	/**
	 * Test show by "priority high".
	 * @throws InvalidInputFormatException
	 */
	@Test
	public void testShow1() throws InvalidInputFormatException {
		String testInput = "high";

		String[] parametersActual = showParser.determineParameters(testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[PRIORITY] = testInput;

		assertArrayEquals(parametersActual, parametersExpected);	
	}

	/**
	 * Test show by "priority medium".
	 * throws InvalidInputFormatException
	 */
	@Test
	public void testShow2() throws InvalidInputFormatException {
		String testInput = "medium";

		String[] parametersActual = showParser.determineParameters(testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[PRIORITY] = testInput;

		assertArrayEquals(parametersActual, parametersExpected);		
	}

	/**
	 * Test show by "priority low".
	 * @throws InvalidInputFormatException
	 */
	@Test
	public void testShow3() throws InvalidInputFormatException {
		String testInput = "low";

		String[] parametersActual = showParser.determineParameters(testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[PRIORITY] = testInput;

		assertArrayEquals(parametersActual, parametersExpected);		
	}

	/**
	 * Test show by time.
	 */
	@Test
	public void testShow4() throws InvalidInputFormatException {
		String testInput = "monday";

		String[] parametersActual = showParser.determineParameters(testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[TIME] = parser.parse(testInput).toString();

		assertArrayEquals(parametersActual, parametersExpected);		
	}

	/**
	 * Test invalid showing(by nothing).
	 */
	@Test
	public void testShow5() {
		String testInput = "";
		String expectedErrorMsg = "Please specify a filter for show command!";

		try {
			showParser.determineParameters(testInput);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}	
	}

	/**
	 * Test invalid showing(by invalid filter).
	 */
	@Test
	public void testShow6() {
		String testInput = "do homework";
		String expectedErrorMsg = "Please choose a valid filter!";

		try {
			showParser.determineParameters(testInput);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}	
	}

	/********************************** Test Sort Command *****************************/

	/**
	 * Test sort by priority.
	 * @throws InvalidInputFormatException
	 */
	@Test
	public void testSort1() throws InvalidInputFormatException {
		String testInput = "priority";

		String[] parametersActual = sortParser.determineParameters(testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[TASK] = testInput;

		assertArrayEquals(parametersActual, parametersExpected);	
	}

	/**
	 * Test sort by time.
	 * @throws InvalidInputFormatException
	 */
	@Test
	public void testSort2() throws InvalidInputFormatException {
		String testInput = "time";

		String[] parametersActual = sortParser.determineParameters(testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[TASK] = testInput;

		assertArrayEquals(parametersActual, parametersExpected);	
	}

	/**
	 * Test sort by name.
	 * @throws InvalidInputFormatException
	 */
	@Test
	public void testSort3() throws InvalidInputFormatException {
		String testInput = "name";

		String[] parametersActual = sortParser.determineParameters(testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[TASK] = testInput;

		assertArrayEquals(parametersActual, parametersExpected);	
	}

	/**
	 * Test invalid sorting(by invalid parameter).
	 */
	@Test
	public void testSort4() {
		String testInput = "play";
		String expectedErrorMsg = "Sort by \"name\", \"time\" or \"priority\" only!";

		try {
			sortParser.determineParameters(testInput);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}	
	}

	/**
	 * Test invalid sorting(no parameter).
	 */
	@Test
	public void testSort5() {
		String testInput = "";
		String expectedErrorMsg = "Please specify a parameter for sorting!";

		try {
			sortParser.determineParameters(testInput);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}	
	}


	/********************************** Test Storage Command ***************************/

	/**
	 * Test valid storage command(valid path).
	 * @throws InvalidInputFormatException
	 * @throws NoFileNameException 
	 * @throws InvalidPathException 
	 */
	@Test
	public void testStorageCommand1() throws InvalidInputFormatException, 
	InvalidPathException, NoFileNameException {
		String testInput = "myfile.txt";

		String[] parametersActual = storageParser.determineParameters(
				CommandType.MOVE,testInput);

		String[] parametersExpected = new String[5];
		parametersExpected[TASK] = testInput;

		assertArrayEquals(parametersActual, parametersExpected);	
	}

	/**
	 * Test invalid storage command(invalid path case 1).
	 */
	@Test
	public void testStorageCommand2() {
		String testInput = "C:\\";
		String expectedErrorMsg = "No file name is entered!";

		try {
			storageParser.determineParameters(CommandType.MOVE, testInput);
		} catch (InvalidPathException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		} catch (NoFileNameException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}
	}
	
	/**
	 * Test invalid storage command(invalid path case 2).
	 */
	@Test
	public void testStorageCommand3() {
		String testInput = "";
		String expectedErrorMsg = "Please enter a non-empty path!";

		try {
			storageParser.determineParameters(CommandType.MOVE, testInput);
		} catch (InvalidPathException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		} catch (NoFileNameException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}
	}
	
	/**
	 * Test invalid storage command(invalid path case 3).
	 */
	@Test
	public void testStorageCommand4() {
		String testInput = "test??test!!test##";
		String expectedErrorMsg = "Invalid path is entered!";

		try {
			storageParser.determineParameters(CommandType.MOVE, testInput);
		} catch (InvalidPathException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		} catch (InvalidInputFormatException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		} catch (NoFileNameException e) {
			String actualErrorMsg = e.getMessage();
			assertEquals(actualErrorMsg, expectedErrorMsg);
		}
	}


}
/* @@author A0127481E */