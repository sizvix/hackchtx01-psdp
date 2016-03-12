package fr.hackchtx01.infra.config.api.repository.properties;

import static fr.hackchtx01.infra.config.api.repository.ConfigRepository.CONFIG_LOCATION_ENV_VARIABLE;
import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.APPLICATION_ERROR;
import static fr.hackchtx01.test.TestHelper.assertApplicationException;
import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import fr.hackchtx01.infra.config.api.Config;
import fr.hackchtx01.infra.config.api.repository.properties.ConfigPropertiesRepository;
import fr.hackchtx01.infra.util.error.ApplicationException;

public class ConfigPropertiesRepositoryTest {
	
	@Test
	public void readConfig_should_work_with_default_config_path() {
		//given
		ConfigPropertiesRepository testedRepo = new ConfigPropertiesRepository();
		String defaultConfigPath = testedRepo.getDefaultConfigPath();
		
		//when
		Config result = testedRepo.readConfig(defaultConfigPath);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(Config.DEFAULT);
	}
	
	@Test(expected = ApplicationException.class)
	public void readConfig_should_fail_with_invalid_file_path() {
		//given
		String invalidConfigFilePath = "invalidPath";
		String expectedMessage = "Unable to find a readable file at path found in \"" + CONFIG_LOCATION_ENV_VARIABLE + "\" env variable : " + invalidConfigFilePath;
		ConfigPropertiesRepository testedRepo = new ConfigPropertiesRepository();
		
		//when
		try {
			testedRepo.readConfig(invalidConfigFilePath);
		} catch(ApplicationException ae) {
		//then
			assertApplicationException(ae, ERROR, APPLICATION_ERROR, expectedMessage);
			throw ae;
		}
	}
	
	@Test
	public void getDefaultConfigPath_should_return_non_empty_string() {
		//given
		ConfigPropertiesRepository testedRepo = new ConfigPropertiesRepository();
		
		//when
		String result = testedRepo.getDefaultConfigPath();
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
	}
}
