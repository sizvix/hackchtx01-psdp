package fr.hackchtx01.authentication.resource;

import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.MISSING_REDIRECT_URI;
import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.UNKNOWN_CLIENT;
import static fr.hackchtx01.infra.rest.error.Level.INFO;
import static fr.hackchtx01.infra.rest.error.Level.WARNING;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FOUND;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.hackchtx01.authentication.repository.OAuth2AccessTokenRepository;
import fr.hackchtx01.authentication.repository.OAuth2AuthorizationCodeRepository;
import fr.hackchtx01.authentication.resource.AuthorizationResource;
import fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage;
import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.client.app.repository.ClientAppRepository;
import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.test.OauthMockRequestBuilder;
import fr.hackchtx01.test.TestHelper;
import fr.hackchtx01.user.User;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationResourceTest {
	@Mock
	OAuth2AuthorizationCodeRepository mockedAuthorizationCodeRepo;
	@Mock
	OAuth2AccessTokenRepository mockedAccessTokenRepo;
	@Mock
	ClientAppRepository mockedClientAppRepo;
	
	private static final String VALID_REDIRECT_URI = ClientApp.DEFAULT_REDIRECT_URI.toString();
	
	private AuthorizationResource getAuthorizationResource(User connectedUser) {
		when(mockedClientAppRepo.getById(ClientApp.DEFAULT_ID)).thenReturn(ClientApp.DEFAULT);
		AuthorizationResource testedResource = new AuthorizationResource(connectedUser, mockedAuthorizationCodeRepo, mockedAccessTokenRepo, mockedClientAppRepo);
		return spy(testedResource);
	}
	
	@Test(expected = WebApiException.class)
	public void authorize_should_handle_empty_authz_request() throws OAuthSystemException {
		//given
		AuthorizationResource testedResource = getAuthorizationResource(TestHelper.generateRandomUser());
		HttpServletRequest invalidRequest = new OauthMockRequestBuilder().build();
		
		//when
		try {
			testedResource.authorize(invalidRequest);
		} catch(WebApiException wae) {
		//then
			verify(testedResource, never()).handleOauthRequest(any(), any());
			TestHelper.assertWebApiException(wae, BAD_REQUEST, WARNING, API_RESPONSE, MISSING_REDIRECT_URI);
			throw wae;
		}
	}
	
	@Test(expected = WebApiException.class)
	public void authorize_should_handle_authz_request_with_invalid_redirect_URI() throws OAuthSystemException {
		//given
		AuthorizationResource testedResource = getAuthorizationResource(TestHelper.generateRandomUser());
		String invalidRedirectURI = "http:/bad  ";
		HttpServletRequest invalidRequest = new OauthMockRequestBuilder().withRedirectUri(invalidRedirectURI).build();
		
		//when
		try {
			testedResource.authorize(invalidRequest);
		} catch(WebApiException wae) {
		//then
			verify(testedResource, never()).handleOauthRequest(any(), any());
			TestHelper.assertWebApiException(wae, BAD_REQUEST, WARNING, API_RESPONSE, OAuthResourceErrorMessage.INVALID_REDIRECT_URI.getDevReadableMessage(invalidRedirectURI));
			throw wae;
		}
	}
	
	@Test
	public void authorize_should_handle_invalid_authz_request() throws OAuthSystemException {
		//given
		AuthorizationResource testedResource = getAuthorizationResource(TestHelper.generateRandomUser());
		HttpServletRequest invalidRequest = new OauthMockRequestBuilder().withRedirectUri(VALID_REDIRECT_URI).build();
		
		//when
		Response response = testedResource.authorize(invalidRequest);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(FOUND.getStatusCode());
		assertThat(response.getLocation().toString()).startsWith(VALID_REDIRECT_URI);
		
		verify(testedResource, never()).handleOauthRequest(any(), any());
	}
	
	@Test
	public void authorize_should_work_with_auth_code_request() throws OAuthSystemException {
		//given
		AuthorizationResource testedResource = getAuthorizationResource(TestHelper.generateRandomUser());
		HttpServletRequest authCodeRequest = new OauthMockRequestBuilder()
			.withRedirectUri(VALID_REDIRECT_URI)
			.withHttpMethod(OAuth.HttpMethod.GET)
			.withClientId(ClientApp.DEFAULT_ID.toString())
			.withOauthResponseType(ResponseType.CODE.toString())
			.build();
		
		//when
		Response response = testedResource.authorize(authCodeRequest);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(FOUND.getStatusCode());
		assertThat(response.getLocation().toString()).startsWith(VALID_REDIRECT_URI);
		
		verify(testedResource).generateAuthorizationCode(any());
	}
	
	@Test
	public void authorize_should_work_with_token_request() throws OAuthSystemException {
		//given
		AuthorizationResource testedResource = getAuthorizationResource(TestHelper.generateRandomUser());
		HttpServletRequest tokenRequest = new OauthMockRequestBuilder()
			.withRedirectUri(VALID_REDIRECT_URI)
			.withHttpMethod(OAuth.HttpMethod.GET)
			.withClientId(ClientApp.DEFAULT_ID.toString())
			.withOauthResponseType(ResponseType.TOKEN.toString())
			.build();
		
		//when
		Response response = testedResource.authorize(tokenRequest);
		
		//then
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(FOUND.getStatusCode());
		assertThat(response.getLocation().toString()).startsWith(VALID_REDIRECT_URI);
		
		verify(testedResource).generateAccessToken(any());
	}
	
	@Test(expected = WebApiException.class)
	public void authorize_should_return_bad_request_when_client_id_is_not_a_valid_UUID() throws OAuthSystemException {
		//given
		AuthorizationResource testedResource = getAuthorizationResource(TestHelper.generateRandomUser());
		String invalidClientId = "not uuid";
		String expectedMessage = "Invalid Param named client_id : " + invalidClientId;
		HttpServletRequest invalidRequest = new OauthMockRequestBuilder()
				.withRedirectUri(VALID_REDIRECT_URI)
				.withHttpMethod(OAuth.HttpMethod.GET)
				.withClientId(invalidClientId)
				.withOauthResponseType(ResponseType.TOKEN.toString())
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
	public void authorize_should_return_bad_request_when_client_id_is_unknown() throws OAuthSystemException {
		//given
		AuthorizationResource testedResource = getAuthorizationResource(TestHelper.generateRandomUser());
		String unknownClientId = UUID.randomUUID().toString();
		String expectedMessage = UNKNOWN_CLIENT.getDevReadableMessage(unknownClientId);
		HttpServletRequest invalidRequest = new OauthMockRequestBuilder()
				.withRedirectUri(VALID_REDIRECT_URI)
				.withHttpMethod(OAuth.HttpMethod.GET)
				.withClientId(unknownClientId)
				.withOauthResponseType(ResponseType.TOKEN.toString())
				.build();
		
		//when
		try {
			testedResource.authorize(invalidRequest);
		} catch(WebApiException wae) {
		//then
			verify(testedResource, never()).generateAccessToken(any());
			TestHelper.assertWebApiException(wae, BAD_REQUEST, WARNING, API_RESPONSE, expectedMessage);
			throw wae;
		}
	}
}
