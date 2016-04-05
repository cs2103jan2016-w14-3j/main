//@@author A0125084L
//package main.java.storage;
//
//import static org.junit.Assert.*;
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
//
//public class StorageTest {
//	
//	private StorageController strCtrl = new StorageController();;
//	private Task task0, task1, task2;
//
//	public void initialise() {
//		task0 = new Task("do homework", "10am", PRIORITY_LEVEL.HIGH, TASK_NATURE.EVENT, TASK_STATUS.UPCOMING);
//		task1 = new Task("play soccer", "5pm",  PRIORITY_LEVEL.LOW, TASK_NATURE.EVENT, TASK_STATUS.UPCOMING);
//		task2 = new Task("eat dinner", "7pm",  PRIORITY_LEVEL.MEDIUM, TASK_NATURE.EVENT, TASK_STATUS.UPCOMING);
//	}
//		
//	@Test
//	public void testAddTask() throws Exception {
//		initialise();
//		ArrayList<Task> expectedList = new ArrayList<Task>();	
//		
//		expectedList.add(task0);
//		strCtrl.addTask(task0);
//		assertEquals(expectedList.get(0).getTask(), strCtrl.displayPendingTasks().get(0).getTask());
//		assertEquals(expectedList.get(0).getTime(), strCtrl.displayPendingTasks().get(0).getTime());
//		assertEquals(expectedList.get(0).getPriority(), strCtrl.displayPendingTasks().get(0).getPriority());
//
//		expectedList.add(task1);
//		strCtrl.addTask(task1);
//		assertEquals(expectedList.get(1).getTask(), strCtrl.displayPendingTasks().get(1).getTask());
//		assertEquals(expectedList.get(1).getTime(), strCtrl.displayPendingTasks().get(1).getTime());
//		assertEquals(expectedList.get(1).getPriority(), strCtrl.displayPendingTasks().get(1).getPriority());
//
//		strCtrl.clearPendingTasks();
//	}
//
//	@Test
//	public void testEditTask() {
//		initialise();
//		ArrayList<Task> expectedList = new ArrayList<Task>();
//
//		expectedList.add(task1);
//		strCtrl.addTask(task0);
//		strCtrl.editPendingTask(task0, task1);
//
//		assertEquals(expectedList.get(0).getTask(), strCtrl.displayPendingTasks().get(0).getTask());
//		assertEquals(expectedList.get(0).getTime(), strCtrl.displayPendingTasks().get(0).getTime());
//		assertEquals(expectedList.get(0).getPriority(), strCtrl.displayPendingTasks().get(0).getPriority());
//
//		strCtrl.clearPendingTasks();
//	}
//
//	@Test
//	public void testDeleteTask() {		
//		initialise();
//		
//		strCtrl.addTask(task0);
//		strCtrl.addTask(task1);
//		assertTrue(strCtrl.displayPendingTasks().size() == 2);
//
//		strCtrl.deletePendingTask(task0);
//		strCtrl.deletePendingTask(task1);
//		assertTrue(strCtrl.displayPendingTasks().size() == 0);
//	}
//
//	@Test
//	public void testSortByName() {
//		initialise();
//		ArrayList<Task> expectedList = new ArrayList<Task>();	
//
//		expectedList.add(task0);
//		expectedList.add(task2);
//		expectedList.add(task1);	
//		strCtrl.addTask(task0);
//		strCtrl.addTask(task1);
//		strCtrl.addTask(task2);
//		strCtrl.sortPendingByTaskName();
//
//		assertEquals(expectedList.get(0).getTask(), strCtrl.displayPendingTasks().get(0).getTask());
//		assertEquals(expectedList.get(1).getTask(), strCtrl.displayPendingTasks().get(1).getTask());
//		assertEquals(expectedList.get(2).getTask(), strCtrl.displayPendingTasks().get(2).getTask());
//
//		strCtrl.clearPendingTasks();
//	}
//
//	@Test
//	public void testUndo() {
//		initialise();
//		
//		strCtrl.addTask(task0);
//		strCtrl.editPendingTask(task0, task1);
//		strCtrl.undo();
//
//		assertEquals(task0.getTask(), strCtrl.displayPendingTasks().get(0).getTask());
//
//		strCtrl.undo();
//		assertTrue(strCtrl.displayPendingTasks().size() == 0);
//	}
//
//	@Test
//	public void testMoveTaskToComplete() {
//		initialise();
//		
//		strCtrl.addTask(task0);
//		strCtrl.addTask(task1);
//
//		strCtrl.moveTaskToComplete(task0);
//		assertEquals(task0.getTask(), strCtrl.displayCompletedTasks().get(0).getTask());
//
//		strCtrl.moveTaskToComplete(task1);
//		assertEquals(task1.getTask(), strCtrl.displayCompletedTasks().get(1).getTask());
//
//		strCtrl.clearPendingTasks();
//		strCtrl.clearCompletedTasks();
//	}
//}
//@@author A0125084L