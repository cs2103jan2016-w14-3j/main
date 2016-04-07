//@@author A0125084L
package main.java.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;

import com.google.gson.Gson;
import main.java.data.Task;

public class PermStorage {
	
	private ArrayList<Task> taskList;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private File file;
	private FileWriter fileWriter;
	private DirectoryController dirController;
	private Gson gson;

	public PermStorage() {
		
		initialiseFile(new File("").getAbsolutePath() + "\\Completed Tasks.txt");
		gson = new Gson();
		taskList = new ArrayList<Task>();
	}
	
	public PermStorage(DirectoryController dirController) {
	
		this.dirController = dirController;
		initialiseFile(dirController.getTaskFilePath());
		gson = new Gson();
		taskList = new ArrayList<Task>();
	}
	
	//create the file and streams
	private void initialiseFile(String filePath) {
		
		file = new File(filePath);
		
		try {
			if(!file.exists()) {
				file.createNewFile();			
			}
		} catch (IOException e) {
			System.err.println("Cannot create file");
		}
		
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			bufferedWriter = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e) {
			System.err.println("Cannot create file writer");
		}
	}

	public void moveToLocation(String path) throws IOException {
		
		File newFile = new File(path);
		Files.copy(file.toPath(), newFile.toPath());
		file = newFile;
		reopenStream();
		dirController.moveToLocation(path);
	}
	
	public void loadFromFile(String path) {
		
		file = new File(path);
		reopenStream();
		dirController.loadFromFile(path);
	}
	
	public void saveToLocation(String path) throws IOException {
		
		File newFile = new File(path);
		Files.copy(file.toPath(), newFile.toPath());
	}
	
	public void writeToFile(Task task) {

		try {
			bufferedWriter.write(gson.toJson(task));
			bufferedWriter.newLine();
			bufferedWriter.flush();
		} catch (IOException e){
			System.err.println("Error writing to file.");
		}
	}
	
	public void editToFile(int lineNum, Task editedTask) {
		deleteFromFile(lineNum);
		writeToFile(editedTask);
	}
	
	public ArrayList<Task> readFromFile() {
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
			System.err.println("Error reading from file.");
		}
		reopenStream();

		return taskList;
	}
	
	public void deleteFromFile(int lineNum) {
		
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
			System.err.println("Error reading from file when deleting.");
		}
		
		clearFile();

		for(int i=0; i<tempTaskList.size(); i++) {
			writeToFile(tempTaskList.get(i));
		}	
		
		reopenStream();
	}
	
	public void clearFile() {
		
		try {
			fileWriter = new FileWriter(file);
			fileWriter.close();
		} catch (IOException e) {
			System.err.println("Cannot clear file");
		}
	}
	
	public void sortFile() {
		
	}
	
	public void copyAllToFile(ArrayList<Task> list) {
		
		clearFile();
		for(int i=0; i<list.size(); i++) {
			writeToFile(list.get(i));
		}
	}
	
	private void reopenStream() {
		
		try {
			bufferedReader.close();
			bufferedWriter.close();
			bufferedReader = new BufferedReader(new FileReader(file));	
			bufferedWriter = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e) {
			System.err.println("Cannot reopen stream");
		}
	}
}
//@@author A0125084L