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
	
	public void editToFile(Task task) throws Exception {
		
		
	}
	
	public void deleteFromFile(String phraseToSearch) throws Exception {
		taskList = new ArrayList<Task>();
		String phrase = phraseToSearch;
		String lineRead;
		
		while((lineRead = bufferedReader.readLine()) != null) {
			if(lineRead.contains(phrase)) {
				//Don't write the line that you want to delete
			}
			else {
				String taskString = bufferedReader.readLine();
				Task taskRead = gson.fromJson(taskString, Task.class);
				taskList.add(taskRead);
			}
		}
		clearFile();
		
		for(int i=0; i<taskList.size(); i++) {
			writeToFile(taskList.get(i));
		}	
	}
	
	public void clearFile() throws Exception {
		fileWriter = new FileWriter(file);
		fileWriter.close();
	}
	
	public void sortFile() {
		
	}
}
