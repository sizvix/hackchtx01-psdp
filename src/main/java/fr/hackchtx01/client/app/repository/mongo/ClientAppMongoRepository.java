package fr.hackchtx01.client.app.repository.mongo;

import static fr.hackchtx01.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_CREATION_CLIENT_APP;
import static fr.hackchtx01.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_DELETE_CLIENT_APP;
import static fr.hackchtx01.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_READ_CLIENT_APP;
import static fr.hackchtx01.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_READ_USER_CLIENT_APPS;
import static fr.hackchtx01.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_UPDATE_CLIENT_APP;
import static fr.hackchtx01.client.app.repository.ClientAppRepositoryErrorMessage.PROBLEM_UPDATE_CLIENT_APP_SECRET;
import static fr.hackchtx01.client.app.repository.mongo.ClientAppMongoConverter.FIELD_OWNER_ID;
import static fr.hackchtx01.infra.db.mongo.MongoDocumentConverter.FIELD_ID;

import java.util.List;
import java.util.UUID;

import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.client.app.repository.ClientAppRepository;
import fr.hackchtx01.infra.db.Dbs;
import fr.hackchtx01.infra.db.mongo.MongoDbConnectionFactory;
import fr.hackchtx01.infra.util.helper.MongoRepositoryHelper;

/**
 * Mongo implementation of the client application repository
 * @author yoan
 */
@Singleton
public class ClientAppMongoRepository extends ClientAppRepository {

	public static final String CLIENT_APP_COLLECTION = "clientApps";
	
	private final MongoCollection<ClientApp> clientAppCollection;
	private final ClientAppMongoConverter appConverter;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientAppMongoRepository.class);
	
	@Inject
	public ClientAppMongoRepository(MongoDbConnectionFactory mongoConnectionFactory) {
		clientAppCollection = mongoConnectionFactory.getCollection(Dbs.PSDP, CLIENT_APP_COLLECTION, ClientApp.class);
		appConverter = new ClientAppMongoConverter();
	}
	
	@Override
	protected void processCreate(ClientApp appToCreate) {
		try {
			clientAppCollection.insertOne(appToCreate);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_CREATION_CLIENT_APP);
		}
	}

	@Override
	protected ClientApp processGetById(UUID clientAppId) {
		Bson filter = Filters.eq(FIELD_ID, clientAppId);
		ClientApp foundApp = null;
		try {
			foundApp = clientAppCollection.find().filter(filter).first();
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_CLIENT_APP);
		}
		return foundApp;
	}
	
	@Override
	protected ImmutableList<ClientApp> processGetByOwner(UUID ownerId) {
		Bson filter = Filters.eq(FIELD_OWNER_ID, ownerId);
		List<ClientApp> foundApps = Lists.newArrayList();
		try {
			foundApps = clientAppCollection.find().filter(filter).into(foundApps);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_USER_CLIENT_APPS);
		}
		return ImmutableList.<ClientApp>copyOf(foundApps);
	}
	
	@Override
	protected void processUpdate(ClientApp clientApp) {
		Bson filter = Filters.eq(FIELD_ID, clientApp.getId());
		Bson update = ClientAppMongoConverter.getClientAppUpdate(clientApp);
		try {
			clientAppCollection.updateOne(filter, update);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_UPDATE_CLIENT_APP);
		}
	}

	@Override
	protected void processDeleteById(UUID listId) {
		Bson filter = Filters.eq(FIELD_ID, listId);
		try {
			clientAppCollection.deleteOne(filter);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_DELETE_CLIENT_APP);
		}
	}

	@Override
	protected void processChangeSecret(ClientApp clientAppToUpdate) {
		Bson filter = Filters.eq(FIELD_ID, clientAppToUpdate.getId());
		Bson update = appConverter.getChangeSecretUpdate(clientAppToUpdate);
		try {
			clientAppCollection.updateOne(filter, update);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_UPDATE_CLIENT_APP_SECRET);
		}
	}
}