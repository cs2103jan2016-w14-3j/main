/* @@author A0127481E */
package main.java.parser;

import java.nio.file.InvalidPathException;

import Enumeration.CommandType;
import Exception.InvalidInputFormatException;
import Exception.NoFileNameException;

/**
 * This parser parses storage related commands including "move" and "save".
 * @author Ouyang Danwen
 *
 */
public class StorageCommandParser {
	
	/* error messages used in this class */
	private static final String ERROR_MESSAGE_EMPTY_PATH = "Please enter non-empty file path!";
	private static final String ERROR_NO_FILE_NAME = "No file name entered!";
	private static final String ERROR_INVALID_PATH = "Invalid path entered!";
	
	/* numeric indices to access parameters array */
	private static final int TASK = 0;
	
	/**
	 * Empty constructor.
	 */
	public StorageCommandParser() {}

	/**
	 * @param commandType
	 * @param commandContent
	 * @return the command parameters as a string array
	 * @throws InvalidInputFormatException
	 * @throws NoFileNameException 
	 */
	public String[] determineParameters(CommandType commandType, String commandContent) 
			throws InvalidInputFormatException, NoFileNameException {
		assert commandType != null;
		assert commandContent != null;

		String[] parameters = new String[5];
		
		if (commandContent.isEmpty()) {
			throw new InvalidInputFormatException(ERROR_MESSAGE_EMPTY_PATH);
		}

		else {
			String path = commandContent;
			parameters[TASK] = validifyPath(path);
		}

		return parameters;
	}

	/**
	 * Check the validity of the path.
	 * Return the validified path or throw exceptions otherwise.
	 * @param path
	 * @return the validified path
	 * @throws NoFileNameException
	 * @throws InvalidPathException
	 */
	private String validifyPath(String path) throws NoFileNameException, InvalidPathException {
		assert path != null;
		
		//append the file type for user
		if (!path.endsWith(".txt") && !path.endsWith("/") 
				&& Character.isLetter(path.charAt(path.length()-1))) {
			path = path.concat(".txt");
		} 
		
		//append the file type for user
		else if (path.endsWith(".")) {
			path = path.concat("txt");
		} 
		
		//invalid file ending
		else if (path.endsWith("/")) {
			throw new NoFileNameException(ERROR_NO_FILE_NAME);
		} 
		
		//invalid path in general
		else {
			throw new InvalidPathException(path, ERROR_INVALID_PATH);
		}
		
		return path;
	}

}
/* @@author A0127481E */