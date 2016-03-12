package fr.hackchtx01.infra.db;

import java.util.UUID;

/**
 * Interface to enforce the presence of an Id
 * @author yoan
 */
@FunctionalInterface
public interface WithId {
	/**
	 * Get the object unique ID
	 * @return UUID
	 */
	public UUID getId();
}
