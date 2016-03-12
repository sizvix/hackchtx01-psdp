package fr.hackchtx01.client.app.resource;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.hackchtx01.infra.util.error.ErrorMessage;

/**
 * Error messages specific to Client app API
 * @author yoan
 */
public enum ClientAppResourceErrorMessage implements ErrorMessage {
	/** Client application not found */
	CLIENT_APP_NOT_FOUND("Client application not found"),
	/** Client application not found */
	CLIENT_APPS_NOT_FOUND("Client applications not found for owner id : %s"),
	/** Client application Id is a mandatory field to update a client application */
	MISSING_CLIENT_APP_ID_FOR_UPDATE("Client application Id is a mandatory field to update a client application"),
	/** Client application with Id : %s aready exists */
	ALREADY_EXISTING_CLIENT_APP("Client application with Id : %s aready exists");

	private String message;
	
	private ClientAppResourceErrorMessage(String message) {
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