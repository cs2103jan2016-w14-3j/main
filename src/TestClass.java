//import static org.junit.Assert.assertEquals;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.junit.Test;
//import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
//
//import main.java.data.PRIORITY_LEVEL;
//import main.java.data.TASK_NATURE;
//import main.java.data.TASK_STATUS;
//import main.java.data.Task;
//import main.java.logic.Logic;
//import main.java.storage.StorageController;
//
//public class TestClass {
//
//	private PrettyTimeParser parser = new PrettyTimeParser();
//	private Logic logic = new Logic();
//	private StorageController strCtrl = new StorageController();;
//
//	@Test
//	public void testAdd() throws Exception {
//		logic.handleUserCommand("add new task by 10pm #high", null);
//		logic.handleUserCommand("add another task by 8pm #medium", null);
//
//		ArrayList<Task> expectedList = new ArrayList<Task>();
//		expectedList.add(new Task("new task", parser.parse("10pm"), PRIORITY_LEVEL.HIGH, TASK_NATURE.DEADLINE, TASK_STATUS.UPCOMING));
//		expectedList.add(new Task("another task", parser.parse("8pm"), PRIORITY_LEVEL.MEDIUM, TASK_NATURE.DEADLINE, TASK_STATUS.UPCOMING));
//
//		assertEquals(expectedList.get(0).getTask(), logic.displayPending().get(0).getTask());
//		assertEquals(expectedList.get(0).getTime(), logic.displayPending().get(0).getTime());
//		assertEquals(expectedList.get(0).getPriority(), logic.displayPending().get(0).getPriority());
//
//		assertEquals(expectedList.get(1).getTask(), logic.displayPending().get(1).getTask());
//		assertEquals(expectedList.get(1).getTime(), logic.displayPending().get(1).getTime());
//		assertEquals(expectedList.get(1).getPriority(), logic.displayPending().get(1).getPriority());
//
//		strCtrl.clearPendingTasks();
//	}
//
////	@Test
////	public void testEdit() throws Exception {
////		logic.handleUserCommand("add new task by 10pm #high", null);
////		
////		ArrayList<Task> expectedList = new ArrayList<Task>();
////
////		expectedList.add(new Task("new task", parser.parse("10pm"), PRIORITY_LEVEL.HIGH, TASK_NATURE.DEADLINE, TASK_STATUS.UPCOMING));
////
////		logic.handleUserCommand("edit new, old by 11pm", expectedList);
////
////		assertEquals(expectedList.get(0).getTask(), strCtrl.displayPendingTasks().get(0).getTask());
////		assertEquals(expectedList.get(0).getTime(), strCtrl.displayPendingTasks().get(0).getTime());
////		assertEquals(expectedList.get(0).getPriority(), strCtrl.displayPendingTasks().get(0).getPriority());
////
////		strCtrl.clearPendingTasks();
////	}
//}
