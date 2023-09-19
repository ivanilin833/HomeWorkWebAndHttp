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

    @Override
    public String toString() {
        return "Request{" +
                "requestLine='" + requestLine + '\'' +
                ", headers='" + headers + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
