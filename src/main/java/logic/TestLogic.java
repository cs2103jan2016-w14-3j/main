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

	/**
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
	
//	@Test
//	public void testDelete() throws Exception {
//		initialise();
//		logic.handleUserCommand("add pending duration task on last monday 5am #medium", null);
//		
//		Task task = new Task("pending duration task",parser.parse("last monday 5am"), PriorityLevel.MEDIUM,TaskType.EVENT,TaskStatus.OVERDUE);
//			
//		logic.handleUserCommand("delete pending duration task on last monday 5am #medium", null);
//		
//		assertEquals(task.getTask(), logic.getTask().getTask());
//
//	}
	
//	@Test
//	public void testEdit() throws Exception {
//		initialise();
//		logic.handleUserCommand("add old task on monday 5am #medium", null);
//		ArrayList<Task> expectedList = new ArrayList<Task>();
//		expectedList.add(new Task("old task",parser.parse("monday 5am"), PriorityLevel.MEDIUM,TaskType.EVENT,TaskStatus.UPCOMING));
//		expectedList.add(new Task("new task",parser.parse("monday 11pm"), PriorityLevel.MEDIUM,TaskType.EVENT,TaskStatus.UPCOMING));
//		logic.handleUserCommand("edit old, new task on 11pm", expectedList);
//		
//		assertEquals(expectedList.get(1).getTask(), logic.getUpdatedTask().getTask());
//		assertEquals(expectedList.get(1).getTime(), logic.getUpdatedTask().getTime());
//		assertEquals(expectedList.get(1).getPriority(), logic.getUpdatedTask().getPriority());
//		assertEquals(expectedList.get(1).getType(), logic.getUpdatedTask().getType());
//		assertEquals(expectedList.get(1).getStatus(), logic.getUpdatedTask().getStatus());
//	}


}
/* @@author A0124078H */