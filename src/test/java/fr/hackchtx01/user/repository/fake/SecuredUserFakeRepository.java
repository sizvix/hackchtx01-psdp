/**
 * 
 */
package fr.hackchtx01.user.repository.fake;

import java.util.UUID;

import fr.hackchtx01.user.SecuredUser;
import fr.hackchtx01.user.repository.SecuredUserRepository;

/**
 * Fake implementation of User repository focused on security information
 * Test purpose only
 * @author yoan
 */
public class SecuredUserFakeRepository extends SecuredUserRepository {
	@Override
	protected void processCreate(SecuredUser userToCreate) { }

	@Override
	protected SecuredUser processGetById(UUID userId) {
		return null;
	}

	@Override
	protected void processChangePassword(SecuredUser userToUpdate) { }

	@Override
	protected SecuredUser processGetByEmail(String userEmail) {
		return null;
	}
}
