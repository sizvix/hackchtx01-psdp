package fr.hackchtx01.infra.util.helper;

import org.apache.shiro.crypto.hash.Sha256Hash;

import fr.hackchtx01.infra.config.guice.ShiroSecurityModule;

/**
 * Utility methods related to security
 * @author yoan
 */
public final class SecurityHelper {
	
	private SecurityHelper() { }
	
	/**
	 * Basic way to get a salted
	 * @param password
	 * @param salt
	 * @return salted hash base 64 encoded
	 */
	public static String hash(String password, Object salt) {
		return new Sha256Hash(password, salt, ShiroSecurityModule.NB_HASH_ITERATION).toBase64();
	}
}
