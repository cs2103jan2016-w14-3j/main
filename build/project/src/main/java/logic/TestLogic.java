/* @@author A0124078H */
package main.java.logic;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.junit.Test;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import main.java.data.Task;
import main.java.enumeration.PriorityLevel;
import main.java.enumeration.TaskStatus;
import main.java.enumeration.TaskType;
import main.java.exception.InvalidInputFormatException;
import main.java.exception.NoFileNameException;
import main.java.logic.Logic;


public class TestLogic {

	private PrettyTimeParser parser;
	private Logic logic;
	
	public void initialise() throws IOException {
		logic = new Logic();
		parser = new PrettyTimeParser();
	}

	/** Cover a case with floating task
	 * @throws NoFileNameException 
	 * @throws IOException 
	 * @throws Exception
	 */
	@Test
	public void testAddFloating() throws InvalidInputFormatException, IOException, NoFileNameException {
		initialise();
		logic.handleUserCommand("add floating task", null);
		
		Task task = new Task("floating task",null, PriorityLevel.NOT_SPECIFIED,TaskType.EVENT,TaskStatus.FLOATING);
			
		assertEquals(task.getTask(), logic.getTask().getTask());
		assertEquals(task.getPriority(), logic.getTask().getPriority());
		assertEquals(task.getType(), logic.getTask().getType());
		assertEquals(task.getStatus(), logic.getTask().getStatus());
	}
	
	/** Covers a case of floating task with priority
	 * @throws InvalidInputFormatException
	 * @throws IOException
	 * @throws NoFileNameException
	 */
	@Test
	public void testAddFloatingWithPriority() throws InvalidInputFormatException, IOException, NoFileNameException  {
		initialise();
		logic.handleUserCommand("add floating task with high priority #high", null);
		
		Task task = new Task("floating task with high priority",null, PriorityLevel.HIGH,TaskType.EVENT,TaskStatus.FLOATING);
			
		assertEquals(task.getTask(), logic.getTask().getTask());
		assertEquals(task.getPriority(), logic.getTask().getPriority());
		assertEquals(task.getType(), logic.getTask().getType());
		assertEquals(task.getStatus(), logic.getTask().getStatus());
	}
	
	/** Covers a case of pending task with deadline
	 * @throws InvalidInputFormatException
	 * @throws IOException
	 * @throws NoFileNameException
	 */
	@Test
	public void testAddPendingDeadline() throws InvalidInputFormatException, IOException, NoFileNameException{
		initialise();
		logic.handleUserCommand("add pending task by monday 8pm", null);
		
		Task task = new Task("pending task",parser.parse("monday 8pm"), PriorityLevel.NOT_SPECIFIED,TaskType.DEADLINE,TaskStatus.UPCOMING);
			
		assertEquals(task.getTask(), logic.getTask().getTask());
		assertEquals(task.getTime(), logic.getTask().getTime());
		assertEquals(task.getPriority(), logic.getTask().getPriority());
		assertEquals(task.getType(), logic.getTask().getType());
		assertEquals(task.getStatus(), logic.getTask().getStatus());
	}
	
	/** Covers the pending task deadline with priority
	 * @throws InvalidInputFormatException
	 * @throws IOException
	 * @throws NoFileNameException
	 */
	@Test
	public void testAddPendingDeadlineWithPriority() throws InvalidInputFormatException, IOException, NoFileNameException{
		initialise();
		logic.handleUserCommand("add pending task by monday 8pm #medium", null);
		
		Task task = new Task("pending task",parser.parse("monday 8pm"), PriorityLevel.MEDIUM,TaskType.DEADLINE,TaskStatus.UPCOMING);
			
		assertEquals(task.getTask(), logic.getTask().getTask());
		assertEquals(task.getTime(), logic.getTask().getTime());
		assertEquals(task.getPriority(), logic.getTask().getPriority());
		assertEquals(task.getType(), logic.getTask().getType());
		assertEquals(task.getStatus(), logic.getTask().getStatus());
	}
	
	/**Covers the pending duration task
	 * @throws InvalidInputFormatException
	 * @throws IOException
	 * @throws NoFileNameException
	 */
	@Test
	public void testAddPendingDuration() throws InvalidInputFormatException, IOException, NoFileNameException  {
		initialise();
		logic.handleUserCommand("add pending duration task from monday 8pm to friday 9pm", null);
		
		Task task = new Task("pending duration task",parser.parse("from monday 8pm to friday 9pm"), PriorityLevel.NOT_SPECIFIED,TaskType.DURATION,TaskStatus.UPCOMING);
			
		assertEquals(task.getTask(), logic.getTask().getTask());
		assertEquals(task.getTime(), logic.getTask().getTime());
		assertEquals(task.getPriority(), logic.getTask().getPriority());
		assertEquals(task.getType(), logic.getTask().getType());
		assertEquals(task.getStatus(), logic.getTask().getStatus());
	}
	
	/**Covers the pending duration task with priority
	 * @throws InvalidInputFormatException
	 * @throws IOException
	 * @throws NoFileNameException
	 */
	@Test
	public void testAddPendingDurationWithPriority() throws InvalidInputFormatException, IOException, NoFileNameException {
		initialise();
		logic.handleUserCommand("add pending duration task from monday 8pm to friday 9pm #low", null);
		
		Task task = new Task("pending duration task",parser.parse("from monday 8pm to friday 9pm"), PriorityLevel.LOW,TaskType.DURATION,TaskStatus.UPCOMING);
			
		assertEquals(task.getTask(), logic.getTask().getTask());
		assertEquals(task.getTime(), logic.getTask().getTime());
		assertEquals(task.getPriority(), logic.getTask().getPriority());
		assertEquals(task.getType(), logic.getTask().getType());
		assertEquals(task.getStatus(), logic.getTask().getStatus());
	}
	
	/**Covers overdue task
	 * @throws InvalidInputFormatException
	 * @throws IOException
	 * @throws NoFileNameException
	 */
	@Test
	public void testAddOverdue() throws InvalidInputFormatException, IOException, NoFileNameException {
		initialise();
		logic.handleUserCommand("add pending duration task on last monday 5am", null);
		
		Task task = new Task("pending duration task",parser.parse("last monday 5am"), PriorityLevel.NOT_SPECIFIED,TaskType.EVENT,TaskStatus.OVERDUE);
			
		assertEquals(task.getTask(), logic.getTask().getTask());
		assertEquals(task.getTime(), logic.getTask().getTime());
		assertEquals(task.getPriority(), logic.getTask().getPriority());
		assertEquals(task.getType(), logic.getTask().getType());
		assertEquals(task.getStatus(), logic.getTask().getStatus());
	}
	
	/** Covers overdue task with priority
	 * @throws InvalidInputFormatException
	 * @throws IOException
	 * @throws NoFileNameException
	 */
	@Test
	public void testAddOverdueWithPriority() throws InvalidInputFormatException, IOException, NoFileNameException {
		initialise();
		logic.handleUserCommand("add pending duration task on last monday 5am #medium", null);
		
		Task task = new Task("pending duration task",parser.parse("last monday 5am"), PriorityLevel.MEDIUM,TaskType.EVENT,TaskStatus.OVERDUE);
			
		assertEquals(task.getTask(), logic.getTask().getTask());
		assertEquals(task.getTime(), logic.getTask().getTime());
		assertEquals(task.getPriority(), logic.getTask().getPriority());
		assertEquals(task.getType(), logic.getTask().getType());
		assertEquals(task.getStatus(), logic.getTask().getStatus());
	}
	
}
/* @@author A0124078H */