import client.Response;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertTrue;
import static wiremock.org.hamcrest.CoreMatchers.is;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by mtumilowicz on 2018-06-24.
 */
public class StubTest extends BaseTest {
    
    @Test
    public void stubFluentApiBody() {
        stubFor(get("/test")
                .willReturn(ok("test")));

        Response response = testClient.get("/test");

        assertThat(response.statusCode(), is(200));
        assertThat(response.content(), is("test"));
    }

    @Test
    public void stubResponseHeaders() {
        givenThat(get(urlEqualTo("/test"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Cache-Control", "no-cache")));

        Response response = testClient.get("/test");
        
        assertTrue(response.headers().get("Content-Type").contains("application/json"));
        assertTrue(response.headers().get("Cache-Control").contains("no-cache"));
    }
}
