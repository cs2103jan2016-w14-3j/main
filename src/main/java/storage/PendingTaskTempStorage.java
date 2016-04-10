//@@author A0125084L
package main.java.storage;

import java.io.IOException;

public class PendingTaskTempStorage extends TempStorage{
	
	public PendingTaskTempStorage() throws IOException {
		super(new PendingTaskPermStorage());
	}
}
//@@author A0125084L