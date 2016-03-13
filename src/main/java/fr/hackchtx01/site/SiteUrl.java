package fr.hackchtx01.site;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import fr.hackchtx01.infra.util.GenericBuilder;

public class SiteUrl implements Bson {
	
	public static final SiteUrl DEFAULT = Builder.createDefault().build();
	
	private final String type;
	private final URI url;
	
	protected SiteUrl(String type, URI url) {
		super();
		checkArgument(StringUtils.isNotBlank(type), "Invalid site url type");
		this.type = type;
		this.url = requireNonNull(url, "Site URL is mandatory");
	}
	
	public static class Builder implements GenericBuilder<SiteUrl> {
		private String type = "Default type";
		private URI url = Site.DEFAULT_SITE_URI;
		
		private Builder() { }
		
		/**
         * The default siteUrl is DEFAULT
         *
         * @return DEFAULT siteUrl
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

            builder.type = otherBuilder.type;
            builder.url = otherBuilder.url;

            return builder;
        }
        
        /**
         * Get a builder based on an existing SiteUrl instance
         *
         * @param siteUrl
         * @return builder
         */
        public static Builder createFrom(final SiteUrl siteUrl) {
            Builder builder = new Builder();

            builder.type = siteUrl.type;
            builder.url = siteUrl.url;
            
            return builder;
        }
        
        @Override
        public SiteUrl build() {
            return new SiteUrl(type, url);
        }
        
        public Builder withType(String type) {
            this.type = type;
            return this;
        }
        
        public Builder withUrl(URI url) {
            this.url = url;
            return this;
        }
	}
	

	public String getType() {
		return type;
	}

	public URI getUrl() {
		return url;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, url);
	}

	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SiteUrl that = (SiteUrl) obj;
        return Objects.equals(this.type, that.type)
        		&& Objects.equals(this.url, that.url);
    }
	
	protected ToStringHelper toStringHelper() {
		return MoreObjects.toStringHelper(this)
					.add("type", type)
					.add("url", url);
   }

	@Override
	public final String toString() {
		return toStringHelper().toString();
	}
	
	@Override
	public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
		return new BsonDocumentWrapper<SiteUrl>(this, codecRegistry.get(SiteUrl.class));
	}
}
