package flyingkite.log;


public interface Formattable {
    default String _fmt(String format, Object... param) {
        return String.format(java.util.Locale.US, format, param);
    }
}
