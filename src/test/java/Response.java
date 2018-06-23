import wiremock.com.google.common.collect.ImmutableListMultimap;
import wiremock.com.google.common.collect.Multimap;
import wiremock.org.apache.http.Header;
import wiremock.org.apache.http.HttpResponse;

import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.common.HttpClientUtils.getEntityAsByteArrayAndCloseStream;
import static wiremock.com.google.common.collect.Iterables.getFirst;
import static wiremock.org.apache.http.Consts.UTF_8;

/**
 * Created by mtumilowicz on 2018-06-23.
 */
public class Response {

    private final HttpResponse httpResponse;
    private final byte[] content;

    public Response(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
        content = getEntityAsByteArrayAndCloseStream(httpResponse);
    }

    public int statusCode() {
        return httpResponse.getStatusLine().getStatusCode();
    }

    public String content() {
        if(content==null) {
            return null;
        }
        return new String(content, Charset.forName(UTF_8.name()));
    }

    public byte[] binaryContent() {
        return content;
    }

    public String firstHeader(String key) {
        return getFirst(headers().get(key), null);
    }

    public Multimap<String, String> headers() {
        ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();

        for (Header header: httpResponse.getAllHeaders()) {
            builder.put(header.getName(), header.getValue());
        }

        return builder.build();
    }

    public String statusMessage() {
        return httpResponse.getStatusLine().getReasonPhrase();
    }
}