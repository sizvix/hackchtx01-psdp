package fr.hackchtx01.authentication.resource;

import static java.util.Objects.requireNonNull;

import org.apache.oltu.oauth2.common.message.OAuthResponse;

import fr.hackchtx01.infra.rest.error.Level;
import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.infra.util.error.CommonErrorCode;

/**
 * Custom application exception to wrap an OauthResponse
 * @author yoan
 */
public class OAuthException extends ApplicationException {
	private static final long serialVersionUID = 280982623174139968L;
	
	/** Error criticity level */
	private final OAuthResponse response;
	
	public OAuthException(OAuthResponse response) {
		super(Level.WARNING, CommonErrorCode.API_RESPONSE, "");
		this.response = requireNonNull(response);
	}

	public OAuthResponse getResponse() {
		return response;
	}
}