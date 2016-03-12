package fr.hackchtx01.infra.db.mongo;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;

import fr.hackchtx01.infra.logging.Markers;

/**
 * Helper to create different kind of mongo indexes if not already existing on a mongo collection
 * @author yoan
 */
public class MongoIndexEnsurer {
	
	private final MongoCollection<?> collection;
	private final String collectionName;
	
	private static final Marker CONFIG = Markers.CONFIG.getMarker();
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoIndexEnsurer.class);
	
	public enum SortOrder {
		ASCENDING(1),
		DESCENDING(-1);
		
		private int order;
		
		private SortOrder(int order) {
			this.order = order;
		}
		
		public int getOrder() {
			return order;
		}
	}
	
	public MongoIndexEnsurer(MongoCollection<?> collection) {
		this.collection = requireNonNull(collection, "Mongo collection is mandatory");
		collectionName = collection.getNamespace().getCollectionName();
	}
	
	public void logStartEnsuringIndexes() {
		LOGGER.info(CONFIG, String.format("Ensuring indexes on %s collection", collectionName));
	}
	
	public void logEndEnsuringIndexes() {
		LOGGER.info(CONFIG, String.format("Finished to ensure indexes on %s collection", collectionName));
	}
	
	public void ensureTTLIndex(String fieldName, SortOrder sortOrder, long ttl, TimeUnit timeUnit) {
		Document indexKey = new Document(fieldName, sortOrder.getOrder());
		IndexOptions options = new IndexOptions();
		options.expireAfter(ttl, timeUnit);
		collection.createIndex(indexKey, options);
	}
	
	public void ensureUniqueIndex(String fieldName, SortOrder sortOrder) {
		Document indexKey = new Document(fieldName, sortOrder.getOrder());
		IndexOptions options = new IndexOptions();
		options.unique(true);
		collection.createIndex(indexKey, options);
	}
	
	public void ensureIndex(String fieldName, SortOrder sortOrder) {
		Document indexKey = new Document(fieldName, sortOrder.getOrder());
		collection.createIndex(indexKey);
	}
}
