
import java.util.ArrayList;


/**
 * Created by lenovo on 14-2-7.
 */

public class Author {
    String name;
    int firstYear, jointPublicationCount;
    ArrayList<String> coauthors;
    int[] neighbours;

    public Author(String nameLine) {
        int index = nameLine.indexOf(':');


        if (index != -1) {
            jointPublicationCount = Integer.parseInt(nameLine.substring(index + 1).trim());
            nameLine = nameLine.substring(0, index);
        } else {
            jointPublicationCount = 1;
        }
        name = nameLine.substring(0, nameLine.length() - 4).trim();
        firstYear = Integer.parseInt(nameLine.substring(nameLine.length() - 4).trim());
        //name = nameLine.trim();
        coauthors = new ArrayList<String>();

        //System.out.println(name + "#" + firstYear + "#" + jointPublicationCount);
    }

    public void addCoauthor(String name) {
        coauthors.add(name);
    }

    @Override
    public String toString() {
        return name;
    }

}
