package fr.hackchtx01.authentication.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.hackchtx01.infra.util.error.ErrorMessage;

/**
 * Error messages specific to the OAuth2 access token repository
 * @author yoan
 */
public enum OAuth2AccessTokenRepositoryErrorMessage implements ErrorMessage {
	/** Invalid access token : %s while %s */
	PROBLEM_INVALID_ACCESS_TOKEN("Invalid access token : %s while %s"),
	/** Unable to insert access token : %s because user ID is null */
	PROBLEM_INSERT_USER_ID_NULL("Unable to insert access token : %s because user ID is null"),
	/** Error while reading access token : %s */
	PROBLEM_READ_ACCESS_TOKEN("Error while reading access token : %s"),
	/** Error while creating access token : %s */
	PROBLEM_CREATION_ACCESS_TOKEN("Error while creating access token : %s"),
	/** Error while deleting access token : %s */
	PROBLEM_DELETE_ACCESS_TOKEN("Error while deleting access token : %s");
	
	private String message;
	
	private OAuth2AccessTokenRepositoryErrorMessage(String message) {
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