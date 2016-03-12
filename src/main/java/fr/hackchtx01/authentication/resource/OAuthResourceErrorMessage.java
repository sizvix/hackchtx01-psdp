package fr.hackchtx01.authentication.resource;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import fr.hackchtx01.infra.util.error.ErrorMessage;

/**
 * Enumeration of all Oauth resources related error messages 
 * @author yoan
 */
public enum OAuthResourceErrorMessage implements ErrorMessage {
	/** Grant type %s is not implemented */
	GRANT_TYPE_NOT_IMPLEMENTED("Grant type %s is not implemented"),
	/** Invalid authorization code : %s */
	INVALID_AUTHZ_CODE("Invalid authorization code : %s"),
	/** Invalid secret for client with id : %s */
	INVALID_CLIENT_SECRET("Invalid secret for client with id : %s"),
	/** Invalid OAuth callback URL provided by client : %s */
	INVALID_REDIRECT_URI("Invalid OAuth callback URL provided by client : %s"),
	/** OAuth client secret needs to be provided by client! */
	MISSING_CLIENT_SECRET("OAuth client secret needs to be provided by client!"),
	/** OAuth callback url needs to be provided by client! */
	MISSING_REDIRECT_URI("OAuth callback URL needs to be provided by client!"),
	/** Client with id %s is unknown */
	UNKNOWN_CLIENT("Client with id %s is unknown");
	
	private String message;
	
	private OAuthResourceErrorMessage(String message) {
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
