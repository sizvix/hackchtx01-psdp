/**
 * 
 */
package fr.hackchtx01.infra.config.api.repository;

import static fr.hackchtx01.infra.logging.Markers.CONFIG;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.hackchtx01.infra.config.api.Config;

/**
 * Application configuation repository
 * @author yoan
 */
public abstract class ConfigRepository {
	/** Name of the environment variable to find the config file */
	public static final String CONFIG_LOCATION_ENV_VARIABLE = "API_PSDP_CONFIG_FILE_PATH";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRepository.class);
	
	/**
	 * Read the API configuration
	 * @return Config if found, else null
	 */
	public Config readConfig() {
		String configLocation = System.getenv(CONFIG_LOCATION_ENV_VARIABLE);
		if (StringUtils.isBlank(configLocation)) {
			configLocation = getDefaultConfigPath();
		} 
		LOGGER.info(CONFIG.getMarker(), "Loaded config file is " + getConfigLocationName(configLocation));
		return readConfig(configLocation);
	}
	
	protected abstract Config readConfig(String configLocation);
	
	protected abstract String getDefaultConfigPath();
	
	protected String getConfigLocationName(String configLocation) {
		String defaultPath = getDefaultConfigPath();
		if (defaultPath.equals(configLocation)) {
			return "Default (src/main/resources/" + defaultPath + ")";
		}
		return "Env defined (" + CONFIG_LOCATION_ENV_VARIABLE + " : " + configLocation +")";
	}
}
