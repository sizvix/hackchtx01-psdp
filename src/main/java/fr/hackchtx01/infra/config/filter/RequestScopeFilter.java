/**
 * 
 */
package fr.hackchtx01.infra.config.filter;

import static fr.hackchtx01.infra.config.guice.PsdpWebModule.CONNECTED_USER;
import static fr.hackchtx01.infra.logging.Markers.AUTHENTICATION;
import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static fr.hackchtx01.infra.util.error.CommonErrorMessage.INVALID;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.user.User;

/**
 * Filter which authenticate the currently connected user
 * @author yoan
 */
@Singleton
public class RequestScopeFilter implements Filter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestScopeFilter.class);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException { }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		User connectedUser = User.DEFAULT;
		try {
			connectedUser = extractUserFromShiroSession();
		} catch(WebApiException wae) {
			httpResponse.sendError(wae.getStatus().getStatusCode(), wae.getMessage());
		} finally {
			//we add the authenticated user infos to the request
			httpRequest.setAttribute(Key.get(User.class, Names.named(CONNECTED_USER)).toString(), connectedUser);
			chain.doFilter(httpRequest, httpResponse);
		}
	}
	
	protected User extractUserFromShiroSession() {
		Object principal = SecurityUtils.getSubject().getPrincipal();
		
		if (!(principal instanceof User)) {
			String message = INVALID.getDevReadableMessage(" found principal : " + principal);
			LOGGER.debug(AUTHENTICATION.getMarker(), message);
			throw new WebApiException(UNAUTHORIZED, ERROR, API_RESPONSE, message);
		}
		
		return (User) principal;
	}
	
	@Override
	public void destroy() { }

}
