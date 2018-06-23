import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static wiremock.org.hamcrest.CoreMatchers.is;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by mtumilowicz on 2018-06-23.
 */
public class ConfigurationTest {
    
    @Rule
    public WireMockRule wireMockRule = new WireMockRule();
    
    public static TestClient testClient = new TestClient();
    
    @Test
    public void test() {
        wireMockRule.stubFor(get(urlEqualTo("/test")).willReturn(aResponse()
                .withHeader("Content-Type", "text/plain")
                .withBody("Hello world!")));

        assertThat(testClient.get("/test").statusCode(), is(200));
        
        verify(getRequestedFor(urlEqualTo("/test")));
    }
}
