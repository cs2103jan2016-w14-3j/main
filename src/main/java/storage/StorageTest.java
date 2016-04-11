//@@author A0125084L
package main.java.storage;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import main.java.data.Task;
import main.java.enumeration.PriorityLevel;
import main.java.enumeration.TaskStatus;
import main.java.enumeration.TaskType;

/**
 * This class contains of unit tests for the storage component
 * Tasks passed by the Logic component is ensured not to be null
 * @author Hou Bo Wen
 *
 */
public class StorageTest {
	
	private static final String ERROR_ADD_TASK = "Error adding task";
	private static final String ERROR_DELETE_TASK = "Error deleting task";
	private static final String ERROR_SORT = "Error while sorting";
	private static final String ERROR_UNDO = "Error while undoing";
	private static final String ERROR_REDO = "Error while redoing";
	private static final String ERROR_MARK_TASK = "Error while marking task";
	private static final String ERROR_UNMARK_TASK = "Error while unmarking task";
	private static final String ERROR_SAVE = "Error saving to location";

	private StorageController storageController;
	private Task pendingTask0, pendingTask1, pendingTask2, completedTask0, completedTask1, completedTask2;
	private List<Date> dateList;

	public void initialise() throws IOException {
		storageController = new StorageController();
		
		dateList = new ArrayList<Date>();
		  
		pendingTask0 = new Task("do homework", dateList, PriorityLevel.HIGH, TaskType.EVENT, TaskStatus.UPCOMING);
		pendingTask1 = new Task("eat dinner", dateList,  PriorityLevel.MEDIUM, TaskType.EVENT, TaskStatus.FLOATING);
		pendingTask2 = new Task("meet friends", dateList,  PriorityLevel.LOW, TaskType.EVENT, TaskStatus.OVERDUE);
		completedTask0 = new Task("play soccer", dateList,  PriorityLevel.HIGH, TaskType.EVENT, TaskStatus.COMPLETED);
		completedTask1 = new Task("submit homework", dateList,  PriorityLevel.MEDIUM, TaskType.EVENT, TaskStatus.COMPLETED);
		completedTask2 = new Task("watch movie", dateList,  PriorityLevel.LOW, TaskType.EVENT, TaskStatus.COMPLETED);
	}
	 
	/*
	 * Checks if two tasks are the same
	 * Returns true if all fields are the same, else return false
	 */
	private boolean isTaskEqual(Task task1, Task task2) {
		boolean isSameName = task1.getTask().equals(task2.getTask());
		boolean isSameTime = task1.getTime().equals(task2.getTime());
		boolean isSamePriority = task1.getPriority().equals(task2.getPriority());
		boolean isSameType = task1.getType().equals(task2.getType());
		boolean isSameStatus = task1.getStatus().equals(task2.getStatus());
		
		if (isSameName && isSameTime && isSamePriority && isSameType && isSameStatus) {
			return true;
		}
		else {
			return false;
		}
	}
		
	/*
	 * Tests the add function
	 */
	@Test
	public void testAddTask() throws IOException {			
		initialise();
		
		try {
			//Add pending task when the list is empty
			storageController.addTask(pendingTask0);		
			assertTrue(isTaskEqual(pendingTask0, storageController.displayPendingTasks().get(0)));

			//Add pending task when list is not empty
			storageController.addTask(pendingTask1);
			assertTrue(isTaskEqual(pendingTask1, storageController.displayPendingTasks().get(1)));

			//Add completed task when list is empty
			storageController.addTask(completedTask0);
			storageController.moveTaskToComplete(completedTask0);
			assertTrue(isTaskEqual(completedTask0, storageController.displayCompletedTasks().get(0)));
			
			//Add completed task when list is not empty
			storageController.addTask(completedTask1);
			storageController.moveTaskToComplete(completedTask1);
			assertTrue(isTaskEqual(completedTask1, storageController.displayCompletedTasks().get(1)));
			
			storageController.clearAllPendingTasks();
			storageController.clearCompletedTasks();
	
		} catch (IOException e) {
			System.err.println(ERROR_ADD_TASK);
		}
	}

