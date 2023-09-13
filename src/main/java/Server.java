import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService executorService;

    public Server(int maxPoll) {
        this.executorService = Executors.newFixedThreadPool(maxPoll);
    }

    public void listen(int port) {
        try (var serverSocket = new ServerSocket(port)) {
            while (true) {
                executorService.submit(new Client(serverSocket.accept()));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
