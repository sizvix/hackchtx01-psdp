/**
 * 
 */
package fr.hackchtx01.infra.config.api.repository.properties;

import static fr.hackchtx01.infra.logging.Markers.CONFIG;
import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.APPLICATION_ERROR;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import fr.hackchtx01.infra.config.api.Config;
import fr.hackchtx01.infra.config.api.repository.ConfigRepository;
import fr.hackchtx01.infra.util.error.ApplicationException;

/**
 * Properties file based implementation of the API config repository
 * @author yoan
 */
@Singleton
public class ConfigPropertiesRepository extends ConfigRepository {
	/** Properties file name located in src/main/resources */
	private static final String DEFAULT_CONFIG_PROPERTIES_FILE_NAME = "config/defaultConfigAPI.properties";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigPropertiesRepository.class);
	
	@Override
	protected Config readConfig(String configLocation) {
		Properties properties = new Properties();
		
		try (InputStream configFileStream = getInputStreamOnConfFile(configLocation)){
			properties.load(configFileStream);
		} catch(IOException ioe) {
			String message = "Problem While loading " + getConfigLocationName(configLocation) + " config properties file.";
			LOGGER.error(CONFIG.getMarker(), message, ioe);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message, ioe);
		}
		
		return ConfigPropertiesConverter.fromProperties(properties);
	}

	private InputStream getInputStreamOnConfFile(String configLocation) {
		if (DEFAULT_CONFIG_PROPERTIES_FILE_NAME.equals(configLocation)) {
			return getInputStreamFromDefaultConfig();
		}
		return getInputStreamFromFileSystem(configLocation);
	}
	
	private InputStream getInputStreamFromDefaultConfig() {
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		return currentClassLoader.getResourceAsStream(DEFAULT_CONFIG_PROPERTIES_FILE_NAME);
	}
	
	private InputStream getInputStreamFromFileSystem(String configLocation) {
		Path configPath = getConfigPath(configLocation);
		ensureConfigFileExists(configPath);
		InputStream configFileStream;
		try {
			configFileStream = Files.newInputStream(configPath);
		} catch(IOException ioe) {
			String message = "Problem while openning stream on path found in \"" + CONFIG_LOCATION_ENV_VARIABLE + "\" env variable : " + configLocation;
			LOGGER.error(CONFIG.getMarker(), message, ioe);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message, ioe);
		}
		return configFileStream;
	}
	
	private Path getConfigPath(String configLocation) {
		Path configPath;
		try {
			configPath = FileSystems.getDefault().getPath(configLocation);
		} catch(InvalidPathException e) {
			String message = "Invalid path found in \"" + CONFIG_LOCATION_ENV_VARIABLE + "\" env variable : " + configLocation;
			LOGGER.error(CONFIG.getMarker(), message, e);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message, e);
		}
		return configPath;
	}
	
	private void ensureConfigFileExists(Path configPath) {
		if (Files.notExists(configPath)) {
			String message = "Unable to find a readable file at path found in \"" + CONFIG_LOCATION_ENV_VARIABLE + "\" env variable : " + configPath;
			LOGGER.error(CONFIG.getMarker(), message);
			throw new ApplicationException(ERROR, APPLICATION_ERROR, message);
		}
	}
	
	@Override
	protected String getDefaultConfigPath() {
		return DEFAULT_CONFIG_PROPERTIES_FILE_NAME;
	}
}
