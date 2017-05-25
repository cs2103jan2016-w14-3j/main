//@@author A0125084L
package main.java.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
 
import com.google.gson.Gson;

import main.java.data.Task;
import main.java.exception.NoFileNameException;

/**
 * Contains methods that read and write tasks to the task file.
 * @author Hou Bo Wen
 *
 */
public class PermStorage {

	private static final String ERROR_COPY = "Error copying file";
	private static final String ERROR_WRITE_TO_FILE = "Error writing to task file.";
	private static final String ERROR_READ_FROM_FILE = "Error reading from task file.";
	private static final String ERROR_READ_WHILE_DELETE = "Error reading from task file while deleting.";
	private static final String ERROR_CLEAR_FILE = "Cannot clear task file";
	private static final String ERROR_CREATE_FILE = "Cannot create task file";
	private static final String ERROR_CREATE_STREAM = "Cannot create streams for task file";
	private static final String ERROR_REOPEN_STREAM = "Cannot reopen streams for task file";	
	private static final String FILE_NAME = "\\Completed Tasks.txt";
	
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private DirectoryController dirController;
	private File taskFile;
	private FileWriter fileWriter;
	private Gson gson;
	private ArrayList<Task> taskList;

	/**
	 * Creates a PermStorage instance 
	 * @throws IOException If an I/O error occurs
	 */
	public PermStorage() throws IOException {
		initialiseFile(new File("").getAbsolutePath() + FILE_NAME);
		gson = new Gson();
		taskList = new ArrayList<Task>();
	}
	
	/**
	 * Creates a PermStorage instance
	 * @param dirController The directoryController to be linked to
	 * @throws IOException If an I/O error occurs
	 */
	public PermStorage(DirectoryController dirController) throws IOException {
		this.dirController = dirController;
		initialiseFile(dirController.getTaskFilePath());
		gson = new Gson();
		taskList = new ArrayList<Task>();
	}
	
	/**
	 * Changes the working directory to the given path
	 * @param path The path of the file in the new working directory
	 * @throws IOException If an I/O error occurs
	 */
	public void moveToLocation(String path) throws IOException {
		File newFile = new File(path);
		Files.copy(taskFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		taskFile = newFile;
		reopenStream();
		dirController.updateDirectory(path);
	}
	
	/**
	 * Loads the file based on the given path 
	 * @param path The path of the file to be loaded
	 * @throws IOException If an I/O error occurs
	 */ 
	public void loadFromFile(String path) throws IOException {
		taskFile = new File(path);
		reopenStream();
		dirController.updateDirectory(path);
		System.out.println(path);
	}
	
	/**
	 * Makes a copy of the task file in the given path
	 * @param path The path of the file to be saved in
	 * @throws IOException If an I/O error occurs
	 * @throws NoFileNameException If no file name is specified in the path
	 */
	public void saveToLocation(String path) throws IOException {
	
		File newFile = new File(path);
		
		try {
			Files.copy(taskFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IOException(ERROR_COPY);
		}
	}
	
	/**
	 * Writes the given task to the task file
	 * @param task The task to be written
	 * @throws IOException If an I/O error occurs
	 */
	public void writeToFile(Task task) throws IOException {

		try {
			bufferedWriter.write(gson.toJson(task));
			bufferedWriter.newLine();
			bufferedWriter.flush();
		} catch (IOException e) {
			throw new IOException(ERROR_WRITE_TO_FILE);
		}
	}
	
	/**
	 * Replaces the task at the given line number with the editedTask task
	 * @param lineNum The line number of the task to be edited
	 * @param editedTask The task that has been edited
	 * @throws IOException If an I/O error occurs
	 */
	public void editToFile(int lineNum, Task editedTask) throws IOException {
		deleteFromFile(lineNum);
		writeToFile(editedTask);
	}
	
	/**
	 * Returns the list of tasks from the task file
	 * @return The list of tasks from the task file
	 * @throws IOException If an I/O error occurs
	 */
	public ArrayList<Task> readFromFile() throws IOException {
		String lineRead;
		taskList.clear();
		
		try {
			while ((lineRead = bufferedReader.readLine()) != null) {
				Task taskRead = gson.fromJson(lineRead, Task.class);
				if (taskRead != null) {
					taskList.add(taskRead);
				}
			}
		} catch (IOException e) {
			throw new IOException(ERROR_READ_FROM_FILE);
		}
		reopenStream();

		return taskList;
	}
	
	/**
	 * Removes the task at the give line number in the task file
	 * @param lineNum The line number of the task to be deleted
	 * @throws IOException If an I/O error occurs
	 */
	public void deleteFromFile(int lineNum) throws IOException {
		
		ArrayList<Task> tempTaskList = new ArrayList<Task>();
		int currentLineNum = 0;   //first line is of index 0
		String lineRead;
		
		try {
			while ((lineRead = bufferedReader.readLine()) != null) {
				if (currentLineNum != lineNum) {
					Task taskRead = gson.fromJson(lineRead, Task.class);
					tempTaskList.add(taskRead);
				}
				currentLineNum++;
			}
		} catch (IOException e) {
			throw new IOException(ERROR_READ_WHILE_DELETE);
		}
		
		clearFile();

		for (int i = 0; i < tempTaskList.size(); i++) {
			writeToFile(tempTaskList.get(i));
		}	
		
		reopenStream();
	}
	
	/**
	 * Clears the file of all tasks
	 * @throws IOException If an I/O error occurs
	 */
	public void clearFile() throws IOException {
		
		try {
			fileWriter = new FileWriter(taskFile);
			fileWriter.close();
		} catch (IOException e) {
			throw new IOException(ERROR_CLEAR_FILE);
		}
	}

	/**
	 * Copies the given list of tasks to the file, overwriting any content in the file 
	 * @param list The list of tasks to be copied to the file
	 * @throws IOException If an I/O error occurs
	 */
	public void copyAllToFile(ArrayList<Task> list) throws IOException {
		
		clearFile();
		for(int i = 0; i < list.size(); i++) {
			writeToFile(list.get(i));
		}
	}
	
	/*
	 * Creates the task file and streams for reading and writing
	 */
	private void initialiseFile(String filePath) throws IOException {
		
		taskFile = new File(filePath);
		
		try {
			if (!taskFile.exists()) {
				taskFile.createNewFile();			
			}
		} catch (IOException e) {
			throw new IOException(ERROR_CREATE_FILE);
		}
		
		try {
			bufferedReader = new BufferedReader(new FileReader(taskFile));
			bufferedWriter = new BufferedWriter(new FileWriter(taskFile, true));
		} catch (IOException e) {
			throw new IOException(ERROR_CREATE_STREAM);
		}
	}

	/*
	 * Creates new streams to allow reading and writing from the top of the file
	 */
	private void reopenStream() throws IOException {
		
		try {
			bufferedReader.close();
			bufferedWriter.close();
			bufferedReader = new BufferedReader(new FileReader(taskFile));	
			bufferedWriter = new BufferedWriter(new FileWriter(taskFile, true));
		} catch (IOException e) {
			throw new IOException(ERROR_REOPEN_STREAM);
		}
	}
}
//@@author A0125084L