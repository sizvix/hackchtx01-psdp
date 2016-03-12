package fr.hackchtx01.infra.config.api.repository;

import static fr.hackchtx01.infra.config.api.repository.ConfigRepository.CONFIG_LOCATION_ENV_VARIABLE;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import fr.hackchtx01.infra.config.api.repository.ConfigRepository;
import fr.hackchtx01.infra.config.api.repository.fake.ConfigFakeRepository;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigRepository.class})
public class ConfigRepositoryTest {
	
	@Test
	public void readConfig_should_use_default_config_path_if_env_var_is_not_defined() {
		//given
		mockStatic(System.class);
		when(System.getenv(CONFIG_LOCATION_ENV_VARIABLE)).thenReturn(null);
		ConfigRepository testedRepo = spy(new ConfigFakeRepository());
		String defaultConfigPath = testedRepo.getDefaultConfigPath();
		
		//when
		testedRepo.readConfig();
		
		//then
		verify(testedRepo).readConfig(defaultConfigPath);
	}
	
	@Test
	public void readConfig_should_use_default_config_path_if_env_var_is_blank() {
		//given
		mockStatic(System.class);
		when(System.getenv(CONFIG_LOCATION_ENV_VARIABLE)).thenReturn("  ");
		ConfigRepository testedRepo = spy(new ConfigFakeRepository());
		String defaultConfigPath = testedRepo.getDefaultConfigPath();
		
		//when
		testedRepo.readConfig();
		
		//then
		verify(testedRepo).readConfig(defaultConfigPath);
	}
	
	@Test
	public void readConfig_should_use_found_config_path_if_env_var_is_defined() {
		//given
		String customConfigPath = "custom/config/Path";
		mockStatic(System.class);
		when(System.getenv(CONFIG_LOCATION_ENV_VARIABLE)).thenReturn(customConfigPath);
		ConfigRepository testedRepo = spy(new ConfigFakeRepository());
		
		//when
		testedRepo.readConfig();
		
		//then
		verify(testedRepo).readConfig(customConfigPath);
	}
}
