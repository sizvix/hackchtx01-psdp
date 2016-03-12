package fr.hackchtx01.client.app.repository;

import static fr.hackchtx01.client.app.repository.ClientAppRepositoryErrorCode.UNSECURE_SECRET;
import static fr.hackchtx01.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_SECRET_VALIDITY;
import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.rest.error.Level.INFO;
import static fr.hackchtx01.infra.util.error.CommonErrorMessage.NOT_FOUND;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.infra.util.error.RepositoryErrorCode;
import fr.hackchtx01.infra.util.helper.SecurityHelper;

/**
 * Client application repository
 * @author yoan
 */
public abstract class ClientAppRepository {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ClientAppRepository.class);
	
	/**
	 * Create a new client app
	 * @param userToCreate
	 */
	public void create(ClientApp appToCreate, String secret) {
		if (appToCreate == null) {
			LOGGER.warn("Client app creation asked with null app");
			return;
		}
		ensureSecretValidity(secret);
		
		ClientApp appWithCreationDate = forceCreationDate(appToCreate);
		ClientApp clientAppToCreate = forceClientSecret(appWithCreationDate, secret);
		processCreate(clientAppToCreate);
	}
	
	private ClientApp forceCreationDate(ClientApp app) {
		LocalDateTime creationDate = LocalDateTime.now();
		return ClientApp.Builder.createFrom(app)
			.withCreationDate(creationDate)
			.withLastUpdate(creationDate)
			.build();
	}
	
	private ClientApp forceClientSecret(ClientApp app, String secret) {
		Object salt = generateSalt();
		String hashedSecret = hashSecret(secret, salt);
		return ClientApp.Builder.createFrom(app)
					   		  	.withSecret(hashedSecret)
					   		  	.withSalt(salt)
					   		  	.build();
	}
	
	protected Object generateSalt() {
		return UUID.randomUUID().toString();
	}
	
	protected void ensureSecretValidity(String secret) {
		if (!checkSecretValidity(secret)) {
			String message = PROBLEM_SECRET_VALIDITY.getDevReadableMessage();
			LOGGER.error(message);
			throw new ApplicationException(ERROR, UNSECURE_SECRET, message);
		}
	}
	
	protected boolean checkSecretValidity(String secret) {
		return StringUtils.isNotBlank(secret);
	}
	
	public String hashSecret(String secret, Object salt) {
		return SecurityHelper.hash(secret, salt);
	}
	
	/**
	 * Get a client app by its Id
	 * @param clientId
	 * @return found app
	 */
	public final ClientApp getById(UUID clientId) {
		if (clientId == null) {
			return null;
		}
		return processGetById(clientId);
	}
	
	/**
	 * Get a client app by its Id and fail if it does not exist
	 * @param clientId
	 * @return found app
	 * @throws ApplicationException if client app not found
	 */
	public final ClientApp findClientAppById(UUID clientId) {
		ClientApp foundApp = getById(clientId);
		ensureAppfound(foundApp);
		return foundApp;
	}
	
	/**
	 * Get all client apps of an user
	 * @param ownerId
	 * @return found apps
	 */
	public final ImmutableList<ClientApp> getByOwner(UUID ownerId) {
		if (ownerId == null) {
			return ImmutableList.of();
		}
		return processGetByOwner(ownerId);
	}
	
	private void ensureAppfound(ClientApp foundApp) {
		if (foundApp == null) {
			throw new ApplicationException(INFO, RepositoryErrorCode.NOT_FOUND, NOT_FOUND.getDevReadableMessage("Client app"));
		}
	}
	
	/**
	 * Change client app secret
	 * @param clientId
	 * @param newSecret
	 */
	public final void changeSecret(UUID clientId, String newSecret) {
		if (clientId == null) {
			LOGGER.warn("Secret update asked with null client app");
			return;
		}
		ensureSecretValidity(newSecret);
		
		ClientApp existingClientApp = findClientAppById(clientId);
		existingClientApp = forceLastUpdateDate(existingClientApp);
		ClientApp clientAppToUpdate = forceClientSecret(existingClientApp, newSecret);
		processChangeSecret(clientAppToUpdate);
	}
	
	private ClientApp forceLastUpdateDate(ClientApp app) {
		return ClientApp.Builder.createFrom(app)
			.withLastUpdate(LocalDateTime.now())
			.build();
	}
	
	/**
	 * Delete a client app by its Id
	 * @param listId
	 */
	public final void deleteById(UUID listId) {
		if (listId == null) {
			LOGGER.warn("Client app deletion asked with null Id");
			return;
		}
		processDeleteById(listId);
	}
	
	/**
	 * Update a Client app
	 * @param askedClientAppToUpdate
	 */
	public final void update(ClientApp askedClientAppToUpdate) {
		if (askedClientAppToUpdate == null) {
			LOGGER.warn("Client app update asked with null client app");
			return;
		}
		ClientApp existingClientApp = findClientAppById(askedClientAppToUpdate.getId());
		
		ClientApp clientAppToUpdate = mergeUpdatesInExistingClientApp(existingClientApp, askedClientAppToUpdate);
		processUpdate(clientAppToUpdate);
	}
	
	private ClientApp mergeUpdatesInExistingClientApp(ClientApp existingClientApp, ClientApp askedClientAppToUpdate) {
		return ClientApp.Builder.createFrom(existingClientApp)
				.withLastUpdate(LocalDateTime.now())
				.withName(askedClientAppToUpdate.getName())
				.withRedirectURI(askedClientAppToUpdate.getRedirectURI())
				.build();
	}
	
	/**
	 * Create a new client app
	 * @param appToCreate
	 */
	protected abstract void processCreate(ClientApp appToCreate);
	
	/**
	 * Get a client app by its Id
	 * @param userId
	 */
	protected abstract ClientApp processGetById(UUID clientId);
	
	/**
	 * Get all client apps of an user
	 * @param ownerId
	 * @return found apps
	 */
	protected abstract ImmutableList<ClientApp> processGetByOwner(UUID ownerId);
	
	/**
	 * Update secret
	 * @param userToUpdate
	 */
	protected abstract void processChangeSecret(ClientApp clientAppToUpdate);
	
	/**
	 * Update an existing client app
	 * @param clientAppToUpdate
	 */
	protected abstract void processUpdate(ClientApp clientAppToUpdate);
	
	/**
	 * Delete a client app by its Id
	 * @param userId
	 */
	protected abstract void processDeleteById(UUID clientId);
}
