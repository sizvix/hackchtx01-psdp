package fr.hackchtx01.site.repository.mongo;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.bson.Document;
import org.bson.codecs.Codec;

import fr.hackchtx01.infra.db.mongo.MongoDocumentConverter;
import fr.hackchtx01.infra.util.helper.DateHelper;
import fr.hackchtx01.site.Site;

public class SiteMongoConverter extends MongoDocumentConverter<Site> {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_URL = "url";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_LAST_UPDATE = "lastUpdate";
    
    public SiteMongoConverter() {
		super();
	}
	
    public SiteMongoConverter(Codec<Document> codec) {
		super(codec);
	}
	
	@Override
	public Site fromDocument(Document doc) {
		if (doc == null) {
			return null;
		}
		
        UUID id = doc.get(FIELD_ID, UUID.class);
        String name = doc.getString(FIELD_NAME);
        URI url = URI.create(doc.getString(FIELD_URL));
        Date created = doc.getDate(FIELD_CREATED);
        LocalDateTime creationDate = DateHelper.toLocalDateTime(created);
        Date lastUpdated = doc.getDate(FIELD_LAST_UPDATE);
        LocalDateTime lastUpdate = DateHelper.toLocalDateTime(lastUpdated);
        
        return Site.Builder.createDefault()
        				   .withId(id)
        				   .withCreationDate(creationDate)
        				   .withLastUpdate(lastUpdate)
        				   .withName(name)
        				   .withUrl(url)
        				   .build();
	}

	@Override
	public Document toDocument(Site site) {
		if (site == null) {
			return new Document();
		}
		
		return new Document(FIELD_ID, site.getId())
				.append(FIELD_NAME, site.getName())
				.append(FIELD_URL, site.getUrl().toString())
				.append(FIELD_CREATED, DateHelper.toDate(site.getCreationDate()))
				.append(FIELD_LAST_UPDATE, DateHelper.toDate(site.getLastUpdate()));
	}
	
	@Override
	public Class<Site> getEncoderClass() {
		return Site.class;
	}
	
	@Override
	public Site generateIdIfAbsentFromDocument(Site site) {
		return documentHasId(site) ? site : Site.Builder.createFrom(site).withRandomId().build();
	}
	
	public static Document getSiteUpdate(Site siteToUpdate) {
		Document updateDoc = new Document(FIELD_LAST_UPDATE, DateHelper.toDate(siteToUpdate.getLastUpdate()))
									.append(FIELD_URL, siteToUpdate.getUrl().toString())
									.append(FIELD_NAME, siteToUpdate.getName());
		return new Document("$set", updateDoc);
	}
}