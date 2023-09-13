import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Client implements Runnable {
    final static List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png",
            "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html",
            "/events.html", "/events.js");
    final BufferedOutputStream out;
    final private Socket socket;
    final private BufferedReader in;
    private String path;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedOutputStream(socket.getOutputStream());
        getPath();
    }

    private void getPath() throws IOException {
        var requestLine = in.readLine();
        var parts = requestLine.split(" ");
        if (parts.length != 3) {
            socket.close();
        }
        path = parts[1];
    }

    private boolean isValidPath() throws IOException {
        if (!validPaths.contains(path)) {
            out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
            socket.close();
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        try {
            if (!isValidPath()) {
                return;
            }

            var filePath = Path.of(".", "public", path);
            var mimeType = Files.probeContentType(filePath);
            var length = Files.size(filePath);

            if (path.equals("/classic.html")) {
                classicReq(filePath, mimeType);
                socket.close();
                return;
            }
            simpleReq(filePath, mimeType, length);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void simpleReq(Path filePath, String mimeType, long length) throws IOException {
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }

    private void classicReq(Path filePath, String mimeType) throws IOException {
        final var template = Files.readString(filePath);
        final var content = template.replace(
                "{time}",
                LocalDateTime.now().toString()
        ).getBytes();
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + content.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.write(content);
        out.flush();
    }
}