	/*
	 * Tests the edit function
	 */
	@Test
	public void testEditTask() throws IOException {
		initialise();
		
		//Edit pending task when the list has only 1 task
		storageController.addTask(pendingTask0);
		storageController.editPendingTask(pendingTask0, pendingTask1);
		assertTrue(isTaskEqual(pendingTask1, storageController.displayPendingTasks().get(0)));
		
		//Edit pending task when the list has more than 1 tasks
		storageController.addTask(pendingTask0);
		storageController.editPendingTask(pendingTask0, pendingTask2);
		assertTrue(isTaskEqual(pendingTask2, storageController.displayPendingTasks().get(1)));
		
		//Edit completed task when the list has only 1 task
		storageController.addTask(completedTask0);
		storageController.moveTaskToComplete(completedTask0);
		storageController.editCompletedTask(completedTask0, completedTask1);
		assertTrue(isTaskEqual(completedTask1, storageController.displayCompletedTasks().get(0)));
		
		//Edit completed task when the list has more than 1 tasks
		storageController.addTask(completedTask0);
		storageController.moveTaskToComplete(completedTask0);
		storageController.editCompletedTask(completedTask0, completedTask2);
		assertTrue(isTaskEqual(completedTask2, storageController.displayCompletedTasks().get(1)));
		
		storageController.clearAllPendingTasks();
		storageController.clearCompletedTasks();
	}

	/*
	 * Tests the delete function
	 */
	@Test
	public void testDeleteTask() {		
	
		try {
			initialise();
			
			//Delete pending task when the list has only 1 task
			storageController.addTask(pendingTask0);
			assertTrue(storageController.displayPendingTasks().size() == 1);
			storageController.deletePendingTask(pendingTask0);
			assertTrue(storageController.displayPendingTasks().size() == 0);

			//Delete pending task when the list has more than 1 task
			storageController.addTask(pendingTask0);
			storageController.addTask(pendingTask1);
			assertTrue(storageController.displayPendingTasks().size() == 2);
			storageController.deletePendingTask(pendingTask1);
			assertTrue(storageController.displayPendingTasks().size() == 1);
			assertTrue(isTaskEqual(pendingTask0, storageController.displayPendingTasks().get(0)));
	
			//Delete completed task when the list has only 1 task
			storageController.addTask(completedTask0);
			storageController.moveTaskToComplete(completedTask0);
			assertTrue(storageController.displayCompletedTasks().size() == 1);
			storageController.deleteCompletedTask(completedTask0);
			assertTrue(storageController.displayCompletedTasks().size() == 0);
			
			//Delete completed task when the list has more than 1 tasks
			storageController.addTask(completedTask0);
			storageController.addTask(completedTask1);
			storageController.moveTaskToComplete(completedTask0);
			storageController.moveTaskToComplete(completedTask1);
			assertTrue(storageController.displayCompletedTasks().size() == 2);
			storageController.deleteCompletedTask(completedTask1);
			assertTrue(storageController.displayCompletedTasks().size() == 1);
			assertTrue(isTaskEqual(completedTask0, storageController.displayCompletedTasks().get(0)));
			
			storageController.clearAllPendingTasks();
			storageController.clearCompletedTasks();
			
		} catch (IOException e) {
			System.err.println(ERROR_DELETE_TASK);
		}
	}

	/*
	 * Tests the sorting function
	 */
	@Test
	public void testSortByName() {
		ArrayList<Task> expectedList = new ArrayList<Task>();	

		try {
			initialise();
			
			expectedList.add(pendingTask0);
			expectedList.add(pendingTask1);
			expectedList.add(pendingTask2);	
			storageController.addTask(pendingTask2);
			storageController.addTask(pendingTask0);
			storageController.addTask(pendingTask1);
			storageController.sortPendingByTaskName();

			assertEquals(expectedList.get(0).getTask(), storageController.displayPendingTasks().get(0).getTask());
			assertEquals(expectedList.get(1).getTask(), storageController.displayPendingTasks().get(1).getTask());
			assertEquals(expectedList.get(2).getTask(), storageController.displayPendingTasks().get(2).getTask());

			storageController.clearAllPendingTasks();
			storageController.clearCompletedTasks();
		
		} catch (IOException e) {
			System.err.println(ERROR_SORT);
		}
	}

	/*
	 * Tests the undo function
	 */
	@Test
	public void testUndo() {

		try {
			initialise();
			
			//Tests undo with only 1 previous command
			storageController.addTask(pendingTask0);
			storageController.editPendingTask(pendingTask0, pendingTask1);
			storageController.undo();
			assertTrue(isTaskEqual(pendingTask0, storageController.displayPendingTasks().get(0)));

			//Tests undo with more than 1 previous command
			storageController.undo();
			assertTrue(storageController.displayPendingTasks().size() == 0);		
			
			storageController.clearAllPendingTasks();
			storageController.clearCompletedTasks();
			
		} catch (IOException e) {
			System.err.println(ERROR_UNDO);
		}
	}

