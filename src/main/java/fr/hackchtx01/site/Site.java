package fr.hackchtx01.site;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ImmutableList;

import fr.hackchtx01.infra.db.WithId;
import fr.hackchtx01.infra.util.GenericBuilder;

public class Site implements Bson, WithId {
	/** Default site ID */
	public static final UUID DEFAULT_ID = UUID.fromString("19342151-fe36-4a90-8cbc-a1b8261e8539");
	/** Default site URI */
	public static final URI DEFAULT_SITE_URI = URI.create("http://default.com");
	/** Default Site instance */
	public static final Site DEFAULT = Builder.createDefault().build();
	
	/** Site unique ID */
	private final UUID id;
	/** Site last name */
	private final String name;
	/** Site URL */
	private final URI url;
	/** site creation date */
	private final LocalDateTime creationDate;
	/** Last time the site was updated */
	private final LocalDateTime lastUpdate;
	private final ImmutableList<SiteUrl> urls;
	
	public Site() {
		id = null;
		name = null;
		url = null;
		creationDate = null;
		lastUpdate = null;
		urls = null;
	}
	
	protected Site(UUID id, String name, URI url, LocalDateTime creationDate, LocalDateTime lastUpdate, ImmutableList<SiteUrl> urls) {
		this.id = requireNonNull(id, "Site Id is mandatory");
		checkArgument(StringUtils.isNotBlank(name), "Invalid site name");
		this.name = name;
		this.url = requireNonNull(url, "Site URL is mandatory");
		this.creationDate = requireNonNull(creationDate, "Creation date is mandatory");
		this.lastUpdate = requireNonNull(lastUpdate, "Last update date is mandatory");
		this.urls = requireNonNull(urls, "Urls list is mandatory");
	}
	
	public static class Builder implements GenericBuilder<Site> {
		private UUID id = DEFAULT_ID;
		private String name = "Default site";
		private URI url = DEFAULT_SITE_URI;
		private LocalDateTime creationDate = LocalDateTime.now();
		private LocalDateTime lastUpdate = LocalDateTime.now();
		private List<SiteUrl> urls = new ArrayList<>();
		
		private Builder() { }
		
		/**
         * The default site is DEFAULT
         *
         * @return DEFAULT Site
         */
        public static Builder createDefault() {
            return new Builder();
        }
        
        /**
         * Duplicate an existing builder
         *
         * @param otherBuilder
         * @return builder
         */
        public static Builder createFrom(final Builder otherBuilder) {
            Builder builder = new Builder();

            builder.id = otherBuilder.id;
            builder.name = otherBuilder.name;
            builder.url = otherBuilder.url;
            builder.creationDate = otherBuilder.creationDate;
            builder.lastUpdate = otherBuilder.lastUpdate;
            builder.urls = otherBuilder.urls;

            return builder;
        }
        
        /**
         * Get a builder based on an existing Site instance
         *
         * @param site
         * @return builder
         */
        public static Builder createFrom(final Site site) {
            Builder builder = new Builder();

            builder.id = site.id;
            builder.name = site.name;
            builder.url = site.url;
            builder.creationDate = site.creationDate;
            builder.lastUpdate = site.lastUpdate;
            builder.urls = site.urls;
            
            return builder;
        }
        
        @Override
        public Site build() {
        	ImmutableList<SiteUrl> finalUrls = ImmutableList.<SiteUrl>copyOf(urls);
            return new Site(id, name, url, creationDate, lastUpdate, finalUrls);
        }
        
        public Builder withId(UUID id) {
            this.id = Objects.requireNonNull(id);
            return this;
        }

        /**
         * Set a random site ID
         *
         * @return builder
         */
        public Builder withRandomId() {
            this.id = UUID.randomUUID();
            return this;
        }
        
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder withUrl(URI url) {
            this.url = url;
            return this;
        }
        
        public Builder withCreationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }
        
        public Builder withLastUpdate(LocalDateTime lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }
        
        public Builder withUrls(List<SiteUrl> urls) {
            this.urls = requireNonNull(urls);
            return this;
        }
        
        public Builder withSiteUrl(SiteUrl url) {
        	urls.add(url);
            return this;
        }
        
        public Builder withoutSiteUrl(SiteUrl url) {
        	urls.remove(url);
            return this;
        }
	}
	
	@Override
	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public URI getUrl() {
		return url;
	}
	
	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}
	
	public ImmutableList<SiteUrl> getUrls() {
		return urls;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, url);
	}

	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Site that = (Site) obj;
        return Objects.equals(this.id, that.id)
                && Objects.equals(this.name, that.name)
        		&& Objects.equals(this.url, that.url);
    }
	
	protected ToStringHelper toStringHelper() {
		return MoreObjects.toStringHelper(this)
					.add("id", id)
					.add("name", name)
					.add("url", url)
					.add("urls", urls)
					.add("created", creationDate)
					.add("lastUpdate", lastUpdate);
   }

	@Override
	public final String toString() {
		return toStringHelper().toString();
	}
	
	@Override
	public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
		return new BsonDocumentWrapper<Site>(this, codecRegistry.get(Site.class));
	}
}
