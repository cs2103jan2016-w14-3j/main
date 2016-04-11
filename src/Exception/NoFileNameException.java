//@@author A0125084L
package Exception;

/**
 * This class models a customized exception object to handle exception
 * caused by an absence of file name
 * @author Hou Bo Wen
 *
 */
public class NoFileNameException extends Exception{

	/**
	 * Constructs a NoFileNameException with the specified detail message.
	 * @param message The detail message
	 */
	public NoFileNameException(String message) {
		super(message);
	}
}
//@@author A0125084L