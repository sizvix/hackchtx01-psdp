package fr.hackchtx01.authentication.resource;

import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.INVALID_REDIRECT_URI;
import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.MISSING_REDIRECT_URI;
import static fr.hackchtx01.authentication.resource.OAuthResourceErrorMessage.UNKNOWN_CLIENT;
import static fr.hackchtx01.infra.config.guice.PsdpWebModule.CONNECTED_USER;
import static fr.hackchtx01.infra.rest.error.Level.WARNING;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FOUND;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;

import com.google.inject.name.Named;

import fr.hackchtx01.authentication.repository.OAuth2AccessTokenRepository;
import fr.hackchtx01.authentication.repository.OAuth2AuthorizationCodeRepository;
import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.client.app.repository.ClientAppRepository;
import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.infra.util.ResourceUtil;
import fr.hackchtx01.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * API to generate OAuth2 authorization code
 * @author yoan
 */
@Path("/auth/authorization")
@Api(value = "authorization")
public class AuthorizationResource {
	
	/** Currently connected user */
	private final User authenticatedUser;
	private final OAuth2AuthorizationCodeRepository authzCodeRepository;
	private final OAuth2AccessTokenRepository accessTokenRepository;
	private final ClientAppRepository clientAppRepository;
	
	@Inject
	public AuthorizationResource(@Named(CONNECTED_USER) User authenticatedUser, OAuth2AuthorizationCodeRepository authzCodeRepository, OAuth2AccessTokenRepository accessTokenRepository, ClientAppRepository clientAppRepository) {
		this.authenticatedUser = requireNonNull(authenticatedUser);
		this.authzCodeRepository = requireNonNull(authzCodeRepository);
		this.accessTokenRepository = requireNonNull(accessTokenRepository);
		this.clientAppRepository = requireNonNull(clientAppRepository);
	}
	
	@GET
	@ApiOperation(value = "Get Oauth2 authorization", notes = "This will can only be done by an authenticated client")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "response_type", value = "Response type", required = true, dataType = "string", paramType = "query", allowableValues = "code, token"),
	    @ApiImplicitParam(name = "client_id", value = "Client Id", required = true, dataType = "string", paramType = "query"),
	    @ApiImplicitParam(name = "redirect_uri", value = "Redirect URI", required = true, dataType = "string", paramType = "query")
	})
	@ApiResponses(value = { @ApiResponse(code = 302, message = "Redirection to provided redirect_uri"), @ApiResponse(code = 401, message = "Not authenticated") })
    public Response authorize(@Context HttpServletRequest request) throws OAuthSystemException {
        try {
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            final OAuthResponse response = handleOauthRequest(request, oauthRequest);
            URI uri = URI.create(response.getLocationUri());
            return Response.status(response.getResponseStatus()).location(uri).build();
        } catch (OAuthProblemException problem) {
            return handleOAuthProblem(problem);
        }
    }

	protected OAuthResponse handleOauthRequest(HttpServletRequest request, OAuthAuthzRequest oauthRequest) throws OAuthSystemException {
		ClientApp clientApp = getAndEnsureClientExists(oauthRequest);
		String redirectURI = oauthRequest.getRedirectURI();
		ensureRedirectURI(clientApp, redirectURI);
		
		//build response according to response_type
		OAuthASResponse.OAuthAuthorizationResponseBuilder oAuthResponseBuilder = OAuthASResponse.authorizationResponse(request, Status.FOUND.getStatusCode());
		OAuthIssuer oauthIssuer = new OAuthIssuerImpl(new MD5Generator());
		ResponseType responseType = extractResponseType(oauthRequest);
		switch(responseType) {
		    case CODE : 
		    	String authorizationCode = generateAuthorizationCode(oauthIssuer);
		        oAuthResponseBuilder.setCode(authorizationCode);
		        break;
		    case TOKEN :
		    	String accessToken = generateAccessToken(oauthIssuer);
		        oAuthResponseBuilder.setAccessToken(accessToken);
		        oAuthResponseBuilder.setExpiresIn(3600l);
		        break;
		    default :
		    	break;
		}

		return oAuthResponseBuilder.location(redirectURI).buildQueryMessage();
	}

	private ClientApp getAndEnsureClientExists(OAuthAuthzRequest oauthRequest) {
		UUID clientId = ResourceUtil.getIdfromParam("client_id", oauthRequest.getParam(OAuth.OAUTH_CLIENT_ID));
		ClientApp clientApp = clientAppRepository.getById(clientId);
		if (clientApp == null) {
			throw new WebApiException(BAD_REQUEST, WARNING, API_RESPONSE, UNKNOWN_CLIENT.getDevReadableMessage(clientId.toString()));
		}
		return clientApp;
	}

	private static ResponseType extractResponseType(OAuthAuthzRequest oauthRequest) {
		String responseTypeParam = oauthRequest.getResponseType();
        return ResponseType.valueOf(responseTypeParam.toUpperCase());
	}
	
	protected String generateAuthorizationCode(OAuthIssuer oauthIssuer) throws OAuthSystemException {
		String authorizationCode = oauthIssuer.authorizationCode();
		authzCodeRepository.create(authorizationCode, authenticatedUser.getId());
		return authorizationCode;
	}
	
	protected String generateAccessToken(OAuthIssuer oauthIssuer) throws OAuthSystemException {
		String accessToken = oauthIssuer.accessToken();
		accessTokenRepository.create(accessToken, authenticatedUser.getId());
		return accessToken;
	}
	
	private Response handleOAuthProblem(OAuthProblemException problem) throws OAuthSystemException {
		final Response.ResponseBuilder responseBuilder = Response.status(FOUND);
        String redirectUri = problem.getRedirectUri();
        ensureValidRedirectURI(redirectUri);
        
        final OAuthResponse response = OAuthASResponse.errorResponse(FOUND.getStatusCode()).error(problem).location(redirectUri).buildQueryMessage();
        final URI location = URI.create(response.getLocationUri());
        return responseBuilder.location(location).build();
	}
	
	private void ensureRedirectURI(ClientApp clientApp, String redirectUriStr) {
		URI redirectURI = ensureValidRedirectURI(redirectUriStr);
		
		if (!clientApp.getRedirectURI().equals(redirectURI)) {
			throw new WebApiException(BAD_REQUEST, WARNING, API_RESPONSE, INVALID_REDIRECT_URI.getDevReadableMessage(redirectUriStr));
		}
	}
	
	private URI ensureValidRedirectURI(String redirectURI) {
		if (StringUtils.isBlank(redirectURI)) {
			throw new WebApiException(BAD_REQUEST, WARNING, API_RESPONSE, MISSING_REDIRECT_URI);
        }
		
		try {
			return new URI(redirectURI);
		} catch(URISyntaxException e) {
			throw new WebApiException(BAD_REQUEST, WARNING, API_RESPONSE, INVALID_REDIRECT_URI.getDevReadableMessage(redirectURI), e);
		}
	}
}