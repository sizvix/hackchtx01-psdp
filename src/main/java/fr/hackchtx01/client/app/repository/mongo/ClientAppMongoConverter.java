package fr.hackchtx01.client.app.repository.mongo;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.bson.Document;
import org.bson.codecs.Codec;

import fr.hackchtx01.client.app.ClientApp;
import fr.hackchtx01.infra.db.mongo.MongoDocumentConverter;
import fr.hackchtx01.infra.util.helper.DateHelper;

/**
 * MongoDb codec to convert client app to BSON
 * @author yoan
 */
public class ClientAppMongoConverter extends MongoDocumentConverter<ClientApp> {
	public static final String FIELD_NAME = "name";
	public static final String FIELD_OWNER_ID = "ownerId";
	public static final String FIELD_REDIRECT_URI = "redirectURI";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_LAST_UPDATE = "lastUpdate";
    public static final String FIELD_SECRET = "secret";
    public static final String FIELD_SALT = "salt";
    
    public ClientAppMongoConverter() {
		super();
	}
	
    public ClientAppMongoConverter(Codec<Document> codec) {
		super(codec);
	}
    
    @Override
	public ClientApp fromDocument(Document doc) {
		if (doc == null) {
			return null;
		}
		
        UUID id = doc.get(FIELD_ID, UUID.class);
        String name = doc.getString(FIELD_NAME);
        UUID ownerId = doc.get(FIELD_OWNER_ID, UUID.class);
        URI redirectURI = URI.create(doc.getString(FIELD_REDIRECT_URI));
        Date created = doc.getDate(FIELD_CREATED);
        LocalDateTime creationDate = DateHelper.toLocalDateTime(created);
        Date lastUpdated = doc.getDate(FIELD_LAST_UPDATE);
        LocalDateTime lastUpdate = DateHelper.toLocalDateTime(lastUpdated);
        String secret = doc.getString(FIELD_SECRET);
        Object salt = doc.get(FIELD_SALT);
        
        return ClientApp.Builder.createDefault()
        				   .withId(id)
        				   .withCreationDate(creationDate)
        				   .withLastUpdate(lastUpdate)
        				   .withName(name)
        				   .withOwnerId(ownerId)
        				   .withRedirectURI(redirectURI)
        				   .withSecret(secret)
        				   .withSalt(salt)
        				   .build();
	}

	@Override
	public Document toDocument(ClientApp app) {
		if (app == null) {
			return new Document();
		}
		
		return new Document(FIELD_ID, app.getId())
				.append(FIELD_NAME, app.getName())
				.append(FIELD_OWNER_ID, app.getOwnerId())
				.append(FIELD_REDIRECT_URI, app.getRedirectURI().toString())
				.append(FIELD_CREATED, DateHelper.toDate(app.getCreationDate()))
				.append(FIELD_LAST_UPDATE, DateHelper.toDate(app.getLastUpdate()))
				.append(FIELD_SECRET, app.getSecret())
				.append(FIELD_SALT, app.getSalt());
	}
    
    @Override
	public Class<ClientApp> getEncoderClass() {
		return ClientApp.class;
	}
	
	@Override
	public ClientApp generateIdIfAbsentFromDocument(ClientApp app) {
		return documentHasId(app) ? app : ClientApp.Builder.createFrom(app).withRandomId().build();
	}
	
	public Document getChangeSecretUpdate(ClientApp appToUpdate) {
		Document updateDoc = new Document(FIELD_LAST_UPDATE, DateHelper.toDate(appToUpdate.getLastUpdate()))
									.append(FIELD_SECRET, appToUpdate.getSecret())
									.append(FIELD_SALT, appToUpdate.getSalt());
		return new Document("$set", updateDoc);
	}
	
	public static Document getClientAppUpdate(ClientApp clientAppToUpdate) {
		Document updateDoc = new Document(FIELD_LAST_UPDATE, DateHelper.toDate(clientAppToUpdate.getLastUpdate()))
									.append(FIELD_NAME, clientAppToUpdate.getName())
									.append(FIELD_REDIRECT_URI, clientAppToUpdate.getRedirectURI().toString());
		return new Document("$set", updateDoc);
	}
}
