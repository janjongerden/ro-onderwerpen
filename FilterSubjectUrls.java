import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FilterSubjectUrls {

    private static final String BASE_URL = "https://www.rijksoverheid.nl";

    public static void main(String[] args) throws FileNotFoundException {
        new FilterSubjectUrls().parse();
    }

    private void parse() throws FileNotFoundException {
        Scanner s = new Scanner(new File("onderwerpen.html"));
        Set<String> urls = new HashSet<>();
        while (s.hasNext()) {
            String line = s.nextLine();
            if (line.contains("<a href=\"/onderwerpen") && !line.contains("/themas/")) {
                urls.add(getUrl(line));
            }
        }

        List<String> urlList = new ArrayList<>(urls);
        Collections.sort(urlList);

        for (String url : urlList) {
            System.out.println(BASE_URL + url);
        }
    }

    private String getUrl(String line) {
        int start = line.indexOf("href=\"") + 6;
        int end = line.indexOf("\">");
        return line.substring(start, end);
    }
}
