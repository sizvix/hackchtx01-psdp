package fr.hackchtx01.client.app.resource;

import static fr.hackchtx01.client.app.repository.ClientAppRepositoryErrorCode.UNSECURE_SECRET;
import static fr.hackchtx01.client.app.resource.ClientAppResourceErrorMessage.ALREADY_EXISTING_CLIENT_APP;
import static fr.hackchtx01.client.app.resource.ClientAppResourceErrorMessage.CLIENT_APPS_NOT_FOUND;
import static fr.hackchtx01.client.app.resource.ClientAppResourceErrorMessage.CLIENT_APP_NOT_FOUND;
import static fr.hackchtx01.client.app.resource.ClientAppResourceErrorMessage.MISSING_CLIENT_APP_ID_FOR_UPDATE;
import static fr.hackchtx01.infra.config.guice.PsdpWebModule.CONNECTED_USER;
import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.rest.error.Level.INFO;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.client.app.repository.ClientAppRepository;
import fr.hackchtx01.client.app.representation.ClientAppRepresentation;
import fr.hackchtx01.infra.rest.Link;
import fr.hackchtx01.infra.rest.RestAPI;
import fr.hackchtx01.infra.rest.RestRepresentation;
import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.infra.util.ResourceUtil;
import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Client application API
 * @author yoan
 */
@Path("/api/client/app")
@Api(value = "/client/app")
@Produces({ "application/json", "application/xml" })
public class ClientAppResource extends RestAPI {
	/** Currently connected user */
	private final User connectedUser;
	private final ClientAppRepository clientAppRepo;
	
	@Inject
	public ClientAppResource(@Named(CONNECTED_USER) User connectedUser, ClientAppRepository clientAppRepo) {
		super();
		this.connectedUser = requireNonNull(connectedUser);
		this.clientAppRepo = Objects.requireNonNull(clientAppRepo);
	}
	
