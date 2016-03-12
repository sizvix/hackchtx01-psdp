package fr.hackchtx01.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.OAuth;

/**
 * Mock an HTTP servlet request in a OAuth2 context
 * @author yoan
 */
public class OauthMockRequestBuilder {
	private HttpServletRequest request;

    public OauthMockRequestBuilder() {
        request = mock(HttpServletRequest.class);
    }

    public OauthMockRequestBuilder withOauthResponseType(String oauthResponseType) {
        when(request.getParameter(OAuth.OAUTH_RESPONSE_TYPE)).thenReturn(oauthResponseType);

        return this;
    }

    public OauthMockRequestBuilder withRedirectUri(String redirectUri) {
        when(request.getParameter(OAuth.OAUTH_REDIRECT_URI)).thenReturn(redirectUri);

        return this;
    }

    public OauthMockRequestBuilder withParam(String paramName, String paramValue) {
        when(request.getParameter(paramName)).thenReturn(paramValue);

        return this;
    }

    public HttpServletRequest build() {
        return request;
    }

    public OauthMockRequestBuilder withContentType(String contentType) {
        when(request.getContentType()).thenReturn(contentType);

        return this;
    }

    public OauthMockRequestBuilder withHttpMethod(String method) {
        when(request.getMethod()).thenReturn(method);

        return this;
    }

    public OauthMockRequestBuilder withClientId(String clientId) {
        when(request.getParameter(OAuth.OAUTH_CLIENT_ID)).thenReturn(clientId);

        return this;
    }

    public OauthMockRequestBuilder withClientSecret(String secret) {
        when(request.getParameter(OAuth.OAUTH_CLIENT_SECRET)).thenReturn(secret);

        return this;
    }

    public OauthMockRequestBuilder withGrantType(String grantType) {
        when(request.getParameter(OAuth.OAUTH_GRANT_TYPE)).thenReturn(grantType);

        return this;
    }

    public OauthMockRequestBuilder withBasicAuthHeader(String authorizationHeader) {
        when(request.getHeader(OAuth.HeaderType.AUTHORIZATION)).thenReturn(authorizationHeader);

        return this;
    }

    public OauthMockRequestBuilder withAccessGrant(String accessGrant) {
        when(request.getParameter(OAuth.OAUTH_CODE)).thenReturn(accessGrant);

        return this;
    }

    public OauthMockRequestBuilder withOauthUsername(String oauthUsername) {
        when(request.getParameter(OAuth.OAUTH_USERNAME)).thenReturn(oauthUsername);

        return this;
    }

    public OauthMockRequestBuilder withOauthPassword(String secret) {
        when(request.getParameter(OAuth.OAUTH_PASSWORD)).thenReturn(secret);

        return this;
    }

    public OauthMockRequestBuilder withOauthRefreshToken(String refreshToken) {
        when(request.getParameter(OAuth.OAUTH_REFRESH_TOKEN)).thenReturn(refreshToken);

        return this;
    }

    public OauthMockRequestBuilder withScopes(String scopes) {
        when(request.getParameter(OAuth.OAUTH_SCOPE)).thenReturn(scopes);

        return this;
    }
}
