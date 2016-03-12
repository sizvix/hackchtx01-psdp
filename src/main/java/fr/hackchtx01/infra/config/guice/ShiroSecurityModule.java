/**
 * 
 */
package fr.hackchtx01.infra.config.guice;

import javax.servlet.ServletContext;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.guice.web.ShiroWebModule;

import com.google.inject.Key;
import com.google.inject.name.Names;

import fr.hackchtx01.authentication.realm.OAuth2AccessTokenRealm;
import fr.hackchtx01.authentication.realm.UserRealm;
import fr.hackchtx01.infra.config.filter.Oauth2AccessTokenAuthenticatingFilter;

/**
 * Guice module to configure Shiro
 * @author yoan
 */
public class ShiroSecurityModule extends ShiroWebModule {
	public static final String NO_SECURITY = "NO_SECURITY";
	public static final String SHA256 = "SHA-256";
	public static final int NB_HASH_ITERATION = 2;
	
	private static final Key<Oauth2AccessTokenAuthenticatingFilter> OAUTH2 = Key.get(Oauth2AccessTokenAuthenticatingFilter.class);
	
	public ShiroSecurityModule(ServletContext servletContext) {
		super(servletContext);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void configureShiroWeb() {
		bind(CacheManager.class).to(MemoryConstrainedCacheManager.class);
		//using expose because it is a private module
		expose(CacheManager.class);
		
		bind(HashedCredentialsMatcher.class).annotatedWith(Names.named(SHA256)).toInstance(getHashedCredentialsMatcher(SHA256));
		expose(HashedCredentialsMatcher.class).annotatedWith(Names.named(SHA256));
		
		bind(HashedCredentialsMatcher.class).annotatedWith(Names.named(NO_SECURITY)).toInstance(getNoSecurityCredentialsMatcher());
		expose(HashedCredentialsMatcher.class).annotatedWith(Names.named(NO_SECURITY));
		
		bindRealm().to(UserRealm.class);
		bindRealm().to(OAuth2AccessTokenRealm.class);
		
		//TODO ajouter SSL au début de la filter chain
		addFilterChain("/rest/auth/authorization", config(NO_SESSION_CREATION, "true"), AUTHC_BASIC);
		//TODO ajouter SSL au début de la filter chain
		//addFilterChain("/rest/auth/token", config(NO_SESSION_CREATION, "true"), SSL);
		//TODO ajouter un filter (custom ou default user) pour le token endpoint
		addFilterChain("/rest/api/**", config(NO_SESSION_CREATION, "true"), OAUTH2);
	}
	
	private HashedCredentialsMatcher getHashedCredentialsMatcher(String algorithmName) {
		HashedCredentialsMatcher credentialMatcher = new HashedCredentialsMatcher(algorithmName);
		credentialMatcher.setHashIterations(NB_HASH_ITERATION);
		return credentialMatcher;
	}
	
	/**
	 * /!\ local dev usage only /!\
	 * Allow to use API without security
	 */
	@Deprecated
	private HashedCredentialsMatcher getNoSecurityCredentialsMatcher() {
		HashedCredentialsMatcher credentialMatcher = new HashedCredentialsMatcher(SHA256) {
			@Override
			public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
				return true;
			}
		};
		return credentialMatcher;
	}
}