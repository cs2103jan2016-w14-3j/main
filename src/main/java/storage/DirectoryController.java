package main.java.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

public class DirectoryController {
	
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private File dirFile;
	private FileWriter fileWriter;
	private String taskFilePath;

	public DirectoryController() {
		
	}
	
	public DirectoryController(String taskFileName, String dirFileName) {

		initialiseFileDir(taskFileName, dirFileName);
	}

	private void initialiseFileDir(String taskFileName, String dirFileName) {

		dirFile = new File(dirFileName);

		if(!dirFile.exists()) {
			try {
				dirFile.createNewFile();
			} catch (IOException e) {
				System.err.println("Error creating directory file");
			}
		}

		try {
			bufferedReader = new BufferedReader(new FileReader(dirFile));
			bufferedWriter = new BufferedWriter(new FileWriter(dirFile, true));
		} catch (IOException e) {
			System.err.println("Error creating stream");
		}

		String lineRead;
		
		try {
			if((lineRead = bufferedReader.readLine()) != null) {
				taskFilePath = lineRead;
			}
			else {
				taskFilePath = new File("").getAbsolutePath() + "\\" + taskFileName;
			}
		} catch (IOException e) {
			System.err.println("Error reading from file");
		}
	}
	
	public String getTaskFilePath() {

		return taskFilePath;
	}

	public void writeDirectory(String dir) {
		
		try {
			bufferedWriter.write(dir);
			bufferedWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveToFile(File file, String path) {

		File newFile = new File(path);
		try {
			Files.copy(file.toPath(), newFile.toPath());
		} catch (IOException e) {
			System.err.println("Error saving file");
		}
		//updateDirectory(path);
	}
	
//	public void updateDirectory(String path) {
//		
//		clearDirFile();
//		writeDirectory(path);
//	}

	public Boolean renameTaskFile(File file, String name) {

		File newFile = new File(name);
		Boolean isSuccess = file.renameTo(newFile);

		return isSuccess;
	}
	
	protected void clearDirFile() {
		
		try {
			fileWriter = new FileWriter(dirFile);
			fileWriter.close();
		} catch (IOException e) {
			System.err.println("Cannot clear file");
		}
	}
}
