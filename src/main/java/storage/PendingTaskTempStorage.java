package main.java.storage;

public class PendingTaskTempStorage extends TempStorage{
	
	public PendingTaskTempStorage() {
		super(new PendingTaskPermStorage());
	}
}
