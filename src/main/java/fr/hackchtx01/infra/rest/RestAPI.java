/**
 * 
 */
package fr.hackchtx01.infra.rest;

import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Restful API
 * @author yoan
 */
public abstract class RestAPI {
	@Context
	private UriInfo uriInfo;
	
	/**
	 * API operation to provide navigation links in the resource
	 * @return HTTP response
	 */
	public abstract Response root();
	
	/**
	 * Provide one link per operation in the API
	 * @return
	 */
	public abstract List<Link> getRootLinks();
	
	/**
	 * Expose current HTTP request URI info
	 * @return
	 */
	//TODO : find a better way to mock UriInfo in tests
	public UriInfo getUriInfo() {
		return uriInfo;
	}
	
}
