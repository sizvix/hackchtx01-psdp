package fr.hackchtx01.authentication.resource;

import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.GRANT_TYPE_NOT_IMPLEMENTED;
import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.INVALID_AUTHZ_CODE;
import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.INVALID_CLIENT_SECRET;
import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.MISSING_CLIENT_SECRET;
import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.UNKNOWN_CLIENT;
import static fr.hackchtx01.infra.rest.error.Level.INFO;
import static fr.hackchtx01.infra.rest.error.Level.WARNING;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import fr.hackchtx01.authentication.repository.OAuth2AccessTokenRepository;
import fr.hackchtx01.authentication.repository.OAuth2AuthorizationCodeRepository;
import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.client.app.repository.ClientAppRepository;
import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.infra.util.ResourceUtil;
import fr.hackchtx01.user.SecuredUser;
import fr.hackchtx01.user.repository.SecuredUserRepository;

@Path("/auth/token")
@Api(value = "token")
public class TokenResource {

	private final OAuth2AuthorizationCodeRepository authzCodeRepository;
	private final OAuth2AccessTokenRepository accessTokenRepository;
	private final ClientAppRepository clientAppRepository;
	private final SecuredUserRepository userRepository;

	public static final String INVALID_CLIENT_DESCRIPTION = "Client authentication failed (e.g., unknown client, no client authentication included, or unsupported authentication method).";
	
