package flyingkite.library.java.util;

import java.util.List;

public class RegexUtil {

    public static String toRegexOr(List<String> keys) {
        return StringUtil.join(keys, "(", "|", ")");
    }

}