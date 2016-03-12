package fr.hackchtx01.client.app.repository.fake;

import java.util.UUID;

import com.google.common.collect.ImmutableList;

import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.client.app.repository.ClientAppRepository;

/**
 * Fake implementation of Client app repository
 * Test purpose only
 * @author yoan
 */
public class ClientAppFakeRepository extends ClientAppRepository {
	@Override
	protected void processCreate(ClientApp appToCreate) { }

	@Override
	protected ClientApp processGetById(UUID clientId) {
		return null;
	}

	@Override
	protected void processChangeSecret(ClientApp clientAppToUpdate) { }

	@Override
	protected void processDeleteById(UUID clientId) { }

	@Override
	protected void processUpdate(ClientApp clientAppToUpdate) { }

	@Override
	protected ImmutableList<ClientApp> processGetByOwner(UUID ownerId) {
		return ImmutableList.of();
	}
}