package fr.hackchtx01.infra.config.jackson;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.hackchtx01.infra.config.jackson.JacksonConfigProvider;

public class JacksonConfigProviderTest {
	
	@Test
	public void getContext_should_return_aninstantiated_objectMapper() {
		//given
		JacksonConfigProvider provider = new JacksonConfigProvider();
		
		//when
		ObjectMapper objectMapper = provider.getContext(this.getClass());
		
		//then
		assertThat(objectMapper).isNotNull();
	}
}
