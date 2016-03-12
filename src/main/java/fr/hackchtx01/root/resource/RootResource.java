/**
 * 
 */
package fr.hackchtx01.root.resource;

import static fr.hackchtx01.infra.config.guice.PsdpWebModule.CONNECTED_USER;
import static fr.hackchtx01.root.RootKey.CLIENT_APP;
import static fr.hackchtx01.root.RootKey.USER;
import static java.util.Objects.requireNonNull;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import fr.hackchtx01.infra.rest.Link;
import fr.hackchtx01.infra.rest.RestAPI;
import fr.hackchtx01.root.BuildInfo;
import fr.hackchtx01.root.repository.BuildInfoRepository;
import fr.hackchtx01.root.representation.RootRepresentation;
import fr.hackchtx01.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;


/**
 * Root Resource
 * @author yoan
 */
@Path("/api")
@Api(value = "/root")
@Produces({ "application/json", "application/xml" })
public class RootResource extends RestAPI {
	/** Currently connected user */
	private final User connectedUser;
	/** Repository to get the build informations */
	private final BuildInfoRepository buildInfoRepository;
	
	@Inject
	public RootResource(@Named(CONNECTED_USER) User connectedUser, BuildInfoRepository buildInfoRepo) {
		super();
		this.buildInfoRepository = requireNonNull(buildInfoRepo);
		this.connectedUser = requireNonNull(connectedUser);
	}
	
	@GET
	@Override
	@ApiOperation(value = "Get API root", authorizations = { @Authorization(value = "oauth2", scopes = {})}, notes = "This will can only be done by the logged in user.", response = RootRepresentation.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Root") })
	public Response root() {
		List<Link> links = getRootLinks();
		BuildInfo buildInfo = buildInfoRepository.getCurrentBuildInfos();
		
		RootRepresentation root = new RootRepresentation(buildInfo, connectedUser.getId(), links);
		
		return Response.ok(root).build();
	}
	
	@Override
	public List<Link> getRootLinks() {
		List<Link> links = Lists.newArrayList(Link.self(getUriInfo()));
		
		links.add(USER.getlink(getUriInfo()));
		links.add(CLIENT_APP.getlink(getUriInfo()));
		
		return links;
	}
}
