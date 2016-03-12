/**
 * 
 */
package fr.hackchtx01.root.repository.fake;

import fr.hackchtx01.root.BuildInfo;
import fr.hackchtx01.root.repository.BuildInfoRepository;

/**
 * Fake implementation of Build info repository
 * Test purpose only
 * @author yoan
 */
public class BuildInfoFakeRepository implements BuildInfoRepository {

	@Override
	public BuildInfo getCurrentBuildInfos() {
		return BuildInfo.DEFAULT;
	}

}
