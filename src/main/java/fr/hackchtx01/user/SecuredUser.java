/**
 * 
 */
package fr.hackchtx01.user;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;

import com.google.common.base.MoreObjects.ToStringHelper;

import fr.hackchtx01.infra.util.GenericBuilder;
import fr.hackchtx01.infra.util.helper.SecurityHelper;

/**
 * User with security information
 * @author yoan
 */
public class SecuredUser extends User {
	/** default raw password */
	public static final String DEFAULT_RAW_PASSWORD = "Default password";
	/** Default password salt */
	public static final Object DEFAULT_SALT = UUID.fromString("94ccbae1-c33b-40fc-addd-f70d493e6eca");
	/** default hashed password - precomputed for performance sake */
	public static final String DEFAULT_HASED_PASSWORD = SecurityHelper.hash(DEFAULT_RAW_PASSWORD, DEFAULT_SALT.toString());
	/** Default secured user instance */
	public static final User DEFAULT = Builder.createDefault().build();
	
	/** User password */
	private final String password;
	/** User password hash salt */
	private final Object salt;
	
	public SecuredUser() {
		super();
		password = null;
		salt = null;
	}
	
	protected SecuredUser(UUID id, String name, String email, LocalDateTime creationDate, LocalDateTime lastUpdate, String password, Object salt) {
		super(id, name, email, creationDate, lastUpdate);
		checkArgument(StringUtils.isNotBlank(password), "Invalid user password");
		this.password = password;
		this.salt = requireNonNull(salt, "The password hash salt is mandatory");
	}
	
	public static class Builder implements GenericBuilder<SecuredUser> {
		private String password = DEFAULT_HASED_PASSWORD;
		private Object salt = DEFAULT_SALT;
		private User user = User.DEFAULT;
		
		private Builder() { }
		
		/**
         * The default user is DEFAULT
         *
         * @return DEFAULT User
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
	
            builder.password = otherBuilder.password;
            builder.salt = otherBuilder.salt;
            builder.user = otherBuilder.user;

            return builder;
        }
        
        /**
         * Get a builder based on an existing User instance
         *
         * @param user
         * @return builder
         */
        public static Builder createFrom(final User user) {
            Builder builder = new Builder();
            builder.user = user;
            return builder;
        }
        
        @Override
        public SecuredUser build() {
            return new SecuredUser(user.getId(), user.getName(), user.getEmail(), user.getCreationDate(), user.getLastUpdate(), password, salt);
        }
        
        public Builder withSalt(Object salt) {
            this.salt = salt;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }
        
        public Builder withRawPassword(String rawPassword) {
        	this.password = SecurityHelper.hash(rawPassword, salt);
        	return this;
        }
	}
	
	public String getPassword() {
		return password;
	}
	
	public Object getSalt() {
		return salt;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), password, salt);
	}

	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SecuredUser that = (SecuredUser) obj;
        return super.equals(obj)
        		&& Objects.equals(this.password, that.password)
        		&& Objects.equals(this.salt, that.salt);
    }
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper().add("password", password).add("salt", salt);
	}
	
	@Override
	public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
		return new BsonDocumentWrapper<SecuredUser>(this, codecRegistry.get(SecuredUser.class));
	}
}
