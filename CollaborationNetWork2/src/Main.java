import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 14-2-9.
 */

public class Main {

    private void solve() {
        Map<String, Integer> map = new HashMap<String, Integer>();


        double[][] connection, cnt;
        int actorNumber = 0;
        ArrayList<String> actors = new ArrayList<String>();
        boolean[] visited;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("actor_items_extended.jl"));
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                String name = new JSONObject(line).getJSONArray("name").get(0).toString();
                map.put(name, actorNumber++);
                actors.add(name);
               // System.out.println(name);
            }

            reader = new BufferedReader(new FileReader("movie_items_extended.jl"));
            connection = new double[actorNumber][actorNumber];
            cnt = new double[actorNumber][actorNumber];
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                JSONArray casts = new JSONObject(line).getJSONArray("casts");
                double score = new JSONObject(line).getDouble("rating");

                for (int i = 0; i < casts.length(); ++i)
                    for (int j = i + 1; j < casts.length(); ++j) {
                        String a = casts.getString(i);
                        String b = casts.getString(j);
                        Integer na = map.get(a);
                        Integer nb = map.get(b);
                        if (na == null || nb == null) continue;
                        connection[na][nb] += score;
                        connection[nb][na] += score;
                        cnt[na][nb] = 1;
                        cnt[nb][na] = 1;
                    }
            }
            System.out.println(map.size());

            // filter the "isolated" nodes (including the pairs)
            int[] queue = new int[actorNumber];
            visited = new boolean[actorNumber];
            int tail = 0;
            queue[tail++] = 0;
            visited[0] = true;
            for (int k = 0; k < tail; ++k) {
                int i = queue[k];
                for (int j = 0; j < actorNumber; ++j)
                    if (!visited[j] && cnt[i][j] > 0) {
                        visited[j] = true;
                        queue[tail++] = j;
                    }
            }
            System.out.println(tail);
            int tt = 0;
            for (int k = 0; k < actorNumber; ++k)
                if (!visited[k]) {
                    ++ tt;
                    for (int i = 0; i < actorNumber; ++i)
                        if (cnt[k][i] > 0) {
                            System.out.println("WWWWW " + k + " " + i + " " + cnt[i][k] + " " + visited[i]) ;
                        }
                }
            System.out.println(tt);

            Map<String, Integer> tmpMap = new HashMap<String, Integer>();
            double[][] tmpCnt = new double[tail][tail];
            double[][] tmpConnection = new double[tail][tail];
            ArrayList<String> tmpActors = new ArrayList<String>();

            for (int i = 0; i < tail; ++i) {
                tmpActors.add(actors.get(queue[i]));
                tmpMap.put(actors.get(queue[i]), i);
                for (int j = 0; j < tail; ++j) {
                    tmpCnt[i][j] = cnt[queue[i]][queue[j]];
                    tmpConnection[i][j] = connection[queue[i]][queue[j]];
                }
            }

            actors = tmpActors;
            cnt = tmpCnt;
            map = tmpMap;
            actorNumber = tail;
            connection = tmpConnection;
            System.out.println("Filtering finished");

            double[] weightSum = new double[actorNumber];
            for (int i = 0; i < actorNumber; ++i) {
                for (int j = 0; j < actorNumber; ++j) {
                    weightSum[i] += connection[i][j];
                }
            }

            double[] pageRankScore = new double[actorNumber];
            Arrays.fill(pageRankScore, 1);
            int iterateNumber = 500;

            double alpha = 0.85;
            for (int k = 0; k < iterateNumber; ++k) {
                double[] tmp = new double[actorNumber];
                for (int i = 0; i < actorNumber; ++i) {
                    for (int j = 0; j < actorNumber; ++j) {
                        if (cnt[j][i] > 0) tmp[i] += alpha * connection[j][i] * pageRankScore[j] / weightSum[j] + (1.0 - alpha) / actorNumber / 7.5;
                    }
                }
                pageRankScore = tmp;
            }

            visited = new boolean[actorNumber];
            for (int k = 0; k < 10; ++k) {
                int cur = -1;
                double best = -100;
                for (int i = 0; i < actorNumber; ++i)
                    if (!visited[i] && pageRankScore[i] > best) {
                        cur = i;
                        best = pageRankScore[i];
                    }
                visited[cur] = true;
                String actor = actors.get(cur);
                System.out.println((k +1) +  "\t" + actor + "\t" + best);
            }

            int [] sumDegree = new int[actorNumber];
            int [] pointCntDegree = new int[actorNumber];
            for (int k = 0; k < actorNumber; ++k) {
                for (int i = 0; i < actorNumber; ++i) {
                    if (k != i) sumDegree[k] += cnt[k][i];
                }
                ++pointCntDegree[sumDegree[k]];
                if (sumDegree[k] == 0) System.out.println("WAR " + actors.get(k));
               // System.out.println(k + " " + actors.get(k) + " " + sumDegree[k]);
            }
            for (int k = 0; k < 100; ++k) {
                System.out.println(k + "\t" + pointCntDegree[k]);
            }

            int sum = 0;
            for (int i = 0; i < actorNumber; ++i) {
                sum += sumDegree[i];
            }
            System.out.println("actor number : " + actorNumber + " edge number: " + sum + " average degree:" +
                    " " + (1.0 * sum / actorNumber));

            double[] averageDegrees = new double[85];
            for (int k = 1; k < 84; ++k) {
                int cntAll = 0;
                for (int i = 0; i < actorNumber; ++i)
                    if (sumDegree[i] == k) {
                        ++cntAll;
                        for (int j = 0; j < actorNumber; ++j) {
                            if (cnt[i][j] > 0)averageDegrees[k] += sumDegree[j];
                        }
                    }
                if (cntAll != 0) averageDegrees[k] /= cntAll * k;
            }

            System.out.println("average degree of the nearest neighbors for k_i = k");
            for (int i = 1; i < 84; ++i) {
                System.out.println(i + "\t" + averageDegrees[i]);
            }

            // clustering coefficient
            double averageCoefficient = 0;
            for (int k = 0; k < actorNumber; ++k) {
                int edge = 0;
                int maxEdges = 0;
                for (int i = 0; i < actorNumber; ++i)
                    for (int j = 0; j < actorNumber; ++j) {
                        if (i < j && cnt[k][i] > 0 && cnt[k][j] > 0) {
                            ++maxEdges;
                            if (cnt[i][j] > 0) ++edge;
                        }
                    }
                if (maxEdges > 0) averageCoefficient += 1.0 * edge / maxEdges;
               // System.out.println(k + " " + (1.0 * edge / maxEdges));
            }
            averageCoefficient /= actorNumber;
            System.out.println("clustering coefficient is : " + averageCoefficient);


            int[][] distance = new int[actorNumber][actorNumber];
            for (int i = 0; i < actorNumber; ++i)
                for (int j = 0; j < actorNumber; ++j) {
                    distance[i][j] = cnt[i][j] > 0 ? 1 : actorNumber;
                }
            for (int k = 0; k < actorNumber; ++k)
                for (int i = 0; i < actorNumber; ++i)
                    for (int j = 0; j < actorNumber; ++j)
                        if (k != i && i != j && j != k && distance[i][k] + distance[k][j] < distance[i][j]) {
                            distance[i][j] = distance[i][k] + distance[k][j];
                        }

            double averagePathLength = 0;
            int maxPath = 0;
            for (int i = 0; i < actorNumber; ++i)
                for (int j = i + 1; j < actorNumber; ++j) {
                    averagePathLength += distance[i][j];
                    maxPath = Math.max(maxPath, distance[i][j]);
                }
            averagePathLength *= 2.0 / actorNumber / (actorNumber - 1);
            System.out.println("average path length is: " + averagePathLength);
            System.out.println("longest path length is: " + maxPath);

            // pair affecterd
            // distance affected pairs
            int[][] neightbours = new int[actorNumber][];
            for (int i = 0; i < actorNumber; ++i) {
                neightbours[i] = new int[sumDegree[i]];
                int p = 0;
                for (int j = 0; j < actorNumber; ++j)
                    if (cnt[i][j] > 0) {
                        neightbours[i][p++] = j;
                    }
            }

            int[] pairCnt = new int[actorNumber];
            for (int key = 0; key < actorNumber; ++key) {
                int[][] distance2 = new int[actorNumber][actorNumber];
                for (int i = 0; i < actorNumber; ++i)
                    for (int j = 0; j < actorNumber; ++j) {
                        distance2[i][j] = maxPath + 1;
                    }

                queue = new int[actorNumber];
                int cnt1 = 0;
                for (int k = 0; k < actorNumber; ++k) {
                    if (k == key) continue;

                    distance2[k][k] = 0;
                    tail = 0;
                    queue[tail++] = k;
                    for (int head = 0; head < tail; ++head) {
                        int tmp = distance2[k][queue[head]];
                        for (int i : neightbours[queue[head]]) {
                            if (i != key && tmp + 1 < distance2[k][i]) {
                                distance2[k][i] = tmp + 1;
                                queue[tail++] = i;
                            }
                        }
                    }
                }


                for (int i = 0; i < actorNumber; ++i)
                    for (int j = i + 1; j < actorNumber; ++j)
                        if (key != i && key != j && distance[i][j] != distance2[i][j]) {
                            ++cnt1;
                        }
                pairCnt[key] = cnt1;
            }

            visited = new boolean[actorNumber];
            for (int k = 0; k < 20; ++k) {
                int cur = -1;
                int best = -100;
                for (int i = 0; i < actorNumber; ++i)
                    if (!visited[i] && pairCnt[i] > best) {
                        cur = i;
                        best = pairCnt[i];
                    }
                visited[cur] = true;
                String actor = actors.get(cur);
                // System.out.println(k + "\t" + author+ "\t" + best + "\t" + cur + "\t" + degrees[cur]);
                System.out.println((k + 1) + "\t" + actor+ "\t" + best);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        new Main().solve();
    }
}
