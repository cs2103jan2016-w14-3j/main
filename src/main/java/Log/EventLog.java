//@@author A0125084L
package main.java.Log;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class EventLog {

	Logger logger;
	FileHandler fileHandler;
	CustomFormatter formatter;
	
	public EventLog() {
	
		logger = Logger.getLogger("FlashPointLogger");
		try {
			fileHandler = new FileHandler("Eventlog.log");
			formatter = new CustomFormatter();
			fileHandler.setFormatter(formatter);
			logger.addHandler(fileHandler);
			logger.setUseParentHandlers(false);
		} catch (SecurityException | IOException e) {
			System.err.println("Error creating FileHandler");
		}
	}
	
	public Logger getLogger() {
		return logger;
	}
}
//@@author A0125084L