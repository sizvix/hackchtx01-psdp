package fr.hackchtx01.infra.db.mongo;

import static fr.hackchtx01.infra.util.error.RepositoryErrorMessage.MONGO_DOCUMENT_WITHOUT_ID;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.UUID;

import org.bson.BsonValue;
import org.bson.Document;
import org.junit.Test;

import fr.hackchtx01.infra.db.WithId;
import fr.hackchtx01.infra.db.mongo.MongoDocumentConverter;

public class MongoDocumentConverterTest {

	private static class BasicObjectWithId implements WithId {
		private UUID id;
		private BasicObjectWithId(UUID id) {
			this.id = id;
		}
		@Override
		public UUID getId() {
			return id;
		}
	}
	
	private static class BasicMongoDocumentConverter extends MongoDocumentConverter<BasicObjectWithId> {
		public BasicObjectWithId generateIdIfAbsentFromDocument(BasicObjectWithId document) { return document == null ? new BasicObjectWithId(UUID.randomUUID()) : document; }
		public Class<BasicObjectWithId> getEncoderClass() { return BasicObjectWithId.class; }
		public BasicObjectWithId fromDocument(Document doc) { return null; }
		public Document toDocument(BasicObjectWithId obj) {return null; }
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getDocumentId_should_fail_if_object_does_not_have_an_Id() {
		//given
		String expectedErrorMsg = MONGO_DOCUMENT_WITHOUT_ID.getDevReadableMessage(); 
		BasicObjectWithId objectWithoutId = new BasicObjectWithId(null);
		BasicMongoDocumentConverter testedConverter = new BasicMongoDocumentConverter();;
		
		//when
		try {
			testedConverter.getDocumentId(objectWithoutId);
		} catch(IllegalArgumentException iae) {
		//then
			assertThat(iae.getMessage()).isEqualTo(expectedErrorMsg);
			throw iae;
		}
	}
	
	@Test
	public void getDocumentId_should_work_if_object_has_an_Id() {
		//given
		BasicObjectWithId objectWithoutId = new BasicObjectWithId(UUID.randomUUID());
		BasicMongoDocumentConverter testedConverter = new BasicMongoDocumentConverter();;
		
		//when
		BsonValue result = testedConverter.getDocumentId(objectWithoutId);
		
		//then
		assertThat(result).isNotNull();
	}
}
