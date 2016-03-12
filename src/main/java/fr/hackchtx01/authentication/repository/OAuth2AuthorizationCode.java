package fr.hackchtx01.authentication.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.google.common.base.MoreObjects;

import fr.hackchtx01.infra.db.WithId;
import fr.hackchtx01.infra.util.GenericBuilder;
import fr.hackchtx01.user.User;

/**
 * Oauth2 authorization code value object
 * @author yoan
 */
public class OAuth2AuthorizationCode implements Bson, WithId {
	/** Default auth code ID */
	public static final UUID DEFAULT_ID = UUID.fromString("8b1241e5-d935-4a40-8927-5eb7b87784b6");
	public static final String DEFAULT_CODE = "default code";
	public static final OAuth2AuthorizationCode DEFAULT = Builder.createDefault().build();
	
	/** Auth code unique ID */
	private final UUID id;
	/** Auth code value */
	private final String code;
	/** Auth code creation date */
	private final LocalDateTime creationDate;
	/** Associated user ID */
	private final UUID userId;
	
	protected OAuth2AuthorizationCode(UUID id, String code, LocalDateTime creationDate, UUID userId) {
		this.id = requireNonNull(id, "Auth code Id is mandatory");
		checkArgument(StringUtils.isNotBlank(code), "Invalid code");
		this.code = code;
		this.creationDate = requireNonNull(creationDate, "Creation date is mandatory");
		this.userId = requireNonNull(userId, "User ID is mandatory");
	}

	public static class Builder implements GenericBuilder<OAuth2AuthorizationCode> {
		private UUID id = DEFAULT_ID;
		private String code = DEFAULT_CODE;
		private LocalDateTime creationDate = LocalDateTime.now();
		private UUID userId = User.DEFAULT_ID;
		
		private Builder() { }
		
		/**
         * The default auth code is DEFAULT
         *
         * @return DEFAULT auth code
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
            builder.code = otherBuilder.code;
            builder.creationDate = otherBuilder.creationDate;
            builder.userId = otherBuilder.userId;
            
            return builder;
        }
        
        /**
         * Get a builder based on an existing OAuth2AuthorizationCode instance
         *
         * @param authCode
         * @return builder
         */
        public static Builder createFrom(final OAuth2AuthorizationCode authCode) {
            Builder builder = new Builder();

            builder.id = authCode.id;
            builder.code = authCode.code;
            builder.creationDate = authCode.creationDate;
            builder.userId = authCode.userId;
            
            return builder;
        }
        
		@Override
		public OAuth2AuthorizationCode build() {
			return new OAuth2AuthorizationCode(id, code, creationDate, userId);
		}
		
		public Builder withId(UUID id) {
            this.id = requireNonNull(id);
            return this;
        }

        /**
         * Set a random ID
         *
         * @return builder
         */
        public Builder withRandomId() {
            this.id = UUID.randomUUID();
            return this;
        }
        
        public Builder withCode(String code) {
            this.code = code;
            return this;
        }
        
        public Builder withCreationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }
        
        public Builder withUserId(UUID userId) {
            this.userId = requireNonNull(userId);
            return this;
        }
        
	}
	
	@Override
	public UUID getId() {
		return id;
	}

	public String getCode() {
		return code;
	}
	
	public LocalDateTime getCreationDate() {
		return creationDate;
	}
	
	public UUID getuserId() {
		return userId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, code, creationDate, userId);
	}

	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OAuth2AuthorizationCode that = (OAuth2AuthorizationCode) obj;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.code, that.code)
            && Objects.equals(this.creationDate, that.creationDate)
	        && Objects.equals(this.userId, that.userId);
    }
	
	@Override
	public final String toString() {
		return MoreObjects.toStringHelper(this)
			.add("id", id).add("code", code)
			.add("created", creationDate)
			.add("userId", userId)
			.toString();
	}
	
	@Override
	public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
		return new BsonDocumentWrapper<OAuth2AuthorizationCode>(this, codecRegistry.get(OAuth2AuthorizationCode.class));
	}
}
