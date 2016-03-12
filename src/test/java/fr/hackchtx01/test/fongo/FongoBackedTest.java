/**
 * 
 */
package fr.hackchtx01.test.fongo;

import org.junit.After;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.fakemongo.Fongo;
import com.mongodb.DB;

import fr.hackchtx01.infra.db.mongo.MongoDbConnectionFactory;

/**
 * Base class for unit tests backed by FongoDb
 * @author yoan
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class FongoBackedTest {
	private static final Fongo FONGO = new Fongo("unit test server");
	private final FongoDbConnectionFactory fongoConnectionFactory = new FongoDbConnectionFactory(FONGO);
	
	@Spy
	protected MongoDbConnectionFactory connectionFactory = getFongoDbConnectionFactory();
	
	@After
    public void afterTest() {
        for (DB db : FONGO.getUsedDatabases()) {
            db.dropDatabase();
        }
    }
	
	protected FongoDbConnectionFactory getFongoDbConnectionFactory() {
		return fongoConnectionFactory;
	}
}
