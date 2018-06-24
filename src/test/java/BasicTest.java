import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static wiremock.org.hamcrest.CoreMatchers.is;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by mtumilowicz on 2018-06-23.
 */
public class BasicTest extends BaseTest {
    
    @Test
    public void test() {
        wireMockRule.stubFor(get(urlEqualTo("/test")).willReturn(aResponse()
                .withHeader("Content-Type", "text/plain")
                .withBody("Hello world!")));

        assertThat(testClient.get("/test").statusCode(), is(200));
        assertThat(testClient.get("/test").content(), is("Hello world!"));
        
        verify(getRequestedFor(urlEqualTo("/test")));
    }
}
