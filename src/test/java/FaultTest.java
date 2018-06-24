import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by mtumilowicz on 2018-06-24.
 */
public class FaultTest extends BaseTest {
    
    @Test
    public void delay() {
        stubFor(get(urlEqualTo("/delayed")).willReturn(
                ok().withFixedDelay(2000)));
        
        testClient.get("/delayed");
    }

    @Test(expected = RuntimeException.class)
    public void fault() {
        stubFor(get(urlEqualTo("/fault"))
                .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        testClient.get("/fault");
    }
}
