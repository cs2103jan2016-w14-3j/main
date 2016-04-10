//@@author A0125084L
package main.java.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import com.google.gson.Gson;
import main.java.data.Task;

/**
 * Contains methods that read and write tasks to the task file.
 * @author Bowen
 *
 */
public class PermStorage {
	
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
	 * @throws IOException 
	 */
	public PermStorage() throws IOException {
		initialiseFile(new File("").getAbsolutePath() + FILE_NAME);
		gson = new Gson();
		taskList = new ArrayList<Task>();
	}
	
	/**
	 * Creates a PermStorage instance
	 * @param dirController
	 * @throws IOException 
	 */
	public PermStorage(DirectoryController dirController) throws IOException {
		this.dirController = dirController;
		initialiseFile(dirController.getTaskFilePath());
		gson = new Gson();
		taskList = new ArrayList<Task>();
	}
	
	/**
	 * Changes the working directory to the path specified
	 * @param path
	 * @throws IOException
	 */
	public void moveToLocation(String path) throws IOException {
		File newFile = new File(path);
		Files.copy(taskFile.toPath(), newFile.toPath());
		taskFile = newFile;
		reopenStream();
		dirController.updateDirectory(path);
	}
	
	/**
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void loadFromFile(String path) throws IOException {
		taskFile = new File(path);
		reopenStream();
		dirController.updateDirectory(path);
	}
	
	/**
	 * 
	 * @param path
	 * @throws Exception
	 */
	public void saveToLocation(String path) throws Exception {

		if(!path.endsWith(".txt") && !path.endsWith("/")) {
			path = path.concat(".txt");
		} else if(path.endsWith("/")) {
			throw new Exception("No file name entered");
		}
		File newFile = new File(path);
		Files.copy(taskFile.toPath(), newFile.toPath());
	}
	
	/**
	 * 
	 * @param task
	 * @throws IOException
	 */
	public void writeToFile(Task task) throws IOException {

		try {
			bufferedWriter.write(gson.toJson(task));
			bufferedWriter.newLine();
			bufferedWriter.flush();
		} catch (IOException e){
			throw new IOException("Error writing to task file.");
		}
	}
	
	/**
	 * 
	 * @param lineNum
	 * @param editedTask
	 * @throws IOException
	 */
	public void editToFile(int lineNum, Task editedTask) throws IOException {
		deleteFromFile(lineNum);
		writeToFile(editedTask);
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public ArrayList<Task> readFromFile() throws IOException {
		String lineRead;
		taskList.clear();
		
		try {
			while((lineRead = bufferedReader.readLine()) != null) {
				Task taskRead = gson.fromJson(lineRead, Task.class);
				if(taskRead != null) {
					taskList.add(taskRead);
				}
			}
		} catch (IOException e) {
			throw new IOException("Error reading from task file.");
		}
		reopenStream();

		return taskList;
	}
	
	/**
	 * 
	 * @param lineNum
	 * @throws IOException
	 */
	public void deleteFromFile(int lineNum) throws IOException {
		
		ArrayList<Task> tempTaskList = new ArrayList<Task>();
		int currentLineNum = 0;   //first line is of index 0
		String lineRead;
		
		try {
			while((lineRead = bufferedReader.readLine()) != null) {
				if(currentLineNum != lineNum) {
					Task taskRead = gson.fromJson(lineRead, Task.class);
					tempTaskList.add(taskRead);
				}
				currentLineNum++;
			}
		} catch (IOException e) {
			throw new IOException("Error reading from task file while deleting.");
		}
		
		clearFile();

		for(int i=0; i<tempTaskList.size(); i++) {
			writeToFile(tempTaskList.get(i));
		}	
		
		reopenStream();
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void clearFile() throws IOException {
		
		try {
			fileWriter = new FileWriter(taskFile);
			fileWriter.close();
		} catch (IOException e) {
			throw new IOException("Cannot clear directory file");
		}
	}

	/**
	 * 
	 * @param list
	 * @throws IOException
	 */
	public void copyAllToFile(ArrayList<Task> list) throws IOException {
		
		clearFile();
		for(int i=0; i<list.size(); i++) {
			writeToFile(list.get(i));
		}
	}
	
	/*
	 * Creates the task file and streams for reading and writing
	 */
	private void initialiseFile(String filePath) throws IOException {
		
		taskFile = new File(filePath);
		
		try {
			if(!taskFile.exists()) {
				taskFile.createNewFile();			
			}
		} catch (IOException e) {
			throw new IOException("Cannot create task file");
		}
		
		try {
			bufferedReader = new BufferedReader(new FileReader(taskFile));
			bufferedWriter = new BufferedWriter(new FileWriter(taskFile, true));
		} catch (IOException e) {
			throw new IOException("Cannot create streams for task file");
		}
	}

	/*
	 * 
	 */
	private void reopenStream() throws IOException {
		
		try {
			bufferedReader.close();
			bufferedWriter.close();
			bufferedReader = new BufferedReader(new FileReader(taskFile));	
			bufferedWriter = new BufferedWriter(new FileWriter(taskFile, true));
		} catch (IOException e) {
			throw new IOException("Cannot reopen streams for task file");
		}
	}
}
//@@author A0125084L