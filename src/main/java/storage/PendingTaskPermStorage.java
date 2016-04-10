//@@author A0125084L
package main.java.storage;

import java.io.IOException;

public class PendingTaskPermStorage extends PermStorage{
	
	public PendingTaskPermStorage() throws IOException {
		super(new DirectoryController("Upcoming Tasks.txt", "Directory Info.txt"));
	}
}
//@@author A0125084L