	@Inject
	public TokenResource(OAuth2AuthorizationCodeRepository authzCodeRepository, OAuth2AccessTokenRepository accessTokenRepository, ClientAppRepository clientAppRepository, SecuredUserRepository userRepository) {
		this.authzCodeRepository = requireNonNull(authzCodeRepository);
		this.accessTokenRepository = requireNonNull(accessTokenRepository);
		this.clientAppRepository = requireNonNull(clientAppRepository);
		this.userRepository = requireNonNull(userRepository);
	}

	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	@ApiOperation(value = "Get Oauth2 access token", notes = "This will can only be done by an authenticated client")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "grant_type", value = "Grant type", required = true, dataType = "string", paramType = "form", allowableValues = "authorization_code, password, refresh_token, client_credentials"),
	    @ApiImplicitParam(name = "redirect_uri", value = "Redirect URI", required = false, dataType = "string", paramType = "form"),
	    @ApiImplicitParam(name = "client_id", value = "Client Id", required = false, dataType = "string", paramType = "form"),
	    @ApiImplicitParam(name = "client_secret", value = "Client Secret", required = false, dataType = "string", paramType = "form"),
	    @ApiImplicitParam(name = "code", value = "Authorization code", required = false, dataType = "string", paramType = "form"),
	    @ApiImplicitParam(name = "username", value = "User email adress", required = false, dataType = "string", paramType = "form"),
	    @ApiImplicitParam(name = "password", value = "User password", required = false, dataType = "string", paramType = "form"),
	  })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Response with access token in payload"), @ApiResponse(code = 401, message = "Not authenticated") })
	public Response authorize(@Context HttpServletRequest request) throws OAuthSystemException {
		try {
			OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);
			OAuthResponse response = handleTokenRequest(oauthRequest);
			return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
		} catch(OAuthProblemException problem) {
			return handleOAuthProblem(problem);
		} catch(OAuthException oae) {
			return handleOAuthProblemResponse(oae.getResponse());
		}
	}

	protected OAuthResponse handleTokenRequest(OAuthTokenRequest oauthRequest) throws OAuthSystemException {
		ClientApp clientApp = ensureClientExists(oauthRequest);
		
		UUID userId;
		GrantType grantType = extractGrantType(oauthRequest);
		switch (grantType) {
			case AUTHORIZATION_CODE :
				userId = authorizeWithCode(oauthRequest, clientApp);
				break;
			case PASSWORD :
				userId = authorizeWithPassword(oauthRequest);
				break;
			case REFRESH_TOKEN :
				//TODO implement OAuth2 refresh token grant
			case CLIENT_CREDENTIALS :
				//TODO implement OAuth2 client credentials grant
			default:
				throw new WebApiException(BAD_REQUEST, WARNING, API_RESPONSE, GRANT_TYPE_NOT_IMPLEMENTED.getDevReadableMessage(grantType.toString()));
		}
		
		String accessToken = generateAccessToken(userId);

		OAuthResponse response = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK).setAccessToken(accessToken).setExpiresIn("3600").buildJSONMessage();
		return response;
	}

	private GrantType extractGrantType(OAuthTokenRequest oauthRequest) {
		String grantTypeParam = oauthRequest.getGrantType();
		return GrantType.valueOf(grantTypeParam.toUpperCase());
	}
	
	private ClientApp ensureClientExists(OAuthTokenRequest oauthRequest) {
		UUID clientId = ResourceUtil.getIdfromParam(OAuth.OAUTH_CLIENT_ID, oauthRequest.getParam(OAuth.OAUTH_CLIENT_ID));
		ClientApp clientApp = clientAppRepository.getById(clientId);
		if (clientApp == null) {
			throw new WebApiException(BAD_REQUEST, WARNING, API_RESPONSE, UNKNOWN_CLIENT.getDevReadableMessage(clientId.toString()));
		}
		return clientApp;
	}
	
	private boolean checkClientSecret(ClientApp clientApp, String secret) {
		if (StringUtils.isBlank(secret)) {
			throw new WebApiException(BAD_REQUEST, INFO, API_RESPONSE, MISSING_CLIENT_SECRET);
		}
		String hashedSecret  = clientAppRepository.hashSecret(secret, clientApp.getSalt());
		return clientApp.getSecret().equals(hashedSecret);
	}

	private UUID authorizeWithCode(OAuthTokenRequest oauthRequest, ClientApp clientApp) throws OAuthSystemException {
		ensureTrustedClient(oauthRequest, clientApp);
		String authzCode = oauthRequest.getCode();
		UUID userId = findUserIdByAuthCode(authzCode);
		if (userId == null) {
			throw new OAuthException(buildBadAuthCodeResponse(authzCode));
		}
		authzCodeRepository.deleteByCode(authzCode);
		return userId;
	}
	
	private void ensureTrustedClient(OAuthTokenRequest oauthRequest, ClientApp clientApp) throws OAuthSystemException {
		String clientSecret = oauthRequest.getParam(OAuth.OAUTH_CLIENT_SECRET);
		if (!checkClientSecret(clientApp, clientSecret)) {
			throw new WebApiException(BAD_REQUEST, WARNING, API_RESPONSE, INVALID_CLIENT_SECRET.getDevReadableMessage(clientApp.getId().toString()));
		}
	}
	
	private UUID findUserIdByAuthCode(String authCode) {
		return authzCodeRepository.getUserIdByAuthorizationCode(authCode);
	}

	private UUID authorizeWithPassword(OAuthTokenRequest oauthRequest) throws OAuthSystemException {
		String userEmail = oauthRequest.getUsername();
		String password =  oauthRequest.getPassword();
		SecuredUser foundUser = userRepository.getByEmail(userEmail);
		if (!checkUserPassword(foundUser, password)) {
			throw new OAuthException(buildInvalidUserPassResponse());
		}
		return foundUser.getId();
	}
	
	private boolean checkUserPassword(SecuredUser foundUser, String password) {
		String hashedPassword = userRepository.hashPassword(password, foundUser.getSalt());
		return foundUser.getPassword().equals(hashedPassword);
	}
	
	protected String generateAccessToken(UUID userId) throws OAuthSystemException {
		OAuthIssuer oauthIssuer = new OAuthIssuerImpl(new MD5Generator());
		String accessToken = oauthIssuer.accessToken();
		accessTokenRepository.create(accessToken, userId);
		return accessToken;
	}
	
	private Response handleOAuthProblem(OAuthProblemException problem) throws OAuthSystemException {
		OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST).error(problem).buildJSONMessage();
		return handleOAuthProblemResponse(response);
	}

	private Response handleOAuthProblemResponse(OAuthResponse response) throws OAuthSystemException {
		return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
	}
	
	private OAuthResponse buildBadAuthCodeResponse(String authzCode) throws OAuthSystemException {
		return OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
							  .setError(OAuthError.TokenResponse.INVALID_GRANT).setErrorDescription(INVALID_AUTHZ_CODE.getDevReadableMessage(authzCode))
							  .buildJSONMessage();
	}

	private OAuthResponse buildInvalidUserPassResponse() throws OAuthSystemException {
		return OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
							  .setError(OAuthError.TokenResponse.INVALID_GRANT).setErrorDescription("invalid username or password")
							  .buildJSONMessage();
	}
}