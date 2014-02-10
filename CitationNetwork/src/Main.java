import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 14-2-9.
 */
public class Main {
    private void generateGraph(boolean connected[][], ArrayList<String> papers) {

        try {
            PrintWriter out = new PrintWriter(new File("graph.gv"));
            int n = connected.length;
            out.println("digraph large {");
            out.println("\toutputorder=edgesfirst;");
            out.println("\tbgcolor = transparent;");
            for (int i = 0; i < n; ++i) {
                String color = "black", shape = "circle";

                out.println("\tnode [shape = " + shape + ", color=" + color +
                        ", label = \"" + (i + 1) + "\"" +
                        "]" + " point" + i + ";");
            }
            for (int i = 0; i < n; ++i)
                for (int j = 0; j < n; ++j) {
                    if (connected[i][j]) {
                        out.println("\tpoint" + i + " -> " + "point" + j + " [color = black];");
                    }
                }
            out.println("}");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void solve() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        ArrayList<String> papers = new ArrayList<String>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("in.txt"));
            int paperNumber = Integer.parseInt(reader.readLine());
            for (int i = 0; i < paperNumber; ++i) {
                papers.add(reader.readLine().trim());
                map.put(papers.get(i), i);
            }
            int[] citedNumber = new int[paperNumber];
            for (int i = 0; i <  paperNumber; ++i) {
                citedNumber[i] = Integer.parseInt(reader.readLine().trim());
            }
            int[] citingNumber = new int[paperNumber];
            for (int i = 0; i <  paperNumber; ++i) {
                citingNumber[i] = Integer.parseInt(reader.readLine().trim());
            }
            double[] indirectedCitingNumber = new double[paperNumber];
            for (int i = 0; i <  paperNumber; ++i) {
                indirectedCitingNumber[i] = Double.parseDouble(reader.readLine().trim());
            }


            boolean[][] cite = new boolean[paperNumber][paperNumber];
            for (int i = 0; i < paperNumber; ++i) {
                String[] line = reader.readLine().split("\t");
                for (int j = 0; j < paperNumber; ++j) {
                    cite[i][j] = line[j].equals("1");
                }
            }


           // generateGraph(cite, papers);

            double[] aicn = new double[paperNumber];
            for (int i = 0; i < paperNumber; ++i) {
                aicn[i] = citedNumber[i] + indirectedCitingNumber[i];
            }

            boolean[] visited = new boolean[paperNumber];

            for (int k = 0; k + 4 < paperNumber; ++k) {
                int cur = -1;
                double v = -100000;
                for (int i = 0; i < paperNumber; ++i) {
                    if (!visited[i] && aicn[i] > v) {
                        v = aicn[i];
                        cur = i;
                    }
                }
                visited[cur] = true;
                System.out.println((k + 1) +  "\t"  + papers.get(cur) + "\t" + citedNumber[cur] + "\t" + aicn[cur]);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Main().solve();
    }
}
