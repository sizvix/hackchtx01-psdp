/**
 * 
 */
package fr.hackchtx01.infra.config.api.repository.properties;

import static fr.hackchtx01.infra.util.helper.PropertiesConverterHelper.getMandatoryIntegerProperty;
import static fr.hackchtx01.infra.util.helper.PropertiesConverterHelper.getMandatoryProperty;
import static fr.hackchtx01.infra.util.helper.PropertiesConverterHelper.getOptionalProperty;

import java.util.Properties;

import fr.hackchtx01.infra.config.api.Config;

/**
 * Converter from properties format to Config
 * @author yoan
 */
public class ConfigPropertiesConverter {
	protected static final String API_HOST_FIELD = "api.host";
	protected static final String API_PORT_FIELD = "api.port";
	
	protected static final String MONGO_HOST_FIELD = "mongo.host";
	protected static final String MONGO_PORT_FIELD = "mongo.port";
	protected static final String MONGO_USER_FIELD = "mongo.user";
	protected static final String MONGO_PASS_FIELD = "mongo.pass";
	
	protected static final String SWAGGER_BASE_PATH_FIELD = "swagger.basePath";
	
	public static Config fromProperties(Properties properties) {
		String apiHost = getMandatoryProperty(properties, API_HOST_FIELD);
		Integer apiPort = getMandatoryIntegerProperty(properties, API_PORT_FIELD);
		
		String mongoHost = getMandatoryProperty(properties, MONGO_HOST_FIELD);
		Integer mongoPort = getMandatoryIntegerProperty(properties, MONGO_PORT_FIELD);
		String mongoUser = getOptionalProperty(properties, MONGO_USER_FIELD);
		String mongoPass = getOptionalProperty(properties, MONGO_PASS_FIELD);
		
		String swaggerBasePath = getMandatoryProperty(properties, SWAGGER_BASE_PATH_FIELD);
		
		return Config.Builder.createDefault()
							.withApiHost(apiHost).withApiPort(apiPort)
							.withMongoHost(mongoHost).withMongoPort(mongoPort)
							.withMongoUser(mongoUser).withMongoPass(mongoPass)
							.withSwaggerBasePath(swaggerBasePath)
							.build();
	}
}
