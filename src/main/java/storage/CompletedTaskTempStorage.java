package main.java.storage;

public class CompletedTaskTempStorage extends TempStorage{

	public CompletedTaskTempStorage() {
		super(new CompletedTaskPermStorage());
	}
}
