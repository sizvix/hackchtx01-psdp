package fr.hackchtx01.site.repository.mongo;

import static fr.hackchtx01.infra.db.mongo.MongoDocumentConverter.FIELD_ID;
import static fr.hackchtx01.infra.db.mongo.MongoIndexEnsurer.SortOrder.ASCENDING;
import static fr.hackchtx01.site.repository.SiteRepositoryErrorMessage.PROBLEM_CREATION_SITE;
import static fr.hackchtx01.site.repository.SiteRepositoryErrorMessage.PROBLEM_CREATION_SITE_URL;
import static fr.hackchtx01.site.repository.SiteRepositoryErrorMessage.PROBLEM_DELETE_SITE;
import static fr.hackchtx01.site.repository.SiteRepositoryErrorMessage.PROBLEM_READ_SITE;
import static fr.hackchtx01.site.repository.SiteRepositoryErrorMessage.PROBLEM_UPDATE_SITE;
import static fr.hackchtx01.site.repository.mongo.SiteMongoConverter.FIELD_URL;
import static fr.hackchtx01.site.repository.mongo.SiteMongoConverter.FIELD_URLS;

import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import fr.hackchtx01.infra.db.Dbs;
import fr.hackchtx01.infra.db.mongo.MongoDbConnectionFactory;
import fr.hackchtx01.infra.db.mongo.MongoIndexEnsurer;
import fr.hackchtx01.infra.util.helper.MongoRepositoryHelper;
import fr.hackchtx01.site.Site;
import fr.hackchtx01.site.SiteUrl;
import fr.hackchtx01.site.repository.SiteRepository;

@Singleton
public class SiteMongoRepository extends SiteRepository {
	public static final String SITE_COLLECTION = "sites";
	
	private final MongoCollection<Site> siteCollection;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SiteMongoRepository.class);
	
	@Inject
	public SiteMongoRepository(MongoDbConnectionFactory mongoConnectionFactory) {
		siteCollection = mongoConnectionFactory.getCollection(Dbs.PSDP, SITE_COLLECTION, Site.class);
		ensureIndexes();
	}
	
	private void ensureIndexes() {
		MongoIndexEnsurer indexEnsurer = new MongoIndexEnsurer(siteCollection);
		indexEnsurer.logStartEnsuringIndexes();
		
		indexEnsurer.ensureUniqueIndex(FIELD_URL, ASCENDING);
		
		indexEnsurer.logEndEnsuringIndexes();
	}
	
	@Override
	protected void processCreate(Site site) {
		try {
			siteCollection.insertOne(site);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_CREATION_SITE);
		}
	}

	@Override
	protected Site processGetById(UUID siteId) {
		Bson filter = Filters.eq(FIELD_ID, siteId);
		Site foundSite = null;
		try {
			foundSite = siteCollection.find().filter(filter).first();
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_READ_SITE);
		}
		return foundSite;
	}
	
	@Override
	protected void processUpdate(Site site) {
		Bson filter = Filters.eq(FIELD_ID, site.getId());
		Bson update = SiteMongoConverter.getSiteUpdate(site);
		try {
			siteCollection.updateOne(filter, update);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_UPDATE_SITE);
		}
	}
	
	@Override
	protected void processDeleteById(UUID siteId) {
		Bson filter = Filters.eq(FIELD_ID, siteId);
		try {
			siteCollection.deleteOne(filter);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_DELETE_SITE);
		}
	}

	@Override
	protected void processAddUrl(UUID siteId, SiteUrl urlToCreate) {
		//ensure site exists before updating it
		findSite(siteId);
		
		Bson filter = Filters.eq(FIELD_ID, siteId);
		Document addItem = new Document("$addToSet", new Document(FIELD_URLS, SiteMongoConverter.toDocument(urlToCreate)));
		try {
			siteCollection.updateOne(filter, addItem);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_CREATION_SITE_URL);
		}
	}

	@Override
	protected Site processFindByURL(String host) {
		String rege = "^http[s]{0,1}:\\/\\/"+host.replaceAll("\\.", "\\.");
		Document filter = new Document(SiteMongoConverter.FIELD_URL, new Document("$regex", rege ));
		List<Site> sites = Lists.newArrayList();
		try {
			siteCollection.find().filter(filter).into(sites);
		} catch(MongoException e) {
			MongoRepositoryHelper.handleMongoError(LOGGER, e, PROBLEM_CREATION_SITE_URL);
		}
		return sites.isEmpty()? null : sites.get(0) ;
	}
}