//@@author A0125084L
package main.java.storage;

import java.io.IOException;

/**
 * This class stores and modifies all completed tasks in the temporary tasks list
 * @author Hou Bo Wen
 *
 */
public class CompletedTaskTempStorage extends TempStorage{

	/**
	 * Creates a CompletedTaskTempStorage instance
	 * @throws IOException If an I/O error occurs
	 */
	public CompletedTaskTempStorage() throws IOException {
		super(new CompletedTaskPermStorage());
	}
}
//@@author A0125084L