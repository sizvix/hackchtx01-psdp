package fr.hackchtx01.authentication.repository.mongo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import fr.hackchtx01.authentication.repository.OAuth2AccessToken;
import fr.hackchtx01.infra.db.mongo.MongoDocumentConverter;
import fr.hackchtx01.infra.util.helper.DateHelper;

/**
 * MongoDb codec to convert OAuth2 access token to BSON
 * @author yoan
 */
public class OAuth2AccessTokenMongoConverter extends MongoDocumentConverter<OAuth2AccessToken> {
	public static final String FIELD_TOKEN = "token";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_NB_REFRESH = "nbRefresh";
    
    public OAuth2AccessTokenMongoConverter() {
		super();
	}
	
    public OAuth2AccessTokenMongoConverter(Codec<Document> codec) {
		super(codec);
	}
    
    @Override
   	public OAuth2AccessToken fromDocument(Document doc) {
   		if (doc == null) {
   			return null;
   		}
   		
       UUID id = doc.get(FIELD_ID, UUID.class);
       String token = doc.getString(FIELD_TOKEN);
       UUID userId = doc.get(FIELD_USER_ID, UUID.class);
       Date created = doc.getDate(FIELD_CREATED);
       LocalDateTime creationDate = DateHelper.toLocalDateTime(created);
       int nbRefresh = doc.getInteger(FIELD_NB_REFRESH);
       
       return OAuth2AccessToken.Builder.createDefault()
       				   .withId(id)
       				   .withCreationDate(creationDate)
       				   .withToken(token)
       				   .withUserId(userId)
       				   .withNbRefresh(nbRefresh)
       				   .build();
   	}

   	@Override
   	public Document toDocument( OAuth2AccessToken authCode) {
   		if (authCode == null) {
   			return new Document();
   		}
   		
   		return new Document(FIELD_ID, authCode.getId())
   				.append(FIELD_TOKEN, authCode.getToken())
   				.append(FIELD_CREATED, DateHelper.toDate(authCode.getCreationDate()))
   				.append(FIELD_USER_ID, authCode.getuserId())
   				.append(FIELD_NB_REFRESH, authCode.getNbRefresh());
   	}
       
   	@Override
   	public Class< OAuth2AccessToken> getEncoderClass() {
   		return OAuth2AccessToken.class;
   	}
   	
   	@Override
   	public OAuth2AccessToken generateIdIfAbsentFromDocument(OAuth2AccessToken accessToken) {
   		return documentHasId(accessToken) ? accessToken : OAuth2AccessToken.Builder.createFrom(accessToken).withRandomId().build();
   	}
   	
   	public Bson filterByToken(String accessToken) {
   		return Filters.eq(FIELD_TOKEN, accessToken);
   	}
}