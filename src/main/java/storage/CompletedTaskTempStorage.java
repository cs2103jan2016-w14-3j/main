//@@author A0125084L
package main.java.storage;

import java.io.IOException;

public class CompletedTaskTempStorage extends TempStorage{

	public CompletedTaskTempStorage() throws IOException {
		super(new CompletedTaskPermStorage());
	}
}
//@@author A0125084L