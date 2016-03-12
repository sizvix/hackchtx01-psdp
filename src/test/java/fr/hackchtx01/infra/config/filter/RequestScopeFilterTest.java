package fr.hackchtx01.infra.config.filter;

import static fr.hackchtx01.infra.util.error.CommonErrorMessage.INVALID;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.hackchtx01.infra.config.filter.RequestScopeFilter;
import fr.hackchtx01.test.AbstractShiroTest;
import fr.hackchtx01.test.FakeEnumeration;
import fr.hackchtx01.user.User;
import fr.hackchtx01.user.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class RequestScopeFilterTest extends AbstractShiroTest {
	
	private static final UUID EXISTING_USER_ID = UUID.randomUUID();
	private static final User EXISTING_USER = User.Builder.createDefault().withId(EXISTING_USER_ID).build();
	@Mock
	UserRepository userRepo;
	@Mock
	HttpServletRequest request;
	@Mock
	HttpServletResponse response;
	@Mock
	FilterChain filterChain;
	
	@InjectMocks
	RequestScopeFilter testedFilter;
	
	@Before
	public void setUpMocks() {
		when(userRepo.getById(EXISTING_USER_ID)).thenReturn(EXISTING_USER);
		when(request.getHeaderNames()).thenReturn(new FakeEnumeration<String>());
	}
	
	@Test
	public void doFilter_should_add_user_info_in_request_attribute_with_a_valid_user_in_shiro_session() throws IOException, ServletException {
		//given
		Subject mockedSubject = mock(Subject.class);
		when(mockedSubject.getPrincipal()).thenReturn(EXISTING_USER);
		setSubject(mockedSubject);
		
		//when
		testedFilter.doFilter(request, response, filterChain);
		
		//then
		verify(request).setAttribute(any(), eq(EXISTING_USER));
	}
	
	@Test
	public void doFilter_should_add_default_user_info_in_request_attribute_with_an_unexisting_user_id() throws IOException, ServletException {
		//given
		String expectedMsg = INVALID.getDevReadableMessage(" found principal : " + null);
		Subject mockedSubject = mock(Subject.class);
		when(mockedSubject.getPrincipal()).thenReturn(null);
		setSubject(mockedSubject);
		
		//when
		testedFilter.doFilter(request, response, filterChain);
		
		//then
		verify(request).setAttribute(any(), eq(User.DEFAULT));
		verify(response).sendError(UNAUTHORIZED.getStatusCode(), expectedMsg);
	}
}