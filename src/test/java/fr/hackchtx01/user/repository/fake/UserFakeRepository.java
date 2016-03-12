package fr.hackchtx01.user.repository.fake;

import java.util.UUID;

import fr.hackchtx01.user.User;
import fr.hackchtx01.user.repository.UserRepository;

/**
 * Fake implementation of User repository
 * Test purpose only
 * @author yoan
 */
public class UserFakeRepository extends UserRepository {

	@Override
	protected void processCreate(User userToCreate) { }

	@Override
	protected User processGetById(UUID userId) {
		return null;
	}

	@Override
	protected void processUpdate(User userToUpdate) { }

	@Override
	protected void processDeleteById(UUID userId) { }

	@Override
	protected User processGetByEmail(String email) {
		return null;
	}

}
