import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService executorService;
    private final Map<String, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();

    public Server(int maxPoll) {
        this.executorService = Executors.newFixedThreadPool(maxPoll);
    }

    public void addHandler(String method, String path, Handler handler) {
        if (!handlers.containsKey(method)) {
            var map = new ConcurrentHashMap<String, Handler>();
            map.put(path, handler);
            handlers.put(method, map);
        } else {
            var map = handlers.get(method);
            map.put(path, handler);
            handlers.put(method, map);
        }
    }

    public void listen(int port) {
        try (var serverSocket = new ServerSocket(port)) {
            while (!serverSocket.isClosed()) {
                final var socket = serverSocket.accept();
                executorService.submit(() -> {
                    try {
                        connectingClient(socket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void connectingClient(Socket socket) throws IOException {
        var client = new Client(socket);
        Request request = client.getRequest();
        String[] line = request.getRequestLine().split(" ");
        var handle = handlers.get(line[0]).entrySet().stream().filter((k) -> line[1].startsWith(k.getKey()))
                .map(Map.Entry::getValue).findFirst();
        if (handle.isPresent()) {
            handle.get().hadle(request, client.getOut());
        } else getErrorHandler().hadle(request, client.getOut());
        client.getSocket().close();
    }

    private Handler getErrorHandler() {
        return (request, bos) -> {
            bos.write((
                    """
                            HTTP/1.1 404 Not Found\r
                            Content-Length: 0\r
                            Connection: close\r
                            \r
                            """
            ).getBytes());
            bos.flush();
        };
    }
}
