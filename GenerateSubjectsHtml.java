import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class GenerateSubjectsHtml {

    private static final String BASE_URL = "https://www.rijksoverheid.nl/onderwerpen/";
    private static final boolean INCLUDE_THEMES = false;

    private static final Set<String> skipSubSubjects = Set.of(
            "Nieuws",
            "Vraag en antwoord",
            "Weblog",
            "Documenten");

    public static void main(String[] args) throws FileNotFoundException {
        List<Subject> subjects = parseOnderwerpenFile();

        for (Subject subject : subjects) {
            printSubject(subject);
        }
    }

    private static String normalize(String text) {
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

    private static List<Subject> parseOnderwerpenFile() throws FileNotFoundException {
        Scanner s = new Scanner(new File("onderwerpen.html"));
        String theme = null;
        List<Subject> subjects = new ArrayList<>();
        while (s.hasNext()) {
            String line = s.nextLine();
            if (line.contains("<h3><a href=\"/onderwerpen/themas")) {
                theme = getLineText(line);
            } else if (theme != null && line.contains("<a href=\"/onderwerpen")) {
                addSubject(subjects, line, theme);
            }
        }
        Collections.sort(subjects);
        return subjects;
    }

    private static void addSubject(List<Subject> subjects, String line, String theme) {
        String subjectText = getLineText(line);
        String subjectPath = getSubjectPath(line);
        List<Subject> existing = subjects.stream()
                .filter(sub -> sub.text.equals(subjectText))
                .collect(toList());
        if (existing.isEmpty()) {
            List<String> subSubjects = getSubSubjects(subjectPath);
            subjects.add(new Subject(subjectText, subjectPath, theme, subSubjects));
        } else {
            existing.get(0).addTheme(theme);
        }
    }

    private static List<String> getSubSubjects(String subjectPath) {
        Scanner s;
        try {
            s = new Scanner(new File("subsubjects/" + subjectPath + ".html"));
        } catch (FileNotFoundException e) {
            return Collections.emptyList();
        }
        List<String> subTexts = new ArrayList<>();
        while (s.hasNext()) {
            String line = s.nextLine();
            if (line.contains("<li><a  href=\"/onderwerpen/")) {
                String subText = getLineText(line);
                if (!skipSubSubjects.contains(subText)) {
                    subTexts.add(subText);
                }
            }
        }
        return subTexts;
    }

    private static void printSubject(Subject subject) {
        StringBuilder line = new StringBuilder()
                .append("<div class=\"subject\">")
                .append("<a href=\"")
                .append(BASE_URL)
                .append(subject.path)
                .append("\">")
                .append(subject.text);

        if (!normalize(subject.text).contains(normalize(subject.path))) {
            line.append(" / ").append(normalize(subject.path));
        }
        line.append("</a>");
        if (INCLUDE_THEMES) {
            for (String theme : subject.themes) {
                line.append(" (").append(theme).append(")");
            }
        }
        for (String subSubject : subject.subSubjects) {
            line.append("<span class=\"subsub\">").append(subSubject).append("</span>");
        }
        line.append("</div>");
        System.out.println(line);
    }

    private static String getSubjectPath(String line) {
        int start = line.indexOf("/onderwerpen/") + 13;
        int end = line.indexOf("\">");
        return line.substring(start, end);
    }

    private static String getLineText(String line) {
        int start = line.indexOf("\">") + 2;
        int end = line.indexOf("</a>");
        return line.substring(start, end);
    }
}
