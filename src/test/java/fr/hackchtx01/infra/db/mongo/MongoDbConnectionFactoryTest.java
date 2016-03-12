package fr.hackchtx01.infra.db.mongo;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.mongodb.MongoCredential;

import fr.hackchtx01.infra.config.api.Config;
import fr.hackchtx01.infra.db.mongo.MongoDbConnectionFactory;

public class MongoDbConnectionFactoryTest {

	@Test
	public void getCredentials_should_return_empty_list_if_no_mongo_credentials_in_config() {
		//given
		Config configWithoutMongoCredentials = Config.Builder.createDefault().build();
		MongoDbConnectionFactory tested = new MongoDbConnectionFactory(configWithoutMongoCredentials);
		
		//when
		List<MongoCredential> result = tested.getCredentials();
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();
	}
	
	@Test
	public void getCredentials_should_return_credential_list_if_mongo_credentials_in_config() {
		//given
		Config configWithMongoCredentials = Config.Builder.createDefault()
				.withMongoUser("user")
				.withMongoPass("pass")
				.build();
		MongoDbConnectionFactory tested = new MongoDbConnectionFactory(configWithMongoCredentials);
		
		//when
		List<MongoCredential> result = tested.getCredentials();
		
		//then
		assertThat(result).isNotNull();
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(1);
	}
}
