import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by oc on 2016/12/12.
 */
public class Graph {

    int n;
    LinkedList<Integer>[] links=new LinkedList[12];

    int lastx1=-1, lasty1=-1, lastx2=-1, lasty2=-1;

    public Graph() {
        for (int i=0;i<12;i++) links[i]=new LinkedList<>();
    }

    public Graph(Graph another) {
        n=another.n;
        for (int i=0;i<12;i++) links[i]=new LinkedList<>(another.links[i]);
    }

    public ArrayList<Integer> getDegreesMoreThanOne() {
        ArrayList<Integer> arrayList=new ArrayList<>();
        for (int i=0;i<12;i++) if (links[i].size()>=2) arrayList.add(i);
        return arrayList;
    }

    public int getComponents13() {
        int[][] map=new int[6+(n+1)/2][6+(n+1)/2];
        for (int i=0;i<12;i+=2) {
            for (int j: links[i]) {
                if (j%2!=0)
                    map[i/2][6+(j-1)/2]=map[6+(j-1)/2][i/2]=1;
            }
        }
        return new GraphMap(map).getComponents();
    }

    public int getComponents14() {
        int[][] map=new int[6+n/2][6+n/2];
        for (int i=0;i<12;i+=2) {
            for (int j: links[i]) {
                if (j%2==0)
                    map[i/2][5+j/2]=map[5+j/2][i/2]=1;
            }
        }
        return new GraphMap(map).getComponents();
    }

    public boolean containCross() {
        for (int x1=0;x1<=5;x1++) {
            for (int x2=x1+1;x2<=5;x2++) {
                for (int y1: links[x1]) {
                    for (int y2: links[x2]) {
                        if (y1>y2) return true;
                    }
                }
            }
        }
        for (int x1=6;x1<=11;x1++) {
            for (int x2=x1+1;x2<=11;x2++) {
                for (int y1: links[x1]) {
                    for (int y2: links[x2]) {
                        if (y1<y2) return true;
                    }
                }
            }
        }
        return false;
    }

    public void setLast(int x1, int y1, int x2, int y2) {
        lastx1=x1;lasty1=y1;lastx2=x2;lasty2=y2;
    }

    public ArrayList<Integer> getDegrees() {
        ArrayList<Integer> arrayList=new ArrayList<>();
        for (int i=1;i<=n;i++) {
            int cnt=0;
            if (i==1 || i==n) cnt++;
            else cnt+=2;
            for (int j=0;j<12;j++) {
                if (links[j].contains(i)) cnt++;
            }
            arrayList.add(cnt);
        }
        return arrayList;
    }

    public boolean valid() {
        ArrayList<Integer> arrayList= getDegrees();
        for (int i=0;i<n;i++) {
            if (i==0 || i==n-1) {
                if (arrayList.get(i)<=3) return false;
            }
            else if (arrayList.get(i)<=4) return false;
        }
        return !containCross() && getComponents13()==getComponents14();
    }

    public String toString() {
        String string=n+"-";
        string+=String.join("_", getDegrees().stream().map(String::valueOf).collect(Collectors.toList()));
        return string;
    }

    public static Graph readFile(File file) {
        if (!file.exists()) return null;
        try {
            Graph graph=new Graph();
            Scanner scanner = new Scanner(file);
            graph.n=scanner.nextInt();
            if (graph.n<2) throw new Exception();
            for (int i=0;i<12;i++) {
                int num=scanner.nextInt();
                if (num<=0 || num>graph.n) throw new Exception();
                for (int j=1;j<=num;j++) {
                    int x=scanner.nextInt();
                    if (x>graph.n) throw new Exception();
                    graph.links[i].addLast(x);
                }
            }
            return graph;
        }
        catch (Exception e) {return null;}
    }

    public static Graph getDefaultGraph() {
        Graph graph=new Graph();
        graph.n=2;
        graph.links[0].addLast(1);
        graph.links[1].addLast(1);
        graph.links[2].addLast(1);graph.links[2].addLast(2);
        graph.links[3].addLast(2);
        graph.links[4].addLast(2);
        graph.links[5].addLast(2);
        graph.links[6].addLast(2);
        graph.links[7].addLast(2);
        graph.links[8].addLast(2);graph.links[8].addLast(1);
        graph.links[9].addLast(1);
        graph.links[10].addLast(1);
        graph.links[11].addLast(1);
        return graph;
    }



}
