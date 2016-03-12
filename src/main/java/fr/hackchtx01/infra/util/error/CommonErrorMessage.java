/**
 * 
 */
package fr.hackchtx01.infra.util.error;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Enumeration of all non specific error messages
 * @author yoan
 */
public enum CommonErrorMessage implements ErrorMessage {
	/** Invalid %s */
	INVALID("Invalid %s"),
	/** Missing %s */
	MISSING("Missing %s"),
	/** %S not found */
	NOT_FOUND("%s not found"),
	/** Problem with %s URL */
	PROBLEM_WITH_URL("Problem with %s URL"),
	/** Unable to find %s from : %s */
	UNABLE_TO_FIND("Unable to find %s from : %s"),
	/** Unknown %s */
	UNKNOWN("Unknown %s");

	private String message;
	
	private CommonErrorMessage(String message) {
		checkArgument(isNotBlank(message), "An error message should not be empty");
		this.message = message;
	}
	
	@Override
	public String getDevReadableMessage() {
		return message;
	}

	@Override
	public String getDevReadableMessage(Object... params) {
		return String.format(message, params);
	}

}
