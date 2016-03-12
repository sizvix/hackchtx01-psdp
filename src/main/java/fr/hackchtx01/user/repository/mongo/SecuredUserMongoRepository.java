/**
 * 
 */
package fr.hackchtx01.user.repository.mongo;

import static fr.hackchtx01.user.repository.UserRepositoryErrorMessage.PROBLEM_CREATION_USER;
import static fr.hackchtx01.user.repository.UserRepositoryErrorMessage.PROBLEM_READ_USER;
import static fr.hackchtx01.user.repository.UserRepositoryErrorMessage.PROBLEM_UPDATE_USER_PASSWORD;
import static fr.hackchtx01.user.repository.mongo.UserMongoConverter.*;
import static fr.hackchtx01.user.repository.mongo.UserMongoRepository.USER_COLLECTION;

import java.util.UUID;

import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import fr.hackchtx01.infra.db.Dbs;
import fr.hackchtx01.infra.db.mongo.MongoDbConnectionFactory;
import fr.hackchtx01.infra.util.helper.MongoRepositoryHelper;
import fr.hackchtx01.user.SecuredUser;
import fr.hackchtx01.user.repository.SecuredUserRepository;

/**
 * Mongo implementation of the user with security information repository 
 * @author yoan
 */
@Singleton
public class SecuredUserMongoRepository extends SecuredUserRepository {

	private final SecuredUserMongoConverter userConverter;
	private final MongoCollection<SecuredUser> userCollection;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SecuredUserMongoRepository.class);
	
	@Inject
	public SecuredUserMongoRepository(MongoDbConnectionFactory mongoConnectionFactory) {
		userCollection = mongoConnectionFactory.getCollection(Dbs.PSDP , USER_COLLECTION, SecuredUser.class);
		userConverter = new SecuredUserMongoConverter();
	}
	
	@Override
	protected void processCreate(SecuredUser user) {
		try {
			userCollection.insertOne(user);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_CREATION_USER);
		}
	}

	@Override
	protected SecuredUser processGetById(UUID userId) {
		Bson filter = Filters.eq(FIELD_ID, userId);
		SecuredUser foundUser = null;
		try {
			foundUser = userCollection.find().filter(filter).first();
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_USER);
		}
		
		return foundUser;
	}

	@Override
	protected void processChangePassword(SecuredUser userToUpdate) {
		Bson filter = Filters.eq("_id", userToUpdate.getId());
		Bson update = userConverter.getChangePasswordUpdate(userToUpdate);
		try {
			userCollection.updateOne(filter, update);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_UPDATE_USER_PASSWORD);
		}
	}

	@Override
	protected SecuredUser processGetByEmail(String userEmail) {
		Bson filter = Filters.eq(FIELD_EMAIL, userEmail);
		SecuredUser foundUser = null;
		try {
			foundUser = userCollection.find().filter(filter).first();
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_USER);
		}
		
		return foundUser;
	}
}