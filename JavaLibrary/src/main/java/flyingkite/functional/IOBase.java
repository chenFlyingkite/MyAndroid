package flyingkite.functional;

import java.io.IOException;

@FunctionalInterface
public interface IOBase {
    void run() throws IOException;
}
