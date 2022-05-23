package flyingkite.library.java.log;

import flyingkite.library.java.tool.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class FileOutput implements Loggable {
    private PrintWriter pw;
    private FileOutputStream fos;
    private File file;

    public FileOutput(String name) {
        this(new File(name));
    }

    public FileOutput(File file1) {
        file = file1;
        validate();
    }

    public File getFile() {
        return file;
    }

    private void validate() {
        if (file.exists() && file.isDirectory()) {
            file.delete();
        }
    }

    public FileOutput open() {
        return open(true);
    }

    public FileOutput open(boolean append) {
        try {
            if (!file.exists()) {
                File p = file.getParentFile();
                if (p != null) {
                    p.mkdirs();
                }
                file.createNewFile();
            }
            close();

            fos = new FileOutputStream(file, append);
            pw = new PrintWriter(fos);
        } catch (IOException e) {
            e.printStackTrace();
            IOUtil.closeIt(fos, pw);
        } finally { // Should not close it since this is open
        }
        return this;
    }

    @Override
    public void log(String msg) {
        writeln(msg);
    }

    public FileOutput writeln(String format, Object... param) {
        return writeln(_fmt(format, param));
    }

    public FileOutput writeln(String msg) {
        if (pw == null) {
            L.log("Did not opened :%s", file);
        } else {
            pw.append(msg).append("\r\n");
        }
        return this;
    }

    public FileOutput close() {
        flush();
        IOUtil.closeIt(fos, pw);
        fos = null;
        pw = null;
        return this;
    }

    public FileOutput delete() {
        file.delete();
        return this;
    }

    public FileOutput flush() {
        IOUtil.flushIt(fos, pw);
        return this;
    }

    @Override
    public String toString() {
        return file.toString();
    }
}
