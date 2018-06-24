import org.junit.Test;

import static wiremock.org.hamcrest.CoreMatchers.is;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by mtumilowicz on 2018-06-24.
 */
public class MappingTest extends BaseTest {
    
    @Test
    public void test() {
        assertThat(testClient.get("/testmapping").statusCode(), is(200));
        assertThat(testClient.get("/testmapping").content(), is("default test mapping"));
    }
}
