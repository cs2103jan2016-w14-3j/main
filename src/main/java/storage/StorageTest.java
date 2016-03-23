 package main.java.storage;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;
import main.java.data.Task;

public class StorageTest {

	private Task task0 = new Task("do homework", "10am", "high", "not done");
	private Task task1 = new Task("play soccer", "5pm", "low", "not done");
	private Task task2 = new Task("eat dinner", "7pm", "medium", "not done");
	private StorageController strCtrl = new StorageController();;

	@Test
	public void testAddTask() {
		ArrayList<Task> expectedList = new ArrayList<Task>();	
		//assert 1==1;

		expectedList.add(task0);
		strCtrl.addTask(task0);
		assertEquals(expectedList.get(0).getTask(), strCtrl.displayPendingTasks().get(0).getTask());
		assertEquals(expectedList.get(0).getTime(), strCtrl.displayPendingTasks().get(0).getTime());
		assertEquals(expectedList.get(0).getPriority(), strCtrl.displayPendingTasks().get(0).getPriority());

		expectedList.add(task1);
		strCtrl.addTask(task1);
		assertEquals(expectedList.get(1).getTask(), strCtrl.displayPendingTasks().get(1).getTask());
		assertEquals(expectedList.get(1).getTime(), strCtrl.displayPendingTasks().get(1).getTime());
		assertEquals(expectedList.get(1).getPriority(), strCtrl.displayPendingTasks().get(1).getPriority());
		
		strCtrl.clearPendingTasks();
	}
	
	@Test
	public void testEditTask() {
		ArrayList<Task> expectedList = new ArrayList<Task>();
		
		expectedList.add(task1);
		strCtrl.addTask(task0);
		strCtrl.editPendingTask(task0, task1);
		
		assertEquals(expectedList.get(0).getTask(), strCtrl.displayPendingTasks().get(0).getTask());
		assertEquals(expectedList.get(0).getTime(), strCtrl.displayPendingTasks().get(0).getTime());
		assertEquals(expectedList.get(0).getPriority(), strCtrl.displayPendingTasks().get(0).getPriority());
		
		strCtrl.clearPendingTasks();
	}
	
	@Test
	public void testDeleteTask() {		
		
		strCtrl.addTask(task0);
		strCtrl.addTask(task1);
		assertTrue(strCtrl.displayPendingTasks().size() == 2);
		
		strCtrl.deletePendingTask(task0);
		strCtrl.deletePendingTask(task1);
		assertTrue(strCtrl.displayPendingTasks().size() == 0);
	}
	
	@Test
	public void testSortByName() {
		ArrayList<Task> expectedList = new ArrayList<Task>();	

		expectedList.add(task0);
		expectedList.add(task2);
		expectedList.add(task1);	
		strCtrl.addTask(task0);
		strCtrl.addTask(task1);
		strCtrl.addTask(task2);
		strCtrl.sortPendingByTaskName();
		
		assertEquals(expectedList.get(0).getTask(), strCtrl.displayPendingTasks().get(0).getTask());
		assertEquals(expectedList.get(1).getTask(), strCtrl.displayPendingTasks().get(1).getTask());
		assertEquals(expectedList.get(2).getTask(), strCtrl.displayPendingTasks().get(2).getTask());
		
		strCtrl.clearPendingTasks();
	}
	
	@Test
	public void testUndo() {
	
		strCtrl.addTask(task0);
		strCtrl.editPendingTask(task0, task1);
		strCtrl.undo();
		
		assertEquals(task0.getTask(), strCtrl.displayPendingTasks().get(0).getTask());
		
		strCtrl.undo();
		assertTrue(strCtrl.displayPendingTasks().size() == 0);
	}
	
	@Test
	public void testMoveTaskToComplete() {
		
		strCtrl.addTask(task0);
		strCtrl.addTask(task1);
		
		strCtrl.moveTaskToComplete(task0);
		assertEquals(task0.getTask(), strCtrl.displayCompletedTasks().get(0).getTask());
		
		strCtrl.moveTaskToComplete(task1);
		assertEquals(task1.getTask(), strCtrl.displayCompletedTasks().get(1).getTask());
		
		strCtrl.clearPendingTasks();
		strCtrl.clearCompletedTasks();
	}
}
