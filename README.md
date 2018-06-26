# wire-mock
The main goal of this project is to present basic features of `WireMock`.
_Reference_: [WireMock documentation](http://wiremock.org/docs)
_Reference_:

# introduction
To start and stop WireMock server per-test case:
```
@Rule
public WireMockRule wireMockRule = new WireMockRule(); // default port: 8080
```
A core feature of WireMock is the ability to return canned HTTP responses 
for requests matching criteria:
```
stubFor(get(urlEqualTo("/test")).willReturn(aResponse()
        .withHeader("Content-Type", "text/plain")
        .withBody("Hello world!")));

Response response = testClient.get("/test");

assertThat(response.statusCode(), is(200));
assertThat(response.content(), is("Hello world!"));
```

# configuration
We could configure `WireMockRule` with a configuration builder as the 
parameter to its constructor:
```
WireMockRule wm = new WireMockRule(options().port(2345));
```

Useful options:
* .httpsPort(8001)
* .dynamicPort()
* .basicAdminAuthenticator("username", "password")
* and many more...

# stubbing
Below stubs are equivalent:
```
givenThat(get(urlEqualTo("/test"))
        .willReturn(aResponse()
        .withHeader("Content-Type", "text/plain")
        .withBody("Hello world!")));
```
```
stubFor(get(urlEqualTo("/test"))
        .willReturn(aResponse()
        .withHeader("Content-Type", "text/plain")
        .withBody("Hello world!")));
```
but `givenThat` is slightly more in `BDD` style.

We could use also fluent API for building responses:
```
stubFor(get("/test")
        .willReturn(ok("test")));
```

Fluent API has many more premade methods:
* `okJson("{ \"message\": \"Hello\" }")`
* `unauthorized()`
* `badRequest()`
* and many more...

[More examples of fluent API](https://github.com/tomakehurst/wiremock/blob/master/src/test/java/ignored/Examples.java#374)

# verifying
The `WireMock` server records all requests it receives in memory 
(at least until it is reset). This makes it possible to verify that a 
request matching a specific pattern was received, and also to fetch the 
requests’ details.

Verifying and querying requests relies on the request journal, which is an 
in-memory log of received requests.

* to verify that a request matching some criteria was received
  ```
  verify(getRequestedFor(urlEqualTo("/test")));
  ```
* precise number of requests matching the criteria:
  ```
  verify(2, getRequestedFor(urlEqualTo("/test")));
  ```
  ```
  verify(lessThan(1), getRequestedFor(urlEqualTo("/test")));
  ```
  
# mapping
If you put a file .json in mappings directory:
```
{
  "request": {
    "method": "GET",
    "urlPattern": "/testmapping"
  },
  "response": {
    "status": 200,
    "body": "default test mapping",
    "headers": {
      "Content-Type": "text/plain"
    }
  }
}
```
then you could simply reference it from the code with no stubbing:
```
assertThat(testClient.get("/testmapping").statusCode(), is(200));
assertThat(testClient.get("/testmapping").content(), is("default test mapping"));
```
Stubbing and `.json` in mapping directory are equivalent approaches.

# faults
One of the main reasons it’s beneficial to use web service fakes when testing 
is to inject faulty behaviour that might be difficult to get the real service 
to produce on demand.
* delays
    ```
    stubFor(get(urlEqualTo("/delayed"))
            .willReturn(
            ok().withFixedDelay(2000)));
    ```
* bad responses:
  ```
  givenThat(get(urlEqualTo("/fault"))
          .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
  ```
  * EMPTY_RESPONSE: Return a completely empty response.
  * MALFORMED_RESPONSE_CHUNK: Send an OK status header, 
  then garbage, then close the connection.
  * RANDOM_DATA_THEN_CLOSE: Send garbage then close the connection.