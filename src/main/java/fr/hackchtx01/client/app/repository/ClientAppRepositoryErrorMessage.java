package fr.hackchtx01.client.app.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.hackchtx01.infra.util.error.ErrorMessage;

/**
 * Error messages specific to the client app repository
 * @author yoan
 */
public enum ClientAppRepositoryErrorMessage implements ErrorMessage {
	/** Secret does not comply to the minimum security level */
	PROBLEM_SECRET_VALIDITY("Secret does not comply to the minimum security level"),
	/** Error while reading client app : %s */
	PROBLEM_READ_CLIENT_APP("Error while reading client app : %s"),
	/** Error while reading this user client applications : %s */
	PROBLEM_READ_USER_CLIENT_APPS("Error while reading this user lient applications : %s"),
	/** Error while creating client app : %s */
	PROBLEM_CREATION_CLIENT_APP("Error while creating client app : %s"),
	/** Error while deleting client app : %s */
	PROBLEM_DELETE_CLIENT_APP("Error while deleting client app : %s"),
	/** Error while updating client app secret : %s */
	PROBLEM_UPDATE_CLIENT_APP_SECRET("Error while updating client app secret : %s"),
	/** Error while updating client app : %s */
	PROBLEM_UPDATE_CLIENT_APP("Error while updating client app : %s"),;
	
	private String message;
	
	private ClientAppRepositoryErrorMessage(String message) {
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
