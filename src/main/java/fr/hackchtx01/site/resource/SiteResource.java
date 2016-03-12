package fr.hackchtx01.site.resource;

import static fr.hackchtx01.infra.config.guice.PsdpWebModule.CONNECTED_USER;
import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.rest.error.Level.INFO;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static fr.hackchtx01.site.resource.SiteResourceErrorMessage.ALREADY_EXISTING_SITE;
import static fr.hackchtx01.site.resource.SiteResourceErrorMessage.MISSING_SITE_ID_FOR_UPDATE;
import static fr.hackchtx01.site.resource.SiteResourceErrorMessage.SITE_NOT_FOUND;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.net.URI;
import java.util.List;
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
import fr.hackchtx01.site.Site;
import fr.hackchtx01.site.repository.SiteRepository;
import fr.hackchtx01.site.representation.SiteRepresentation;
import fr.hackchtx01.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@Path("/api/site")
@Api(value = "/site")
@Produces({ "application/json", "application/xml" })
public class SiteResource extends RestAPI {
	/** Currently connected site */
	private final User connectedUser;
	private final SiteRepository siteRepo;
	
	@Inject
	public SiteResource(@Named(CONNECTED_USER) User connectedUser, SiteRepository siteRepo) {
		super();
		this.connectedUser = requireNonNull(connectedUser);
		this.siteRepo = requireNonNull(siteRepo);
	}
	
	@GET
	@ApiOperation(value = "Get site API root", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in site.", response = RestRepresentation.class)
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
		URI getByIdURI = getUriInfo().getAbsolutePathBuilder().path(SiteResource.class, "getById").build("{siteId}");
		links.add(new Link("getById", getByIdURI));
		URI updateURI = getUriInfo().getAbsolutePath();
		links.add(new Link("update", updateURI));
		URI deleteByIdURI = getUriInfo().getAbsolutePathBuilder().path(SiteResource.class, "deleteById").build("{siteId}");
		links.add(new Link("deleteById", deleteByIdURI));
		
		return links;
	}
	
	@POST
	@ApiOperation(value = "Create site", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in site.")
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "Site created"),
		@ApiResponse(code = 400, message = "Invalid Site"),
		@ApiResponse(code = 409, message = "Already existing site")})
	public Response create(@ApiParam(value = "Site to create", required = true) SiteRepresentation siteToCreate) {
		Site siteCreated = SiteRepresentation.toSite(siteToCreate);
		//if the Id was not provided we generate one
		if (siteCreated.getId().equals(Site.DEFAULT_ID)) {
			siteCreated = Site.Builder.createFrom(siteCreated).withRandomId().build();
		}
		
		ensureSiteNotExists(siteCreated.getId());
		siteRepo.create(siteCreated);
		SiteRepresentation createdSiteRepresentation = new SiteRepresentation(siteCreated, getUriInfo());
		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(siteCreated.getId().toString()).build();
		return Response.created(location).entity(createdSiteRepresentation).build();
	}
	
	@GET
	@Path("/{siteId}")
	@ApiOperation(value = "Get site by Id", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in site.", response = SiteRepresentation.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Found site"),
		@ApiResponse(code = 400, message = "Invalid site Id"),
		@ApiResponse(code = 404, message = "Site not found") })
	public Response getById(@PathParam("siteId") @ApiParam(value = "Site identifier", required = true) String siteIdStr) {
		Site foundSite = findSiteById(siteIdStr);
		SiteRepresentation foundSiteRepresentation = new SiteRepresentation(foundSite, getUriInfo());
		return Response.ok().entity(foundSiteRepresentation).build();
	}
	
	@PUT
	@ApiOperation(value = "Update", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in site.")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "Site updated"),
		@ApiResponse(code = 400, message = "Invalid site Id"),
		@ApiResponse(code = 404, message = "Site not found") })
	public Response update(@ApiParam(value = "Site to update", required = true) SiteRepresentation siteToUpdate) {
		Site updatedSite = SiteRepresentation.toSite(siteToUpdate);
		ensureSiteIdProvidedForUpdate(updatedSite.getId());
		siteRepo.update(updatedSite);

		UriBuilder ub = getUriInfo().getAbsolutePathBuilder();
        URI location = ub.path(updatedSite.getId().toString()).build();
		return Response.noContent().location(location).build();
	}
	
	@DELETE
	@Path("/{siteId}")
	@ApiOperation(value = "Delete site by Id", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This can only be done by the logged in site.")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Site deleted"),
		@ApiResponse(code = 400, message = "Invalid site Id"),
		@ApiResponse(code = 404, message = "Site not found") })
	public Response deleteById(@PathParam("siteId") @ApiParam(value = "Site identifier", required = true) String siteIdStr) {
		Site foundSite = findSiteById(siteIdStr);
		siteRepo.deleteById(foundSite.getId());
		return Response.ok().build();
	}
	
	private Site findSiteById(String siteIdStr) {
		UUID siteId = ResourceUtil.getIdfromParam("siteId", siteIdStr);
		Site foundSite = siteRepo.getById(siteId);
		
		ensureFoundSite(foundSite);
		
		return foundSite;
	}
	
	private void ensureFoundSite(Site foundSite) {
		if (foundSite == null) {
			throw new WebApiException(NOT_FOUND, INFO, API_RESPONSE, SITE_NOT_FOUND);
		}
	}
	
	private void ensureSiteNotExists(UUID siteId) {
		Site foundSite = siteRepo.getById(siteId);
		
		if (foundSite != null) {
			throw new WebApiException(CONFLICT, ERROR, API_RESPONSE, ALREADY_EXISTING_SITE.getDevReadableMessage(siteId));
		}
	}
	
	private void ensureSiteIdProvidedForUpdate(UUID siteId) {
		if (siteId.equals(Site.DEFAULT_ID)) {
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, MISSING_SITE_ID_FOR_UPDATE);
		}
	}
}