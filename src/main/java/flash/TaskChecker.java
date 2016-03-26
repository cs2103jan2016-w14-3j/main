package main.java.flash;

public class TaskChecker implements Runnable {
	private Thread taskChecker;
	private String threadName;

	TaskChecker( String name){
		threadName = name;
		
	}
	public void run() {
		
		try {
			while (true){
				//start checking here
				Thread.sleep(5 * 60 * 1000);
			}
		} catch (InterruptedException e) {
			System.out.println("Thread " +  threadName + " interrupted.");
		}
	}

	public void start ()
	{
		//System.out.println("Starting " +  threadName );
		if (taskChecker == null)
		{
			taskChecker = new Thread (this, threadName);
			taskChecker.start ();
		}
	}

}
