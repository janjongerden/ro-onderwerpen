import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Subject implements Comparable<Subject> {
    String text;
    String path;
    Set<String> themes;
    List<String> subSubjects;

    public Subject(String subjectText, String subjectPath, String theme, List<String> subSubjects) {
        text = subjectText;
        path = subjectPath;
        this.themes = new HashSet<>();
        themes.add(theme);
        this.subSubjects = subSubjects;
    }

    public void addTheme(String theme) {
        themes.add(theme);
    }

    @Override
    public int compareTo(Subject subject) {
        return text.compareTo(subject.text);
    }

    public String getFullName() {
        String fullName = text;

        if (!Util.normalize(text).contains(Util.normalize(path))) {
            fullName += " / " + Util.normalize(path);
        }
        return fullName;
    }
}
