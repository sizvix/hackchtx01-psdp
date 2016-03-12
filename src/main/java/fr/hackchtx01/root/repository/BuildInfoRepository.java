/**
 * 
 */
package fr.hackchtx01.root.repository;

import fr.hackchtx01.root.BuildInfo;

/**
 * Build information repository
 * @author yoan
 */
public interface BuildInfoRepository {
	
	/**
	 * Get information about the current build
	 * @return build information, null if not found
	 */
	public BuildInfo getCurrentBuildInfos();
}
