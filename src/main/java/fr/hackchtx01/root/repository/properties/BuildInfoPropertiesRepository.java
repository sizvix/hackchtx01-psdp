/**
 * 
 */
package fr.hackchtx01.root.repository.properties;

import static com.google.common.base.Preconditions.checkArgument;
import static fr.hackchtx01.infra.logging.Markers.CONFIG;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import fr.hackchtx01.root.BuildInfo;
import fr.hackchtx01.root.repository.BuildInfoRepository;

/**
 * Properties file based implementation of the build infos repository
 * @author yoan
 */
@Singleton
public class BuildInfoPropertiesRepository implements BuildInfoRepository {
	/** Properties file name located in src/main/resources */
	public static final String BUILD_INFO_DEFAULT_PROPERTIES_FILE_NAME = "version.properties";
	/** The loaded build infos */
	private BuildInfo BUILD_INFOS = null;
	private final String buildInfoFileName;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BuildInfoPropertiesRepository.class);
	
	public BuildInfoPropertiesRepository(String buildInfoFileName) {
		checkArgument(StringUtils.isNotBlank(buildInfoFileName), "The build info file name is mandatory");
		this.buildInfoFileName = buildInfoFileName;
	}
	
	@Override
	public BuildInfo getCurrentBuildInfos() {
		if (BUILD_INFOS == null) {
			BUILD_INFOS = tryToloadPropertiesFile();
		} 
		return BUILD_INFOS;
	}

	/**
	 * Try to load properties file 
	 * @return build info if read ok else return null
	 */
	protected BuildInfo tryToloadPropertiesFile() {
		Properties properties = new Properties();
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(buildInfoFileName);
		
		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				LOGGER.warn(CONFIG.getMarker(), "Problem while loading : " + buildInfoFileName, e);
				return BuildInfo.DEFAULT;
			}
		} else {
			LOGGER.warn(CONFIG.getMarker(), "property file '" + buildInfoFileName + "' not found in the classpath");
			return BuildInfo.DEFAULT;
		}
		
		return BuildInfoPropertiesConverter.fromProperties(properties);
	}
}
