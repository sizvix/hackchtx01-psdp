package fr.hackchtx01.authentication.repository.mongo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import fr.hackchtx01.authentication.repository.OAuth2AuthorizationCode;
import fr.hackchtx01.infra.db.mongo.MongoDocumentConverter;
import fr.hackchtx01.infra.util.helper.DateHelper;

/**
 * MongoDb codec to convert OAuth2 authorization code to BSON
 * @author yoan
 */
public class OAuth2AuthorizationCodeMongoConverter extends MongoDocumentConverter<OAuth2AuthorizationCode> {
	public static final String FIELD_CODE = "code";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_USER_ID = "userId";
    
    public OAuth2AuthorizationCodeMongoConverter() {
		super();
	}
	
    public OAuth2AuthorizationCodeMongoConverter(Codec<Document> codec) {
		super(codec);
	}
    
    @Override
   	public OAuth2AuthorizationCode fromDocument(Document doc) {
   		if (doc == null) {
   			return null;
   		}
   		
       UUID id = doc.get(FIELD_ID, UUID.class);
       String code = doc.getString(FIELD_CODE);
       UUID userId = doc.get(FIELD_USER_ID, UUID.class);
       Date created = doc.getDate(FIELD_CREATED);
       LocalDateTime creationDate = DateHelper.toLocalDateTime(created);
       
       return OAuth2AuthorizationCode.Builder.createDefault()
       				   .withId(id)
       				   .withCreationDate(creationDate)
       				   .withCode(code)
       				   .withUserId(userId)
       				   .build();
   	}

   	@Override
   	public Document toDocument(OAuth2AuthorizationCode authCode) {
   		if (authCode == null) {
   			return new Document();
   		}
   		
   		return new Document(FIELD_ID, authCode.getId())
   				.append(FIELD_CODE, authCode.getCode())
   				.append(FIELD_CREATED, DateHelper.toDate(authCode.getCreationDate()))
   				.append(FIELD_USER_ID, authCode.getuserId());
   	}
       
   	@Override
   	public Class<OAuth2AuthorizationCode> getEncoderClass() {
   		return OAuth2AuthorizationCode.class;
   	}
   	
   	@Override
   	public OAuth2AuthorizationCode generateIdIfAbsentFromDocument(OAuth2AuthorizationCode authCode) {
   		return documentHasId(authCode) ? authCode : OAuth2AuthorizationCode.Builder.createFrom(authCode).withRandomId().build();
   	}
   	
   	public Bson filterByCode(String authzCode) {
   		return Filters.eq(FIELD_CODE, authzCode);
   	}
}
