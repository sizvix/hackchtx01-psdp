package fr.hackchtx01.client.app;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.net.URI;
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
 * Client application
 * @author yoan
 */
public class ClientApp implements Bson, WithId {
	/** Default client app ID */
	public static final UUID DEFAULT_ID = UUID.fromString("addab685-8f35-4eeb-85b2-d3f74e4466da");
	/** Default password salt */
	public static final Object DEFAULT_SALT = UUID.fromString("dbafd399-217a-4b95-bb2b-2a754e90710c");
	/** Default redirect URI */
	public static final URI DEFAULT_REDIRECT_URI = URI.create("http://localhost:8080");
	/** Default client app instance */
	public static final ClientApp DEFAULT = Builder.createDefault().build();
	
	/** App unique ID */
	private final UUID id;
	/** App name */
	private final String name;
	/** User owner of the client application */
	private final UUID ownerId;
	/** Oauth2 redirect URI */
	private final URI redirectURI;
	/** App creation date */
	private final LocalDateTime creationDate;
	/** Last time the app was updated */
	private final LocalDateTime lastUpdate;
	/** App secret */
	private final String secret;
	/** App secret hash salt */
	private final Object salt;
	
	protected ClientApp(UUID id, String name, UUID ownerId, URI redirectURI, LocalDateTime creationDate, LocalDateTime lastUpdate, String secret, Object salt) {
		this.id = requireNonNull(id, "App Id is mandatory");
		checkArgument(StringUtils.isNotBlank(name), "Invalid app name");
		this.name = name;
		this.ownerId = requireNonNull(ownerId, "App owner Id is mandatory");
		this.redirectURI = requireNonNull(redirectURI, "Oauth2 redirect URI is mandatory");
		this.creationDate = requireNonNull(creationDate, "Creation date is mandatory");
		this.lastUpdate = requireNonNull(lastUpdate, "Last update date is mandatory");
		checkArgument(StringUtils.isNotBlank(secret), "Invalid app secret");
		this.secret = secret;
		this.salt = requireNonNull(salt, "The app secret hash salt is mandatory");
	}
	
	public static class Builder implements GenericBuilder<ClientApp> {
		private UUID id = DEFAULT_ID;
		private String name = "Default app";
		private UUID ownerId = User.DEFAULT_ID;
		private URI redirectURI = DEFAULT_REDIRECT_URI;
		private LocalDateTime creationDate = LocalDateTime.now();
		private LocalDateTime lastUpdate = LocalDateTime.now();
		private String secret = "Default secret";
		private Object salt = DEFAULT_SALT;
		
		private Builder() { }
		
		/**
         * The default app is DEFAULT
         *
         * @return DEFAULT app
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
            builder.ownerId = otherBuilder.ownerId;
            builder.redirectURI = otherBuilder.redirectURI;
            builder.creationDate = otherBuilder.creationDate;
            builder.lastUpdate = otherBuilder.lastUpdate;
            builder.secret = otherBuilder.secret;
            builder.salt = otherBuilder.salt;
            
            return builder;
        }
        
        /**
         * Get a builder based on an existing ClientApp instance
         *
         * @param app
         * @return builder
         */
        public static Builder createFrom(final ClientApp app) {
            Builder builder = new Builder();

            builder.id = app.id;
            builder.name = app.name;
            builder.ownerId = app.ownerId;
            builder.redirectURI = app.redirectURI;
            builder.creationDate = app.creationDate;
            builder.lastUpdate = app.lastUpdate;
            builder.secret = app.secret;
            builder.salt = app.salt;
            
            return builder;
        }
        
		@Override
		public ClientApp build() {
			return new ClientApp(id, name, ownerId, redirectURI, creationDate, lastUpdate, secret, salt);
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
        
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder withOwnerId(UUID ownerId) {
            this.ownerId = requireNonNull(ownerId);
            return this;
        }
        
        public Builder withRedirectURI(URI redirectURI) {
            this.redirectURI = redirectURI;
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
        
        public Builder withSalt(Object salt) {
            this.salt = salt;
            return this;
        }

        public Builder withSecret(String secret) {
            this.secret = secret;
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
	
	public UUID getOwnerId() {
		return ownerId;
	}
	
	public URI getRedirectURI() {
		return redirectURI;
	}
	
	public LocalDateTime getCreationDate() {
		return creationDate;
	}
	
	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}

	public String getSecret() {
		return secret;
	}

	public Object getSalt() {
		return salt;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, ownerId, redirectURI);
	}

	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ClientApp that = (ClientApp) obj;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.name, that.name)
            && Objects.equals(this.ownerId, that.ownerId)
            && Objects.equals(this.redirectURI, that.redirectURI);
    }
	
	@Override
	public final String toString() {
		return MoreObjects.toStringHelper(this)
			.add("id", id).add("name", name)
			.add("ownerId", ownerId)
			.add("redirectURI", redirectURI)
			.add("created", creationDate)
			.add("lastUpdate", lastUpdate)
			.toString();
	}
	
	@Override
	public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
		return new BsonDocumentWrapper<ClientApp>(this, codecRegistry.get(ClientApp.class));
	}
}
