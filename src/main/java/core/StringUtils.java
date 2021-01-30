package core;

public class StringUtils {
    public static boolean isNotBlank(String s) {
        return s != null && !s.trim().equals("");
    }
}
