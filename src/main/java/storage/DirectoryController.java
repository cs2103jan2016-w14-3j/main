package main.java.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryController {

	private static final String DIR_FILE_NAME = "Directory Info.txt";
	
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private File dirFile;
	private FileWriter fileWriter;
	private String taskFilePath;

	public DirectoryController() {
		
	}
	
	public DirectoryController(String fileName) {

		initialiseFileDir(fileName);
	}

	private void initialiseFileDir(String fileName) {

		dirFile = new File(DIR_FILE_NAME);

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
				taskFilePath = new File("").getAbsolutePath() + "\\" + fileName;
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
	
	public void changeDirectory(File file, String path) {

		try {
			
			Files.move(file.toPath(), Paths.get(path));
			updateDirectory(path);
		} catch (IOException e) {
			System.err.println("Invalid path");
		}
	}
	
	private void updateDirectory(String path) {
		
		clearDirFile();
		writeDirectory(path);
	}

	public Boolean renameTaskFile(File file, String name) {

		File newFile = new File(name);
		Boolean isSuccess = file.renameTo(newFile);

		return isSuccess;
	}
	
	private void clearDirFile() {
		
		try {
			fileWriter = new FileWriter(dirFile);
			fileWriter.close();
		} catch (IOException e) {
			System.err.println("Cannot clear file");
		}
	}
}
