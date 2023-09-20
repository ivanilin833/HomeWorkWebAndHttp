import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private final String requestLine;
    private final String headers;
    private final String body;

    public Request(String[] request) {
        requestLine = request[0];
        headers = request[1];
        body = request[2];
    }

    public String getRequestLine() {
        return requestLine;
    }

    public String getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getQueryParam(String name) {
        List<NameValuePair> params = URLEncodedUtils.parse(URI.create(requestLine.split(" ")[1]), StandardCharsets.UTF_8);
        return params.stream().filter(p -> p.getName().equals(name)).map(NameValuePair::getValue).findFirst().get();
    }

    public List<String> getQueryParams() {
        List<NameValuePair> params = URLEncodedUtils.parse(URI.create(requestLine.split(" ")[1]), StandardCharsets.UTF_8);
        return params.stream().map(p -> p.getName() + "=" + p.getValue()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestLine='" + requestLine + '\'' +
                ", headers='" + headers + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
