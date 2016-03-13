package fr.hackchtx01.site;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import fr.hackchtx01.infra.util.GenericBuilder;

public class Field implements Bson {
	
	public static final Field DEFAULT = Builder.createDefault().build();
	
	private final String type;
	private final String name;
	
	protected Field(String type, String name) {
		super();
		checkArgument(StringUtils.isNotBlank(type), "Invalid site url field type");
		this.type = type;
		checkArgument(StringUtils.isNotBlank(name), "Invalid site url field name");
		this.name = name;
	}
	
	public static class Builder implements GenericBuilder<Field> {
		private String type = "Default type";
		private String name = "Default name";
		
		private Builder() { }
		
		/**
         * The default Field is DEFAULT
         *
         * @return DEFAULT Field
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
            builder.name = otherBuilder.name;

            return builder;
        }
        
        /**
         * Get a builder based on an existing Field instance
         *
         * @param siteUrl
         * @return builder
         */
        public static Builder createFrom(final Field siteUrl) {
            Builder builder = new Builder();

            builder.type = siteUrl.type;
            builder.name = siteUrl.name;
            
            return builder;
        }
        
        @Override
        public Field build() {
            return new Field(type, name);
        }
        
        public Builder withType(String type) {
            this.type = type;
            return this;
        }
        
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
	}
	

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, name);
	}

	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Field that = (Field) obj;
        return Objects.equals(this.type, that.type)
        		&& Objects.equals(this.name, that.name);
    }
	
	protected ToStringHelper toStringHelper() {
		return MoreObjects.toStringHelper(this)
					.add("type", type)
					.add("name", name);
   }

	@Override
	public final String toString() {
		return toStringHelper().toString();
	}
	
	@Override
	public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
		return new BsonDocumentWrapper<Field>(this, codecRegistry.get(Field.class));
	}
}