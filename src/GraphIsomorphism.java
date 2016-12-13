import java.util.*;

/**
 * Created by oc on 2016/12/13.
 */
public class GraphIsomorphism {

    public static boolean isIsomorphic(int[][] m1, int[][] m2) {
        if (m1==null || m2==null) return false;
        return areIsomorphic(new GraphIsomorphism(BitMatrix.getBitMatrix(m1)),
                new GraphIsomorphism(BitMatrix.getBitMatrix(m2)), m1.length)!=null;

    }

    private static class Node {
        int data;
        Node[] children;

        Node(int data){
            this.data= data;
        }

        void addChildren(Node[] C){
            this.children= C;
        }

    }

    private static class BitMatrix {
        BitSet[] matrix;

        BitMatrix(int n){ //Initialize a square binary matrix with side n.
            this.matrix= new BitSet[n];
            for (int i= 0; i < n; i++){
                this.matrix[i]= new BitSet(n);
            }
        }

        static BitMatrix getBitMatrix(int[][] map) {
            int n=map.length;
            BitMatrix bitMatrix=new BitMatrix(n);
            for (int i=0;i<n;i++)
                for (int j=0;j<n;j++)
                    if (map[i][j]==1)
                        bitMatrix.setBit(i, j, true);
            return bitMatrix;
        }

        void setBit(int i, int j, boolean value){
            this.matrix[i].set(j, value);
        }

        boolean getBit(int i, int j){
            return this.matrix[i].get(j);
        }

        int getSize(){
            return  this.matrix.length;
        }

    }


    private static class AbstractTree {
        int n;
        int height;
        int[] width; //Number of nodes in the spanning tree at a level.
        int[] level; //Map the index of a node to its level in the spanning tree.

        AbstractTree(int n){
            this.n= n;
            level= new int[n];
        }

        void setLevel(int node, int l){
            level[node]= l;
        }

        int getLevel(int i){
            return level[i];
        }

        void setHeight(){
            int largest= 0;
            for (int i= 0; i < n; i++){
                if (level[i] > largest){
                    largest= level[i];
                }
            }
            height= largest;
        }

        void setWidths(){
            width= new int[height+1];
            width[0]= 1;
            for (int i= 0; i < n; i++){
                if (level[i] != 0){
                    width[level[i]]++;
                }
            }
        }

    }

    private static class Map {
        int length;
        int[][] map;

        Map(int n){
            length= 0;
            map= new int[n][2];
        }

        void add(int i, int key, int value){
            map[i][0]= key;
            map[i][1]= value;
            length++;
        }

        int getKey(int i){
            return map[i][0];
        }

        int getValue(int i){
            return map[i][1];
        }

        void pop(){
            length--;
        }
    }


    private Node[] V;

    private GraphIsomorphism(BitMatrix matrix) {
        int n = matrix.getSize();
        this.V = new Node[n];
        for (int i = 0; i < n; i++) {
            V[i] = new Node(i);
        }
        for (int i = 0; i < n; i++) {
            int childrenSize = 0;
            for (int j = 0; j < n; j++) {
                if (matrix.getBit(i, j)) {
                    childrenSize++;
                }
            }
            Node[] C = new Node[childrenSize];
            int k = 0;
            for (int j = 0; j < n; j++) {
                if (matrix.getBit(i, j)) {
                    C[k] = V[j];
                    k++;
                }
            }
            V[i].addChildren(C);
        }
    }

