/**
 * 
 */
package fr.hackchtx01.infra.config.api.repository.fake;

import fr.hackchtx01.infra.config.api.Config;
import fr.hackchtx01.infra.config.api.repository.ConfigRepository;

/**
 * Fake implementation of Config repository
 * Test purpose only
 * @author yoan
 */
public class ConfigFakeRepository extends ConfigRepository {

	@Override
	protected Config readConfig(String configLocation) {
		return Config.DEFAULT;
	}

	@Override
	protected String getDefaultConfigPath() {
		return "fake/config/path";
	}

}
