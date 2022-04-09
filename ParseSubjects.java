import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class ParseSubjects {

    private static final String BASE_URL = "https://rijksoverheid.nl/onderwerpen/";

    public static void main(String[] args) throws FileNotFoundException {
        new ParseSubjects().parse();
    }

    static class Subject implements Comparable<Subject> {
        String text;
        String path;
        Set<String> themes;

        public Subject(String subjectText, String subjectPath, String theme) {
            text = subjectText;
            path = subjectPath;
            this.themes = new HashSet<>();
            themes.add(theme);
        }

        public void addTheme(String theme) {
            themes.add(theme);
        }

        @Override
        public int compareTo(Subject subject) {
            return text.compareTo(subject.text);
        }
    }

    private String normalize(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("-", " ")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll(",", "")
                .replaceAll(":", "")
                .replaceAll("'", "")
                .replaceAll(" in ", " ")
                .replaceAll(" door ", " ")
                .replaceAll(" via ", " ")
                .replaceAll(" en ", " ")
                .replaceAll(" bij ", " ")
                .replaceAll(" en ", " ").trim();
    }

    private void parse() throws FileNotFoundException {
        Scanner s = new Scanner(new File("onderwerpen.txt"));
        String theme = null;
        List<Subject> subjects = new ArrayList<>();
        while (s.hasNext()) {
            String line = s.nextLine();
            if (line.contains("<h3><a href=\"/onderwerpen/themas")) {
                theme = getLineText(line);
            } else if (theme != null && line.contains("<a href=\"/onderwerpen")) {
                String subjectText = getLineText(line);
                String subjectPath = getSubjectPath(line);
                List<Subject> existing = subjects.stream()
                        .filter(sub -> sub.text.equals(subjectText))
                        .collect(toList());
                if (existing.isEmpty()) {
                    subjects.add(new Subject(subjectText, subjectPath, theme));
                } else {
                    existing.get(0).addTheme(theme);
                }
            }
        }
        Collections.sort(subjects);
        for (Subject subject : subjects) {
            printSubject(subject);
        }
    }

    private void printSubject(Subject subject) {
        StringBuilder line = new StringBuilder()
                .append("<div class=\"subject\">")
                .append("<a href=\"")
                .append(BASE_URL)
                .append(subject.path)
                .append("\">")
                .append(subject.text);

        if (!normalize(subject.text).equals(normalize(subject.path))) {
            line.append(" / ").append(normalize(subject.path));
        }
        line.append("</a>");
        for (String theme : subject.themes) {
            line.append(" (").append(theme).append(")");
        }
        line.append("</div>");
        System.out.println(line);
    }

    private String getSubjectPath(String line) {
        int start = line.indexOf("/onderwerpen/") + 13;
        int end = line.indexOf("\">");
        return line.substring(start, end);
    }

    private String getLineText(String line) {
        int start = line.indexOf("\">") + 2;
        int end = line.indexOf("</a>");
        return line.substring(start, end);
    }
}
