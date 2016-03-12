package fr.hackchtx01.authentication.repository.mongo;

import static fr.hackchtx01.authentication.repository.mongo.OAuth2AccessTokenMongoConverter.*;
import static fr.hackchtx01.infra.db.mongo.MongoDocumentConverter.FIELD_ID;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.UUID;

import org.bson.Document;
import org.junit.Test;

import fr.hackchtx01.authentication.repository.OAuth2AccessToken;
import fr.hackchtx01.authentication.repository.mongo.OAuth2AccessTokenMongoConverter;
import fr.hackchtx01.infra.util.helper.DateHelper;
import fr.hackchtx01.test.TestHelper;

public class OAuth2AccessTokenMongoConverterTest {
	@Test
	public void fromDocument_should_return_null_with_null_document() {
		//given
		Document nullDoc = null;
		OAuth2AccessTokenMongoConverter testedConverter = new OAuth2AccessTokenMongoConverter();
		
		//when
		OAuth2AccessToken result = testedConverter.fromDocument(nullDoc);
		
		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void fromDocument_should_work_with_valid_doc() {
		//given
		OAuth2AccessToken expectedAuthCode = TestHelper.generateRandomOAuth2AccessToken();
		OAuth2AccessTokenMongoConverter testedConverter = new OAuth2AccessTokenMongoConverter();
		Document doc = new Document(FIELD_ID, expectedAuthCode.getId())
							.append(FIELD_TOKEN, expectedAuthCode.getToken())
							.append(FIELD_USER_ID, expectedAuthCode.getuserId())
							.append(FIELD_CREATED, DateHelper.toDate(expectedAuthCode.getCreationDate()))
							.append(FIELD_NB_REFRESH, expectedAuthCode.getNbRefresh());
		//when
		OAuth2AccessToken result = testedConverter.fromDocument(doc);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectedAuthCode.getId());
		assertThat(result.getToken()).isEqualTo(expectedAuthCode.getToken());
		assertThat(result.getuserId()).isEqualTo(expectedAuthCode.getuserId());
		assertThat(result.getCreationDate()).isEqualTo(expectedAuthCode.getCreationDate());
		assertThat(result.getNbRefresh()).isEqualTo(expectedAuthCode.getNbRefresh());
	}
	
	@Test
	public void toDocument_should_return_empty_doc_with_null_auth_code() {
		//given
		OAuth2AccessToken nullAuthCode = null;
		OAuth2AccessTokenMongoConverter testedConverter = new OAuth2AccessTokenMongoConverter();
		
		//when
		Document result = testedConverter.toDocument(nullAuthCode);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(new Document());
	}
	
	@Test
	public void toDocument_should_work_with_valid_list() {
		//given
		OAuth2AccessToken clientApp = TestHelper.generateRandomOAuth2AccessToken();
		OAuth2AccessTokenMongoConverter testedConverter = new OAuth2AccessTokenMongoConverter();
		
		//when
		Document result = testedConverter.toDocument(clientApp);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.get(FIELD_ID, UUID.class)).isEqualTo(clientApp.getId());
		assertThat(result.getString(FIELD_TOKEN)).isEqualTo(clientApp.getToken());
		assertThat(result.get(FIELD_USER_ID, UUID.class)).isEqualTo(clientApp.getuserId());
		assertThat(DateHelper.toLocalDateTime(result.getDate(FIELD_CREATED))).isEqualTo(clientApp.getCreationDate());
		assertThat(result.getInteger(FIELD_NB_REFRESH)).isEqualTo(clientApp.getNbRefresh());
	}
}
