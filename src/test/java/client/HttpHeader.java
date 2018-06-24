package client;

/**
 * Created by mtumilowicz on 2018-06-23.
 */
public class HttpHeader {

    private String name;
    private String value;

    public static HttpHeader withHeader(String name, String value) {
        return new HttpHeader(name, value);
    }

    public HttpHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }


}
