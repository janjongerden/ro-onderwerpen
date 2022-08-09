import java.util.Locale;

public final class Util {
    public static String normalize(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("-", " ")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll(",", "")
                .replaceAll(":", "")
                .replaceAll("'", "")
                .replaceAll("&#039;", "")
                .replaceAll("ï", "i")
                .replaceAll("ë", "e")
                .replaceAll(" in ", " ")
                .replaceAll(" door ", " ")
                .replaceAll(" via ", " ")
                .replaceAll(" het ", " ")
                .replaceAll(" van ", " ")
                .replaceAll(" bij ", " ")
                .replaceAll(" en ", " ").trim();
    }

}

