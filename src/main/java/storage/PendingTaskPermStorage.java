//@@author A0125084L
package main.java.storage;

import java.io.IOException;

/**
 * This class stores and modifies all pending tasks in the pending tasks file
 * @author Hou Bo Wen
 *
 */
public class PendingTaskPermStorage extends PermStorage{
	
	/**
	 * Creates a PendingTaskPermStorage instance
	 * @throws IOException If an I/O error occurs
	 */
	public PendingTaskPermStorage() throws IOException {
		super(new DirectoryController("Upcoming Tasks.txt", "Directory Info.txt"));
	}
}
//@@author A0125084L