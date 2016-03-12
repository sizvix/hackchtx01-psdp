package fr.hackchtx01.authentication.realm;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.hackchtx01.authentication.realm.UserRealm;
import fr.hackchtx01.test.TestHelper;
import fr.hackchtx01.user.SecuredUser;
import fr.hackchtx01.user.User;
import fr.hackchtx01.user.repository.SecuredUserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserRealmTest {

	@Mock
	CacheManager mockedCacheManager;
	@Mock
	HashedCredentialsMatcher mockedCredentialsMatcher;
	@Mock
	SecuredUserRepository mockedUserRepository;
	@InjectMocks
	UserRealm testedRealm;
	
	@Test
	public void doGetAuthenticationInfo_should_return_infos_with_existing_user() {
		//given
		SecuredUser existingUser = TestHelper.generateRandomSecuredUser();
		User expectedPrincipal = User.Builder.createFrom(existingUser).build();
		when(mockedUserRepository.getByEmail(existingUser.getEmail())).thenReturn(existingUser);
		UsernamePasswordToken userToken = new UsernamePasswordToken(existingUser.getEmail(), "password");
		
		//when
		AuthenticationInfo result = testedRealm.doGetAuthenticationInfo(userToken);
		
		//then
		assertThat(result).isNotNull();
		assertThat(result.getPrincipals().getPrimaryPrincipal()).isEqualTo(expectedPrincipal);
	}
	
	@Test
	public void doGetAuthenticationInfo_should_return_null_with_not_existing_user() {
		//given
		UsernamePasswordToken userToken = new UsernamePasswordToken("not_existing@mail.com", "password");
		
		//when
		AuthenticationInfo result = testedRealm.doGetAuthenticationInfo(userToken);
		
		//then
		assertThat(result).isNull();
	}
}