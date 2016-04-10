//@@author A0125084L
package main.java.storage;

import java.io.IOException;

/**
 * This class stores and modifies all pending tasks in the pending tasks list
 * @author Bowen
 *
 */
public class PendingTaskTempStorage extends TempStorage{
	
	/**
	 * Creates a PendingTaskTempStorage instance
	 * @throws IOException If an I/O error occurs
	 */
	public PendingTaskTempStorage() throws IOException {
		super(new PendingTaskPermStorage());
	}
}
//@@author A0125084L