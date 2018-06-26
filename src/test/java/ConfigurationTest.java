import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import client.TestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static wiremock.org.hamcrest.CoreMatchers.is;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by mtumilowicz on 2018-06-24.
 */
public class ConfigurationTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(8999));

    @Test
    public void test() {
        TestClient testClient = new TestClient(wireMockRule.port());
        
        givenThat(get(urlEqualTo("/test")).willReturn(aResponse()
                .withHeader("Content-Type", "text/plain")
                .withBody("Hello world!")));

        assertThat(testClient.get("/test").statusCode(), is(200));
        assertThat(testClient.get("/test").content(), is("Hello world!"));

        verify(getRequestedFor(urlEqualTo("/test")));
    }
}
