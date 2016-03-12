package fr.hackchtx01.user.repository;

import static fr.hackchtx01.infra.rest.error.Level.INFO;
import static fr.hackchtx01.infra.util.error.RepositoryErrorCode.NOT_FOUND;
import static fr.hackchtx01.user.resource.UserResourceErrorMessage.USER_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.hackchtx01.infra.util.error.ApplicationException;
import fr.hackchtx01.user.User;

/**
 * User repository
 * @author yoan
 */
public abstract class UserRepository {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);
	
	/**
	 * Create a new User
	 * @param askedUserToCreate
	 */
	public final void create(User askedUserToCreate) {
		if (askedUserToCreate == null) {
			LOGGER.warn("User creation asked with null user");
			return;
		}
		
		User userToCreate = forceCreationDate(askedUserToCreate);
		processCreate(userToCreate);
	}
	
	private User forceCreationDate(User user) {
		LocalDateTime creationDate = LocalDateTime.now();
		return User.Builder.createFrom(user)
			.withCreationDate(creationDate)
			.withLastUpdate(creationDate)
			.build();
	}
	
	/**
	 * Get a user by its Id
	 * @param userId
	 * @return found user or null if not found
	 */
	public final User getById(UUID userId) {
		if (userId == null) {
			return null;
		}
		return processGetById(userId);
	}
	
	/**
	 * Get a user by its email adress
	 * @param email
	 * @return found user or null if not found
	 */
	public final User getByEmail(String email) {
		if (StringUtils.isBlank(email)) {
			return null;
		}
		return processGetByEmail(email);
	}
	
	/**
	 * Update a User
	 * @param askedUserToUpdate
	 */
	public final void update(User askedUserToUpdate) {
		if (askedUserToUpdate == null) {
			LOGGER.warn("User update asked with null user");
			return;
		}
		User existingUser = findUser(askedUserToUpdate.getId());
		
		User userToUpdate = mergeUpdatesInExistingUser(existingUser, askedUserToUpdate);
		processUpdate(userToUpdate);
	}
	
	private User mergeUpdatesInExistingUser(User existingUser, User askedUserToUpdate) {
		return User.Builder.createFrom(existingUser)
				.withLastUpdate(LocalDateTime.now())
				.withName(askedUserToUpdate.getName())
				.withEmail(askedUserToUpdate.getEmail())
				.build();
	}
	
	/**
	 * Get a user by its Id and fail if it does not exist
	 * @param userId
	 * @return found user
	 * @throws ApplicationException if user not found
	 */
	public final User findUser(UUID userId) {
		User foundUser = getById(userId);
		
		if (foundUser == null) {
			throw new ApplicationException(INFO, NOT_FOUND, USER_NOT_FOUND);
		}
		
		return foundUser;
	}
	
	
	/**
	 * Delete a user by its Id
	 * @param userId
	 */
	public final void deleteById(UUID userId) {
		if (userId == null) {
			LOGGER.warn("User deletion asked with null Id");
			return;
		}
		processDeleteById(userId);
	}
	
	/**
	 * Create a new user
	 * @param userToCreate
	 */
	protected abstract void processCreate(User userToCreate);
	
	/**
	 * Get a user by its Id
	 * @param userId
	 */
	protected abstract User processGetById(UUID userId);
	
	/**
	 * Get a user by its email adress
	 * @param email
	 */
	protected abstract User processGetByEmail(String email);
	
	/**
	 * Update an existing user
	 * @param userToUpdate
	 */
	protected abstract void processUpdate(User userToUpdate);
	
	/**
	 * Delete a user by its Id
	 * @param userId
	 */
	protected abstract void processDeleteById(UUID userId);
}
