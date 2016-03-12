package fr.hackchtx01.authentication.realm;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.HostAuthenticationToken;

/**
 * Oauth2 access token for shiro
 * @author yoan
 */
public class Oauth2ShiroAccessToken implements HostAuthenticationToken {
	private static final long serialVersionUID = 4187185639667433466L;
	
	/** The location from where the login attempt occurs, or null if not known or explicitly omitted. */
    private final String host;
    private final String accessToken;
    
    public Oauth2ShiroAccessToken(String accessToken) {
    	this(accessToken, null);
    }
    
    public Oauth2ShiroAccessToken(String accessToken, String host) {
    	checkArgument(StringUtils.isNotBlank(accessToken), "access token is mandatory");
    	this.accessToken = accessToken;
    	this.host = host;
    }
    
	@Override
	public Object getPrincipal() {
		return accessToken;
	}

	@Override
	public Object getCredentials() {
		return accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	@Override
	public String getHost() {
		return host;
	}
}