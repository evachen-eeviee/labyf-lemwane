package vue.utils;

public class ThemeManager {

    private static String currentBackground = "background-sunrise";

    public static String getCurrentBackground() {
        return currentBackground;
    }

    public static void setCurrentBackground(String id) {
        currentBackground = id;
    }
}
