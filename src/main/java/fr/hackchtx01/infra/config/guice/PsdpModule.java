package fr.hackchtx01.infra.config.guice;

import static fr.hackchtx01.root.repository.properties.BuildInfoPropertiesRepository.BUILD_INFO_DEFAULT_PROPERTIES_FILE_NAME;
import static java.util.Objects.requireNonNull;

import javax.servlet.ServletContext;

import org.reflections.Reflections;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import fr.hackchtx01.authentication.repository.OAuth2AccessTokenRepository;
import fr.hackchtx01.authentication.repository.OAuth2AuthorizationCodeRepository;
import fr.hackchtx01.authentication.repository.mongo.OAuth2AccessTokenMongoRepository;
import fr.hackchtx01.authentication.repository.mongo.OAuth2AuthorizationCodeMongoRepository;
import fr.hackchtx01.authentication.resource.AuthorizationResource;
import fr.hackchtx01.authentication.resource.RedirectResource;
import fr.hackchtx01.authentication.resource.TokenResource;
import fr.hackchtx01.client.app.repository.ClientAppRepository;
import fr.hackchtx01.client.app.repository.mongo.ClientAppMongoRepository;
import fr.hackchtx01.client.app.resource.ClientAppResource;
import fr.hackchtx01.infra.config.api.Config;
import fr.hackchtx01.infra.config.api.repository.ConfigRepository;
import fr.hackchtx01.infra.config.api.repository.properties.ConfigPropertiesRepository;
import fr.hackchtx01.infra.config.jackson.JacksonConfigProvider;
import fr.hackchtx01.infra.rest.error.GlobalExceptionMapper;
import fr.hackchtx01.root.repository.BuildInfoRepository;
import fr.hackchtx01.root.repository.properties.BuildInfoPropertiesRepository;
import fr.hackchtx01.root.resource.RootResource;
import fr.hackchtx01.user.repository.SecuredUserRepository;
import fr.hackchtx01.user.repository.UserRepository;
import fr.hackchtx01.user.repository.mongo.SecuredUserMongoRepository;
import fr.hackchtx01.user.repository.mongo.UserMongoRepository;
import fr.hackchtx01.user.resource.UserResource;

/**
 * Guice Module to configure bindings
 * @author yoan
 */
public class PsdpModule extends AbstractModule {
	private static final Config configAppli;
	
	private final ServletContext servletContext;
	
	public PsdpModule(ServletContext servletContext) {
		this.servletContext = requireNonNull(servletContext);
	}
	
	static {
		ConfigRepository configRepo = new ConfigPropertiesRepository();
		configAppli = configRepo.readConfig();
	}
	
	@Override
	protected void configure() {
		install(new SwaggerModule(servletContext, new Reflections("fr.hackchtx01"), configAppli));
		
		//resources
		bind(RootResource.class);
		bind(UserResource.class);
		bind(AuthorizationResource.class);
		bind(TokenResource.class);
		bind(ClientAppResource.class);
		
		//providers
		bind(GlobalExceptionMapper.class);
		bind(JacksonConfigProvider.class);
		
		//bindings
		bind(Config.class).toInstance(configAppli);
		bind(UserRepository.class).to(UserMongoRepository.class);
		bind(SecuredUserRepository.class).to(SecuredUserMongoRepository.class);
		bind(ConfigRepository.class).to(ConfigPropertiesRepository.class);
		bind(ClientAppRepository.class).to(ClientAppMongoRepository.class);
		
		bind(OAuth2AuthorizationCodeRepository.class).to(OAuth2AuthorizationCodeMongoRepository.class);
		bind(OAuth2AccessTokenRepository.class).to(OAuth2AccessTokenMongoRepository.class);
		
		bindForLocalHostOnly();
	}
	
	@Provides
	BuildInfoRepository provideBuildInfoRepository() {
		return new BuildInfoPropertiesRepository(BUILD_INFO_DEFAULT_PROPERTIES_FILE_NAME);
	}
	
	/**
	 * Binding only for development purpose
	 */
	private void bindForLocalHostOnly() {
		String host = configAppli.getApiHost();
		if ("localhost".equals(host) || "127.0.0.1".equals(host)) {
			bind(RedirectResource.class);
		}
	}
}
