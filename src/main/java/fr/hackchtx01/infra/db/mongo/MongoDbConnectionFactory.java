package fr.hackchtx01.infra.db.mongo;

import static fr.hackchtx01.infra.logging.Markers.CONFIG;
import static java.util.Objects.requireNonNull;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import fr.hackchtx01.authentication.repository.mongo.OAuth2AccessTokenMongoConverter;
import fr.hackchtx01.authentication.repository.mongo.OAuth2AuthorizationCodeMongoConverter;
import fr.hackchtx01.client.app.repository.mongo.ClientAppMongoConverter;
import fr.hackchtx01.infra.config.api.Config;
import fr.hackchtx01.infra.db.Dbs;
import fr.hackchtx01.site.repository.mongo.SiteMongoConverter;
import fr.hackchtx01.user.repository.mongo.SecuredUserMongoConverter;
import fr.hackchtx01.user.repository.mongo.UserMongoConverter;

@Singleton
public class MongoDbConnectionFactory {
	
	private final Config config;
	private final MongoClient mongoClient;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbConnectionFactory.class);
	
	@Inject
	public MongoDbConnectionFactory(Config config) {
		this.config = requireNonNull(config);
		mongoClient = new MongoClient(getServerAdress(), getCredentials(), getOptions());
	}
	
	public MongoDatabase getDB(Dbs db) {
		return mongoClient.getDatabase(db.getDbName());
	}
	
	public MongoCollection<Document> getCollection(Dbs db, String collectionName) {
		return getDB(db).getCollection(collectionName);
	}
	
	public <TDOC> MongoCollection<TDOC> getCollection(Dbs db, String collectionName, Class<TDOC> documentClass) {
		return getDB(db).getCollection(collectionName, documentClass);
	}
	
	private ServerAddress getServerAdress() {
		String host = config.getMongoHost();
		int port = config.getMongoPort();
		return new ServerAddress(host, port);
	}
	
	protected List<MongoCredential> getCredentials() {
		String user = config.getMongoUser();
		String password = config.getMongoPass();
		if (StringUtils.isBlank(user) || StringUtils.isBlank(password)) {
			LOGGER.warn(CONFIG.getMarker(), "Using MongoDb without credentials");
			return ImmutableList.<MongoCredential>of();
		}
		LOGGER.info(CONFIG.getMarker(), "Using MongoDb with user : " + user);
		return ImmutableList.<MongoCredential>of(MongoCredential.createCredential(user, Dbs.PSDP.getDbName(), password.toCharArray()));
	}
	
	private MongoClientOptions getOptions() {
		MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
		addCodecsToOptions(optionsBuilder);
		return optionsBuilder.build();
	}
	
	private void addCodecsToOptions(MongoClientOptions.Builder optionsBuilder) {
		CodecRegistry finalCodecRegistry = generateFinalCodecRegistry();
		
		optionsBuilder.codecRegistry(finalCodecRegistry);
	}
	
	protected CodecRegistry generateFinalCodecRegistry() {
		CodecRegistry customCodecRegistry = generateCustomCodecRegistry();
		return CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(), customCodecRegistry);
	}
	
	private CodecRegistry generateCustomCodecRegistry() {
		Codec<Document> defaultDocumentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
		UserMongoConverter userCodec = new UserMongoConverter(defaultDocumentCodec);
		SecuredUserMongoConverter securedUserCodec = new SecuredUserMongoConverter(defaultDocumentCodec);
		ClientAppMongoConverter clientAppCodec = new ClientAppMongoConverter(defaultDocumentCodec);
		OAuth2AuthorizationCodeMongoConverter authCodeCodec = new OAuth2AuthorizationCodeMongoConverter(defaultDocumentCodec);
		OAuth2AccessTokenMongoConverter accessTokenCodec = new OAuth2AccessTokenMongoConverter(defaultDocumentCodec);
		SiteMongoConverter siteCodec = new SiteMongoConverter(defaultDocumentCodec);
		
		return CodecRegistries.fromCodecs(userCodec, securedUserCodec, clientAppCodec, authCodeCodec, accessTokenCodec, siteCodec);
	}
}