/**
 * 
 */
package fr.hackchtx01.user.repository.mongo;

import static fr.hackchtx01.user.repository.mongo.UserMongoConverter.FIELD_CREATED;
import static fr.hackchtx01.user.repository.mongo.UserMongoConverter.FIELD_EMAIL;
import static fr.hackchtx01.user.repository.mongo.UserMongoConverter.FIELD_ID;
import static fr.hackchtx01.user.repository.mongo.UserMongoConverter.FIELD_LAST_UPDATE;
import static fr.hackchtx01.user.repository.mongo.UserMongoConverter.FIELD_NAME;
import static org.fest.assertions.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.bson.Document;
import org.junit.Test;

import fr.hackchtx01.infra.util.helper.DateHelper;
import fr.hackchtx01.test.TestHelper;
import fr.hackchtx01.user.User;
import fr.hackchtx01.user.repository.mongo.UserMongoConverter;

/**
 * 
 * @author yoan
 */
public class UserMongoConverterTest {
	
	@Test
	public void fromDocument_should_return_null_with_null_document() {
		//given
		Document nullDoc = null;
		UserMongoConverter testedConverter = new UserMongoConverter();
		
		//when
		User result = testedConverter.fromDocument(nullDoc);
		
		//then
		assertThat(result).isNull();
	}
	
	@Test
	public void fromDocument_should_work_with_valid_doc() {
		//given
		UUID expectId = UUID.randomUUID();
		String expectedName = "name";
		String expectedMail = "mail";
		LocalDateTime expectedCreationDate = LocalDateTime.now();
		LocalDateTime expectedLastUpdate = LocalDateTime.now();
		
		Document doc = new Document(FIELD_ID, expectId)
							.append(FIELD_NAME, expectedName)
							.append(FIELD_EMAIL, expectedMail)
							.append(FIELD_CREATED, DateHelper.toDate(expectedCreationDate))
							.append(FIELD_LAST_UPDATE, DateHelper.toDate(expectedLastUpdate));
		UserMongoConverter testedConverter = new UserMongoConverter();
		
		//when
		User result = testedConverter.fromDocument(doc);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectId);
		assertThat(result.getName()).isEqualTo(expectedName);
		assertThat(result.getEmail()).isEqualTo(expectedMail);
		assertThat(result.getCreationDate()).isEqualTo(expectedCreationDate);
		assertThat(result.getLastUpdate()).isEqualTo(expectedLastUpdate);
	}
	
	@Test
	public void toDocument_should_return_empty_doc_with_null_user() {
		//given
		User nulluser = null;
		UserMongoConverter testedConverter = new UserMongoConverter();
		
		//when
		Document result = testedConverter.toDocument(nulluser);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(new Document());
	}
	
	@Test
	public void toDocument_should_work_with_valid_user() {
		//given
		User user = TestHelper.generateRandomUser();
		UserMongoConverter testedConverter = new UserMongoConverter();
		
		//when
		Document result = testedConverter.toDocument(user);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.get(FIELD_ID)).isEqualTo(user.getId());
		assertThat(result.getString(FIELD_NAME)).isEqualTo(user.getName());
		assertThat(result.getString(FIELD_EMAIL)).isEqualTo(user.getEmail());
		assertThat(DateHelper.toLocalDateTime(result.getDate(FIELD_CREATED))).isEqualTo(user.getCreationDate());
		assertThat(DateHelper.toLocalDateTime(result.getDate(FIELD_LAST_UPDATE))).isEqualTo(user.getLastUpdate());
	}
}
