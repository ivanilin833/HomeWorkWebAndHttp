import java.io.BufferedOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface Handler {
    void hadle(Request request, BufferedOutputStream bos) throws IOException;
}
