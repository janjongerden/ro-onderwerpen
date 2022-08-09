import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.Files.isDirectory;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@SuppressWarnings("resource")
public class GenerateHistoricalData {

    private static final int MAX_CHANGES_SHOWN = 4;

    // these older files contain differently normalized data, leading
    // to false positive changes
    private static final Set<String> ignoreFiles = Set.of(
         "2022-04-12.html",
         "2022-04-11.html",
         "2022-04-10.html",
         "2022-04-09.html"
    );

    public static void main(String[] args) throws IOException {
        printSubjectHistory(new ArrayList<>());
    }

    public static void printSubjectHistory(List<Subject> newSubjects) throws IOException {
        Set<String> yesterday;
        Set<String> today = newSubjects.stream().map(Subject::getFullName).collect(toSet());

        List<Path> historicalFiles = getHistoricalFiles();

        List<String> changes = new ArrayList<>();
        for (Path file : historicalFiles) {
            yesterday = extractSubjects(file);
            changes.addAll(compareSubjects(yesterday, today, file.toFile().getName().substring(0, 10)));
            today = yesterday;
        }
        printChanges(changes);
    }

    private static void printChanges(List<String> changes) {
        System.out.println("<div class=news><ul>");
        changes.stream()
                .limit(MAX_CHANGES_SHOWN)
                .forEach(System.out::println);
        System.out.println("</ul></div>");
        if (changes.size() > MAX_CHANGES_SHOWN) {
            System.out.println("<details class=oldNews>");
            System.out.println("<summary>oudere veranderingen...</summary><ul>");
            changes.stream()
                    .skip(MAX_CHANGES_SHOWN)
                    .forEach(System.out::println);
            System.out.println("</ul></details><br/>");
        }
    }

    private static List<Path> getHistoricalFiles() throws IOException {
        return Files.list(Paths.get("./archive/"))
                .filter(file -> {
                    String name = file.toFile().getName();
                    return !isDirectory(file) && name.startsWith("20")
                            && name.endsWith(".html") && !ignoreFiles.contains(name);
                })
                .sorted(reverseOrder())
                .collect(toList());
    }

    private static List<String> compareSubjects(Set<String> yesterday, Set<String> today, String date) {
        List<String> changes = today.stream()
                .filter(subject -> !yesterday.contains(subject))
                .map(subject -> newsLine("&#9989; Nieuw", date, subject))
                .collect(toList());

        changes.addAll( yesterday.stream()
                .filter(subject -> !today.contains(subject))
                .map(subject -> newsLine("&#128683; Verwijderd", date, subject))
                .collect(toList()));

        return changes;
    }

    private static String newsLine(String operation, String date, String subject) {
        return "<li>" + operation + " sinds <strong>" + date + "</strong>: <em>'" + subject + "'</em></li>";
    }

    private static Set<String> extractSubjects(Path file) throws IOException {
        Scanner s = new Scanner(file);
        Set<String> subjects = new HashSet<>();
        while (s.hasNext()) {
            String line = s.nextLine();
            if (line.contains("<div class=\"subject\">")) {
                subjects.add(line.substring(line.indexOf("\">", 69) + 2, line.indexOf("</a>")));
            }
        }
        return subjects;
    }
}
