package main.java.backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import com.google.gson.Gson;

import main.java.data.Task;

public class Storage {

	private ArrayList<Task> taskList;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private File file;
	private FileWriter fileWriter;
	private String fileName;
	private Gson gson;
	
	public Storage() throws Exception{
		
		initialiseFile("Task List.txt");
		gson = new Gson();
		taskList = new ArrayList<Task>();
	}
	
	//create the file and streams
	public void initialiseFile(String name) throws Exception {
		
		fileName = name;
		file = new File(fileName);
		
		if(!file.exists()) {
			file.createNewFile();
		}		
		bufferedReader = new BufferedReader(new FileReader(file));
		bufferedWriter = new BufferedWriter(new FileWriter(file, true));
	}

	public void writeToFile(Task task) throws Exception {
		 bufferedWriter.write(gson.toJson(task));
		 bufferedWriter.newLine();
    	 bufferedWriter.flush();
	}
	
	public void editToFile(int lineNum, Task editedTask) throws Exception {
		
		deleteFromFile(lineNum);
		writeToFile(editedTask);
	}
	
	public ArrayList<Task> readFromFile() throws Exception {
		String lineRead;
		
		while((lineRead = bufferedReader.readLine()) != null) {
			String taskString = bufferedReader.readLine();
			Task taskRead = gson.fromJson(taskString, Task.class);
			taskList.add(taskRead);
		}
		return taskList;
	}
	
	public void deleteFromFile(int lineNum) throws Exception {
		ArrayList<Task> tempTaskList = new ArrayList<Task>();
		int currentLineNum = 0;   //first line is of index 0
		String lineRead;
		
		while((lineRead = bufferedReader.readLine()) != null) {
			if(currentLineNum != lineNum) {
				String taskString = bufferedReader.readLine();
				Task taskRead = gson.fromJson(taskString, Task.class);
				tempTaskList.add(taskRead);
			}
			currentLineNum++;
		}
		clearFile();
		
		for(int i=0; i<tempTaskList.size(); i++) {
			writeToFile(tempTaskList.get(i));
		}	
	}
	
	public void clearFile() throws Exception {
		fileWriter = new FileWriter(file);
		fileWriter.close();
	}
	
	public void sortFile() {
		
	}
}
