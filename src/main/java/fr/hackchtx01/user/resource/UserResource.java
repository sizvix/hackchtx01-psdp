package fr.hackchtx01.user.resource;

import static fr.hackchtx01.infra.config.guice.PsdpWebModule.CONNECTED_USER;
import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.rest.error.Level.INFO;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static fr.hackchtx01.user.repository.UserRepositoryErrorCode.UNSECURE_PASSWORD;
import static fr.hackchtx01.user.resource.UserResourceErrorMessage.ALREADY_EXISTING_USER;
import static fr.hackchtx01.user.resource.UserResourceErrorMessage.MISSING_USER_ID_FOR_UPDATE;
import static fr.hackchtx01.user.resource.UserResourceErrorMessage.USER_NOT_FOUND;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.net.URI;
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

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import fr.hackchtx01.infra.rest.Link;
import fr.hackchtx01.infra.rest.RestAPI;
import fr.hackchtx01.infra.rest.RestRepresentation;
import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.infra.util.ResourceUtil;
import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.user.User;
import fr.hackchtx01.user.repository.SecuredUserRepository;
import fr.hackchtx01.user.repository.UserRepository;
import fr.hackchtx01.user.representation.SecuredUserRepresentation;
import fr.hackchtx01.user.representation.UserRepresentation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * User API
 * @author yoan
 */
@Path("/api/user")
@Api(value = "/user")
@Produces({ "application/json", "application/xml" })
public class UserResource extends RestAPI {
	/** Currently connected user */
	private final User connectedUser;
	private final UserRepository userRepo;
	private final SecuredUserRepository securedUserRepo;
	
	@Inject
	public UserResource(@Named(CONNECTED_USER) User connectedUser, UserRepository userRepo, SecuredUserRepository securedUserRepo) {
		super();
		this.connectedUser = requireNonNull(connectedUser);
		this.userRepo = Objects.requireNonNull(userRepo);
		this.securedUserRepo = Objects.requireNonNull(securedUserRepo);
	}
	
