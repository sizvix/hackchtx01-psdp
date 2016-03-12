/**
 * 
 */
package fr.hackchtx01.infra.util.error;

/**
 * Common interface for all the error codes
 * @author yoan
 */
public interface ErrorMessage {
	/**
	 * Get the error message as a String
	 * @return error message
	 */
	public String getDevReadableMessage();
	
	/**
	 * Get the message formated with the provided params
	 * @param params
	 * @return formated error message
	 */
	public String getDevReadableMessage(Object... params);
}
