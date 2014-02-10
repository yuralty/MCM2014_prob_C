import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 14-2-7.
 */

public class Main {

    private void generateGraph(boolean connected[][]) {

        try {
            PrintWriter out = new PrintWriter(new File("graph.gv"));
            int n = connected.length;
            out.println("graph large {");
            out.println("\toutputorder=edgesfirst;");
            out.println("\tbgcolor = transparent;");
            for (int i = 0; i < n; ++i) {
                out.println("\tnode [shape = point, color=blue, label = \"\", style = filled]" + " point" + i + ";");
            }
            for (int i = 0; i < n; ++i)
                for (int j = i + 1; j < n; ++j) {
                    if (connected[i][j]) {
                        out.println("\tpoint" + i + " -- " + "point" + j + " [penwidth=0.08, color = grey81];");
                    }
                }
            out.println("}");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void generateGraph(boolean connected[][], int[] mark) {

        try {
            PrintWriter out = new PrintWriter(new File("graph.gv"));
            int n = connected.length;
            out.println("graph large {");
            out.println("\toutputorder=edgesfirst;");
            out.println("\tbgcolor = transparent;");
            for (int i = 0; i < n; ++i) {
                String color = "blue", shape = "point";
                double size = 0.03;
                if (mark[i] == 1) {
                    color = "red";
                    shape = "square";
                    size = 0.07;
                }
                if (mark[i] == 2) {
                    color = "green";
                    shape = "triangle";
                    size = 0.1;
                }
                if (mark[i] == 3) {
                    color = "deeppink4";
                    shape = "diamond";
                    size =0.12;
                }
                out.println("\tnode [shape = " + shape + ", color=" + color + ", label = \"\", " +
                        "style = filled, height = " + size + ", width = " + size + "]" + " point" + i + ";");
            }
            for (int i = 0; i < n; ++i)
                for (int j = i + 1; j < n; ++j) {
                    if (connected[i][j]) {
                        out.println("\tpoint" + i + " -- " + "point" + j + " [penwidth=0.08, color = grey81];");
                    }
                }
            out.println("}");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    double[] pageRank(boolean connected[][]) {
        int N = connected.length;
        double[] res = new double[N];
        Arrays.fill(res, 1.0);

        double alpha = 0.85;
        int maxSteps = 200;

        int[] degrees = new int[N];
        for (int k = 0; k < N; ++k) {
            for (int i = 0; i < N; ++i)
                if (connected[k][i]) {
                    ++degrees[k];
                }
        }

        for (int c = 0; c < maxSteps; ++c) {
            double[] tmp = new double[N];
            for (int k = 0; k < N; ++k) {
                for (int i = 0; i < N; ++i)
                    if (connected[i][k]) {
                        tmp[k] += alpha * (2*res[k]/(res[k]+res[i]))*res[i] / degrees[i];
                    }
                tmp[k] += (1 - alpha)/N;
            }
            res = tmp;
        }

        return res;
    }

    private void solve() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        ArrayList<Author> authors = new ArrayList<Author>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("Erdos1.txt"));
            while (true) {
                String nameLine = reader.readLine();
                if (nameLine.equals("#end")) {
                    break;
                }

                Author author = new Author(nameLine);
                map.put(author.name, map.size());
                authors.add(author);

                String coauthor = "#";
                while (true) {
                    coauthor = reader.readLine().trim();
                    if (coauthor.equals("")) {
                        break;
                    }
                    author.addCoauthor(coauthor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // filter the "isolated" nodes (including the pairs)
        ArrayList<Author> subAuthors = new ArrayList<Author>();
        Map<String, Integer> subMap = new HashMap<String, Integer>();
        subAuthors.add(authors.get(0));
        for (int k = 0; k < subAuthors.size(); ++k) {
            Author author = subAuthors.get(k);
            subMap.put(author.name, k);
            for (String name : author.coauthors) {
                Integer j = map.get(name);
                if (j != null) {
                    Author other = authors.get(j);
                    if (!subMap.containsKey(other.name)) {
                        subAuthors.add(other);
                        subMap.put(other.name, -1);
                    }
                }
            }
        }
        authors = subAuthors;
        map = subMap;
        System.out.println("Filtering finished");


        int authorNumber = authors.size();
        boolean[][] connected = new boolean[authors.size()][authors.size()];
        for (int i = 0; i < authors.size(); ++i) {
            for (String name : authors.get(i).coauthors) {
                Integer j = map.get(name);
                if (j != null) {
                    connected[i][j] = true;
                    if (i == j) {
                        System.out.println("?" + authors.get(i) + "    "+ authors.get(j));
                    }
                }
            }
        }

        for (int i = 0; i < authorNumber; ++i)
            for (int j = 0; j < authorNumber; ++j)
                if (connected[i][j] && !connected[j][i]) {
                    System.out.println(authors.get(i) + "     " + authors.get(j));
                }

       //generateGraph(connected);

       //if (true) return;




        /* For Degree calculation */
        int[] degrees = new int[authorNumber];
        for (int k = 0; k < authorNumber; ++k) {
            for (int i = 0; i < authorNumber; ++i)
                if (connected[k][i]) {
                    ++degrees[k];
                }
        }

        double averageDegree = 0;
        for (int k = 0; k < authorNumber; ++k) {
            averageDegree += degrees[k];
        }
        System.out.println("total edges: " + averageDegree / 2);
        averageDegree /= authorNumber;
        System.out.println("average degree: " + averageDegree);

        boolean[]  visited = new boolean[connected.length];
        for (int k = 0; k < 10; ++k) {
            int cur = -1;
            int best = -1;
            for (int i = 0; i < authorNumber; ++i)
                if (!visited[i] && degrees[i] > best) {
                    cur = i;
                    best = degrees[i];
                }
            visited[cur] = true;
            Author author = authors.get(cur);
            System.out.println(k + "\t" + author+ "\t" + best);
        }

        int[] mark = new int[authorNumber]; // marks

        /*  For Page Rank*/
        double[] rank = pageRank(connected);

        visited = new boolean[connected.length];
        for (int k = 0; k < 20; ++k) {
            int cur = -1;
            double best = -1;
            for (int i = 0; i < authorNumber; ++i)
                if (!visited[i] && rank[i] > best) {
                    cur = i;
                    best = rank[i];
                }
            visited[cur] = true;
            Author author = authors.get(cur);
            System.out.println(k + "\t" + author + "\t" + best + "\t" +degrees[cur]);
            mark[cur] += 1;
        }

        // perpa
        for (int k = 0; k < authorNumber; ++k) {
            Author author = authors.get(k);
            author.neighbours = new int[degrees[k]];
            int cnt = 0;
            for (String name : author.coauthors) {
                if (map.containsKey(name)) {
                    author.neighbours[cnt++] = map.get(name);
                }
            }
        }



        int[] cntDegree = new int [55];
        for (int i = 0; i < authorNumber; ++i) {
            cntDegree[degrees[i]]++;
        }
        for (int i = 1; i < 53; ++i) {
            System.out.println(i + "\t" + cntDegree[i]);
        }

        double[] averageDegrees = new double[55];
        for (int k = 1; k < 53; ++k) {
            int cnt = 0;
            for (int i = 0; i < authorNumber; ++i)
                if (degrees[i] == k) {
                    ++cnt;
                    for (int j : authors.get(i).neighbours) {
                        averageDegrees[k] += degrees[j];
                    }
                }
            if (cnt != 0) averageDegrees[k] /= cnt * k;
        }

        System.out.println("average degree of the nearest neighbors for k_i = k");
        for (int i = 1; i < 53; ++i) {
            System.out.println(averageDegrees[i]);
        }

        int sum = 0;
        for (int i = 0; i < authorNumber; ++i) {
            sum += degrees[i];
        }
        System.out.println(sum * 1.0 / authorNumber / (authorNumber - 1));

        // clustering coefficient
        double averageCoefficient = 0;
        for (int k = 0; k < authorNumber; ++k) {
            Author author = authors.get(k);
            int cnt = 0;
            int n = author.neighbours.length;
            for (int i : author.neighbours)
                for (int j : author.neighbours) {
                    if (i < j && connected[i][j]) ++cnt;
                }
            if (n > 1) averageCoefficient += 2 * cnt / n / (n - 1);
            System.out.println(k + " " + degrees[k] + " " + 2.0 * cnt / n / (n - 1));
        }
        averageCoefficient /= authorNumber;
        System.out.println("clustering coefficient is : " + averageCoefficient);

        // path length

        int[][] distance = new int[authorNumber][authorNumber];
        for (int i = 0; i < authorNumber; ++i)
            for (int j = 0; j < authorNumber; ++j) {
                distance[i][j] = connected[i][j] ? 1 : authorNumber;
            }
        for (int k = 0; k < authorNumber; ++k)
            for (int i = 0; i < authorNumber; ++i)
                for (int j = 0; j < authorNumber; ++j)
                    if (k != i && i != j && j != k && distance[i][k] + distance[k][j] < distance[i][j]) {
                        distance[i][j] = distance[i][k] + distance[k][j];
                    }
        double averagePathLength = 0;
        int maxPath = 0;
        for (int i = 0; i < authorNumber; ++i)
            for (int j = i + 1; j < authorNumber; ++j) {
                averagePathLength += distance[i][j];
                maxPath = Math.max(maxPath, distance[i][j]);
            }
        averagePathLength *= 2.0 / authorNumber / (authorNumber - 1);
        System.out.println("average path length is: " + averagePathLength);
        System.out.println("longest path length is: " + maxPath);

        // distance - point

        double[] resd = new double[authorNumber];
        for (int key = 0; key < authorNumber; ++key) {
            int[][] distance2 = new int[authorNumber][authorNumber];
            for (int i = 0; i < authorNumber; ++i)
                for (int j = 0; j < authorNumber; ++j) {
                    distance2[i][j] = maxPath + 1;
                }

            int[] queue = new int[authorNumber];
            for (int k = 0; k < authorNumber; ++k) {
                if (k == key) continue;

                distance2[k][k] = 0;
                int tail = 0;
                queue[tail++] = k;
                for (int head = 0; head < tail; ++head) {
                    int tmp = distance2[k][queue[head]];
                    for (int i : authors.get(queue[head]).neighbours) {
                        if (i != key && tmp + 1 < distance2[k][i]) {
                            distance2[k][i] = tmp + 1;
                            queue[tail++] = i;
                        }
                    }
                }
            }

            double averagePathLength2 = 0;
            int cnt = 0;
            for (int i = 0; i < authorNumber; ++i)
                for (int j = i + 1; j < authorNumber; ++j) {
                    if (i != key && j != key) {
                        averagePathLength2 += distance2[i][j];
                        if (distance2[i][j] == maxPath + 1) {
                            ++cnt;
                        }
                    }
                }
            averagePathLength2 *= 2.0 / (authorNumber - 1) / (authorNumber - 2);
            System.out.println("erase " + key + " average path length is: " + averagePathLength2 + " " + cnt);
            resd[key] = averagePathLength2;
        }

        visited = new boolean[authorNumber];
        for (int k = 0; k < 10; ++k) {
            int cur = -1;
            double best = -100;
            for (int i = 0; i < authorNumber; ++i)
                if (!visited[i] && resd[i] > best) {
                    cur = i;
                    best = resd[i];
                }
            visited[cur] = true;
            Author author = authors.get(cur);
            System.out.println(k + "\t" + author+ "\t" + best + "\t" + cur + "\t" + degrees[cur]);
        }
        // distance affected pairs
        int[] pairCnt = new int[authorNumber];
        for (int key = 0; key < authorNumber; ++key) {
            int[][] distance2 = new int[authorNumber][authorNumber];
            for (int i = 0; i < authorNumber; ++i)
                for (int j = 0; j < authorNumber; ++j) {
                    distance2[i][j] = maxPath + 1;
                }

            int[] queue = new int[authorNumber];
            for (int k = 0; k < authorNumber; ++k) {
                if (k == key) continue;

                distance2[k][k] = 0;
                int tail = 0;
                queue[tail++] = k;
                for (int head = 0; head < tail; ++head) {
                    int tmp = distance2[k][queue[head]];
                    for (int i : authors.get(queue[head]).neighbours) {
                        if (i != key && tmp + 1 < distance2[k][i]) {
                            distance2[k][i] = tmp + 1;
                            queue[tail++] = i;
                        }
                    }
                }
            }

            int cnt = 0;
            for (int i = 0; i < authorNumber; ++i)
                for (int j = i + 1; j < authorNumber; ++j)
                    if (key != i && key != j && distance[i][j] != distance2[i][j]) {
                        ++cnt;
                    }
            pairCnt[key] = cnt;
        }

        visited = new boolean[authorNumber];
        for (int k = 0; k < 20; ++k) {
            int cur = -1;
            int best = -100;
            for (int i = 0; i < authorNumber; ++i)
                if (!visited[i] && pairCnt[i] > best) {
                    cur = i;
                    best = pairCnt[i];
                }
            visited[cur] = true;
            Author author = authors.get(cur);
           // System.out.println(k + "\t" + author+ "\t" + best + "\t" + cur + "\t" + degrees[cur]);
            System.out.println(author+ "\t" + best);
            mark[cur] += 2;
        }

        generateGraph(connected, mark);


        // famous
        System.out.println(authors.get(map.get("SARKOZY, ANDRAS")).jointPublicationCount);
        visited = new boolean[authorNumber];
        for (int k = 0; k < 20; ++k) {
            int cur = -1;
            int best = -100;
            for (int i = 0; i < authorNumber; ++i)
                if (!visited[i] && authors.get(i).jointPublicationCount > best) {
                    cur = i;
                    best = authors.get(i).jointPublicationCount;
                }
            visited[cur] = true;
            Author author = authors.get(cur);
            // System.out.println(k + "\t" + author+ "\t" + best + "\t" + cur + "\t" + degrees[cur]);
            System.out.println(k + " " + author+ "\t" + best);
            mark[cur] += 2;
        }

    }

    public static void main(String args[]) {
        new Main().solve();
    }
}
