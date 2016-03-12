/**
 * 
 */
package fr.hackchtx01.infra.db.mongo;

import static com.google.common.base.Preconditions.checkArgument;
import static fr.hackchtx01.infra.util.error.RepositoryErrorMessage.MONGO_DOCUMENT_WITHOUT_ID;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.UuidCodec;

import fr.hackchtx01.infra.db.WithId;

/**
 * Converter between mongo document and POJO
 * Usable as a Mongo Codec to convert POJO into BSON
 * @param <POJO> Type of the POJO
 * @author yoan
 */
public abstract class MongoDocumentConverter<POJO extends WithId> implements CollectibleCodec<POJO> {
	public static final String FIELD_ID = "_id";
	
	protected Codec<Document> documentCodec;
	
	protected MongoDocumentConverter() {
		this.documentCodec = new DocumentCodec();
	}
	
	protected MongoDocumentConverter(Codec<Document> codec) {
		this.documentCodec = codec;
	}
	
	@Override
	public void encode(BsonWriter writer, POJO obj, EncoderContext encoderContext) {
		Document document = toDocument(obj);
		documentCodec.encode(writer, document, encoderContext);
	}

	@Override
	public POJO decode(BsonReader reader, DecoderContext decoderContext) {
		Document document = documentCodec.decode(reader, decoderContext);
		return fromDocument(document);
	}
	
	@Override
	public boolean documentHasId(POJO obj) {
		return obj.getId() != null;
	}
	
	@Override
	public BsonValue getDocumentId(POJO obj) {
		checkArgument(documentHasId(obj), MONGO_DOCUMENT_WITHOUT_ID.getDevReadableMessage());
		return getIdAsBsonValue(obj);
	}
	
	private BsonValue getIdAsBsonValue(WithId object) {
		BsonDocument idHoldingDocument = new BsonDocument();
		UuidCodec uuidCodec = new UuidCodec();
        BsonWriter writer = new BsonDocumentWriter(idHoldingDocument);
        writer.writeStartDocument();
        writer.writeName(FIELD_ID);
        uuidCodec.encode(writer, object.getId(), EncoderContext.builder().build());
        writer.writeEndDocument();
        return idHoldingDocument.get(FIELD_ID);
	}
	
	/**
	 * Convert a mongo document to a POJO
	 * @param doc : mongo document
	 * @return an instance of the POJO
	 */
	public abstract POJO fromDocument(Document doc);
	
	/**
	 * Converte a POJO to a mongo document
	 * @param obj : POJO
	 * @return a mongo document
	 */
	public abstract Document toDocument(POJO obj);
}
