//@@author A0125084L
package main.java.storage;

public class PendingTaskTempStorage extends TempStorage{
	
	public PendingTaskTempStorage() {
		super(new PendingTaskPermStorage());
	}
}
//@@author A0125084L