import client.TestClient;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;

/**
 * Created by mtumilowicz on 2018-06-24.
 */
public class BaseTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    static TestClient testClient = new TestClient();
}
