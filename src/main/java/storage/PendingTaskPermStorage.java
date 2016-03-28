package main.java.storage;

public class PendingTaskPermStorage extends PermStorage{
	
	public PendingTaskPermStorage() {
		super(new DirectoryController("Upcoming tasks.txt", "Directory Info.txt"));
	}
}
