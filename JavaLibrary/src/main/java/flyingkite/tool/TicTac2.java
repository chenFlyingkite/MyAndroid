package flyingkite.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

import flyingkite.logging.L;

public class TicTac2 {
    // https://en.wikipedia.org/wiki/ISO_8601
    private static final SimpleDateFormat formatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    private final Stack<Long> tictac = new Stack<>();

    protected boolean log = true;
    protected boolean enable = true;

    public long tic() {
        if (!enable) return -1;
        long tic = System.currentTimeMillis();
        tictac.push(tic);
        return tic;
    }

    /**
     * Evaluate time diff and return the tac time
     * @return time diff = tac - tic, -1 if no tic
     */
    public long tacL() {
        long tac = System.currentTimeMillis();
        if (tictac.size() < 1) {
            return -1;
        }

        long tic = tictac.pop();
        return tac - tic;
    }

    public long tac(String format, Object... params) {
        return tac(String.format(format, params));
    }

    public long tac(String msg) {
        if (!enable) return -1;

        long tac = System.currentTimeMillis();
        if (tictac.empty()) {
            logError(tac, msg);
            return -1;
        }
        long tic = tictac.pop();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tictac.size(); i++) {
            sb.append(" ");
        }
        sb.append("[").append(tac - tic).append("] : ").append(msg);
        logTac(sb.toString());
        return tac - tic;
    }

    public void enable(boolean enabled) {
        enable = enabled;
    }

    public void reset() {
        tictac.clear();
    }

    protected void logError(long tac, String msg) {
        L.log("X_X Omitted. tic = N/A, tac = %s : %s", getTime(tac), msg);
    }

    protected void logTac(String msg) {
        if (log) {
            L.log(msg);
        }
    }

    protected String getTime(long time) {
        return formatISO8601.format(new Date(time));
    }

    public void setLog(boolean writeLog) {
        log = writeLog;
    }
}
