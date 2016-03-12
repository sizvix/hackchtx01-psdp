package fr.hackchtx01.infra.config.filter;

import static java.util.Locale.ENGLISH;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.jboss.resteasy.spi.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.hackchtx01.authentication.realm.Oauth2ShiroAccessToken;

/**
 * Shiro filter to extract access token from HTTP request or sending challenge if not fond
 * @author yoan
 */
public class Oauth2AccessTokenAuthenticatingFilter extends AuthenticatingFilter {

    /** HTTP Authorization header : Authorization */
    protected static final String AUTHORIZATION_HEADER = "Authorization";
    /** HTTP Authentication header : WWW-Authenticate */
    protected static final String AUTHENTICATE_HEADER = "WWW-Authenticate";
    /** The name that is displayed during the challenge process of authentication */
    protected static final String APPLICATION_NAME = "PsdpAPI";
    /** Scheme in the Authorization header */
    protected static final String AUTH_SCHEME = "Bearer";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Oauth2AccessTokenAuthenticatingFilter.class);

    /**
     * Processes unauthenticated requests. It handles the two-stage request/challenge authentication protocol.
     * @param request  incoming ServletRequest
     * @param response outgoing ServletResponse
     * @return true if the request should be processed; false if the request should not continue to be processed
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        boolean loggedIn = false; //false by default or we wouldn't be in this method
        if (isLoginAttempt(request, response)) {
            loggedIn = executeLogin(request, response);
        }
        if (!loggedIn) {
            sendChallenge(response);
        }
        return loggedIn;
    }

    @Override
    protected final boolean isLoginRequest(ServletRequest request, ServletResponse response) {
        return isLoginAttempt(request, response);
    }
    
    /**
     * Determines whether the incoming request is an attempt to log in.
     * @param request  incoming ServletRequest
     * @param response outgoing ServletResponse
     * @return true if the incoming request is an attempt to log in based, false otherwise
     */
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        String authzHeader = getAuthzHeader(request);
        return authzHeader != null && isLoginAttempt(authzHeader);
    }
    
    /**
     * Returns the Authorization header from the http request
     * @param request the incoming request
     * @return the Authorization header's value.
     */
    protected String getAuthzHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        return httpRequest.getHeader(AUTHORIZATION_HEADER);
    }

    /**
     * Detect if the provided authzHeader starts with the AUTH_SCHEME
     * @param authzHeader the 'Authorization' header value
     * @return true if the authzHeader value matches the AUTH_SCHEME
     */
    protected boolean isLoginAttempt(String authzHeader) {
        String authzScheme = AUTH_SCHEME.toLowerCase(ENGLISH);
        return authzHeader.toLowerCase(ENGLISH).startsWith(authzScheme);
    }

    /**
     * Builds the challenge for authorization by setting a HTTP 401 (Unauthorized) status as well as the response's WWW-Authenticate header.
     * @param response outgoing ServletResponse
     */
    protected void sendChallenge(ServletResponse response) {
    	LOGGER.debug("Authentication required: sending 401 Authentication challenge response.");
        
    	HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        OAuthResponse oauthResponse;
		try {
			oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
								           .setRealm(APPLICATION_NAME)
								           .setError(OAuthError.ResourceResponse.INVALID_TOKEN)
								           .buildHeaderMessage();
			 httpResponse.setHeader(AUTHENTICATE_HEADER, oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
		} catch (OAuthSystemException e) {
			throw new ApplicationException(e);
		}
    }

    /**
     * Creates an AuthenticationToken for use during login attempt with the provided credentials in the http header.
     * @param request  incoming ServletRequest
     * @param response outgoing ServletResponse
     * @return the AuthenticationToken used to execute the login attempt
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String authorizationHeader = getAuthzHeader(request);
        String host = getHost(request);
        if (StringUtils.isBlank(authorizationHeader)) {
            // Create an empty authentication token since there is no
            // Authorization header.
        	return new Oauth2ShiroAccessToken("", host);
        }

    	LOGGER.debug("Attempting to execute login with headers [" + authorizationHeader + "]");

        String accessToken = extractAccessToken(authorizationHeader);

        return new Oauth2ShiroAccessToken(accessToken, host);
    }

    /**
     * Extract the access token from header value
     * @param authorizationHeader the authorization header obtained from the request.
     * @return the token value or null
     */
    protected static String extractAccessToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            return null;
        }
        String[] authTokens = authorizationHeader.split(" ");
        if (authTokens == null || authTokens.length < 2) {
            return null;
        }
        return authTokens[1];
    }
}
