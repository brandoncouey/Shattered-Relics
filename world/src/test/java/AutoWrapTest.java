import java.util.ArrayList;
import java.util.List;

public class AutoWrapTest {


    public static int MAX_PER_LINE = 25;

    public static int LINES_PER_PAGE = 11;

    public static final String BOOK = "Within the pages of this book you will find the secret to working the very elements themselves. Early in the fifth age, a new ore was discovered. This ore has a unique property of absorbing, transforming or focusing elemental energy. A workshop was erected close by to work this new material. The workshop was set up for artisans and inventors to be able to come and create devices made from the unique ore, found only in the village of the Seer's. After some time of successful industry the true power of this ore became apparent as greater and more powerful weapons were created. Realising the threat this posed, the magi of the time closed down the workshop and bound it under lock and key, also trying to destroy all knowledge of manufacturing processes. Yet this book remains and you may still find a way to enter the workshop within this leather bound volume. ";

    static ArrayList<String> lines = new ArrayList<>();

    static ArrayList<List<String>> pages = new ArrayList<>();

    public static void main(String[] args) {


        String[] split = BOOK.split(" ");

        String currLine = "";

        int line = 0;
        for (String word : split) {

            int curLen = currLine.length();

            if (curLen + word.length() > MAX_PER_LINE) {
                lines.add(currLine);
                currLine = word + " ";
            } else {
                currLine += word += " ";
            }
            line++;

            if (line == split.length) {
                lines.add(currLine);
            }
        }
        lines.stream().forEach(System.out::println);

        int currentLines = 0;
        List<String> linesOnPage = new ArrayList<>();
        for (String l : lines) {
            linesOnPage.add(l);
            currentLines++;
            if (currentLines == LINES_PER_PAGE) {
                pages.add(linesOnPage);
                currentLines = 0;
                linesOnPage.clear();;
            }
        }

        System.out.println(pages.size());
    }



}
