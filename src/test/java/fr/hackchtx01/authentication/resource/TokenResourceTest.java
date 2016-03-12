package fr.hackchtx01.authentication.resource;

import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.INVALID_AUTHZ_CODE;
import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.INVALID_CLIENT_SECRET;
import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.MISSING_CLIENT_SECRET;
import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.UNKNOWN_CLIENT;
import static fr.hackchtx01.infra.rest.error.Level.INFO;
import static fr.hackchtx01.infra.rest.error.Level.WARNING;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.hackchtx01.authentication.repository.OAuth2AccessTokenRepository;
import fr.hackchtx01.authentication.repository.OAuth2AuthorizationCodeRepository;
import fr.hackchtx01.authentication.resource.TokenResource;
import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.client.app.repository.ClientAppRepository;
import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.test.OauthMockRequestBuilder;
import fr.hackchtx01.test.TestHelper;
import fr.hackchtx01.user.SecuredUser;
import fr.hackchtx01.user.User;
import fr.hackchtx01.user.repository.SecuredUserRepository;

@RunWith(MockitoJUnitRunner.class)
public class TokenResourceTest {
	@Mock
	OAuth2AuthorizationCodeRepository mockedAuthorizationCodeRepo;
	@Mock
	OAuth2AccessTokenRepository mockedAccessTokenRepo;
	@Mock
	ClientAppRepository mockedClientAppRepo;
	@Mock
	SecuredUserRepository mockedUserRepo;
	
	private static final String FORM_URLENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String VALID_REDIRECT_URI = "http://www.google.fr";
	
	private TokenResource getTokenResource(SecuredUser connectedUser) {
		when(mockedClientAppRepo.getById(ClientApp.DEFAULT_ID)).thenReturn(ClientApp.DEFAULT);
		when(mockedClientAppRepo.hashSecret(eq(ClientApp.DEFAULT.getSecret()), any())).thenReturn(ClientApp.DEFAULT.getSecret());
		TokenResource testedResource = new TokenResource(mockedAuthorizationCodeRepo, mockedAccessTokenRepo, mockedClientAppRepo, mockedUserRepo);
		return spy(testedResource);
	}
	