	@GET
	@ApiOperation(value = "Get client application API root", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.", response = RestRepresentation.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Root"), @ApiResponse(code = 401, message = "Not authenticated") })
	@Override
	public Response root() {
		RestRepresentation rootRepresentation = new RestRepresentation(getRootLinks());
		return Response.ok().entity(rootRepresentation).build();
	}
	
	@Override
	public List<Link> getRootLinks() {
		List<Link> links = Lists.newArrayList(Link.self(getUriInfo()));
		
		URI createURI = getUriInfo().getAbsolutePath();
		links.add(new Link("create", createURI));
		URI getByIdURI = getUriInfo().getAbsolutePathBuilder().path(ClientAppResource.class, "getById").build("{appId}");
		links.add(new Link("getById", getByIdURI));
		URI getByOwnerIdURI = getUriInfo().getAbsolutePathBuilder().path(ClientAppResource.class, "getByOwnerId").build(connectedUser.getId().toString());
		links.add(new Link("getByOwnerId", getByOwnerIdURI));
		URI updateURI = getUriInfo().getAbsolutePath();
		links.add(new Link("update", updateURI));
		URI changeSecretKeyURI = getUriInfo().getAbsolutePathBuilder().path(ClientAppResource.class, "changeSecretKey").build("{appId}");
		links.add(new Link("changeSecretKey", changeSecretKeyURI));
		URI deleteByIdURI = getUriInfo().getAbsolutePathBuilder().path(ClientAppResource.class, "deleteById").build("{appId}");
		links.add(new Link("deleteById", deleteByIdURI));
		
		return links;
	}
	
	@POST
	@ApiOperation(value = "Create client app", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "User created"),
		@ApiResponse(code = 400, message = "Invalid User"),
		@ApiResponse(code = 409, message = "Already existing user")})
	public Response create(@ApiParam(value = "Client application to create", required = true) ClientAppRepresentation clientAppToCreate) {
		ClientApp clientAppCreated = ClientAppRepresentation.toClientApp(clientAppToCreate);
		//if the Id was not provided we generate one
		if (clientAppCreated.getId().equals(ClientApp.DEFAULT_ID)) {
			clientAppCreated = ClientApp.Builder.createFrom(clientAppCreated).withRandomId().build();
		}
		
		ensureClientAppNotExists(clientAppCreated.getId());
		String secretkey = generateNewSecretKey();
		createClientApp(clientAppCreated, secretkey);
		ClientAppRepresentation createdAppRepresentation = new ClientAppRepresentation(clientAppCreated, getUriInfo());
		createdAppRepresentation.setSecretKey(secretkey);
		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(clientAppCreated.getId().toString()).build();
		return Response.created(location).entity(createdAppRepresentation).build();
	}
	
	@GET
	@Path("/{appId}")
	@ApiOperation(value = "Get client app by Id", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.", response = ClientAppRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found client application"),
		@ApiResponse(code = 400, message = "Invalid client application Id"),
		@ApiResponse(code = 404, message = "Client application not found") })
	public Response getById(@PathParam("appId") @ApiParam(value = "Client application identifier", required = true) String appIdStr) {
		ClientApp foundApp = findClientAppById(appIdStr);
		ClientAppRepresentation foundAppRepresentation = new ClientAppRepresentation(foundApp, getUriInfo());
		return Response.ok().entity(foundAppRepresentation).build();
	}
	
	@GET
	@Path("/user/{ownerId}")
	@ApiOperation(value = "Get client apps by Id", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This will can only be done by the logged in user.", response = ClientAppRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found client applications"),
		@ApiResponse(code = 400, message = "Invalid owner Id"),
		@ApiResponse(code = 404, message = "Owner not found") })
	public Response getByOwnerId(@PathParam("ownerId") @ApiParam(value = "Owner identifier", required = true) String ownerIdStr) {
		ImmutableList<ClientApp> foundApps = findClientAppsByOwnerId(ownerIdStr);
		List<ClientAppRepresentation> appsRepresentation = new ArrayList<>();
		foundApps.forEach(app -> appsRepresentation.add(new ClientAppRepresentation(app, getUriInfo())));
		return Response.ok().entity(appsRepresentation).build();
	}
	
	@PUT
	@ApiOperation(value = "Update", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "Client application updated"),
		@ApiResponse(code = 400, message = "Invalid client application Id"),
		@ApiResponse(code = 404, message = "Client application not found") })
	public Response update(@ApiParam(value = "Client application to update", required = true) ClientAppRepresentation appToUpdate) {
		ClientApp updatedClientApp = ClientAppRepresentation.toClientApp(appToUpdate);
		ensureAppIdProvidedForUpdate(updatedClientApp.getId());
		clientAppRepo.update(updatedClientApp);

		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(updatedClientApp.getId().toString()).build();
		return Response.noContent().location(location).build();
	}

	private ClientApp findClientAppById(String appIdStr) {
		UUID appId = ResourceUtil.getIdfromParam("appId", appIdStr);
		ClientApp foundApp = clientAppRepo.getById(appId);
		
		ensureFoundApp(foundApp);
		
		return foundApp;
	}
	
	@POST
	@Path("/{appId}/secret")
	@ApiOperation(value = "Change secret key", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Secret key changed"),
		@ApiResponse(code = 400, message = "Invalid client application Id"),
		@ApiResponse(code = 404, message = "Client application not found") })
	public Response changeSecretKey(@PathParam("appId") @ApiParam(value = "Id of the client app to update", required = true) String appIdStr) {
		UUID appId = ResourceUtil.getIdfromParam("appId", appIdStr);
		String newSecretKey = generateNewSecretKey();
		clientAppRepo.changeSecret(appId, newSecretKey);

		ClientApp foundApp = findClientAppById(appIdStr);
		ClientAppRepresentation foundAppRepresentation = new ClientAppRepresentation(foundApp, getUriInfo());
		foundAppRepresentation.setSecretKey(newSecretKey);
		
        return Response.ok().entity(foundAppRepresentation).build();
	}
	
	@DELETE
	@Path("/{appId}")
	@ApiOperation(value = "Delete client application by Id", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Client application deleted"),
		@ApiResponse(code = 400, message = "Invalid client application Id"),
		@ApiResponse(code = 404, message = "Client application not found") })
	public Response deleteById(@PathParam("appId") @ApiParam(value = "Id of the client app to delete", required = true) String appIdStr) {
		ClientApp foundApp = findClientAppById(appIdStr);
		clientAppRepo.deleteById(foundApp.getId());
		return Response.ok().build();
	}
	
	private ImmutableList<ClientApp> findClientAppsByOwnerId(String ownerIdStr) {
		UUID ownerId = ResourceUtil.getIdfromParam("ownerId", ownerIdStr);
		ImmutableList<ClientApp> foundApps = clientAppRepo.getByOwner(ownerId);
		
		if (foundApps.isEmpty()) {
			throw new WebApiException(NOT_FOUND, INFO, API_RESPONSE, CLIENT_APPS_NOT_FOUND.getDevReadableMessage(ownerIdStr));
		}
		
		return foundApps;
	}
	
	private void ensureFoundApp(ClientApp foundApp) {
		if (foundApp == null) {
			throw new WebApiException(NOT_FOUND, INFO, API_RESPONSE, CLIENT_APP_NOT_FOUND);
		}
	}
	
	private void ensureClientAppNotExists(UUID appId) {
		ClientApp foundClientApp = clientAppRepo.getById(appId);
		
		if (foundClientApp != null) {
			throw new WebApiException(CONFLICT, ERROR, API_RESPONSE, ALREADY_EXISTING_CLIENT_APP.getDevReadableMessage(appId));
		}
	}
	
	private void createClientApp(ClientApp clientAppCreated, String secret) {
		try {
			clientAppRepo.create(clientAppCreated, secret);
		} catch (ApplicationException ae) {
			if (ae.getErrorCode() == UNSECURE_SECRET) {
				throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, ae.getMessage());
			}
			throw ae;
		}
	}
	
	private void ensureAppIdProvidedForUpdate(UUID appId) {
		if (appId.equals(ClientApp.DEFAULT_ID)) {
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, MISSING_CLIENT_APP_ID_FOR_UPDATE);
		}
	}
	
	private String generateNewSecretKey() {
		return UUID.randomUUID().toString();
	}
}
