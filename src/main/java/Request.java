import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class Request {
    private final String requestLine;
    private final String headers;
    private final String body;
    private final List<NameValuePair> queryParams;

    public Request(String[] request) {
        requestLine = request[0];
        headers = request[1];
        body = request[2];
        queryParams = URLEncodedUtils.parse(URI.create(requestLine.split(" ")[1]), StandardCharsets.UTF_8);
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

    public Optional<String> getQueryParam(String name) {
        return queryParams.stream().filter(p -> p.getName().equals(name)).map(NameValuePair::getValue).findFirst();
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
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
