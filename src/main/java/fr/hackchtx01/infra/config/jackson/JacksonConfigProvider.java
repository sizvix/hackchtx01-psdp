/**
 * 
 */
package fr.hackchtx01.infra.config.jackson;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

/**
 * Jackson configuration provider
 * @author yoan
 */
@Provider
@Produces("application/json")
public class JacksonConfigProvider implements ContextResolver<ObjectMapper> {
	/** ObjectMapper shared by the whole application */
    private final ObjectMapper objectMapper;

    public JacksonConfigProvider() {
        objectMapper = new ObjectMapper();
        //serializer ignore null fields
        objectMapper.getSerializationConfig().withSerializationInclusion(Include.NON_NULL);
        //deserializer must no fail on unknown object
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configureDateFormat();
    }

    private void configureDateFormat() {
    	objectMapper.registerModule(new JSR310Module());
    	objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    	// Currently it is not possible to configure the LocaleDate serializationformat with JSR310Module
    }
    
    @Override
    public ObjectMapper getContext(Class<?> arg0) {
        return objectMapper;
    }
 }
