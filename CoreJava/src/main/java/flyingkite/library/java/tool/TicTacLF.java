package flyingkite.library.java.tool;

import flyingkite.library.java.log.LF;

public class TicTacLF extends TicTac2 {
    private LF logFile;

    public TicTacLF(LF file) {
        logFile = file;
        logFile.getFile().open();
    }

    public LF getLogFile() {
        return logFile;
    }

    @Override
    protected void logError(long tac, String msg) {
        logFile.log("X_X Omitted. tic = N/A, tac = %s : %s", getTime(tac), msg);
        logFile.getFile().flush();
    }

    @Override
    protected void logTac(String msg) {
        logFile.log(msg);
        logFile.getFile().flush();
    }
}