	@Test
	public void authorize_should_return_bad_request_with_empty_token_request() throws OAuthSystemException {
		//given
		TokenResource testedResource = getTokenResource(TestHelper.generateRandomSecuredUser());
		HttpServletRequest invalidRequest = new OauthMockRequestBuilder().build();
		
		//when
		Response response = testedResource.authorize(invalidRequest);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getEntity()).isNotNull();
		verify(testedResource, never()).handleTokenRequest(any());
	}
	
	@Test(expected = WebApiException.class)
	public void authorize_should_return_bad_request_when_client_id_is_not_a_valid_UUID() throws OAuthSystemException {
		//given
		TokenResource testedResource = getTokenResource(TestHelper.generateRandomSecuredUser());
		String invalidClientId = "not uuid";
		String expectedMessage = "Invalid Param named client_id : " + invalidClientId;
		HttpServletRequest invalidRequest = new OauthMockRequestBuilder()
				.withHttpMethod(OAuth.HttpMethod.POST)
				.withClientId(invalidClientId)
				.withClientSecret("secret")
				.withGrantType(GrantType.AUTHORIZATION_CODE.toString())
				.withRedirectUri(VALID_REDIRECT_URI)
				.withAccessGrant("code")
				.withContentType(FORM_URLENCODED_CONTENT_TYPE)
				.build();
		
		//when
		try {
			testedResource.authorize(invalidRequest);
		} catch(WebApiException wae) {
		//then
			verify(testedResource, never()).generateAccessToken(any());
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void authorize_should_return_bad_request_with_unknown_client_id() throws OAuthSystemException {
		//given
		TokenResource testedResource = getTokenResource(TestHelper.generateRandomSecuredUser());
		String unknownClientId = UUID.randomUUID().toString();
		String expectedMessage = UNKNOWN_CLIENT.getDevReadableMessage(unknownClientId);
		HttpServletRequest requestWithUnknownClientId = new OauthMockRequestBuilder()
			.withHttpMethod(OAuth.HttpMethod.POST)
			.withClientId(unknownClientId)
			.withClientSecret("secret")
			.withGrantType(GrantType.AUTHORIZATION_CODE.toString())
			.withRedirectUri(VALID_REDIRECT_URI)
			.withAccessGrant("code")
			.withContentType(FORM_URLENCODED_CONTENT_TYPE)
			.build();
		
		//when
		try {
			testedResource.authorize(requestWithUnknownClientId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, WARNING, API_RESPONSE, expectedMessage);
			verify(testedResource, never()).generateAccessToken(any());
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void authorize_should_return_bad_request_with_missing_client_secret() throws OAuthSystemException {
		//given
		TokenResource testedResource = getTokenResource(TestHelper.generateRandomSecuredUser());
		UUID clientId = ClientApp.DEFAULT_ID;
		HttpServletRequest requestWithUnknownClientId = new OauthMockRequestBuilder()
			.withHttpMethod(OAuth.HttpMethod.POST)
			.withClientId(clientId.toString())
			.withClientSecret(" ")
			.withGrantType(GrantType.AUTHORIZATION_CODE.toString())
			.withRedirectUri(VALID_REDIRECT_URI)
			.withAccessGrant("code")
			.withContentType(FORM_URLENCODED_CONTENT_TYPE)
			.build();
		
		//when
		try {
			testedResource.authorize(requestWithUnknownClientId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, INFO, API_RESPONSE, MISSING_CLIENT_SECRET);
			verify(testedResource, never()).generateAccessToken(any());
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void authorize_should_return_bad_request_with_invalid_client_secret() throws OAuthSystemException {
		//given
		TokenResource testedResource = getTokenResource(TestHelper.generateRandomSecuredUser());
		UUID clientId = ClientApp.DEFAULT_ID;
		String expectedMessage = INVALID_CLIENT_SECRET.getDevReadableMessage(clientId);
		HttpServletRequest requestWithUnknownClientId = new OauthMockRequestBuilder()
			.withHttpMethod(OAuth.HttpMethod.POST)
			.withClientId(clientId.toString())
			.withClientSecret("invalid secret")
			.withGrantType(GrantType.AUTHORIZATION_CODE.toString())
			.withRedirectUri(VALID_REDIRECT_URI)
			.withAccessGrant("code")
			.withContentType(FORM_URLENCODED_CONTENT_TYPE)
			.build();
		
		//when
		try {
			testedResource.authorize(requestWithUnknownClientId);
		} catch(WebApiException wae) {
		//then
			TestHelper.assertWebApiException(wae, BAD_REQUEST, WARNING, API_RESPONSE, expectedMessage);
			verify(testedResource, never()).generateAccessToken(any());
			throw wae;
		}
	}
	
	@Test
	public void authorize_should_fail_with_unknown_authz_code_request() throws OAuthSystemException {
		//given
		TokenResource testedResource = getTokenResource(TestHelper.generateRandomSecuredUser());
		String unknownAuthzCode = "unknown code";
		String expectedMessage = INVALID_AUTHZ_CODE.getDevReadableMessage(unknownAuthzCode);
		HttpServletRequest requestWithUnknownClientId = new OauthMockRequestBuilder()
			.withHttpMethod(OAuth.HttpMethod.POST)
			.withClientId(ClientApp.DEFAULT_ID.toString())
			.withClientSecret(ClientApp.DEFAULT.getSecret())
			.withGrantType(GrantType.AUTHORIZATION_CODE.toString())
			.withRedirectUri(VALID_REDIRECT_URI)
			.withAccessGrant(unknownAuthzCode)
			.withContentType(FORM_URLENCODED_CONTENT_TYPE)
			.build();
		
		//when
		Response response = testedResource.authorize(requestWithUnknownClientId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getEntity()).isNotNull();
		assertThat((String) response.getEntity()).contains(expectedMessage);
		verify(testedResource, never()).generateAccessToken(any());
	}
	
	@Test
	public void authorize_should_handle_valid_authz_code_request() throws OAuthSystemException {
		//given
		TokenResource testedResource = getTokenResource(TestHelper.generateRandomSecuredUser());
		String validAuthzCode = "valid";
		when(mockedAuthorizationCodeRepo.getUserIdByAuthorizationCode(validAuthzCode)).thenReturn(User.DEFAULT_ID);
		HttpServletRequest requestWithUnknownClientId = new OauthMockRequestBuilder()
			.withHttpMethod(OAuth.HttpMethod.POST)
			.withClientId(ClientApp.DEFAULT_ID.toString())
			.withClientSecret(ClientApp.DEFAULT.getSecret())
			.withGrantType(GrantType.AUTHORIZATION_CODE.toString())
			.withRedirectUri(VALID_REDIRECT_URI)
			.withAccessGrant(validAuthzCode)
			.withContentType(FORM_URLENCODED_CONTENT_TYPE)
			.build();
		
		//when
		Response response = testedResource.authorize(requestWithUnknownClientId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
		assertThat(response.getEntity()).isNotNull();
	}
	
	@Test
	public void authorize_should_handle_valid_password_request() throws OAuthSystemException {
		//given
		User user = TestHelper.generateRandomUser();
		String password = "password";
		SecuredUser securedUser = SecuredUser.Builder.createFrom(user)
				.withSalt("salt")
				.withRawPassword(password)
				.build();
			
		TokenResource testedResource = getTokenResource(securedUser);
		when(mockedUserRepo.getByEmail(securedUser.getEmail())).thenReturn(securedUser);
		when(mockedUserRepo.hashPassword(password, "salt")).thenReturn(securedUser.getPassword());
		HttpServletRequest requestWithUnknownClientId = new OauthMockRequestBuilder()
			.withHttpMethod(OAuth.HttpMethod.POST)
			.withClientId(ClientApp.DEFAULT_ID.toString())
			//FIXME apche otlu bug => client secret should not be required for passw ord flow
			.withClientSecret(ClientApp.DEFAULT.getSecret())
			.withGrantType(GrantType.PASSWORD.toString())
			.withOauthUsername(securedUser.getEmail())
			.withOauthPassword(password)
			.withContentType(FORM_URLENCODED_CONTENT_TYPE)
			.build();
		
		//when
		Response response = testedResource.authorize(requestWithUnknownClientId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
		assertThat(response.getEntity()).isNotNull();
	}
}
