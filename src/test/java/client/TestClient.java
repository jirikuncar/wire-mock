package client;

import com.github.tomakehurst.wiremock.http.GenericHttpUriRequest;
import wiremock.org.apache.http.HttpEntity;
import wiremock.org.apache.http.HttpHost;
import wiremock.org.apache.http.HttpResponse;
import wiremock.org.apache.http.auth.AuthScope;
import wiremock.org.apache.http.auth.UsernamePasswordCredentials;
import wiremock.org.apache.http.client.AuthCache;
import wiremock.org.apache.http.client.CredentialsProvider;
import wiremock.org.apache.http.client.HttpClient;
import wiremock.org.apache.http.client.methods.*;
import wiremock.org.apache.http.client.protocol.HttpClientContext;
import wiremock.org.apache.http.entity.ContentType;
import wiremock.org.apache.http.entity.StringEntity;
import wiremock.org.apache.http.impl.auth.BasicScheme;
import wiremock.org.apache.http.impl.client.BasicAuthCache;
import wiremock.org.apache.http.impl.client.BasicCredentialsProvider;
import wiremock.org.apache.http.impl.client.HttpClientBuilder;
import wiremock.org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;

import static com.github.tomakehurst.wiremock.common.Exceptions.throwUnchecked;
import static com.github.tomakehurst.wiremock.common.TextType.JSON;
import static wiremock.org.apache.http.entity.ContentType.APPLICATION_JSON;
import static wiremock.org.apache.http.entity.ContentType.APPLICATION_XML;

/**
 * Created by mtumilowicz on 2018-06-23.
 */
public class TestClient {

    private static final String LOCAL_WIREMOCK_ROOT = "http://%s:%d%s";
    private static final String LOCAL_WIREMOCK_NEW_RESPONSE_URL = "http://%s:%d/__admin/mappings/new";
    private static final String LOCAL_WIREMOCK_EDIT_RESPONSE_URL = "http://%s:%d/__admin/mappings/edit";
    private static final String LOCAL_WIREMOCK_RESET_DEFAULT_MAPPINS_URL = "http://%s:%d/__admin/mappings/reset";
    private static final String LOCAL_WIREMOCK_SNAPSHOT_PATH = "/__admin/recordings/snapshot";

    private int port;
    private String address;

    public TestClient(int port, String address) {
        this.port = port;
        this.address = address;
    }

    public TestClient(int port) {
        this(port, "localhost");
    }

    public TestClient() {
        this(8080);
    }

    private String mockServiceUrlFor(String path) {
        return String.format(LOCAL_WIREMOCK_ROOT, address, port, path);
    }

    private String newMappingUrl() {
        return String.format(LOCAL_WIREMOCK_NEW_RESPONSE_URL, address, port);
    }

    private String editMappingUrl() {
        return String.format(LOCAL_WIREMOCK_EDIT_RESPONSE_URL, address, port);
    }

    private String resetDefaultMappingsUrl() {
        return String.format(LOCAL_WIREMOCK_RESET_DEFAULT_MAPPINS_URL, address, port);
    }

    public Response get(String url, HttpHeader... headers) {
        String actualUrl = URI.create(url).isAbsolute() ? url : mockServiceUrlFor(url);
        HttpUriRequest httpRequest = new HttpGet(actualUrl);
        return executeMethodAndConvertExceptions(httpRequest, headers);
    }

    public Response put(String url, HttpHeader... headers) {
        HttpUriRequest httpRequest = new HttpPut(mockServiceUrlFor(url));
        return executeMethodAndConvertExceptions(httpRequest, headers);
    }

    public Response putWithBody(String url, String body, String contentType, HttpHeader... headers) {
        HttpPut httpPut = new HttpPut(mockServiceUrlFor(url));
        return requestWithBody(httpPut, body, contentType, headers);
    }

    private Response requestWithBody(
            HttpEntityEnclosingRequestBase request, String body, String contentType, HttpHeader... headers) {
        request.setEntity(new StringEntity(body, ContentType.create(contentType, "utf-8")));
        return executeMethodAndConvertExceptions(request, headers);
    }

    public Response postWithBody(String url, String body, String bodyMimeType, String bodyEncoding) {
        return post(url, new StringEntity(body, ContentType.create(bodyMimeType, bodyEncoding)));
    }

    public Response post(String url, HttpEntity entity, HttpHeader... headers) {
        HttpPost httpPost = new HttpPost(mockServiceUrlFor(url));
        httpPost.setEntity(entity);
        return executeMethodAndConvertExceptions(httpPost, headers);
    }

    public Response postJson(String url, String body, HttpHeader... headers) {
        HttpPost httpPost = new HttpPost(mockServiceUrlFor(url));
        httpPost.setEntity(new StringEntity(body, APPLICATION_JSON));
        return executeMethodAndConvertExceptions(httpPost, headers);
    }

    public Response postXml(String url, String body, HttpHeader... headers) {
        HttpPost httpPost = new HttpPost(mockServiceUrlFor(url));
        httpPost.setEntity(new StringEntity(body, APPLICATION_XML));
        return executeMethodAndConvertExceptions(httpPost, headers);
    }

    public Response delete(String url) {
        HttpDelete httpDelete = new HttpDelete(mockServiceUrlFor(url));
        return executeMethodAndConvertExceptions(httpDelete);
    }
    
    private int postJsonAndReturnStatus(String url, String json) {
        return postJsonAndReturnStatus(url, json, "utf-8");
    }

    private int postJsonAndReturnStatus(String url, String json, String charset) {
        HttpPost post = new HttpPost(url);
        try {
            if (json != null) {
                post.setEntity(new StringEntity(json, ContentType.create(JSON.toString(), charset)));
            }
            HttpResponse httpResponse = httpClient().execute(post);
            return httpResponse.getStatusLine().getStatusCode();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int postEmptyBodyAndReturnStatus(String url) {
        return postJsonAndReturnStatus(url, null);
    }

    private Response executeMethodAndConvertExceptions(HttpUriRequest httpRequest, HttpHeader... headers) {
        try {
            for (HttpHeader header : headers) {
                httpRequest.addHeader(header.getName(), header.getValue());
            }
            HttpResponse httpResponse = httpClient().execute(httpRequest);
            return new Response(httpResponse);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public Response getWithPreemptiveCredentials(String url, int port, String username, String password) {
        HttpHost target = new HttpHost("localhost", port);
        HttpClient httpClient = httpClientWithPreemptiveAuth(target, username, password);

        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(target, basicAuth);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);

        try {
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpClient.execute(target, httpget, localContext);
            return new Response(response);
        } catch (IOException e) {
            return throwUnchecked(e, Response.class);
        }
    }

    public Response request(final String methodName, String url, HttpHeader... headers) {
        HttpUriRequest httpRequest = new GenericHttpUriRequest(methodName, mockServiceUrlFor(url));
        return executeMethodAndConvertExceptions(httpRequest, headers);
    }

    private static HttpClient httpClient() {
        return HttpClientBuilder.create()
                .disableAuthCaching()
                .disableAutomaticRetries()
                .disableCookieManagement()
                .disableRedirectHandling()
                .disableContentCompression()
                .build();
    }

    private static HttpClient httpClientWithPreemptiveAuth(HttpHost target, String username, String password) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(target),
                new UsernamePasswordCredentials(username, password));

        return HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
    }
}
