import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(64);

        server.addHandler("GET", "/forms.html", (request, bos) -> {
            Path filePath;
            if (!request.getRequestLine().split(" ")[1].contains("?")) {
                filePath = Path.of(".", "public", request.getRequestLine().split(" ")[1]);
            } else {
                filePath = Path.of(".", "public", request.getRequestLine().split(" ")[1]
                        .substring(0, request.getRequestLine().split(" ")[1].indexOf("?")));
            }
            var mimeType = Files.probeContentType(filePath);
            var length = Files.size(filePath);
            bos.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, bos);
            bos.flush();
        });

        server.addHandler("GET", "/classic.html", ((request, bos) -> {
            var filePath = Path.of(".", "public", request.getRequestLine().split(" ")[1]);
            var mimeType = Files.probeContentType(filePath);
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            bos.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            bos.write(content);
            bos.flush();
        }));
        server.listen(9999);
    }
}
