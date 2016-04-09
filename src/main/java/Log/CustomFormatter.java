//@@author A0125084L
package main.java.Log;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomFormatter extends Formatter{

	@Override
	public String format(LogRecord record) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(record.getLevel());
		stringBuilder.append(": ");
		stringBuilder.append(record.getMessage());
		stringBuilder.append(" [");
		stringBuilder.append(new Date(record.getMillis()));
		stringBuilder.append("]\n");
		
		return stringBuilder.toString();
	}
}
//@@author A0125084L