import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService executorService;
    private final Map<String, Map<String, Handler>> handlers = new HashMap<>();

    public Server(int maxPoll) {
        this.executorService = Executors.newFixedThreadPool(maxPoll);
    }

    public void addHandler(String method, String path, Handler handler) {
        if (!handlers.containsKey(method)) {
            var map = new HashMap<String, Handler>();
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
            while (true) {
                executorService.submit(getRunnable(serverSocket));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private Runnable getRunnable(ServerSocket serverSocket) {
        return () -> {
            try {
                var client = new Client(serverSocket.accept());
                Request request = client.getRequest();
                String[] line = request.getRequestLine().split(" ");
                //System.out.println(Thread.currentThread().getName() + "\n" + request);
                var handle = handlers.get(line[0]).getOrDefault(line[1], getErrorHandler());
                handle.hadle(request, client.getOut());
                client.getSocket().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
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
