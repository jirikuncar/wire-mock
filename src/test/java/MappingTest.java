import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import client.TestClient;

import static wiremock.org.hamcrest.CoreMatchers.is;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by mtumilowicz on 2018-06-24.
 */
public class MappingTest extends BaseTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();
    
    private static TestClient testClient = new TestClient();

    @Test
    public void test() {
        assertThat(testClient.get("/testmapping").statusCode(), is(200));
        assertThat(testClient.get("/testmapping").content(), is("default test mapping"));
    }
}
