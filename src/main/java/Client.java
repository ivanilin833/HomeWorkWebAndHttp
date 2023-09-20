import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    final BufferedOutputStream out;
    final private Socket socket;
    final private BufferedReader in;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedOutputStream(socket.getOutputStream());
    }

    public Request getRequest() throws IOException {
        StringBuilder sb = new StringBuilder();
        String[] req = new String[3];
        req[0] = in.readLine();
        int count = 1;
        for (String line = in.readLine(); !line.equals(""); line = in.readLine()) {
            if (!line.equals("/r/n")) {
                sb.append("\n").append(line);
            } else {
                if (in.readLine().equals("/r/n")) {
                    req[count] = sb.toString();
                    count++;
                }
            }
        }
        req[count] = sb.toString();
        return new Request(req);
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedOutputStream getOut() {
        return out;
    }
}
