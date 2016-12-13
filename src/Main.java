import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * Created by oc on 2016/12/12.
 */
public class Main {

    public static void main(String[] args){
        new Main().run();
    }

    LinkedList<Graph> stack;
    JFrame jframe;
    GraphPanel graphPanel;
    Random random;

    File dir=null;

    public Main() {
        stack=new LinkedList<>();
        dir=new File("save");
        if (!dir.exists()) dir.mkdir();
        random=new Random();
    }

    private void run() {
        jframe=new JFrame("现代图论：找寻所有基本模块 - 1601214454 陈章");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(1000, 700);
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);
        addMenu();

        graphPanel=new GraphPanel();
        jframe.add(graphPanel);

        stack.add(Graph.getDefaultGraph());

        draw();
    }

    private void addMenu() {
        MenuBar menuBar=new MenuBar();
        Menu fileMenu=new Menu("File");
        fileMenu.add(new MenuItem("Save (s)")).addActionListener(e->save());
        fileMenu.add(new MenuItem("Load (l)")).addActionListener(e->load());
        fileMenu.add(new MenuItem("Check isomorphism (c)")).addActionListener(e->checkIsomorphism());
        fileMenu.add(new MenuItem("Exit (e)")).addActionListener(e->exit());
        menuBar.add(fileMenu);

        Menu editMenu=new Menu("Edit");
        editMenu.add(new MenuItem("Add a point (a)")).addActionListener(e->add());
        editMenu.add(new MenuItem("Next generation (n)")).addActionListener(e->next());
        editMenu.add(new MenuItem("Undo (u)")).addActionListener(e->undo());
        menuBar.add(editMenu);

        jframe.setMenuBar(menuBar);
        jframe.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_S: save();break;
                    case KeyEvent.VK_L: load();break;
                    case KeyEvent.VK_C: checkIsomorphism();break;
                    case KeyEvent.VK_E:case KeyEvent.VK_Q: exit();break;
                    case KeyEvent.VK_A:case KeyEvent.VK_RIGHT: add();break;
                    case KeyEvent.VK_N:case KeyEvent.VK_UP: next();break;
                    case KeyEvent.VK_U:case KeyEvent.VK_BACK_SPACE:case KeyEvent.VK_LEFT: undo();break;
                }
            }
        });
    }

    public void draw() {
        draw("");
    }

    public void draw(String string) {
        if (stack.isEmpty()) return;
        if (stack.size()>=1000) stack.removeLast();
        Graph graph=stack.peek();
        graphPanel.setGraph(graph);
        graphPanel.setString(string);
        graphPanel.repaint();
    }

    private void save() {
        if (stack.isEmpty()) return;
        Graph graph=stack.peek();
        if (!graph.valid()) {
            if (JOptionPane.showConfirmDialog(jframe, "Invalid graph, confirm to save?", "Warning", JOptionPane.YES_NO_OPTION)
                    ==JOptionPane.NO_OPTION) return;
        }
        String s=findIsomorphism(graph);
        if (s!=null) {
            if (JOptionPane.showConfirmDialog(jframe, "Isomorphism with "+s+", confirm to save?", "Warning", JOptionPane.YES_NO_OPTION)
                    ==JOptionPane.NO_OPTION) return;
        }
        s=graph.toString();
        File gFile=new File(dir, s+".graph"), iFile=new File(dir, s+".png");
        for (int i=1;;i++) {
            if (!gFile.exists()) break;
            gFile=new File(dir, s+"~"+i+".graph");
            iFile=new File(dir, s+"~"+i+".png");
        }
        try {
            FileWriter fileWriter=new FileWriter(gFile);
            fileWriter.write(graph.n+"\n");
            for (int i=0;i<12;i++) {
                int size=graph.links[i].size();
                fileWriter.write(size+" ");
                for (int j=0;j<size;j++)
                    fileWriter.write(graph.links[i].get(j)+" ");
                fileWriter.write("\n");
            }
            fileWriter.close();

            BufferedImage bufferedImage=new BufferedImage(graphPanel.getWidth(), graphPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d=bufferedImage.createGraphics();
            graphPanel.setString("Saved as "+gFile.getName());
            graphPanel.paint(g2d);
            ImageIO.write(bufferedImage, "PNG", iFile);

            draw("File saved to save/"+gFile.getName()+", save/"+iFile.getName());

        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(jframe, "Save failed!");
        }

    }

    private void load() {
        JFileChooser jFileChooser=new JFileChooser(dir);
        jFileChooser.setAcceptAllFileFilterUsed(false);
        jFileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".graph");
            }

            @Override
            public String getDescription() {
                return "Graph file (*.graph)";
            }
        });
        if (jFileChooser.showOpenDialog(jframe)==JFileChooser.APPROVE_OPTION) {
            Graph graph=Graph.readFile(jFileChooser.getSelectedFile());
            if (graph!=null) {
                stack.clear();
                stack.addFirst(graph);
                draw("Load "+jFileChooser.getSelectedFile().getName()+" successfully!");
            }
            else {
                JOptionPane.showMessageDialog(jframe, "Invalid graph!");
            }
        }


    }

    private void exit() {
        if (JOptionPane.showConfirmDialog(jframe, "Exit?", "", JOptionPane.YES_NO_OPTION)==JOptionPane.OK_OPTION)
            System.exit(0);
    }

    private void add() {
        Graph graph=new Graph(stack.peek());
        graph.n++;
        graph.links[5].addLast(graph.n);
        graph.links[6].addFirst(graph.n);
        graph.lastx1=-1;
        stack.addFirst(graph);
        draw("Add a new point.");
    }

    private void next() {
        Graph graph=new Graph(stack.peek());

        int x1=-1, y1=-1, x2=-1, y2=-1;

        if (graph.links[5].contains(graph.n-1) && graph.links[6].contains(graph.n-1)) {
            if (graph.links[5].contains(graph.n-2)) {draw("Unable to generate the next graph.");return;}
            x1=5;
            y1=graph.n-1;

            for (int i=0;i<500;i++) {
                int u = randomGenerate(graph, x1, y1);
                if (u==-1) {draw("Unable to generate the next graph.");return;}
                x2=u%12;
                y2=u/12;
                if (testGraph(new Graph(graph), x1, y1, x2, y2)) return;
            }
        }
        else {
            for (int i=0;i<1000;i++) {
                int u=randomGenerate(graph, -1, -1);
                if (u==-1) {draw("Unable to generate the next graph.");return;}
                x1=u%12;y1=u/12;
                int v=randomGenerate(graph, x1, y1);
                if (v==-1) {draw("Unable to generate the next graph.");return;}
                x2=v%12;
                y2=v/12;
                if (testGraph(new Graph(graph), x1, y1, x2, y2)) return;
            }
        }
        draw("Unable to generate the next graph.");
    }

    private boolean testGraph(Graph tmp, int x1, int y1, int x2, int y2) {
        if (exchange(tmp, x1, y1) && exchange(tmp, x2, y2) && tmp.valid()) {
            tmp.setLast(x1, y1, x2, y2);
            stack.addFirst(tmp);
            String string=findIsomorphism(tmp);
            if (string!=null) draw("Isomorphic with file: "+string);
            else draw();
            return true;
        }
        return false;
    }

    private boolean exchange(Graph graph, int x, int y) {
        if (graph.links[x].size()<=1) return false;
        if (y==graph.links[x].getFirst()) {
            int xx=x-1, yy=y+(x>=6?-1:1);
            if (yy<=0 || yy>graph.n) return false;
            if (xx<0) xx+=12;
            graph.links[x].removeFirst();
            graph.links[xx].addLast(yy);
        }
        else if (y==graph.links[x].getLast()) {
            int xx=x+1, yy=y+(x>=6?1:-1);
            if (yy<=0 || yy>graph.n) return false;
            if (xx>=12) xx-=12;
            graph.links[x].removeLast();
            graph.links[xx].addFirst(yy);
        }
        return true;
    }

    private boolean cross(int x1, int y1, int x2, int y2) {
        if (x1<0 || y1<0 || x2<0 || y2<0) return true;
        if (x1>=0 && x1<=5 && x2>=6 && x2<=11) return true;
        if (x1>=6 && x1<=11 && x2>=0 && x2<=5) return true;
        if (x1>=0 && x1<=5) {
            if (y1<y2 && x1>x2) return false;
            if (y1>y2 && x1<x2) return false;
        }
        else {
            if (y1<y2 && x1<x2) return false;
            if (y1>y2 && x1>x2) return false;
        }
        return true;
    }

    private int randomGenerate(Graph graph, int x1, int y1) {
        ArrayList<Integer> arrayList=graph.getDegreesMoreThanOne();
        for (int i=0;i<500;i++) {
            int u=random.nextInt(arrayList.size());
            int v=random.nextInt(2);
            int x=arrayList.get(u), y=(v==0?graph.links[x].getFirst():graph.links[x].getLast());
            if (x==x1 || (y1>=0 && y%2==y1%2) || !cross(x1, y1, x, y)) continue;
            return y*12+x;
        }
        return -1;
    }

    private void undo() {
        if (stack.size()>1) {
            stack.pop();
            draw();
        }
    }

    private String findIsomorphism(Graph graph) {
        GraphMap graphMap=new GraphMap(graph);
        File[] files=dir.listFiles((dir1, name) -> name!=null && name.endsWith(".graph"));
        for (File file: files) {
            if (GraphIsomorphism.isIsomorphic(graphMap.map,
                    new GraphMap(Graph.readFile(file)).map))
                return file.getName();
        }
        return null;
    }

    private void checkIsomorphism() {
        File[] files=dir.listFiles((dir1, name) -> name!=null && name.endsWith(".graph"));
        int cnt=0;
        System.out.println("Checking isomorphism...");
        for (int i=0;i<files.length;i++) {
            for (int j=i+1;j<files.length;j++) {
                if (GraphIsomorphism.isIsomorphic(new GraphMap(Graph.readFile(files[i])).map,
                        new GraphMap(Graph.readFile(files[j])).map)) {
                    cnt++;
                    System.out.println("Isomorphism found! ["+files[i].getName()+", "+files[j].getName()+"]");
                }
            }
        }
        System.out.println("Check finished... "+cnt+" pairs of isomorphism found.\n\n");
        JOptionPane.showMessageDialog(jframe, cnt+" pairs of isomorphism found.\nCheck console output for more details.");
    }

}