    private static AbstractTree BFS(GraphIsomorphism G, int m, int[] miniMap) {
        // Breadth First Search method to initialize AbstractTree object from Graph G.
        int n = G.V.length;

        AbstractTree tree = new AbstractTree(n);
        boolean[] visited = new boolean[n];
        int[] queue = new int[n]; // Queue object as an array.

        visited[m] = true;
        queue[0] = m;
        tree.setLevel(m, 0);
        int l = 1; // Marks the end of the queue in the array.

        int i = 0; // Marks the beginning of the queue in the array.
        while (i < l) { // Check if queue is empty.
            int r = queue[i];
            i++;
            int k = tree.getLevel(r); // Current node's level.
            for (int j = 0; j < G.V[r].children.length; j++) {
                Node s = G.V[r].children[j];
/*				int b = 0;
				for (int a = 0; a < n; a++){
					if (G.V[a] == s){
						b = a;
						break;
					}
				}*/
                int b = miniMap[s.data];
                if (!visited[b]) {
                    visited[b] = true;
                    queue[l] = b;
                    tree.setLevel(b, k + 1); // Note: one deeper level is k+1.
                    l++;
                }
            }
        }

        tree.setHeight();
        tree.setWidths();

        return tree;
    }

    private static Map areIsomorphic(GraphIsomorphism G1, GraphIsomorphism G2, int size) {
        int n = G1.V.length;
        Map map = new Map(n);

        if (n != G2.V.length) {
            return null;
        }

        long totalTime = System.nanoTime();
        AbstractTree[] trees1 = new AbstractTree[n];
        AbstractTree[] trees2 = new AbstractTree[n];

        //Quick maps to fix performance issue after making branch MatchByConnectedComponents
        //Note: we don't know the largest payload so we have to use a java.util.Map as we can't set a defined size.
        int[] miniMap1 = new int[size];

        for (int a = 0; a < n; a++){
            miniMap1[G1.V[a].data] = a;
        }

        int[] miniMap2 = new int[size];

        for (int a = 0; a < n; a++){
            miniMap2[G2.V[a].data] = a;
        }

        for (int i = 0; i < n; i++) {
            trees1[i] = GraphIsomorphism.BFS(G1, i, miniMap1);
            trees2[i] = GraphIsomorphism.BFS(G2, i, miniMap2);
        }
        long endTime = System.nanoTime();
        long duration = endTime - totalTime;

        boolean[] matched = new boolean[n]; // Keep track of matched nodes in G2.
        int mismatched = -1;

        for (int i = 0; i < n; i++) {
            int length = map.length;
            for (int j = 0; j < n; j++) {
                if (!matched[j] && j > mismatched) {
                    boolean match = checkConditions(map, trees1[i], trees2[j], true);
                    if (match) {
                        map.add(length, i, j); // Add key-value pair (i,j) to map.
                        matched[j] = true; // Node j in G2 is now matched.
                        mismatched = -1;
                        break; // Break at first match (such that j-th index in G2 is larger than 'mismatched').
                    }
                }
            }
            if (map.length == length) { // Check if new key-value pair was not added.
                if (i - 1 < 0) { // If true, we cannot find a match for the very first node in G1.
                    return null;
                }
                mismatched = map.getValue(i - 1); // Update 'mismatched' because last key-value pair is wrong.
                matched[mismatched] = false; // Update information about matched node in G2.
                map.pop(); // Remove the last key-value pair from map.
                i = map.length - 1; // On next iteration, we will try to match i-th node in G1 with new node in G2.
            }
        }

        return map;
    }

    private static boolean checkConditions(Map map, AbstractTree tree1, AbstractTree tree2, boolean match) {
        if (tree1.height != tree2.height) { // Fastest condition to check for non-isomorphism.
            match = false;
        } else {
            for (int k = 0; k < tree1.height; k++) {
                if (tree1.width[k] != tree2.width[k]) { // Number of nodes a distance k away must be preserved.
                    match = false;
                    break;
                }
            }
            if (match) {
                for (int k = 0; k < map.length; k++) {
                    int key = map.getKey(k);
                    int value = map.getValue(k);
                    int keyLevel = tree1.getLevel(key);
                    int valueLevel = tree2.getLevel(value);
                    if (keyLevel != valueLevel) { // Check to see if shortest distance is preserved.
                        match = false;
                        break;
                    }
                }
            }
        }
        return match;
    }

}
