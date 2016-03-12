package fr.hackchtx01.authentication.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.hackchtx01.infra.util.error.ErrorMessage;

/**
 * Error messages specific to the OAuth2 authorization code repository
 * @author yoan
 */
public enum OAuth2AuthorizationCodeRepositoryErrorMessage implements ErrorMessage {
	/** Invalid authorization code : %s while %s */
	PROBLEM_INVALID_AUTH_CODE("Invalid authorization code : %s while %s"),
	/** Unable to insert auth code : %s because user ID is null */
	PROBLEM_INSERT_USER_ID_NULL("Unable to insert auth code : %s because user ID is null"),
	/** Error while reading authorization code : %s */
	PROBLEM_READ_AUTH_CODE("Error while reading authorization code : %s"),
	/** Error while creating authorization code : %s */
	PROBLEM_CREATION_AUTH_CODE("Error while creating authorization code : %s"),
	/** Error while deleting authorization code : %s */
	PROBLEM_DELETE_AUTH_CODE("Error while deleting authorization code : %s");
	
	private String message;
	
	private OAuth2AuthorizationCodeRepositoryErrorMessage(String message) {
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
