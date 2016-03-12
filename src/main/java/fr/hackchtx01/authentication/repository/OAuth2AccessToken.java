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
 * Oauth2 access token value object
 * @author yoan
 */
public class OAuth2AccessToken implements Bson, WithId {
	/** Default auth token ID */
	public static final UUID DEFAULT_ID = UUID.fromString("47e260b3-14ea-4b83-ac13-dafc3752b350");
	public static final String DEFAULT_TOKEN = "default token";
	public static final OAuth2AccessToken DEFAULT = Builder.createDefault().build();
	
	/** Access token unique ID */
	private final UUID id;
	/** Access token value */
	private final String token;
	/** Access token creation date */
	private final LocalDateTime creationDate;
	/** Associated user ID */
	private final UUID userId;
	/** Number of refresh on this token */
	private final int nbRefresh;
	
	protected OAuth2AccessToken(UUID id, String token, LocalDateTime creationDate, UUID userId, int nbRefresh) {
		this.id = requireNonNull(id, "Access token Id is mandatory");
		checkArgument(StringUtils.isNotBlank(token), "Invalid token");
		this.token = token;
		this.creationDate = requireNonNull(creationDate, "Creation date is mandatory");
		this.userId = requireNonNull(userId, "User ID is mandatory");
		checkArgument(nbRefresh >= 0, "Invalid number of refresh");
		this.nbRefresh = nbRefresh;
	}

	public static class Builder implements GenericBuilder<OAuth2AccessToken> {
		private UUID id = DEFAULT_ID;
		private String token = DEFAULT_TOKEN;
		private LocalDateTime creationDate = LocalDateTime.now();
		private UUID userId = User.DEFAULT_ID;
		private int nbRefresh = 0;
		
		private Builder() { }
		
		/**
         * The default auth token is DEFAULT
         *
         * @return DEFAULT auth token
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
            builder.token = otherBuilder.token;
            builder.creationDate = otherBuilder.creationDate;
            builder.userId = otherBuilder.userId;
            builder.nbRefresh = otherBuilder.nbRefresh;
            
            return builder;
        }
        
        /**
         * Get a builder based on an existing OAuth2AccessToken instance
         *
         * @param authCode
         * @return builder
         */
        public static Builder createFrom(final OAuth2AccessToken authCode) {
            Builder builder = new Builder();

            builder.id = authCode.id;
            builder.token = authCode.token;
            builder.creationDate = authCode.creationDate;
            builder.userId = authCode.userId;
            builder.nbRefresh = authCode.nbRefresh;
            
            return builder;
        }
        
		@Override
		public OAuth2AccessToken build() {
			return new OAuth2AccessToken(id, token, creationDate, userId, nbRefresh);
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
        
        public Builder withToken(String token) {
            this.token = token;
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
        
        public Builder withNbRefresh(int nbRefresh) {
            this.nbRefresh = nbRefresh;
            return this;
        }
	}
	
	@Override
	public UUID getId() {
		return id;
	}

	public String getToken() {
		return token;
	}
	
	public LocalDateTime getCreationDate() {
		return creationDate;
	}
	
	public UUID getuserId() {
		return userId;
	}
	
	public int getNbRefresh() {
		return nbRefresh;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, token, creationDate, userId, nbRefresh);
	}

	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OAuth2AccessToken that = (OAuth2AccessToken) obj;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.token, that.token)
            && Objects.equals(this.creationDate, that.creationDate)
	        && Objects.equals(this.userId, that.userId)
	        && Objects.equals(this.nbRefresh, that.nbRefresh);
    }
	
	@Override
	public final String toString() {
		return MoreObjects.toStringHelper(this)
			.add("id", id).add("token", token)
			.add("created", creationDate)
			.add("userId", userId)
			.add("nbRefresh", nbRefresh)
			.toString();
	}
	
	@Override
	public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
		return new BsonDocumentWrapper<OAuth2AccessToken>(this, codecRegistry.get(OAuth2AccessToken.class));
	}
}
