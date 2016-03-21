package main.java.storage;

public class CompletedTaskPermStorage extends PermStorage{

	public CompletedTaskPermStorage() {
		super(new CompletedTaskDirController());
	}
}