	/*
	 * Tests the redo function
	 */
	@Test
	public void testRedo() {
	
		try {
			initialise();
			
			//Tests redo with only 1 previous undo command
			storageController.addTask(pendingTask0);
			storageController.editPendingTask(pendingTask0, pendingTask1);
			storageController.undo();
			assertTrue(isTaskEqual(pendingTask0, storageController.displayPendingTasks().get(0)));
			storageController.redo();
			assertTrue(isTaskEqual(pendingTask1, storageController.displayPendingTasks().get(0)));
		
			//Tests redo with more than 1 previous undo command
			storageController.undo();
			assertTrue(isTaskEqual(pendingTask0, storageController.displayPendingTasks().get(0)));
			storageController.redo();
			assertTrue(isTaskEqual(pendingTask1, storageController.displayPendingTasks().get(0)));
			
			storageController.clearAllPendingTasks();
			storageController.clearCompletedTasks();
			
		} catch (IOException e) {
			System.err.println(ERROR_REDO);
		}
		
	}
	
	/*
	 * Tests the mark function
	 */
	@Test
	public void testMoveTaskToComplete() {
		
		try {
			initialise();
			storageController.addTask(pendingTask0);
			storageController.moveTaskToComplete(pendingTask0);

			//Asserts that task has been marked as completed
			assertTrue(storageController.displayPendingTasks().size() == 0);
			assertTrue(storageController.displayCompletedTasks().size() == 1);
			assertEquals(pendingTask0.getTask(), storageController.displayCompletedTasks().get(0).getTask());
			assertEquals(pendingTask0.getTime(), storageController.displayCompletedTasks().get(0).getTime());
			assertEquals(pendingTask0.getPriority(), storageController.displayCompletedTasks().get(0).getPriority());
			assertEquals(pendingTask0.getType(), storageController.displayCompletedTasks().get(0).getType());
			assertEquals(TaskStatus.COMPLETED, storageController.displayCompletedTasks().get(0).getStatus());
			
			storageController.clearAllPendingTasks();
			storageController.clearCompletedTasks();
			
		} catch (IOException e) {
			System.err.println(ERROR_MARK_TASK);
		}
	}
	
	/*
	 * Tests the unmark function
	 */
	@Test
	public void testMoveTaskToPending() {
	
		try {
			initialise();
			storageController.addTask(pendingTask0);
			storageController.moveTaskToComplete(pendingTask0);
			
			//Asserts that task has been marked as completed
			assertTrue(storageController.displayPendingTasks().size() == 0);
			assertTrue(storageController.displayCompletedTasks().size() == 1);
			assertEquals(pendingTask0.getTask(), storageController.displayCompletedTasks().get(0).getTask());
			assertEquals(pendingTask0.getTime(), storageController.displayCompletedTasks().get(0).getTime());
			assertEquals(pendingTask0.getPriority(), storageController.displayCompletedTasks().get(0).getPriority());
			assertEquals(pendingTask0.getType(), storageController.displayCompletedTasks().get(0).getType());
			assertEquals(TaskStatus.COMPLETED, storageController.displayCompletedTasks().get(0).getStatus());
			
			storageController.moveTaskToPending(pendingTask0);
			
			//Asserts that task has been unmarked
			assertTrue(storageController.displayPendingTasks().size() == 1);
			assertTrue(storageController.displayCompletedTasks().size() == 0);
			assertEquals(pendingTask0.getTask(), storageController.displayPendingTasks().get(0).getTask());
			assertEquals(pendingTask0.getTime(), storageController.displayPendingTasks().get(0).getTime());
			assertEquals(pendingTask0.getPriority(), storageController.displayPendingTasks().get(0).getPriority());
			assertEquals(pendingTask0.getType(), storageController.displayPendingTasks().get(0).getType());
			assertEquals(TaskStatus.FLOATING, storageController.displayPendingTasks().get(0).getStatus());
		
			storageController.clearAllPendingTasks();
			storageController.clearCompletedTasks();
			
		} catch (IOException e) {
			System.err.println(ERROR_UNMARK_TASK);
		}	
	}
	
	/*
	 * Tests the save function
	 */
	@Test
	public void testSaveToLocation() {
		
		String pathCurrentDir = "test save file.txt";
		
		//Sections commented off as directories vary on different computers
//		String pathOtherDir = "C:\\Users\\Bowen\\Desktop\\test save.txt";
		
		try {
			initialise();
			
			//Tests saving file to the current directory
			File file = new File(pathCurrentDir);
			assertFalse(file.exists());
			storageController.saveToLocation(pathCurrentDir);
			assertTrue(file.exists());
	
			file.delete();
			
//			//Tests saving file to other directory
//			file = new File(pathOtherDir);
//			assertFalse(file.exists());
//			storageController.saveToLocation(pathOtherDir);
//			assertTrue(file.exists());
//			
//			file.delete();
			
		} catch (IOException e) {
			System.err.println(ERROR_SAVE);
		}
	}

}
//@@author A0125084L