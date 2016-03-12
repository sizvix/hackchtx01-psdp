package fr.hackchtx01.authentication.repository.mongo;

import static fr.hackchtx01.authentication.repository.mongo.OAuth2AuthorizationCodeMongoConverter.*;
import static fr.hackchtx01.infra.db.mongo.MongoDocumentConverter.FIELD_ID;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.UUID;

import org.bson.Document;
import org.junit.Test;

import fr.hackchtx01.authentication.repository.OAuth2AuthorizationCode;
import fr.hackchtx01.authentication.repository.mongo.OAuth2AuthorizationCodeMongoConverter;
import fr.hackchtx01.infra.util.helper.DateHelper;
import fr.hackchtx01.test.TestHelper;

public class OAuth2AuthorizationCodeMongoConverterTest {
	
	@Test
	public void fromDocument_should_return_null_with_null_document() {
		//given
		Document nullDoc = null;
		OAuth2AuthorizationCodeMongoConverter testedConverter = new OAuth2AuthorizationCodeMongoConverter();
		
		//when
		OAuth2AuthorizationCode result = testedConverter.fromDocument(nullDoc);
		
		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void fromDocument_should_work_with_valid_doc() {
		//given
		OAuth2AuthorizationCode expectedAuthCode = TestHelper.generateRandomOAuth2AuthorizationCode();
		OAuth2AuthorizationCodeMongoConverter testedConverter = new OAuth2AuthorizationCodeMongoConverter();
		Document doc = new Document(FIELD_ID, expectedAuthCode.getId())
							.append(FIELD_CODE, expectedAuthCode.getCode())
							.append(FIELD_USER_ID, expectedAuthCode.getuserId())
							.append(FIELD_CREATED, DateHelper.toDate(expectedAuthCode.getCreationDate()));
		//when
		OAuth2AuthorizationCode result = testedConverter.fromDocument(doc);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectedAuthCode.getId());
		assertThat(result.getCode()).isEqualTo(expectedAuthCode.getCode());
		assertThat(result.getuserId()).isEqualTo(expectedAuthCode.getuserId());
		assertThat(result.getCreationDate()).isEqualTo(expectedAuthCode.getCreationDate());
	}
	
	@Test
	public void toDocument_should_return_empty_doc_with_null_auth_code() {
		//given
		OAuth2AuthorizationCode nullAuthCode = null;
		OAuth2AuthorizationCodeMongoConverter testedConverter = new OAuth2AuthorizationCodeMongoConverter();
		
		//when
		Document result = testedConverter.toDocument(nullAuthCode);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(new Document());
	}
	
	@Test
	public void toDocument_should_work_with_valid_list() {
		//given
		OAuth2AuthorizationCode clientApp = TestHelper.generateRandomOAuth2AuthorizationCode();
		OAuth2AuthorizationCodeMongoConverter testedConverter = new OAuth2AuthorizationCodeMongoConverter();
		
		//when
		Document result = testedConverter.toDocument(clientApp);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.get(FIELD_ID, UUID.class)).isEqualTo(clientApp.getId());
		assertThat(result.getString(FIELD_CODE)).isEqualTo(clientApp.getCode());
		assertThat(result.get(FIELD_USER_ID, UUID.class)).isEqualTo(clientApp.getuserId());
		assertThat(DateHelper.toLocalDateTime(result.getDate(FIELD_CREATED))).isEqualTo(clientApp.getCreationDate());
	}
}
