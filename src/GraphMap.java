import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by oc on 2016/12/13.
 */
public class GraphMap {

    int[][] map;
    int n;

    public GraphMap(int[][] map) {
        this.map=map;
        n=map==null?0:map.length;
    }

    public GraphMap(Graph graph) {
        if (graph==null) {
            n=0;
            map=null;
        }
        else {
            n = 12 + graph.n;
            map = new int[n][n];
            for (int i = 0; i < 12; i++) {
                for (int j : graph.links[i]) {
                    map[i][11 + j] = map[11 + j][i] = 1;
                }
            }
        }
    }

    public int getComponents() {
        boolean[] vis=new boolean[n];
        int cnt=0;
        for (int i=0;i<n;i++) {
            if (vis[i]) continue;
            cnt++;
            LinkedList<Integer> linkedList=new LinkedList<>();
            linkedList.add(i);
            while (!linkedList.isEmpty()) {
                int j=linkedList.poll();
                for (int w=0;w<n;w++)
                    if (!vis[w] && map[w][j]==1) {
                        vis[w]=true;
                        linkedList.add(w);
                    }
            }
        }
        return cnt;
    }

}
