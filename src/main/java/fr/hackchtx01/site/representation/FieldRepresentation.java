package fr.hackchtx01.site.representation;

import static fr.hackchtx01.infra.rest.error.Level.ERROR;
import static fr.hackchtx01.infra.util.error.CommonErrorCode.API_RESPONSE;
import static fr.hackchtx01.infra.util.error.CommonErrorMessage.INVALID;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import fr.hackchtx01.infra.rest.error.WebApiException;
import fr.hackchtx01.site.Field;

@XmlRootElement(name = "field")
public class FieldRepresentation {
	private String type;
	private String name;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldRepresentation.class);
	
	public FieldRepresentation() {
		super();
	}
	
	/** Test Purpose only */
	@Deprecated 
	public FieldRepresentation(String type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public FieldRepresentation(Field siteUrl) {
		super();
		requireNonNull(siteUrl);
		this.type = siteUrl.getType();
		this.name = siteUrl.getName();
	}
	
	public static Field toField(FieldRepresentation representation) {
		requireNonNull(representation, "Unable to create Field from null FieldRepresentation");
		
		Field.Builder siteUrlBuilder = Field.Builder.createDefault()
						   .withType(representation.type)
						   .withName(representation.name);
		
		Field site;
		try {
			site = siteUrlBuilder.build();
		} catch (NullPointerException | IllegalArgumentException e) {
			String message = INVALID.getDevReadableMessage("site url field name") + " : " + e.getMessage();
			LOGGER.error(message, e);
			throw new WebApiException(BAD_REQUEST, ERROR, API_RESPONSE, message, e);
		}
		return site;
	}

	@XmlElement(name = "type")
	public String getType() {
		return type;
	}

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static  List<FieldRepresentation> extractFieldsRepresentations(ImmutableList<Field> fields) {
		List<FieldRepresentation> representations = new ArrayList<>();
		fields.forEach(name -> representations.add(new FieldRepresentation(name)));
		return representations;
	}
	
	public static List<Field> toFields(List<FieldRepresentation> representations) {
		requireNonNull(representations, "Unable to create Fields from null FieldRepresentations");
		
		List<Field> fields = new ArrayList<>();
		representations.forEach(representation -> fields.add(toField(representation)));
		
		return fields;
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
        FieldRepresentation that = (FieldRepresentation) obj;
        return Objects.equals(this.type, that.type)
                && Objects.equals(this.name, that.name);
    }
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("type", type)
											   .add("name", name)
											   .toString();
	}
}

