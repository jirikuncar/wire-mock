import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by mtumilowicz on 2018-06-24.
 */
public class VerifyTest extends BaseTest {

    @Test
    public void verifyAtLeastOnce() {
        stubFor(get(urlEqualTo("/test")).willReturn(aResponse()
                .withHeader("Content-Type", "text/plain")
                .withBody("Hello world!")));

        testClient.get("/test");
        testClient.get("/test");

        verify(getRequestedFor(urlEqualTo("/test")));
    }

    @Test
    public void verifyLessThanOnce() {
        stubFor(get(urlEqualTo("/test")).willReturn(aResponse()
                .withHeader("Content-Type", "text/plain")
                .withBody("Hello world!")));

        verify(lessThan(1), getRequestedFor(urlEqualTo("/test")));
    }
    
    @Test
    public void verifyCount() {
        stubFor(get(urlEqualTo("/test")).willReturn(aResponse()
                .withHeader("Content-Type", "text/plain")
                .withBody("Hello world!")));
        
        testClient.get("/test");
        testClient.get("/test");

        verify(2, getRequestedFor(urlEqualTo("/test")));
    }
}