	@GET
	@ApiOperation(value = "Get user API root", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.", response = RestRepresentation.class)
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
		URI getByIdURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "getById").build(connectedUser.getId().toString());
		links.add(new Link("getById", getByIdURI));
		URI updateURI = getUriInfo().getAbsolutePath();
		links.add(new Link("update", updateURI));
		URI changePasswordURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "changePassword").build(connectedUser.getId().toString(), "{newPassword}");
		links.add(new Link("changePassword", changePasswordURI));
		URI deleteByIdURI = getUriInfo().getAbsolutePathBuilder().path(UserResource.class, "deleteById").build(connectedUser.getId().toString());
		links.add(new Link("deleteById", deleteByIdURI));
		
		return links;
	}
	
	@POST
	@ApiOperation(value = "Create user", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "User created"),
		@ApiResponse(code = 400, message = "Invalid User"),
		@ApiResponse(code = 409, message = "Already existing user")})
	public Response create(@ApiParam(value = "User to create", required = true) SecuredUserRepresentation userToCreate) {
		String password = userToCreate.getPassword();
		User userCreated = UserRepresentation.toUser(userToCreate);
		//if the Id was not provided we generate one
		if (userCreated.getId().equals(User.DEFAULT_ID)) {
			userCreated = User.Builder.createFrom(userCreated).withRandomId().build();
		}
		
		ensureUserNotExists(userCreated.getId());
		createUser(userCreated, password);
		UserRepresentation createdUserRepresentation = new UserRepresentation(userCreated, getUriInfo());
		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(userCreated.getId().toString()).build();
		return Response.created(location).entity(createdUserRepresentation).build();
	}
	
	@GET
	@Path("/{userId}")
	@ApiOperation(value = "Get user by Id", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.", response = UserRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found user"),
		@ApiResponse(code = 400, message = "Invalid user Id"),
		@ApiResponse(code = 404, message = "User not found") })
	public Response getById(@PathParam("userId") @ApiParam(value = "User identifier", required = true) String userIdStr) {
		User foundUser = findUserById(userIdStr);
		UserRepresentation foundUserRepresentation = new UserRepresentation(foundUser, getUriInfo());
		return Response.ok().entity(foundUserRepresentation).build();
	}
	
	@GET
	@Path("/email/{userEmail}")
	@ApiOperation(value = "Get user by Email adress", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.", response = UserRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found user"),
		@ApiResponse(code = 400, message = "Invalid user email"),
		@ApiResponse(code = 404, message = "User not found") })
	public Response getByEmail(@PathParam("userEmail") @ApiParam(value = "User email adress", required = true) String userEmail) {
		User foundUser = findUserByEmail(userEmail);
		UserRepresentation foundUserRepresentation = new UserRepresentation(foundUser, getUriInfo());
		return Response.ok().entity(foundUserRepresentation).build();
	}
	
	@PUT
	@ApiOperation(value = "Update", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "User updated"),
		@ApiResponse(code = 400, message = "Invalid user Id"),
		@ApiResponse(code = 404, message = "User not found") })
	public Response update(@ApiParam(value = "User to update", required = true) UserRepresentation userToUpdate) {
		User updatedUser = UserRepresentation.toUser(userToUpdate);
		ensureUserIdProvidedForUpdate(updatedUser.getId());
		userRepo.update(updatedUser);

		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(updatedUser.getId().toString()).build();
		return Response.noContent().location(location).build();
	}
	
	@PUT
	@Path("/{userId}/password/{newPassword}")
	@ApiOperation(value = "Change password", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "Password changed"),
		@ApiResponse(code = 400, message = "Invalid user Id"),
		@ApiResponse(code = 404, message = "User not found") })
	public Response changePassword(@PathParam("userId") @ApiParam(value = "Id of the user to update", required = true) String userIdStr, 
								   @PathParam("newPassword") @ApiParam(value = "new password", required = true) String newPassword) {
		UUID userId = ResourceUtil.getIdfromParam("userId", userIdStr);
		
		securedUserRepo.changePassword(userId, newPassword);

		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(userId.toString()).build();
		return Response.noContent().location(location).build();
	}
	
	@DELETE
	@Path("/{userId}")
	@ApiOperation(value = "Delete user by Id", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in user.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "User deleted"),
		@ApiResponse(code = 400, message = "Invalid user Id"),
		@ApiResponse(code = 404, message = "User not found") })
	public Response deleteById(@PathParam("userId") @ApiParam(value = "User identifier", required = true) String userIdStr) {
		User foundUser = findUserById(userIdStr);
		userRepo.deleteById(foundUser.getId());
		return Response.ok().build();
	}
	
	private User findUserById(String userIdStr) {
		UUID userId = ResourceUtil.getIdfromParam("userId", userIdStr);
		User foundUser = userRepo.getById(userId);
		
		ensureFoundUser(foundUser);
		
		return foundUser;
	}
	
	private User findUserByEmail(String userEmailStr) {
		String userEmail = ResourceUtil.getEmailfromParam("userEmail", userEmailStr);
		User foundUser = userRepo.getByEmail(userEmail);
		ensureFoundUser(foundUser);
		return foundUser;
	}
	
	private void ensureFoundUser(User foundUser) {
		if (foundUser == null) {
			throw new WebApiException(NOT_FOUND, INFO, API_RESPONSE, USER_NOT_FOUND);
		}
	}
	
	private void ensureUserNotExists(UUID userId) {
		User foundUser = userRepo.getById(userId);
		
		if (foundUser != null) {
			throw new WebApiException(CONFLICT, ERROR, API_RESPONSE, ALREADY_EXISTING_USER.getDevReadableMessage(userId));
		}
	}
	
	private void createUser(User userCreated, String password) {
		try {
			securedUserRepo.create(userCreated, password);
		} catch (ApplicationException ae) {
			if (ae.getErrorCode() == UNSECURE_PASSWORD) {
				throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, ae.getMessage());
			}
			throw ae;
		}
	}
	
	private void ensureUserIdProvidedForUpdate(UUID userId) {
		if (userId.equals(User.DEFAULT_ID)) {
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, MISSING_USER_ID_FOR_UPDATE);
		}
	}
}
