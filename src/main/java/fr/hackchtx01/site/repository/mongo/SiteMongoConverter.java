package fr.hackchtx01.site.repository.mongo;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.bson.codecs.Codec;

import com.google.common.collect.ImmutableList;

import fr.hackchtx01.infra.db.mongo.MongoDocumentConverter;
import fr.hackchtx01.infra.util.helper.DateHelper;
import fr.hackchtx01.site.Site;
import fr.hackchtx01.site.SiteUrl;

public class SiteMongoConverter extends MongoDocumentConverter<Site> {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_URL = "url";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_LAST_UPDATE = "lastUpdate";
    public static final String FIELD_URLS = "urls";
    public static final String FIELD_URL_TYPE = "type";
    
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
        List<SiteUrl> urls = extractUrls(doc);
        
        return Site.Builder.createDefault()
        				   .withId(id)
        				   .withCreationDate(creationDate)
        				   .withLastUpdate(lastUpdate)
        				   .withName(name)
        				   .withUrl(url)
        				   .withUrls(urls)
        				   .build();
	}
	
	private List<SiteUrl> extractUrls(Document doc) {
		@SuppressWarnings("unchecked")
		List<Document> array = (List<Document>) doc.get(FIELD_URLS);
		List<SiteUrl> urls = new ArrayList<SiteUrl>();
		array.forEach(url -> urls.add(extractUrl(url)));
		return urls;
	}
	
	private SiteUrl extractUrl(Document doc) {
		if (doc == null) {
			return null;
		}
		
        String type = doc.getString(FIELD_URL_TYPE);
        URI url = URI.create(doc.getString(FIELD_URL));
        
        return SiteUrl.Builder.createDefault()
        				   .withType(type)
        				   .withUrl(url)
        				   .build();
	}

	@Override
	public Document toDocument(Site site) {
		if (site == null) {
			return new Document();
		}
		
		List<Document> urlArray = getUrlArray(site.getUrls());
		
		return new Document(FIELD_ID, site.getId())
				.append(FIELD_NAME, site.getName())
				.append(FIELD_URL, site.getUrl().toString())
				.append(FIELD_URLS, urlArray)
				.append(FIELD_CREATED, DateHelper.toDate(site.getCreationDate()))
				.append(FIELD_LAST_UPDATE, DateHelper.toDate(site.getLastUpdate()));
	}
	
	protected List<Document> getUrlArray(ImmutableList<SiteUrl> urls) {
		List<Document> urlArray = new ArrayList<>();
		urls.forEach(url -> urlArray.add(toDocument(url)));
		return urlArray;
	}
	
	public static Document toDocument(SiteUrl url) {
		if (url == null) {
			return new Document();
		}
		
		return new Document(FIELD_URL_TYPE, url.getType())
				.append(FIELD_URL, url.getUrl().toString());
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