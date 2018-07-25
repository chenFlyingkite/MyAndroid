package flyingkite.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

import flyingkite.log.Formattable;
import flyingkite.log.L;

/**
 * The class performing the same intention with {@link TicTac}.
 * Unlike {@link TicTac} provides static method and uses global time stack,
 * {@link TicTac2} provides for creating instance and use its own one time stack. <br/>
 * {@link TicTac2} is specially better usage for tracking performance in different AsyncTasks,
 * by each task create a new object and call its {@link TicTac2#tic()} and {@link TicTac2#tac(String)} in task.
 *
 * <p>Here is an example of usage:</p>
 * <pre class="prettyprint">
 * public class Main {
 *     public static void main(String[] args) {
 *         // Let's start the tic-tac
 *         TicTac.tic();
 *             f();
 *         TicTac.tac("f is done");
 *         TicTac.tic();
 *             g();
 *             TicTac.tic();
 *                 g1();
 *             TicTac.tac("g1 is done");
 *             TicTac.tic();
 *                 g2();
 *             TicTac.tac("g2 is done");
 *         TicTac.tac("g + g1 + g2 is done");
 *         // Now is ended
 *     }
 *
 *     private void f() {
 *         // your method body
 *     }
 *     private void g() {
 *          // your method body
 *     }
 *     private void g1() {
 *          // your method body
 *     }
 *     private void g2() {
 *          // your method body
 *     }
 * }
 * </pre>
 */
public class TicTac2 implements Formattable {
    // https://en.wikipedia.org/wiki/ISO_8601
    protected static final SimpleDateFormat formatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    protected final Stack<Long> tictac = new Stack<>();

    protected boolean log = true;
    protected boolean enable = true;

    /**
     * Push time of tic
     * @return tic Time of tic, -1 if disabled
     */
    public long tic() {
        if (!enable) return -1;

        long tic = System.currentTimeMillis();
        tictac.push(tic);
        return tic;
    }

    /**
     * Evaluate time diff and return the tac time
     * @return time diff = tac - tic, -1 if no tic or disabled
     */
    public long tacL() {
        if (!enable) return -1;

        long tac = System.currentTimeMillis();
        if (tictac.size() < 1) {
            return -1;
        }

        long tic = tictac.pop();
        return tac - tic;
    }

    /**
     * Print formatted
     * @see #tac(String)
     */
    public long tac(String format, Object... params) {
        return tac(_fmt(format, params));
    }

    /**
     * Evaluate time diff, Print logs and return the tac time
     * @return time diff = tac - tic, -1 if no tic or disabled
     */
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

    /**
     * Print log or not
     * @see #logTac(String)
     * @see #logError(long, String)
     */
    public void setLog(boolean writeLog) {
        log = writeLog;
    }

    /**
     * Enable tictac or not, if disabled all the tic() tac() method returns -1 directly
     * @see #tic()
     * @see #tac(String)
     * @see #tac(String, Object...)
     * @see #tacL()
     */
    public void enable(boolean enabled) {
        enable = enabled;
    }

    /**
     * Clear all the pushed tics
     */
    public void reset() {
        tictac.clear();
    }

    /**
     * Print log when {@link #tac(String)} is called with no tic
     */
    protected void logError(long tac, String msg) {
        if (log) {
            L.log("X_X Omitted. tic = N/A, tac = %s : %s", getTime(tac), msg);
        }
    }

    /**
     * Print log when {@link #tac(String)} is called with valid tic
     */
    protected void logTac(String msg) {
        if (log) {
            L.log(msg);
        }
    }

    /**
     * Format time to be ISO8601 format, yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     * Like 2018-07-24T12:34:56.789Z
     */
    protected String getTime(long time) {
        return formatISO8601.format(new Date(time));
    }

    @Override
    public String toString() {
        return _fmt("tictac.size() = %s", tictac.size());
    }
}
