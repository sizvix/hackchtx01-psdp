/**
 * 
 */
package fr.hackchtx01.test.fongo;

import static java.util.Objects.requireNonNull;

import org.bson.Document;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import fr.hackchtx01.infra.config.api.Config;
import fr.hackchtx01.infra.db.Dbs;
import fr.hackchtx01.infra.db.mongo.MongoDbConnectionFactory;

/**
 * Fongo implementation of the MongoDb connection factory
 * @author yoan
 */
public class FongoDbConnectionFactory extends MongoDbConnectionFactory {
	private final Fongo fongo;
	
	public FongoDbConnectionFactory(Fongo fongo) {
		super(Config.DEFAULT);
		this.fongo = requireNonNull(fongo);
	}
	
	@Override
	public MongoDatabase getDB(Dbs db) {
		return fongo.getDatabase(db.getDbName()).withCodecRegistry(generateFinalCodecRegistry());
	}
	
	public MongoCollection<Document> getCollection(Dbs db, String collectionName) {
		return getDB(db).getCollection(collectionName);
	}
	
	public <TDOC> MongoCollection<TDOC> getCollection(Dbs db, String collectionName, Class<TDOC> documentClass) {
		return getDB(db).getCollection(collectionName, documentClass);
	}
}
