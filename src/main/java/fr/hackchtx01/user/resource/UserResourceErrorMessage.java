package fr.hackchtx01.user.resource;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.hackchtx01.infra.util.error.ErrorMessage;

/**
 * Error messages specific to User API
 * @author yoan
 */
public enum UserResourceErrorMessage  implements ErrorMessage {
	/** User not found */
	USER_NOT_FOUND("User not found"),
	/** User Id is a mandatory field to update an user */
	MISSING_USER_ID_FOR_UPDATE("User Id is a mandatory field to update an user"),
	/** User with Id : %s aready exists */
	ALREADY_EXISTING_USER("User with Id : %s already exists");

	private String message;
	
	private UserResourceErrorMessage(String message) {
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