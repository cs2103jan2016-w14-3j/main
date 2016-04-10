//@@author A0125084L
package main.java.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

/**
 * Contains methods to read and write to a file, which stores
 * the path of the current working directory.
 * @author Hou Bo Wen
 *
 */
public class DirectoryController {
	
	private static final String ERROR_CREATE_FILE = "Cannot create directory file";
	private static final String ERROR_CREATE_STREAM = "Cannot create streams for directory file";
	private static final String ERROR_READ_FROM_FILE = "Error reading from directory file";
	private static final String ERROR_WRITE_TO_FILE = "Error writing to directory file";
	private static final String ERROR_CLEAR_FILE = "Cannot clear file";
	
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private File directoryFile;
	private FileWriter fileWriter;
	private String taskFilePath;

	/**
	 * Creates a DirectoryController instance
	 */
	public DirectoryController() {
		
	}
	
	/**
	 * Creates a DirectoryController instance
	 * @param taskFileName The name of the task file 
	 * @param dirFileName The name of the directory file
	 * @throws IOException If an I/O error occurs
	 */
	public DirectoryController(String taskFileName, String dirFileName) throws IOException {
		initialiseFileDirectory(taskFileName, dirFileName);
	}

	/**
	 * Returns the path of the task file in the current working directory
	 * @return The path of the task file in the current working directory
	 */
	public String getTaskFilePath() {
		return taskFilePath;
	}

	/**
	 * Updates the new path of where the task file is stored
	 * @throws IOException If an I/O error occurs
	 */
	public void updateDirectory(String path) throws IOException {	
		clearDirectoryFile();
		writeDirectory(path);
	}
	
	/*
	 * Creates a file to store the path of the task file
	 */
	private void initialiseFileDirectory(String taskFileName, String dirFileName) throws IOException{
		directoryFile = new File(dirFileName);

		if (!directoryFile.exists()) {
			try {
				directoryFile.createNewFile();
			} catch (IOException e) {
				throw new IOException(ERROR_CREATE_FILE);
			}
		}

		try {
			bufferedReader = new BufferedReader(new FileReader(directoryFile));
			bufferedWriter = new BufferedWriter(new FileWriter(directoryFile, true));
		} catch (IOException e) {
			throw new IOException(ERROR_CREATE_STREAM);
		}

		String lineRead;
		
		try {
			if ((lineRead = bufferedReader.readLine()) != null) {
				taskFilePath = lineRead;
			} else {
				taskFilePath = new File("").getAbsolutePath() + "\\" + taskFileName;
			}
		} catch (IOException e) {
			throw new IOException(ERROR_READ_FROM_FILE);
		}
	}
	
	/*
	 * Writes the path of the task file to the directory file
	 */
	private void writeDirectory(String dir) throws IOException{
		
		try {
			bufferedWriter.write(dir);
			bufferedWriter.flush();
		} catch (IOException e) {
			throw new IOException(ERROR_WRITE_TO_FILE);
		}
	}

	/*
	 * Clears the directory file
	 */
	private void clearDirectoryFile() throws IOException {
		
		try {
			fileWriter = new FileWriter(directoryFile);
			fileWriter.close();
		} catch (IOException e) {
			throw new IOException(ERROR_CLEAR_FILE);
		}
	}
}
//@@author A0125084L